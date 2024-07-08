package FirstTable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class insertCellButtonUtils {

    public static void addInsertCellRightListener(JButton button, JTable binTable, JComboBox<Integer> comboBox, JFrame frame) {
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleInsertCell(binTable, comboBox, frame, true);
            }
        });
    }

    public static void addInsertCellLeftListener(JButton button, JTable binTable, JComboBox<Integer> comboBox, JFrame frame) {
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleInsertCell(binTable, comboBox, frame, false);
            }
        });
    }

    private static void handleInsertCell(JTable binTable, JComboBox<Integer> comboBox, JFrame frame, boolean isRight) {
        int selectedRow = binTable.getSelectedRow();
        int selectedColumn = binTable.getSelectedColumn();
        if (selectedRow != -1 && selectedColumn != -1) {
            int numberOfCells = (int) comboBox.getSelectedItem();
            JDialog dialog = new JDialog(frame, "Введите значения", true);
            dialog.setLayout(new FlowLayout());
            dialog.setLocationRelativeTo(null);
            JTextField[] textFields = new JTextField[numberOfCells];
            for (int i = 0; i < numberOfCells; i++) {
                textFields[i] = new JTextField(2);
                dialog.add(textFields[i]);
                textFields[i].setInputVerifier(new InputVerifier() {
                    @Override
                    public boolean verify(JComponent input) {
                        JTextField textField = (JTextField) input;
                        String value = textField.getText();
                        if (value.isEmpty()) {
                            return true;
                        }
                        if (!value.matches("[0-9A-F]{2}")) {
                            JOptionPane.showMessageDialog(null, "Ошибка: введите двузначное число в формате 16-ричной системы (0-9, A-F).", "Ошибка ввода", JOptionPane.ERROR_MESSAGE);
                            return false;
                        }
                        return true;
                    }
                });
            }
            JButton insertButton = new JButton("Вставить");
            insertButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    BinTableModel model = (BinTableModel) binTable.getModel();
                    int nextRow = selectedRow;
                    int nextColumn = selectedColumn;
                    for (int i = 0; i < numberOfCells; i++) {
                        String value = textFields[i].getText();
                        if (isRight) {
                            model.insertCellRightAndShift(nextRow, nextColumn);
                        } else {
                            model.insertCellLeftAndShift(nextRow, nextColumn);
                        }
                        int[] nextCell = model.getNextCell(nextRow, nextColumn);
                        nextRow = nextCell[0];
                        nextColumn = nextCell[1];
                        model.setValueAt(value, nextRow, nextColumn);
                    }

                    binTable.repaint();
                    binTable.changeSelection(selectedRow, selectedColumn + (isRight ? numberOfCells - 1 : -numberOfCells + 1), false, false);
                    dialog.dispose();
                }
            });
            dialog.add(insertButton);
            dialog.setSize(300, 100);
            dialog.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(frame, "Пожалуйста, выберите ячейку.", "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }
}
