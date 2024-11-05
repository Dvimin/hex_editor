package ui;

import javax.swing.*;

// Класс MyCellEditor расширяет DefaultCellEditor для работы с ячейками таблицы.
public class MyCellEditor extends DefaultCellEditor {

    // Конструктор для создания редактора ячеек таблицы.
    public MyCellEditor() {
        super(new JTextField());
    }

    // Переопределенный метод stopCellEditing для валидации значения ячейки перед завершением редактирования.
    // @return true, если редактирование ячейки может быть завершено, в противном случае - false.
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
