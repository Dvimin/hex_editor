package FirstTable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class ButtonUtils {
    private static ArrayList<JTextField> searchFields = new ArrayList<>();
    private static JTable binTable;
    private static ByteSearch byteSearch;

    public static void setTable(JTable table) {
        binTable = table;
        byteSearch = new ByteSearch((BinTableModel) binTable.getModel());
    }

    public static JPanel createInputPanel() {
        searchFields.clear();
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JTextField searchField = new JTextField(2);
        searchFields.add(searchField);
        inputPanel.add(searchField);
        return inputPanel;
    }

    public static JPanel createButtonPanel(JPanel inputPanel) {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton addButton = new JButton("+");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JTextField newField = new JTextField(2);
                searchFields.add(newField);
                inputPanel.add(newField);
                inputPanel.revalidate();
                inputPanel.repaint();
            }
        });
        buttonPanel.add(addButton);

        JButton removeButton = new JButton("-");
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (inputPanel.getComponentCount() > 1) {
                    JTextField removedField = searchFields.remove(searchFields.size() - 1);
                    inputPanel.remove(removedField);
                    inputPanel.revalidate();
                    inputPanel.repaint();
                }
            }
        });
        buttonPanel.add(removeButton);

        JButton findButton = new JButton("Найти");
        findButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String[] searchByte = new String[searchFields.size()];
                for (int i = 0; i < searchFields.size(); i++) {
                    String value = searchFields.get(i).getText();
                    searchByte[i] = value;
                }
                int startRow = binTable.getSelectedRow();
                int startColumn = binTable.getSelectedColumn();
                if (startRow == -1 || startColumn == -1) {
                    startRow = 0;
                    startColumn = 1;
                } else {
                    int[] nextCell = getNextCell(startRow, startColumn);
                    startRow = nextCell[0];
                    startColumn = nextCell[1];
                }
                int[] foundCoordinates = byteSearch.searchBytes(startRow, startColumn, searchByte, true);
                int foundRow = foundCoordinates[0];
                int foundColumn = foundCoordinates[1];
                if (foundRow == -1 && foundColumn == -1) {
                    JOptionPane.showMessageDialog(null, "Совпадений не найдено", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }  else {
                    binTable.changeSelection(foundRow, foundColumn, false, false);
                }
            }
        });
        buttonPanel.add(findButton);
        return buttonPanel;
    }

    public static int[] getNextCell(int currentRow, int currentColumn) {
        int rowCount = binTable.getRowCount();
        int columnCount = binTable.getColumnCount();

        if (currentColumn < columnCount - 1) {
            currentColumn++;
        } else {
            currentColumn = 1;
            currentRow++;
            if (currentRow >= rowCount) {
                currentRow = 0;
            }
        }

        return new int[]{currentRow, currentColumn};
    }
}
