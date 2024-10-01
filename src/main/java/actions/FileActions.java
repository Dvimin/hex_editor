package actions;

import ui.BinTableModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class FileActions {

    private static final int PAGE_SIZE = 256; // Размер страницы
    private File currentFile;
    private File backupFile;
    private RandomAccessFile raf;
    private int currentPage = 0;
    private int totalPages = 0;

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
                showErrorDialog("Ошибка чтения файла: ", e);
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
        Path backupFilePath = Paths.get(backupDir.toString(), file.getName() + ".bak");
        backupFile = backupFilePath.toFile();
        Files.copy(file.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
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
                            showErrorDialog("Ошибка при удалении файла из папки backup: ", e);
                        }
                    });
        }
    }

    // Открытие файла и расчет количества страниц
    private void openAndLoadFile(BinTableModel btm) throws IOException {
        raf = new RandomAccessFile(currentFile, "r");
        totalPages = calculateTotalPages();
        loadPage(btm, 0);
    }

    // Метод для загрузки страницы по номеру
    private void loadPage(BinTableModel btm, int pageNumber) throws IOException {
        btm.clearData();
        long offset = pageNumber * PAGE_SIZE;
        raf.seek(offset);
        byte[] pageData = new byte[PAGE_SIZE];
        int bytesRead = raf.read(pageData);
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
                showErrorDialog("Ошибка при загрузке страницы: ", e);
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
            } catch (IOException e) {
                showErrorDialog("Ошибка при сохранении файла: ", e);
            }
        }
    }


    // Метод для загрузки данных страницы без обновления UI
    private byte[] loadPageData(int pageNumber) throws IOException {
        long offset = pageNumber * PAGE_SIZE;
        raf.seek(offset);
        byte[] pageData = new byte[PAGE_SIZE];
        int bytesRead = raf.read(pageData);
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
                showErrorDialog("Ошибка при закрытии файла: ", e);
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
                showErrorDialog("Ошибка при удалении бэкап файла.", new IOException("Не удалось удалить бэкап файл."));
            } else {
                System.out.println("Файл успешно удалён.");
            }
            backupFile = null;
        } else {
            System.out.println("Бэкап-файл не существует или уже был удалён.");
        }
    }

    // Общий метод для обработки ошибок
    private void showErrorDialog(String message, Exception e) {
        JOptionPane.showMessageDialog(null, message + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
}
