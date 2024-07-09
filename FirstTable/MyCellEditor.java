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

