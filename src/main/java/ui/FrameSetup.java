package ui;

import actions.FileActions;

import javax.swing.*;
import java.awt.*;

public class FrameSetup {
    // Настройка основного фрейма приложения.
    public void setupFrame() {
        JFrame frame = new JFrame("HEX-editor");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridBagLayout());

        BinTableModel btm = new BinTableModel();
        FileActions fileActions = new FileActions();
        fileActions.openInitialFile(btm);

        TableSetup tableSetup = new TableSetup();
        tableSetup.setupTable(frame, btm);

        InfoDialog.showInfoDialog();

        frame.setSize(new Dimension(1100, 350));
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
    }
}