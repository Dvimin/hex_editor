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

    private static final int PAGE_SIZE = 512; // Размер страницы
    private File currentFile;
    private RandomAccessFile raf;

    // Открытие файла и загрузка первой страницы в модель таблицы
    public void openFile(BinTableModel btm, ActionEvent actionEvent) {
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            currentFile = fileChooser.getSelectedFile();
            try {
                raf = new RandomAccessFile(currentFile, "r");
                loadPage(btm, 0);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Ошибка чтения файла: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    // Прямое открытие файла и загрузка первой страницы
    public void openFileDirectly(BinTableModel btm, File file) throws IOException {
        currentFile = file;
        raf = new RandomAccessFile(currentFile, "r");
        loadPage(btm, 0);
    }

    // Загрузка страницы по номеру
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
    }

    // Метод для загрузки тестового файла в модель таблицы.
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

    private int currentPage = 0;

    public void nextPage(BinTableModel btm) {
        currentPage++;
        try {
            loadPage(btm, currentPage);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Ошибка загрузки страницы: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void previousPage(BinTableModel btm) {
        if (currentPage > 0) {
            currentPage--;
            try {
                loadPage(btm, currentPage);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Ошибка загрузки страницы: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Вы находитесь на первой странице.", "Ошибка", JOptionPane.WARNING_MESSAGE);
        }
    }

    // Выход из приложения
    public void exit() {
        System.exit(1);
    }
}
