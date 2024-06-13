package FirstTable;
import java.awt.*;
import javax.swing.*;

public class Main {
    public static void main(String[] args){
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



        BinTableModel btm = new BinTableModel();
        JTable binTable = new JTable(btm);
        JScrollPane binTableScroolPage = new JScrollPane(binTable);
        binTableScroolPage.setPreferredSize((new Dimension(400, 400)));

        frame.add(binTableScroolPage, new GridBagConstraints(0, 0, 5, 1, 1, 1,
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



        frame.setVisible(true);
        frame.pack();

    }
}
