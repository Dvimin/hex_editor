package FirstTable;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

// Класс RendererNameRow наследует DefaultTableCellRenderer для кастомной отрисовки ячеек таблицы.
public class RendererNameRow extends DefaultTableCellRenderer {

    /**
     * Переопределенный метод getTableCellRendererComponent для настройки отображения ячейки таблицы.
     * @param table таблица, содержащая ячейку
     * @param value значение ячейки
     * @param isSelected флаг, указывающий, выбрана ли ячейка
     * @param hasFocus флаг, указывающий, имеет ли ячейка фокус
     * @param row индекс строки ячейки
     * @param column индекс столбца ячейки
     * @return компонент, отображающий ячейку
     */
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        // Устанавливаем фон ячейки в светло-серый для первого столбца, иначе в белый
        if (column == 0) {
            cell.setBackground(Color.LIGHT_GRAY);
        } else {
            cell.setBackground(Color.WHITE);
        }

        // Устанавливаем рамку ячейки
        if (cell instanceof JComponent) {
            ((JComponent) cell).setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        }

        return cell;
    }
}
