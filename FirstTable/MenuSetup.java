package FirstTable;


import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuSetup {

    public void setupMenu(JFrame frame, BinTableModel btm, TableSetup tableSetup) {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("Файл");
        JMenu helpMenu = new JMenu("Помощь");
        menuBar.add(fileMenu);
        menuBar.add(helpMenu);

        JMenuItem openMenuItem = new JMenuItem("Открыть");
        JMenuItem saveMenuItem = new JMenuItem("Сохранить");
        JMenuItem exitMenuItem = new JMenuItem("Выйти");

        fileMenu.add(openMenuItem);
        fileMenu.add(saveMenuItem);
        fileMenu.add(exitMenuItem);

        JMenuItem helpmenuItem1 = new JMenuItem("Нужна помощь?");
        helpMenu.add(helpmenuItem1);

        frame.setJMenuBar(menuBar);

        FileActions fileActions = new FileActions();

        openMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                fileActions.openFile(btm, actionEvent);
            }
        });

        saveMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                fileActions.saveFile(actionEvent, btm);
            }
        });

        exitMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                fileActions.exit();
            }
        });

        helpmenuItem1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JOptionPane.showMessageDialog(frame, "Увы, помощи пока нет", "Помощь", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        frame.setJMenuBar(menuBar);
    }
}
