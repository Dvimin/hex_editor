package FirstTable;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;

public class MyCellEditor extends AbstractCellEditor implements TableCellEditor {
    private JTextField textField;

    public MyCellEditor() {
        textField = new JTextField();
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        textField.setText((String) value);
        return textField;
    }

    @Override
    public Object getCellEditorValue() {
        String text = textField.getText();
        if (isValid(text)) {
            return text;
        } else {
            JOptionPane.showMessageDialog(null, "Invalid data", "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
    private boolean isHex(String s) {
        try {
            Integer.parseInt(s, 16);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    private boolean isValid(String text) {
        if (isHex(text)){
            return true;
        } else return false;

    }

}
