package actions;

import ui.BinTableModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;

public class FileActions {

    private static final int PAGE_SIZE = 256; // Размер страницы
    private File currentFile;
    private RandomAccessFile raf;
    private int currentPage = 0;
    private int totalPages = 0;

    // Открытие файла и загрузка первой страницы в модель таблицы
    public void openFile(BinTableModel btm, ActionEvent actionEvent) {
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            currentFile = fileChooser.getSelectedFile();
            try {
                raf = new RandomAccessFile(currentFile, "r");
                totalPages = calculateTotalPages();
                loadPage(btm, 0);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Ошибка чтения файла: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    // Метод для загрузки страницы по номеру
    private void loadPage(BinTableModel btm, int pageNumber) throws IOException {
        btm.clearData();
        long offset = pageNumber * PAGE_SIZE;
        raf.seek(offset);
        byte[] pageData = new byte[PAGE_SIZE];
        int bytesRead = raf.read(pageData);
        if (bytesRead != -1) {
            if (bytesRead < PAGE_SIZE) {
                byte[] actualData = new byte[bytesRead];
                System.arraycopy(pageData, 0, actualData, 0, bytesRead);
                btm.addData(actualData);
            } else {
                btm.addData(pageData);
            }
        }
        currentPage = pageNumber;
    }

    // Переход на следующую страницу
    public void nextPage(BinTableModel btm) {
        if (currentPage < totalPages - 1) {
            try {
                loadPage(btm, currentPage + 1);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Ошибка при загрузке следующей страницы: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(null, "Вы на последней странице.", "Информация", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // Переход на предыдущую страницу
    public void previousPage(BinTableModel btm) {
        if (currentPage > 0) {
            try {
                loadPage(btm, currentPage - 1);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Ошибка при загрузке предыдущей страницы: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(null, "Вы на первой странице.", "Информация", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // Метод для подсчета общего количества страниц
    private int calculateTotalPages() throws IOException {
        long fileSize = raf.length();
        return (int) Math.ceil((double) fileSize / PAGE_SIZE);
    }

    // Метод для загрузки тестового файла в модель таблицы
    public void setupTestFile(BinTableModel btm) {
        File file = new File("src/main/resources/test.txt");
        if (file.exists()) {
            try {
                openFileDirectly(btm, file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.err.println("Тестовый файл не найден: " + file.getAbsolutePath());
        }
    }

    // Прямое открытие файла и загрузка первой страницы
    public void openFileDirectly(BinTableModel btm, File file) throws IOException {
        currentFile = file;
        raf = new RandomAccessFile(currentFile, "r");
        totalPages = calculateTotalPages();
        loadPage(btm, 0);
    }

    // Сохранение данных модели таблицы в файл
    public void saveFile(ActionEvent actionEvent, BinTableModel btm) {
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showSaveDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try (FileOutputStream fos = new FileOutputStream(selectedFile)) {
                byte[] data = btm.getAllData();
                fos.write(data);
                JOptionPane.showMessageDialog(null, "Файл сохранен: " + selectedFile.getAbsolutePath(), "Сохранено", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Ошибка при сохранении файла: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    // Выход из приложения
    public void exit() {
        System.exit(1);
    }
}