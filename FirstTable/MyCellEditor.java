package FirstTable;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import java.awt.*;

public class MyCellEditor extends AbstractCellEditor implements TableCellEditor {
    private JTextField textField;
    private JTable table;
    private int row, column;

    public MyCellEditor() {
        textField = new JTextField();
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        this.table = table;
        this.row = row;
        this.column = column;
        textField.setText((String) value);
        return textField;
    }

    @Override
    public Object getCellEditorValue() {
        return textField.getText();
    }

    @Override
    public boolean stopCellEditing() {
        String text = textField.getText();
        if (isValid(text)) {
            ((AbstractTableModel) table.getModel()).setValueAt(text, row, column);
            return super.stopCellEditing();
        } else {
            JOptionPane.showMessageDialog(null, "Ошибка: введите двузначное число в формате 16-ричной системы (0-9, A-F).", "Ошибка ввода", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private boolean isHex(String s) {
        return s.matches("[0-9A-F]{2}");
    }

    private boolean isValid(String text) {
        return isHex(text);
    }
}