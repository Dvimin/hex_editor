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
        fileActions.createAndOpenInitialFile(btm);

        TableSetup tableSetup = new TableSetup();
        tableSetup.setupTable(frame, btm);
        MenuSetup menuSetup = new MenuSetup();
        menuSetup.setupMenu(frame, btm, tableSetup);

        frame.setSize(new Dimension(1100, 400));
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
    }
}