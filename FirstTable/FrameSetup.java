package FirstTable;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class FrameSetup {

    public void setupFrame() {
        JFrame frame = new JFrame("HEX-editor");
        frame.setSize(new Dimension(600, 400));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new GridBagLayout());

        BinTableModel btm = new BinTableModel();

        File file = new File("FirstTable/TestFile/test.txt");
        byte[] hex = new byte[16];

        try {
            byte[] data = Files.readAllBytes(file.toPath());
            for (int i = 0; i < data.length; i += 16) {
                System.arraycopy(data, i, hex, 0, Math.min(16, data.length - i));
                btm.addData(hex);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        TableSetup tableSetup = new TableSetup();
        tableSetup.setupTable(frame, btm);


        MenuSetup menuSetup = new MenuSetup();
        menuSetup.setupMenu(frame, btm, tableSetup);
        frame.setVisible(true);
        frame.pack();
    }
}