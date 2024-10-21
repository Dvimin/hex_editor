package actions;

import ui.BinTableModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.*;

public class FileActions {

    public static final int PAGE_SIZE = 256; // Размер страницы
    private File currentFile;
    private File backupFile;
    private RandomAccessFile raf;
    private static int currentPage = 0;
    private static int totalPages = 0;

    // Создание и открытие начального файла
    public void openInitialFile(BinTableModel btm) {
        try {
            Path resourceFilePath = Paths.get("src/main/resources/InitialFile.txt");
            if (Files.notExists(resourceFilePath)) {
                createInitialFileInResources();
            }
            closeAndDeleteBackup();
            currentFile = resourceFilePath.toFile();
            createFileCopy(currentFile);
            openAndLoadFile(btm);
        } catch (IOException e) {
            showErrorDialog(null, "Ошибка при открытии начального файла: ", e);
        }
    }

    // Создание начального файла в ресурсах с размером в одну страницу
    public void createInitialFileInResources() {
        try {
            Path resourceFilePath = Paths.get("src/main/resources/InitialFile.txt");
            byte[] zeros = new byte[1];
            Files.write(resourceFilePath, zeros, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            showErrorDialog(null, "Ошибка при создании файла в ресурсах: ", e);
        }
    }

    // Открытие файла и загрузка первой страницы в модель таблицы
    public void openFile(BinTableModel btm, ActionEvent actionEvent) {
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            closeAndDeleteBackup();
            currentFile = fileChooser.getSelectedFile();
            try {
                createFileCopy(currentFile);
                openAndLoadFile(btm);
            } catch (IOException e) {
                showErrorDialog(null, "Ошибка чтения файла: ", e);
            }
        }
    }

    // Метод для создания копии файла в папке backup
    private void createFileCopy(File file) throws IOException {
        String projectDir = System.getProperty("user.dir");
        Path backupDir = Paths.get(projectDir, "backup");
        clearBackupDirectory(backupDir);
        if (!Files.exists(backupDir)) {
            Files.createDirectory(backupDir);
        }
        backupFile = getBackupFilePath().toFile();
        Files.copy(file.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    // Автоматическое открытие файла с определённой страницы файла backup
    public void autoOpenFileAtPage(BinTableModel btm) {
        File backupFile = getBackupFilePath().toFile();

        if (backupFile.exists()) {
            try (RandomAccessFile raf = new RandomAccessFile(backupFile, "r");
                 FileChannel fileChannel = raf.getChannel();
                 FileLock lock = fileChannel.lock(0, Long.MAX_VALUE, true)) {
                openAndLoadFileAtPage(btm, currentPage);
            } catch (IOException e) {
                showErrorDialog(null, "Ошибка чтения файла: ", e);
            }
        } else {
            showErrorDialog(null, "Файл не был сохранён или открыт ранее.", new Exception("Нет текущего файла."));
        }
    }


    // Открытие файла и загрузка указанной страницы файла backup
    private void openAndLoadFileAtPage(BinTableModel btm, int pageNumber) throws IOException {
        if (raf != null) {
            raf.close();
        }
        File backupFile = getBackupFilePath().toFile();
        raf = new RandomAccessFile(backupFile, "r");
        totalPages = calculateTotalPages();
        if (pageNumber >= 0 && pageNumber < totalPages) {
            loadPage(btm, pageNumber);
        } else {
            showErrorDialog(null, "Некорректный номер страницы. Всего страниц: " + totalPages, new IllegalArgumentException("Некорректный номер страницы"));
            loadPage(btm, 0);
        }
    }

    // Автоматическое сохранение всех стариц файла backup
    public void autoSaveFileAtPage(BinTableModel btm) {
        Path backupDirectoryPath = Paths.get("backup");
        Path tempBackupFilePath = backupDirectoryPath.resolve("backup2.txt");
        File tempBackupFile = tempBackupFilePath.toFile();
        File backupFile = getBackupFilePath().toFile();

        try (FileOutputStream fos = new FileOutputStream(tempBackupFile);
             BufferedOutputStream bos = new BufferedOutputStream(fos)) {

            writeAllPages(bos, btm);
            bos.flush();
        } catch (IOException e) {
            showErrorDialog(null, "Ошибка при записи временного файла: ", e);
            return;
        }

        try (FileInputStream fis = new FileInputStream(tempBackupFile);
             FileOutputStream backupFos = new FileOutputStream(backupFile);
             BufferedOutputStream backupBos = new BufferedOutputStream(backupFos)) {

            byte[] bufferCopy = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(bufferCopy)) != -1) {
                backupBos.write(bufferCopy, 0, bytesRead);
            }
            backupBos.flush();

        } catch (IOException e) {
            showErrorDialog(null, "Ошибка при копировании данных во временный файл бэкапа: ", e);
        }
        try {
            Files.delete(tempBackupFilePath);
        } catch (IOException e) {
            System.out.println("Не удалось удалить временный файл. Причина: " + e.getMessage());
        }
    }

    // Метод для очистки папки backup
    private void clearBackupDirectory(Path backupDir) throws IOException {
        if (Files.exists(backupDir)) {
            Files.walk(backupDir)
                    .filter(Files::isRegularFile)
                    .forEach(file -> {
                        try {
                            Files.delete(file);
                        } catch (IOException e) {
                            showErrorDialog(null, "Ошибка при удалении файла из папки backup: ", e);
                        }
                    });
        }
    }

    // Открытие файла и расчет количества страниц
    private void openAndLoadFile(BinTableModel btm) throws IOException {
        if (raf != null) {
            raf.close();
        }
        raf = new RandomAccessFile(currentFile, "r");
        totalPages = calculateTotalPages();
        loadPage(btm, 0);
    }

    // Метод для загрузки страницы по номеру
    private void loadPage(BinTableModel btm, int pageNumber) throws IOException {
        btm.clearData();
        long offset = pageNumber * PAGE_SIZE;
        long currentPosition = raf.getFilePointer();
        raf.seek(offset);
        byte[] pageData = new byte[PAGE_SIZE];
        int bytesRead = raf.read(pageData);
        raf.seek(currentPosition);
        if (bytesRead > 0) {
            btm.addData(trimData(pageData, bytesRead));
        }
        currentPage = pageNumber;
    }

    // Метод для получения пути к файлу бэкапа
    private Path getBackupFilePath() {
        Path backupDirectoryPath = Paths.get("backup");
        return backupDirectoryPath.resolve("backup.txt");
    }

    // Обрезаем массив байтов до фактического количества считанных байтов
    private byte[] trimData(byte[] data, int length) {
        if (length < PAGE_SIZE) {
            byte[] trimmedData = new byte[length];
            System.arraycopy(data, 0, trimmedData, 0, length);
            return trimmedData;
        }
        return data;
    }

    // Переход на следующую страницу
    public void nextPage(BinTableModel btm) {
        navigatePage(btm, currentPage + 1, "Вы на последней странице.");
    }

    // Переход на предыдущую страницу
    public void previousPage(BinTableModel btm) {
        navigatePage(btm, currentPage - 1, "Вы на первой странице.");
    }

    // Проверка на конец файла
    public boolean hasNextPage() {
        return currentPage < totalPages - 1;
    }

    // Общий метод для навигации по страницам
    private void navigatePage(BinTableModel btm, int newPage, String boundaryMessage) {
        if (newPage >= 0 && newPage < totalPages) {
            try {
                loadPage(btm, newPage);
            } catch (IOException e) {
                showErrorDialog(null, "Ошибка при загрузке страницы: ", e);
            }
        } else {
            JOptionPane.showMessageDialog(null, boundaryMessage, "Информация", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // Метод для подсчета общего количества страниц
    private int calculateTotalPages() throws IOException {
        return (int) Math.ceil((double) raf.length() / PAGE_SIZE);
    }

    // Сохранение текущей страницы, включая все до и после нее
    public void saveCurrentPageWithContext(ActionEvent actionEvent, BinTableModel btm) {
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showSaveDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            if (selectedFile.exists()) {
                int response = JOptionPane.showConfirmDialog(null,
                        "Файл уже существует. Перезаписать?",
                        "Предупреждение",
                        JOptionPane.YES_NO_OPTION);
                if (response != JOptionPane.YES_OPTION) {
                    selectedFile = createNewFileWithSuffix(selectedFile);
                }
            }

            try (FileOutputStream fos = new FileOutputStream(selectedFile);
                 BufferedOutputStream bos = new BufferedOutputStream(fos)) {
                writeAllPages(bos, btm);
                bos.flush();
                JOptionPane.showMessageDialog(null, "Файл сохранен: " + selectedFile.getAbsolutePath(), "Сохранено", JOptionPane.INFORMATION_MESSAGE);
                openInitialFile(btm);

            } catch (IOException e) {
                showErrorDialog(null, "Ошибка при сохранении файла: ", e);
            }
        }
    }

    // Метод для записи всех страниц в BufferedOutputStream
    private void writeAllPages(BufferedOutputStream bos, BinTableModel btm) throws IOException {
        for (int pageNumber = 0; pageNumber < currentPage; pageNumber++) {
            byte[] pageData = loadPageData(pageNumber);
            bos.write(pageData);
        }

        byte[] currentPageData = btm.getAllData();
        bos.write(currentPageData);

        if (currentPage < totalPages - 1) {
            for (int pageNumber = currentPage + 1; pageNumber < totalPages; pageNumber++) {
                byte[] pageData = loadPageData(pageNumber);
                bos.write(pageData);
            }
        }
    }

    // Метод для загрузки данных страницы без обновления UI
    private byte[] loadPageData(int pageNumber) throws IOException {
        if (raf == null) {
            throw new IllegalStateException("RandomAccessFile не был инициализирован.");
        }
        if (pageNumber < 0 || pageNumber >= totalPages) {
            throw new IllegalArgumentException("Неправильный номер страницы: " + pageNumber);
        }
        long offset = pageNumber * PAGE_SIZE;
        byte[] pageData = new byte[PAGE_SIZE];
        int bytesRead;
        try {
            raf.seek(offset);
            bytesRead = raf.read(pageData);
        } catch (IOException e) {
            throw new IOException("Ошибка чтения файла на странице: " + pageNumber, e);
        }

        if (bytesRead == -1) {
            throw new IOException("Достигнут конец файла!");
        }
        return trimData(pageData, bytesRead);
    }

    // Метод для создания нового файла с суффиксом, если файл уже существует
    private File createNewFileWithSuffix(File originalFile) {
        String originalPath = originalFile.getAbsolutePath();
        String newFilePath;
        int counter = 1;

        do {
            newFilePath = originalPath.replaceFirst("(\\.\\w+)?$", "(" + counter + ")$1");
            counter++;
        } while (new File(newFilePath).exists());

        return new File(newFilePath);
    }

    // Закрытие файла и выход из приложения
    public void exit() {
        closeAndDeleteBackup();
        System.exit(0);
    }

    // Закрытие текущего файла и удаление бэкапа
    public void closeAndDeleteBackup() {
        closeFile();
        deleteBackupFile();
    }

    // Закрытие файла
    private void closeFile() {
        if (raf != null) {
            try {
                raf.close();
                raf = null;
            } catch (IOException e) {
                showErrorDialog(null, "Ошибка при закрытии файла: ", e);
            }
        }
    }

    // Удаление бэкап-файла
    private void deleteBackupFile() {
        if (backupFile != null && backupFile.exists()) {
            if (!backupFile.delete()) {
                showErrorDialog(null, "Ошибка при удалении бэкап файла.", new IOException("Не удалось удалить бэкап файл."));
            }
            backupFile = null;
        }
    }

    // Возвращает общее количество старниц в открытом файле
    public static int getTotalPages() {
        return (totalPages > 0) ? totalPages : 1;
    }

    // Возвращает номер текущей открытой страницы в открытом файле
    public static int getCurrentPage() {
        return currentPage + 1;
    }

    // Общий метод для обработки ошибок
    private void showErrorDialog(Component parent, String message, Exception e) {
        String fullMessage = String.format("%s%n%s", message, e.getMessage());
        JOptionPane.showMessageDialog(parent, fullMessage, "Ошибка", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }

    //Метод для обновления панели навигации
    public static void updateNavigationLabel(JLabel navigationLabel) {
        int currentPage = getCurrentPage();
        int totalPages = getTotalPages();
        navigationLabel.setText("Page: " + currentPage + " of " + totalPages);
    }


}
