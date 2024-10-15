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

    private static final int PAGE_SIZE = 256; // Размер страницы
    private File currentFile;
    private File backupFile;
    private RandomAccessFile raf;
    private int currentPage = 0;
    private int totalPages = 0;

    public void createAndOpenInitialFile(BinTableModel btm) {
        createInitialFileInResources();
        try {
            Path resourceFilePath = Paths.get("src/main/resources/InitialFile.txt");
            currentFile = resourceFilePath.toFile();
            openAndLoadFile(btm);
        } catch (IOException e) {
            showErrorDialog(null, "Ошибка при открытии файла: ", e);
        }
    }

    public void createInitialFileInResources() {
        try {
            Path resourceFilePath = Paths.get("src/main/resources/InitialFile.txt");

            byte[] zeros = new byte[]{0x00};
            Files.write(resourceFilePath, zeros, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            System.out.println("Файл создан: " + resourceFilePath.toString());
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
        Path backupFilePath = Paths.get(backupDir.toString(), "backup.txt");
        backupFile = backupFilePath.toFile();
        Files.copy(file.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        System.out.println("Бэкап-файл создан.");
    }

    // Автоматическое открытие файла с определённой страницы
    public void autoOpenFileAtPage(BinTableModel btm) {
        Path backupDirectoryPath = Paths.get("backup");
        Path backupFilePath = backupDirectoryPath.resolve("backup.txt");
        File backupFile = backupFilePath.toFile();

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


    // Открытие файла и загрузка указанной страницы
    private void openAndLoadFileAtPage(BinTableModel btm, int pageNumber) throws IOException {
        if (raf != null) {
            raf.close();
        }
        raf = new RandomAccessFile(backupFile, "r");
        totalPages = calculateTotalPages();
        if (pageNumber >= 0 && pageNumber < totalPages) {
            loadPage(btm, pageNumber);
        } else {
            showErrorDialog(null, "Некорректный номер страницы. Всего страниц: " + totalPages, new IllegalArgumentException("Некорректный номер страницы"));
            loadPage(btm, 0);
        }
    }

    // Автоматическое сохранение всех стариц
    public void autoSaveFileAtPage(BinTableModel btm) {
        Path backupDirectoryPath = Paths.get("backup");
        Path backupFilePath = backupDirectoryPath.resolve("backup.txt");
        Path tempBackupFilePath = backupDirectoryPath.resolve("backup2.txt");
        File backupFile = backupFilePath.toFile();
        File tempBackupFile = tempBackupFilePath.toFile();

        try (FileOutputStream fos = new FileOutputStream(tempBackupFile);
             BufferedOutputStream bos = new BufferedOutputStream(fos)) {

            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            for (int pageNumber = 0; pageNumber < currentPage; pageNumber++) {
                byte[] pageData = loadPageData(pageNumber);
                buffer.write(pageData);
            }

            byte[] currentPageData = btm.getAllData();
            buffer.write(currentPageData);

            for (int pageNumber = currentPage + 1; pageNumber < totalPages; pageNumber++) {
                byte[] pageData = loadPageData(pageNumber);
                buffer.write(pageData);
            }

            bos.write(buffer.toByteArray());
            bos.flush();
            System.out.println("Временный файл бэкапа успешно записан: " + tempBackupFile.getAbsolutePath());

            try (FileInputStream fis = new FileInputStream(tempBackupFile);
                 FileOutputStream backupFos = new FileOutputStream(backupFile);
                 BufferedOutputStream backupBos = new BufferedOutputStream(backupFos)) {

                byte[] bufferCopy = new byte[1024];
                int bytesRead;
                while ((bytesRead = fis.read(bufferCopy)) != -1) {
                    backupBos.write(bufferCopy, 0, bytesRead);
                }

                backupBos.flush();
                System.out.println("Файл бэкапа успешно обновлён: " + backupFile.getAbsolutePath());

            } catch (IOException e) {
                showErrorDialog(null, "Ошибка при копировании данных во временный файл бэкапа: ", e);
            }

            System.out.println("Проверяем возможность удаления временного файла: " + tempBackupFile.getAbsolutePath());
            if (tempBackupFile.exists()) {
                System.out.println("Временный файл существует и его размер: " + tempBackupFile.length() + " байт");

                if (tempBackupFile.delete()) {
                    System.out.println("Временный файл успешно удалён.");
                } else {
                    System.out.println("Не удалось удалить временный файл. Причина: файл может быть занят другим процессом или у приложения нет прав.");
                }
            } else {
                System.out.println("Временный файл не существует перед попыткой удаления.");
            }
            try {
                Files.delete(tempBackupFilePath);
                System.out.println("Временный файл успешно удалён.");
            } catch (NoSuchFileException e) {
                System.out.println("Временный файл не существует.");
            } catch (DirectoryNotEmptyException e) {
                System.out.println("Директория не пуста: " + e.getFile());
            } catch (IOException e) {
                System.out.println("Не удалось удалить временный файл. Причина: " + e.getMessage());
            }

        } catch (IOException e) {
            showErrorDialog(null, "Ошибка при записи временного файла: ", e);
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

                for (int pageNumber = 0; pageNumber < currentPage; pageNumber++) {
                    byte[] pageData = loadPageData(pageNumber);
                    bos.write(pageData);
                }

                byte[] currentPageData = btm.getAllData();
                bos.write(currentPageData);

                for (int pageNumber = currentPage + 1; pageNumber < totalPages; pageNumber++) {
                    byte[] pageData = loadPageData(pageNumber);
                    bos.write(pageData);
                }
                bos.flush();
                JOptionPane.showMessageDialog(null, "Файл сохранен: " + selectedFile.getAbsolutePath(), "Сохранено", JOptionPane.INFORMATION_MESSAGE);
                createAndOpenInitialFile(btm);

            } catch (IOException e) {
                showErrorDialog(null, "Ошибка при сохранении файла: ", e);
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
            System.out.println("Попытка удалить файл: " + backupFile.getAbsolutePath());
            boolean deleted = backupFile.delete();
            if (!deleted) {
                System.out.println("Не удалось удалить бэкап файл.");
                showErrorDialog(null, "Ошибка при удалении бэкап файла.", new IOException("Не удалось удалить бэкап файл."));
            } else {
                System.out.println("Файл успешно удалён.");
            }
            backupFile = null;
        } else {
            System.out.println("Бэкап-файл не существует или уже был удалён.");
        }
    }

    // Общий метод для обработки ошибок
    private void showErrorDialog(Component parent, String message, Exception e) {
        String fullMessage = String.format("%s%n%s", message, e.getMessage());
        JOptionPane.showMessageDialog(parent, fullMessage, "Ошибка", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
}
