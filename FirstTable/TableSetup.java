package FirstTable;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.MouseEvent;

// Класс TableSetup предназначен для настройки таблицы JTable.
public class TableSetup {

    /**
     * Метод setupTable настраивает таблицу с заданным фреймом и моделью данных.
     * @param frame фрейм, в который встраивается таблица
     * @param btm модель данных для таблицы
     */
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
                    if (selectedColumns.length != 1 && selectedColumns.length != 2 && selectedColumns.length != 4 && selectedColumns.length != 8) {
                        return "Error";
                    }
                    StringBuilder html = new StringBuilder("<html><table>");
                    for (int row : selectedRows) {
                        html.append("<tr>");

                        html.append("<td style=\"text-align: right;\">")
                                .append("<b>").append("int без знака:</b>").append("<br>")
                                .append("<b>").append("int со знаком:</b>").append("<br>")
                                .append("<b>").append("float:</b>").append("<br>")
                                .append("<b>").append("double:</b>").append("<br>")
                                .append("</td>");

                        for (int column : selectedColumns) {
                            Object cellValue = getValueAt(row, column);
                            if (cellValue != null) {
                                int decimalValue = Integer.parseInt(cellValue.toString(), 16);
                                html.append("<td style=\"text-align: right;\">")
                                        .append(decimalValue).append("<br>")
                                        .append((byte) decimalValue).append("<br>")
                                        .append(Float.intBitsToFloat(decimalValue)).append("<br>")
                                        .append(Double.longBitsToDouble(decimalValue)).append("<br>")
                                        .append("</td>");
                            }
                        }
                        html.append("</tr>");
                    }

                    html.append("</table></html>");
                    return html.toString();
                }
                return super.getToolTipText(event);
            }
        };

        // Установка пустой рамки для выделения фокусной ячейки
        Border border = BorderFactory.createLineBorder(new Color(0, 0, 0, 0), 0);
        UIManager.put("Table.focusCellHighlightBorder", border);

        // Настройка параметров таблицы
        binTable.getTableHeader().setReorderingAllowed(false);
        binTable.getTableHeader().setResizingAllowed(false);
        binTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        binTable.setCellSelectionEnabled(true);
        binTable.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);

        // Настройка кнопок
        ButtonSetup buttonSetup = new ButtonSetup();
        JPanel buttonPanel = buttonSetup.setupButtons(frame, binTable, btm);

        // Добавление слушателя выделения ячеек
        binTable.getSelectionModel().addListSelectionListener(new CustomSelectionListener(binTable));
        binTable.setGridColor(Color.WHITE);

        // Установка рендерера для первого столбца и остальных столбцов таблицы
        binTable.getColumnModel().getColumn(0).setCellRenderer(new RendererNameRow());
        for (int i = 1; i < binTable.getColumnCount(); i++) {
            binTable.getColumnModel().getColumn(i).setCellRenderer(new OtherColumnsRenderer());
        }

        // Установка редактора ячеек для остальных столбцов
        MyCellEditor editor = new MyCellEditor();
        for (int i = 1; i < binTable.getColumnCount(); i++) {
            binTable.getColumnModel().getColumn(i).setCellEditor(editor);
        }

        // Настройка заголовка таблицы
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

        // Создание панели с прокруткой для таблицы
        JScrollPane binTableScrollPane = new JScrollPane(binTable);
        binTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        // Настройка предпочтительной ширины столбцов
        TableColumn column;
        column = binTable.getColumnModel().getColumn(0);
        column.setPreferredWidth(80);
        for (int i = 1; i < binTable.getColumnCount(); i++) {
            column = binTable.getColumnModel().getColumn(i);
            if (i % 4 == 0) {
                column.setPreferredWidth(50);
            } else {
                column.setPreferredWidth(30);
            }
        }

        // Создание левой и правой панели для разделения с кнопками
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(binTableScrollPane, BorderLayout.CENTER);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(buttonPanel, BorderLayout.NORTH);

        // Создание разделителя и установка начальной позиции
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setDividerLocation(500);

        // Добавление разделителя в контент панель фрейма
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(splitPane, BorderLayout.CENTER);
    }
}
