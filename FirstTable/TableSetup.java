package FirstTable;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.MouseEvent;

public class TableSetup {

    public void setupTable(JFrame frame, BinTableModel btm) {
        JTable binTable = new JTable(btm) {
            @Override
            public String getToolTipText(MouseEvent event) {
                java.awt.Point p = event.getPoint();
                int rowIndex = rowAtPoint(p);
                int colIndex = columnAtPoint(p);

                if (rowIndex > -1 && colIndex > -1 && isCellSelected(rowIndex, colIndex)) {
                    int[] selectedRows = getSelectedRows();
                    int[] selectedColumns = getSelectedColumns();
                    if (selectedColumns.length != 1 && selectedColumns.length != 2 && selectedColumns.length != 4 && selectedColumns.length != 8){
                        return "Error";
                    }
                    StringBuilder tooltipUnsigned = new StringBuilder("int без знака:");
                    StringBuilder tooltipSigned =   new StringBuilder("int со знаком:");

                    for (int row : selectedRows) {
                        for (int column : selectedColumns) {
                            Object cellValue = getValueAt(row, column);
                            if (cellValue != null) {
                                int decimalValue = Integer.parseInt(cellValue.toString(), 16);
                                tooltipUnsigned.append(" ").append(decimalValue).append(";");
                                tooltipSigned.append(" ").append((byte) decimalValue).append(";");
                            }
                        }
                    }
                    if (tooltipUnsigned.length() > 0 && tooltipUnsigned.charAt(tooltipUnsigned.length() - 1) == ';') {
                        tooltipUnsigned.deleteCharAt(tooltipUnsigned.length() - 1);
                    }
                    if (tooltipSigned.length() > 0 && tooltipSigned.charAt(tooltipSigned.length() - 1) == ';') {
                        tooltipSigned.deleteCharAt(tooltipSigned.length() - 1);
                    }
                    return "<html>" + tooltipUnsigned.toString() + "<br>" + tooltipSigned.toString() + "</html>";
                }
                return super.getToolTipText(event);
            }
        };

        Border border = BorderFactory.createLineBorder(new Color(0,0,0,0), 0);
        UIManager.put("Table.focusCellHighlightBorder", border);

        binTable.getTableHeader().setReorderingAllowed(false);
        binTable.getTableHeader().setResizingAllowed(false);
        binTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        binTable.setCellSelectionEnabled(true);
        binTable.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        binTable.getSelectionModel().addListSelectionListener(new CustomSelectionListener(binTable));
        binTable.setGridColor(Color.WHITE);
        binTable.getColumnModel().getColumn(0).setCellRenderer(new RendererNameRow());
        for (int i = 1; i <binTable.getColumnCount(); i++){
            binTable.getColumnModel().getColumn(i).setCellRenderer(new OtherColumnsRenderer());
        }

        MyCellEditor editor = new MyCellEditor();
        for (int i = 1; i < binTable.getColumnCount(); i++) {
            binTable.getColumnModel().getColumn(i).setCellEditor(editor);
        }

        JTableHeader header = binTable.getTableHeader();
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                cell.setBackground(Color.LIGHT_GRAY);

                if (cell instanceof JComponent) {
                    ((JComponent) cell).setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.WHITE));
                }
                return cell;
            }
        });

        JScrollPane binTableScroolPage = new JScrollPane(binTable);
        binTable.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );

        TableColumn column = null;
        column = binTable.getColumnModel().getColumn(0);
        column.setPreferredWidth(80);
        for (int i = 1; i < binTable.getColumnCount(); i ++) {
            column = binTable.getColumnModel().getColumn(i);
            if ((i)%4 == 0) {
                column.setPreferredWidth(50);
            } else {
                column.setPreferredWidth(30);
            }
        }

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(binTableScroolPage, BorderLayout.CENTER);
        frame.add(panel, new GridBagConstraints(0, 0, 5, 1, 1, 1,
                GridBagConstraints.NORTH, GridBagConstraints.BOTH,
                new Insets(1, 1, 1, 1), 0, 0));
    }
}