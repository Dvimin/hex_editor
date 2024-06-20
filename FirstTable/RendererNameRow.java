package FirstTable;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class RendererNameRow extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (column == 0)cell.setBackground(Color.LIGHT_GRAY);
        else cell.setBackground(Color.WHITE);
//        if (isSelected &&  column != 0)cell.setBackground(Color.YELLOW);

        if (cell instanceof JComponent) {
            ((JComponent) cell).setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        }
        return cell;
    }
}
