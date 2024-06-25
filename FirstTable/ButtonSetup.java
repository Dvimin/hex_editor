package FirstTable;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

public class ButtonSetup {

    public static void setupButtons(JFrame frame, JTable binTable, BinTableModel btm) {
        JButton deleteButton = new JButton("Удалить байт");
        JButton copyBlockButton = new JButton("Копировать блок");
        JButton pasteWithShiftButton = new JButton("Вставить со сдвигом");
        JButton pasteWithoutShiftButton = new JButton("Вставить без сдвига");
        JButton clearButton = new JButton("Очистить данные");

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int[] selectedRows = binTable.getSelectedRows();
                int[] selectedColumns = binTable.getSelectedColumns();
                for (int row : selectedRows) {
                    for (int column : selectedColumns) {
                        btm.setValueAt("00", row, column);
                    }
                }
                System.out.println("Selected rows: " + Arrays.toString(selectedRows));
                System.out.println("Selected columns: " + Arrays.toString(selectedColumns));
                binTable.repaint();
            }
        });

        copyBlockButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int[] selectedRows = binTable.getSelectedRows();
                int[] selectedColumns = binTable.getSelectedColumns();
                byte[][] copiedBlock = new byte[selectedRows.length][selectedColumns.length];

                for (int i = 0; i < selectedRows.length; i++) {
                    for (int j = 0; j < selectedColumns.length; j++) {
                        copiedBlock[i][j] = (byte) btm.getValueAt(selectedRows[i], selectedColumns[j]);
                    }
                }
            }
        });

        pasteWithShiftButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            }
        });

        pasteWithoutShiftButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            }
        });

        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int rowCount = btm.getRowCount();
                int columnCount = btm.getColumnCount();

                for (int i = 0; i < rowCount; i++) {
                    for (int j = 1; j < columnCount; j++) {
                        btm.setValueAt("", i, j);
                    }
                }
            }
        });

        JPanel buttonPanel = new JPanel(new GridBagLayout());
        Component[] components = {deleteButton, copyBlockButton, pasteWithShiftButton, pasteWithoutShiftButton, clearButton};
        for (int i = 0; i < components.length; i++) {
            buttonPanel.add(components[i], new GridBagConstraints(i, 0, 1, 1, 1, 1,
                    GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL,
                    new Insets(1, 1, 1, 1), 0, 0));
        }
        frame.add(buttonPanel, new GridBagConstraints(0, 1, 5, 1, 1, 0,
                GridBagConstraints.SOUTH, GridBagConstraints.HORIZONTAL,
                new Insets(1, 1, 1, 1), 0, 0));
    }
}