package FirstTable;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;


public class Main {

    public static void main(String[] args) throws Exception {


        JFrame frame = new JFrame("HEX-editor");
        frame.setSize(new Dimension(600, 400));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new GridBagLayout());

        JButton addButton = new JButton("Добавить");
        JButton deleteButton = new JButton("Удалить");
        JButton clearButton = new JButton("Очистить");
        JTextField searchText = new JTextField(10);
        JButton searchButton = new JButton("Поиск");
        BinTableModel btm = new BinTableModel();

//       ------------------------------------------------------------ подсказки при выделении области ячеек ----------------------------
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
//            binTable.getColumnModel().getColumn(i).setCellEditor(new MyCellEditor());
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

        // ------------------------ редактирование таблицы




        // ---------------------------------------------------- часть кода убирается в рабочей версии---------------------------------
        File file = new File("FirstTable/TestFile/test.txt");
        byte[] hex = new byte[16];

        try {
            byte[] data = Files.readAllBytes(file.toPath());
            for (int i = 0; i < data.length; i += 16) {
                System.arraycopy(data, i, hex, 0, Math.min(16, data.length - i));
                btm.addData(hex);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // ---------------------------------------------------------------------------------------------------------------

        binTable.getColumnModel().getColumn(0).setCellRenderer(new RendererNameRow());
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


        // ---------------------------------------- Созддание меню ---------------------
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("Файл");
        JMenu editMenu= new JMenu("Правка");
        JMenu helpMenu = new JMenu("Помощь");
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(helpMenu);

        JMenuItem openMenuItem = new JMenuItem("Открыть");
        JMenuItem saveMenuItem = new JMenuItem("Сохранить");
        JMenuItem exitMenuItem = new JMenuItem("Выйти");

        fileMenu.add(openMenuItem);
        fileMenu.add(saveMenuItem);
        fileMenu.add(exitMenuItem);

        openMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JFileChooser fileChooser = new JFileChooser();
                int returnValue = fileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    try {
                        byte[] data = Files.readAllBytes(selectedFile.toPath());
                        for (int i = 0; i < data.length; i += btm.getColumnCount()) {
                            byte[] hex = new byte[btm.getColumnCount()];
                            System.arraycopy(data, i, hex, 0, Math.min(btm.getColumnCount(), data.length - i));
                            btm.addData(hex);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        saveMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.showSaveDialog(frame);
            }
        });

        exitMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(1);
            }
        });


        JMenuItem helpmenuItem1 = new JMenuItem("Нужна помощь?");
        helpMenu.add(helpmenuItem1);

        helpMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            }
        });

        frame.setJMenuBar(menuBar);




//------------------------------------------------- Отображение кнопок на панель и размещение элементов на фрэйме -------------------------

//        JButton addColumnButton = new JButton("+");
//        JButton deleteColumnButton = new JButton("-");
//
//        addColumnButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent actionEvent) {
//                btm.addColumn(hex);
//            }
//        });
//
//        deleteColumnButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent actionEvent) {
//                btm.deleteColumn(hex);
//            }
//        });
        final int COUNTButton = 5;

//        JPanel columnButtonPanel = new JPanel(new GridBagLayout());
//        Component[] columnComponents = {addColumnButton, deleteColumnButton};
//        for (int i = 0; i < columnComponents.length; i++) {
//            columnButtonPanel.add(columnComponents[i], new GridBagConstraints(0, i, 1, 1, 1, 1,
//                    GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL,
//                    new Insets(1, 1, 1, 1), 0, 0));
//        }

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(binTableScroolPage, BorderLayout.CENTER);
//        panel.add(columnButtonPanel, BorderLayout.EAST);

        frame.add(panel, new GridBagConstraints(0, 0, COUNTButton, 1, 1, 1,
                GridBagConstraints.NORTH, GridBagConstraints.BOTH,
                new Insets(1, 1, 1, 1), 0, 0));


        JPanel buttonPanel = new JPanel(new GridBagLayout());
        Component[] components = {addButton, deleteButton, clearButton, searchText, searchButton};
        for (int i = 0; i < components.length; i++) {
            buttonPanel.add(components[i], new GridBagConstraints(i, 0, 1, 1, 1, 1,
                    GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL,
                    new Insets(1, 1, 1, 1), 0, 0));
        }
        frame.add(buttonPanel, new GridBagConstraints(0, 1, COUNTButton, 1, 1, 0,
                GridBagConstraints.SOUTH, GridBagConstraints.HORIZONTAL,
                new Insets(1, 1, 1, 1), 0, 0));


        frame.setVisible(true);
        frame.pack();

    }
}
