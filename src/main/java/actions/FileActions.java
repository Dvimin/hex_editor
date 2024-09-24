package actions;

import ui.BinTableModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

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
                openAndLoadFile(btm);
            } catch (IOException e) {
                showErrorDialog("Ошибка чтения файла: ", e);
            }
        }
    }

    // Открытие файла и расчет количества страниц
    private void openAndLoadFile(BinTableModel btm) throws IOException {
        raf = new RandomAccessFile(currentFile, "r");
        totalPages = calculateTotalPages();
        loadPage(btm, 0); // Загрузка первой страницы
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

    // Сохранение данных модели таблицы в файл
    public void saveFile(ActionEvent actionEvent, BinTableModel btm) {
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showSaveDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try (FileOutputStream fos = new FileOutputStream(selectedFile)) {
                fos.write(btm.getAllData());
                JOptionPane.showMessageDialog(null, "Файл сохранен: " + selectedFile.getAbsolutePath(), "Сохранено", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                showErrorDialog("Ошибка при сохранении файла: ", e);
            }
        }
    }

    // Общий метод для обработки ошибок
    private void showErrorDialog(String message, Exception e) {
        JOptionPane.showMessageDialog(null, message + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }

    // Закрытие файла и выход из приложения
    public void exit() {
        closeFile();
        System.exit(0);
    }

    // Закрытие текущего файла
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
}