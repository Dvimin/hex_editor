package FirstTable;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class FileActions {

    public void openFile(BinTableModel btm, ActionEvent actionEvent) {
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                byte[] data = Files.readAllBytes(selectedFile.toPath());
                for (int i = 0; i < data.length; i += btm.getColumnCount()) {
                    byte[] hex = new byte[btm.getColumnCount()];
                    System.arraycopy(data, i, hex, 0, Math.min(btm.getColumnCount(), data.length - i));
                    btm.addData(hex);
                }
                System.out.println("The file is open!");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void saveFile(ActionEvent actionEvent) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.showSaveDialog(null);
    }

    public void exit() {
        System.exit(1);
    }
}