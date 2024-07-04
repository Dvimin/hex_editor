package FirstTable;

import javax.swing.*;


public class MyCellEditor extends DefaultCellEditor {

    public MyCellEditor() {
        super(new JTextField());
    }

    @Override
    public boolean stopCellEditing() {
        String value = (String) super.getCellEditorValue();
        if (value.isEmpty()) {
            return super.stopCellEditing();
        }
        if (!value.matches("[0-9A-F]{2}")) {
            JOptionPane.showMessageDialog(null, "Ошибка: введите двузначное число в формате 16-ричной системы (0-9, A-F).", "Ошибка ввода", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return super.stopCellEditing();
    }
}

//public class MyCellEditor extends AbstractCellEditor implements TableCellEditor {
//    private JTextField textField;
//    private JTable table;
//    private int row, column;
//
//    public MyCellEditor() {
//        textField = new JTextField();
//    }
//
//    @Override
//    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
//        this.table = table;
//        this.row = row;
//        this.column = column;
//        textField.setText((String) value);
//        return textField;
//    }
//
//    @Override
//    public Object getCellEditorValue() {
//        return textField.getText();
//    }
//
//    @Override
//    public boolean stopCellEditing() {
//        String text = textField.getText();
//        if (isValid(text)) {
//            ((AbstractTableModel) table.getModel()).setValueAt(text, row, column);
//            return super.stopCellEditing();
//        } else {
//            JOptionPane.showMessageDialog(null, "Ошибка: введите двузначное число в формате 16-ричной системы (0-9, A-F).", "Ошибка ввода", JOptionPane.ERROR_MESSAGE);
//            return false;
//        }
//    }
//
//    private boolean isHex(String s) {
//        return s.matches("[0-9A-F]{2}");
//    }
//
//    private boolean isValid(String text) {
//        return isHex(text);
//    }
//}
