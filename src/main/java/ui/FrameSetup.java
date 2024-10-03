package ui;

import actions.FileActions;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class FrameSetup {
    // Настройка основного фрейма приложения.
    public void setupFrame() {
        JFrame frame = new JFrame("HEX-editor");
        frame.setSize(new Dimension(1000, 400));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new GridBagLayout());

        BinTableModel btm = new BinTableModel();
        FileActions fileActions = new FileActions();
        fileActions.createAndOpenInitialFile(btm); // Вызов метода для загрузки тестового файла

        TableSetup tableSetup = new TableSetup();
        tableSetup.setupTable(frame, btm);
        MenuSetup menuSetup = new MenuSetup();
        menuSetup.setupMenu(frame, btm, tableSetup);
        frame.setVisible(true);
        frame.pack();
    }
}