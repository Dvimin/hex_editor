package FirstTable;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import javax.swing.*;


public class Main {

    public static void main(String[] args) throws Exception {


        JFrame frame = new JFrame("Table");
        frame.setSize(new Dimension(600, 400));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new GridBagLayout());

        JButton addButton = new JButton("Добавить");
        JButton deleteButton = new JButton("Удалить");
        JButton clearButton = new JButton("Очистить");
        JTextField searchText = new JTextField(10);
        JButton searchButton = new JButton("Поиск");
        JButton openfileButton = new JButton("Отыкрыть");
        JButton savefileButton = new JButton("Сохранить");

        BinTableModel btm = new BinTableModel();
        JTable binTable = new JTable(btm);
        binTable.setCellSelectionEnabled(true);
        binTable.getTableHeader().setReorderingAllowed(false);
        binTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        binTable.getTableHeader().setResizingAllowed(false);

        // часть кода убирается в рабочей версии
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
        // ----------------------

        binTable.getColumnModel().getColumn(0).setCellRenderer(new Renderer());
        JScrollPane binTableScroolPage = new JScrollPane(binTable);
        binTableScroolPage.setPreferredSize((new Dimension(400, 400)));

        JButton addColumnButton = new JButton("+");
        JButton deleteColumnButton = new JButton("-");



        openfileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
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
                    } catch (IOException e) {
                        
                        e.printStackTrace();
                    }
                }
            }
        });
        savefileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.showSaveDialog(frame);
            }
        });

        addColumnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                btm.addColumn(hex);
            }
        });

        deleteColumnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                btm.deleteColumn(hex);
            }
        });




        final int COUNTButton = 7;
        frame.add(binTableScroolPage, new GridBagConstraints(0, 0, COUNTButton, 1, 1, 1,
                GridBagConstraints.NORTH, GridBagConstraints.BOTH,
                new Insets(1, 1, 1, 1), 0, 0));

        frame.add(addButton, new GridBagConstraints(0, 1, 1, 1, 1, 1,
                GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL,
                new Insets(1, 1, 1, 1), 0, 0));

        frame.add(deleteButton, new GridBagConstraints(1, 1, 1, 1, 1, 1,
                GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL,
                new Insets(1, 1, 1, 1), 0, 0));

        frame.add(clearButton, new GridBagConstraints(2, 1, 1, 1, 1, 1,
                GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL,
                new Insets(1, 1, 1, 1), 0, 0));

        frame.add(searchText, new GridBagConstraints(3, 1, 1, 1, 1, 1,
                GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL,
                new Insets(1, 1, 1, 1), 0, 0));

        frame.add(searchButton, new GridBagConstraints(4, 1, 1, 1, 1, 1,
                GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL,
                new Insets(1, 1, 1, 1), 0, 0));

        frame.add(openfileButton, new GridBagConstraints(5, 1, 1, 1, 1, 1,
                GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL,
                new Insets(1, 1, 1, 1), 0, 0));

        frame.add(savefileButton, new GridBagConstraints(6, 1, 1, 1, 1, 1,
                GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL,
                new Insets(1, 1, 1, 1), 0, 0));




        frame.setVisible(true);
        frame.pack();

    }
}
