package FirstTable;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class Renderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        cell.setBackground(Color.LIGHT_GRAY);

        if (cell instanceof JComponent) {
            ((JComponent) cell).setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        }

        return cell;
    }


}
