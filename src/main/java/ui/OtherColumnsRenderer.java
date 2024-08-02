package ui;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

// Класс OtherColumnsRenderer наследует DefaultTableCellRenderer для кастомной отрисовки ячеек таблицы.
public class OtherColumnsRenderer extends DefaultTableCellRenderer {

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

        // Устанавливаем фон ячейки в желтый, если она выбрана, иначе используем фон таблицы
        if (isSelected) {
            cell.setBackground(Color.YELLOW);
        } else {
            cell.setBackground(table.getBackground());
        }

        return cell;
    }
}
