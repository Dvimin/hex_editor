package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class CellInsertionDialog {

    private static JTextField[] textFields;
    private static JDialog dialog;

    /**
     * Отображает диалоговое окно для вставки ячеек в таблицу.
     *
     * @param frame          родительское окно
     * @param binTable       таблица, в которую вставляются ячейки
     * @param selectedRow    выбранная строка в таблице
     * @param selectedColumn выбранный столбец в таблице
     * @param numberOfCells  количество ячеек для вставки
     * @param title          заголовок диалогового окна
     * @param insertAction   действие вставки, обработчик ActionListener
     */
    public static void showDialog(JFrame frame, JTable binTable, int selectedRow, int selectedColumn, int numberOfCells, String title, ActionListener insertAction) {
        dialog = new JDialog(frame, title, true);
        dialog.setLayout(new FlowLayout());
        dialog.setLocationRelativeTo(null);

        textFields = new JTextField[numberOfCells];
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
        insertButton.addActionListener(insertAction);

        dialog.add(insertButton);
        dialog.setSize(300, 100);
        dialog.setVisible(true);
    }

    public static JTextField[] getTextFields() {
        return textFields;
    }

    public static JDialog getDialog() {
        return dialog;
    }
}
