package actions;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import ui.BinTableModel;

public class FileActions {

    // Открытие файла и загрузка данных в модель таблицы
    public void openFile(BinTableModel btm, ActionEvent actionEvent) {
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                byte[] data = Files.readAllBytes(selectedFile.toPath());
                btm.clearData();
                int blockSize = 16;
                for (int i = 0; i < data.length; i += blockSize) {
                    byte[] hex = new byte[blockSize];
                    System.arraycopy(data, i, hex, 0, Math.min(blockSize, data.length - i));
                    btm.addData(hex);
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Ошибка чтения файла: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
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

    // Выход из приложения
    public void exit() {
        System.exit(1);
    }
}
