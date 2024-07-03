package FirstTable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

public class ButtonSetup {

    private static String[][] copiedBlock;

    public static void setupButtons(JFrame frame, JTable binTable, BinTableModel btm) {
        JButton editButton = new JButton("Изменить");
        JButton resetButton = new JButton("Сбросить");
        JButton deleteButton = new JButton("Удалить");
        JButton pasteWithShiftLeftButton = new JButton("Вставить слева со сдвигом");
        JButton pasteWithShiftRightButton = new JButton("Вставить справа со сдвигом");
        JButton copyBlockButton = new JButton("Копировать");
        JButton cutBlockWithShiftButton = new JButton("Вырезать со сдвигом");
        JButton cutBlockWithResetButton = new JButton("Вырезать со сбросом");
        JButton pasteWithoutShiftButton = new JButton("Вставить без сдвига");
        JButton clearButton = new JButton("Очистить данные");
        JButton insertCellRightButton = new JButton("Вставить ячейку справа");
        JButton insertCellLeftButton = new JButton("Вставить ячейку слева");
        binTable.changeSelection(0, 1, false, false);

        insertCellRightButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = binTable.getSelectedRow();
                int selectedColumn = binTable.getSelectedColumn();
                if (selectedRow != -1 && selectedColumn != -1) {
                    ((BinTableModel) binTable.getModel()).insertCellRightAndShift(selectedRow, selectedColumn);
                    binTable.repaint();
                    binTable.changeSelection(selectedRow, selectedColumn, false, false);
                } else {
                    JOptionPane.showMessageDialog(frame, "Пожалуйста, выберите ячейку.", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        insertCellLeftButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = binTable.getSelectedRow();
                int selectedColumn = binTable.getSelectedColumn();
                BinTableModel model = (BinTableModel) binTable.getModel();
                if (selectedRow != -1 && selectedColumn != -1) {
                    ((BinTableModel) binTable.getModel()).insertCellLeftAndShift(selectedRow, selectedColumn);
                    binTable.repaint();
                    int[] nextCell = model.getNextCell(selectedRow, selectedColumn);
                    selectedRow = nextCell[0];
                    selectedColumn = nextCell[1];
                    binTable.changeSelection(selectedRow, selectedColumn, false, false);
                } else {
                    JOptionPane.showMessageDialog(frame, "Пожалуйста, выберите ячейку.", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = binTable.getSelectedRow();
                int column = binTable.getSelectedColumn();
                MyCellEditor editor = new MyCellEditor();
                if (row != -1 && column != -1) {
                    binTable.getColumnModel().getColumn(column).setCellEditor(editor);
                    binTable.editCellAt(row, column, e);
                } else {
                    JOptionPane.showMessageDialog(frame, "Пожалуйста, выберите ячейку для редактирования.", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int[] selectedRows = binTable.getSelectedRows();
                int[] selectedColumns = binTable.getSelectedColumns();
                BinTableModel model = (BinTableModel) binTable.getModel();
                if (selectedRows.length > 0 && selectedColumns.length > 0) {
                    for (int i = 0; i < selectedRows.length; i++) {
                        for (int j = 0; j < selectedColumns.length; j++) {
                            model.setValueAt("00", selectedRows[i], selectedColumns[j]);
                        }
                    }
                    binTable.repaint();
                } else {
                    JOptionPane.showMessageDialog(frame, "Пожалуйста, выберите хотя бы одну ячейку для обнуления.", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int[] selectedRows = binTable.getSelectedRows();
                int[] selectedColumns = binTable.getSelectedColumns();
                BinTableModel model = (BinTableModel) binTable.getModel();
                if (selectedRows.length > 0 && selectedColumns.length > 0) {
                    Arrays.sort(selectedRows);
                    Arrays.sort(selectedColumns);
                    for (int i = selectedRows.length - 1; i >= 0; i--) {
                        for (int j = selectedColumns.length - 1; j >= 0; j--) {
                                model.deleteCellAndShift(selectedRows[i], selectedColumns[j]);
                        }
                    }
                    binTable.repaint();
                    binTable.changeSelection(selectedRows[0], selectedColumns[0], false, false);
                } else {
                    JOptionPane.showMessageDialog(frame, "Пожалуйста, выберите хотя бы одну ячейку для удаления.", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            }
        });


        copyBlockButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int[] selectedRows = binTable.getSelectedRows();
                int[] selectedColumns = binTable.getSelectedColumns();
                if (selectedRows.length == 0 || selectedColumns.length == 0) {
                    JOptionPane.showMessageDialog(null, "Пожалуйста, выберите хотя бы одну ячейку для копирования.", "Ошибка", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                copiedBlock = new String[selectedRows.length][selectedColumns.length];

                for (int i = 0; i < selectedRows.length; i++) {
                    for (int j = 0; j < selectedColumns.length; j++) {
                        copiedBlock[i][j] = (String) btm.getValueAt(selectedRows[i], selectedColumns[j]);
                    }
                }
            }
        });

        cutBlockWithShiftButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int[] selectedRows = binTable.getSelectedRows();
                int[] selectedColumns = binTable.getSelectedColumns();
                BinTableModel model = (BinTableModel) binTable.getModel();
                copiedBlock = new String[selectedRows.length][selectedColumns.length];
                if (selectedRows.length > 0 && selectedColumns.length > 0) {
                    Arrays.sort(selectedRows);
                    Arrays.sort(selectedColumns);
                    for (int i = 0; i < selectedRows.length; i++) {
                        for (int j = 0; j < selectedColumns.length; j++) {
                            copiedBlock[i][j] = (String) btm.getValueAt(selectedRows[i], selectedColumns[j]);
                        }
                    }
                    for (int i = selectedRows.length - 1; i >= 0; i--) {
                        for (int j = selectedColumns.length - 1; j >= 0; j--) {
                            model.deleteCellAndShift(selectedRows[i], selectedColumns[j]);
                        }
                    }
                    binTable.repaint();
                    binTable.changeSelection(selectedRows[0], selectedColumns[0], false, false);
                } else {
                    JOptionPane.showMessageDialog(frame, "Пожалуйста, выберите хотя бы одну ячейку.", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }

            }
        });

        cutBlockWithResetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int[] selectedRows = binTable.getSelectedRows();
                int[] selectedColumns = binTable.getSelectedColumns();
                BinTableModel model = (BinTableModel) binTable.getModel();
                copiedBlock = new String[selectedRows.length][selectedColumns.length];
                if (selectedRows.length > 0 && selectedColumns.length > 0) {
                    Arrays.sort(selectedRows);
                    Arrays.sort(selectedColumns);
                    for (int i = 0; i < selectedRows.length; i++) {
                        for (int j = 0; j < selectedColumns.length; j++) {
                            copiedBlock[i][j] = (String) btm.getValueAt(selectedRows[i], selectedColumns[j]);
                            model.setValueAt("00", selectedRows[i], selectedColumns[j]);
                        }
                    }
                    binTable.repaint();
                    binTable.changeSelection(selectedRows[0], selectedColumns[0], false, false);
                } else {
                    JOptionPane.showMessageDialog(frame, "Пожалуйста, выберите хотя бы одну ячейку.", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        pasteWithoutShiftButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (copiedBlock != null) {
                    int[] selectedRows = binTable.getSelectedRows();
                    int[] selectedColumns = binTable.getSelectedColumns();

                    if (selectedRows.length == copiedBlock.length &&
                            selectedColumns.length == copiedBlock[0].length) {
                        for (int i = 0; i < selectedRows.length; i++) {
                            for (int j = 0; j < selectedColumns.length; j++) {
                                btm.setValueAt(copiedBlock[i][j], selectedRows[i], selectedColumns[j]);
                            }
                        }
                        binTable.repaint();
                    } else {
                        JOptionPane.showMessageDialog(frame,
                                "Размер скопированного блока и выделенного участка не совпадают",
                                "Ошибка", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Сначала скопируйте блок",
                            "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        pasteWithShiftRightButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (copiedBlock != null) {
                    int selectedRow = binTable.getSelectedRow();
                    int selectedColumn = binTable.getSelectedColumn();

                    if (selectedRow != -1 && selectedColumn != -1) {
                        BinTableModel model = (BinTableModel) binTable.getModel();

                        for (int i = 0; i < copiedBlock.length; i++) {
                            for (int j = 0; j < copiedBlock[i].length; j++) {
                                model.insertCellRightAndShift(selectedRow, selectedColumn);
                                int[] nextCell = model.getNextCell(selectedRow, selectedColumn);
                                selectedRow = nextCell[0];
                                selectedColumn = nextCell[1];
                                model.setValueAt(copiedBlock[i][j], selectedRow, selectedColumn);
                            }
                        }
                        binTable.repaint();
                        binTable.changeSelection(selectedRow, selectedColumn, false, false);
                    } else {
                        JOptionPane.showMessageDialog(frame, "Пожалуйста, выберите ячейку.", "Ошибка", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Сначала скопируйте блок", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        pasteWithShiftLeftButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (copiedBlock != null) {
                    int selectedRow = binTable.getSelectedRow();
                    int selectedColumn = binTable.getSelectedColumn();

                    if (selectedRow != -1 && selectedColumn != -1) {
                        BinTableModel model = (BinTableModel) binTable.getModel();

                        for (int i = copiedBlock.length - 1; i >= 0; i--) {
                            for (int j = copiedBlock[i].length - 1; j >= 0; j--) {
                                model.insertCellLeftAndShift(selectedRow, selectedColumn);
                                model.setValueAt(copiedBlock[i][j], selectedRow, selectedColumn);
                            }
                        }
                        binTable.repaint();
                        binTable.changeSelection(selectedRow, selectedColumn, false, false);
                    } else {
                        JOptionPane.showMessageDialog(frame, "Пожалуйста, выберите ячейку.", "Ошибка", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Сначала скопируйте блок", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int rowCount = btm.getRowCount();
                int columnCount = btm.getColumnCount();

                for (int i = 0; i < rowCount; i++) {
                    for (int j = 1; j < columnCount; j++) {
                        btm.setValueAt("", i, j);
                    }
                }
            }
        });

        JPanel buttonPanel = new JPanel(new GridBagLayout());
        Component[] components = {editButton, resetButton, deleteButton, copyBlockButton, cutBlockWithShiftButton, cutBlockWithResetButton, pasteWithoutShiftButton, pasteWithShiftLeftButton, pasteWithShiftRightButton, clearButton, insertCellLeftButton, insertCellRightButton};
        for (int i = 0; i < components.length; i++) {
            buttonPanel.add(components[i], new GridBagConstraints(i, 0, 1, 1, 1, 1,
                    GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL,
                    new Insets(1, 1, 1, 1), 0, 0));
        }
        frame.add(buttonPanel, new GridBagConstraints(0, 1, 5, 1, 1, 0,
                GridBagConstraints.SOUTH, GridBagConstraints.HORIZONTAL,
                new Insets(1, 1, 1, 1), 0, 0));
    }
}