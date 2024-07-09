package FirstTable;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuSetup {

    // Настройка меню для приложения
    public void setupMenu(JFrame frame, BinTableModel btm, TableSetup tableSetup) {
        JMenuBar menuBar = new JMenuBar();

        // Создание меню "Файл"
        JMenu fileMenu = new JMenu("Файл");
        JMenu helpMenu = new JMenu("Помощь");
        menuBar.add(fileMenu);
        menuBar.add(helpMenu);

        // Пункты меню "Файл"
        JMenuItem openMenuItem = new JMenuItem("Открыть");
        JMenuItem saveMenuItem = new JMenuItem("Сохранить");
        JMenuItem exitMenuItem = new JMenuItem("Выйти");

        fileMenu.add(openMenuItem);
        fileMenu.add(saveMenuItem);
        fileMenu.add(exitMenuItem);

        // Пункты меню "Помощь"
        JMenuItem helpmenuItem1 = new JMenuItem("Нужна помощь?");
        helpMenu.add(helpmenuItem1);

        // Установка меню на фрейм
        frame.setJMenuBar(menuBar);

        // Обработчики событий для пунктов меню
        FileActions fileActions = new FileActions();

        // Обработчик для пункта "Открыть"
        openMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                fileActions.openFile(btm, actionEvent);
            }
        });

        // Обработчик для пункта "Сохранить"
        saveMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                fileActions.saveFile(actionEvent, btm);
            }
        });

        // Обработчик для пункта "Выйти"
        exitMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                fileActions.exit();
            }
        });

        // Обработчик для пункта "Нужна помощь?"
        helpmenuItem1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JOptionPane.showMessageDialog(frame, "Увы, помощи пока нет", "Помощь", JOptionPane.INFORMATION_MESSAGE);
            }
        });

    }
}
