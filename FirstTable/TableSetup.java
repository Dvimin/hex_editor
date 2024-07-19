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
                    StringBuilder tooltipUnsigned = new StringBuilder("int без знака:");
                    StringBuilder tooltipSigned = new StringBuilder("int со знаком:");
                    StringBuilder tooltipFloat = new StringBuilder("float:");
                    StringBuilder tooltipDouble = new StringBuilder("double:");

                    for (int row : selectedRows) {
                        for (int column : selectedColumns) {
                            Object cellValue = getValueAt(row, column);
                            if (cellValue != null) {
                                int decimalValue = Integer.parseInt(cellValue.toString(), 16);
                                tooltipUnsigned.append(" ").append(decimalValue).append(";");
                                tooltipSigned.append(" ").append((byte) decimalValue).append(";");
                                float floatValue = Float.intBitsToFloat(decimalValue);
                                tooltipFloat.append(" ").append(floatValue).append(";");
                                double doubleValue = Double.longBitsToDouble(decimalValue);
                                tooltipDouble.append(" ").append(doubleValue).append(";");
                            }
                        }
                    }
                    if (tooltipUnsigned.length() > 0 && tooltipUnsigned.charAt(tooltipUnsigned.length() - 1) == ';') {
                        tooltipUnsigned.deleteCharAt(tooltipUnsigned.length() - 1);
                    }
                    if (tooltipSigned.length() > 0 && tooltipSigned.charAt(tooltipSigned.length() - 1) == ';') {
                        tooltipSigned.deleteCharAt(tooltipSigned.length() - 1);
                    }
                    if (tooltipFloat.length() > 0 && tooltipFloat.charAt(tooltipFloat.length() - 1) == ';') {
                        tooltipFloat.deleteCharAt(tooltipFloat.length() - 1);
                    }
                    if (tooltipDouble.length() > 0 && tooltipDouble.charAt(tooltipDouble.length() - 1) == ';') {
                        tooltipDouble.deleteCharAt(tooltipDouble.length() - 1);
                    }
                    return "<html>" + tooltipUnsigned.toString() + "<br>" + tooltipSigned.toString() + "<br>" + tooltipFloat.toString() + "<br>" + tooltipDouble.toString() + "</html>";
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
