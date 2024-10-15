package actions;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import ui.BinTableModel;
import ui.CellInsertionDialog;
import ui.MyCellEditor;
import utils.ButtonUtils;

public class ButtonSetup {

    private static String[][] copiedBlock;

    // Настройка панели с кнопками для управления таблицей
    public static JPanel setupButtons(JFrame frame, JTable binTable, BinTableModel btm, FileActions fileActions) {
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
        JButton byteSearchButton = new JButton("Найти");
        JButton emptyButton = new JButton();
        JButton previousPageButton = new JButton("Предыдущая страница (←)");
        JButton nextPageButton = new JButton("(→) Следующая страница");
        JButton openFileButton = new JButton("Открыть файл");
        JButton saveFileButton = new JButton("Сохранить файл");
        JLabel navigationLabel = new JLabel("Page: " + fileActions.getCurrentPage() + " of " + fileActions.getTotalPages());

        binTable.changeSelection(0, 1, false, false);

        // Настройка бокса для выбора количества ячеек при вставке справа
        JComboBox<Integer> insertCellRightComboBox = new JComboBox<>(new Integer[]{1, 2, 4, 8});

        // Обработчик для вставки ячейки справа
        insertCellRightButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = binTable.getSelectedRow();
                int selectedColumn = binTable.getSelectedColumn();
                if (selectedRow != -1 && selectedColumn != -1) {
                    int numberOfCells = (int) insertCellRightComboBox.getSelectedItem();
                    CellInsertionDialog.showDialog(frame, binTable, selectedRow, selectedColumn, numberOfCells, "Введите значения", new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            BinTableModel model = (BinTableModel) binTable.getModel();
                            int nextRow = selectedRow;
                            int nextColumn = selectedColumn;
                            JTextField[] textFields = CellInsertionDialog.getTextFields(); // Получаем ссылку на массив текстовых полей
                            JDialog dialog = CellInsertionDialog.getDialog(); // Получаем ссылку на диалоговое окно
                            for (int i = 0; i < numberOfCells; i++) {
                                String value = textFields[i].getText();
                                ((BinTableModel) binTable.getModel()).insertCellRightAndShift(nextRow, nextColumn);
                                int[] nextCell = model.getNextCell(nextRow, nextColumn);
                                nextRow = nextCell[0];
                                nextColumn = nextCell[1];
                                ((BinTableModel) binTable.getModel()).setValueAt(value, nextRow, nextColumn);
                            }
                            binTable.repaint();
                            fileActions.autoSaveFileAtPage(btm);
                            fileActions.autoOpenFileAtPage(btm);
                            binTable.changeSelection(selectedRow, selectedColumn + numberOfCells - 1, false, false);
                            dialog.dispose();
                            updateNavigationLabel(fileActions, navigationLabel);
                        }
                    });
                } else {
                    JOptionPane.showMessageDialog(frame, "Пожалуйста, выберите ячейку.", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Настройка комбобокса для выбора количества ячеек при вставке слева
        JComboBox<Integer> insertCellLeftComboBox = new JComboBox<>(new Integer[]{1, 2, 4, 8});

        // Обработчик для вставки ячейки слева
        insertCellLeftButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = binTable.getSelectedRow();
                int selectedColumn = binTable.getSelectedColumn();
                if (selectedRow != -1 && selectedColumn != -1) {
                    int numberOfCells = (int) insertCellLeftComboBox.getSelectedItem();
                    CellInsertionDialog.showDialog(frame, binTable, selectedRow, selectedColumn, numberOfCells, "Введите значения", new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            BinTableModel model = (BinTableModel) binTable.getModel();
                            int nextRow = selectedRow;
                            int nextColumn = selectedColumn;
                            JTextField[] textFields = CellInsertionDialog.getTextFields();
                            JDialog dialog = CellInsertionDialog.getDialog();
                            for (int i = numberOfCells - 1; i >= 0; i--) {
                                String value = textFields[i].getText();
                                model.insertCellLeftAndShift(nextRow, nextColumn);
                                model.setValueAt(value, nextRow, nextColumn);
                            }
                            binTable.repaint();
                            fileActions.autoSaveFileAtPage(btm);
                            fileActions.autoOpenFileAtPage(btm);
                            dialog.dispose();
                            updateNavigationLabel(fileActions, navigationLabel);
                        }
                    });
                } else {
                    JOptionPane.showMessageDialog(frame, "Пожалуйста, выберите ячейку.", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Обработчик для редактирования ячейки
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = binTable.getSelectedRow();
                int column = binTable.getSelectedColumn();
                MyCellEditor editor = new MyCellEditor();
                if (row != -1 && column != -1) {
                    binTable.getColumnModel().getColumn(column).setCellEditor(editor);
                    binTable.editCellAt(row, column, e);
                    Component editorComponent = binTable.getEditorComponent();
                    if (editorComponent != null) {
                        editorComponent.requestFocusInWindow();
                    }
                    fileActions.autoSaveFileAtPage(btm);
                    fileActions.autoOpenFileAtPage(btm);
                } else {
                    JOptionPane.showMessageDialog(frame, "Пожалуйста, выберите ячейку для редактирования.", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Обработчик для сброса значений ячеек
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
                    fileActions.autoSaveFileAtPage(btm);
                    fileActions.autoOpenFileAtPage(btm);
                } else {
                    JOptionPane.showMessageDialog(frame, "Пожалуйста, выберите хотя бы одну ячейку для обнуления.", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Обработчик для удаления ячеек
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
                    model.fireTableDataChanged();
                    binTable.repaint();
                    fileActions.autoSaveFileAtPage(btm);
                    fileActions.autoOpenFileAtPage(btm);
                    binTable.changeSelection(selectedRows[0], selectedColumns[0], false, false);
                    updateNavigationLabel(fileActions, navigationLabel);
                } else {
                    JOptionPane.showMessageDialog(frame, "Пожалуйста, выберите хотя бы одну ячейку для удаления.", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Обработчик для копирования выделенного блока
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

        // Обработчик для вырезания сдвигом
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
                    fileActions.autoSaveFileAtPage(btm);
                    fileActions.autoOpenFileAtPage(btm);
                    binTable.changeSelection(selectedRows[0], selectedColumns[0], false, false);
                    updateNavigationLabel(fileActions, navigationLabel);
                } else {
                    JOptionPane.showMessageDialog(frame, "Пожалуйста, выберите хотя бы одну ячейку.", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Обработчик для вырезания с сбросом
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
                    fileActions.autoSaveFileAtPage(btm);
                    fileActions.autoOpenFileAtPage(btm);
                    binTable.changeSelection(selectedRows[0], selectedColumns[0], false, false);
                } else {
                    JOptionPane.showMessageDialog(frame, "Пожалуйста, выберите хотя бы одну ячейку.", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Обработчик для вставки без сдвига
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
                        fileActions.autoSaveFileAtPage(btm);
                        fileActions.autoOpenFileAtPage(btm);
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

        // Обработчик для вставки справа со сдвигом
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
                        fileActions.autoSaveFileAtPage(btm);
                        fileActions.autoOpenFileAtPage(btm);
                        binTable.changeSelection(selectedRow, selectedColumn, false, false);
                        updateNavigationLabel(fileActions, navigationLabel);
                    } else {
                        JOptionPane.showMessageDialog(frame, "Пожалуйста, выберите ячейку.", "Ошибка", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Сначала скопируйте блок", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Обработчик для вставки слева со сдвигом
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
                        fileActions.autoSaveFileAtPage(btm);
                        fileActions.autoOpenFileAtPage(btm);
                        binTable.changeSelection(selectedRow, selectedColumn, false, false);
                        updateNavigationLabel(fileActions, navigationLabel);
                    } else {
                        JOptionPane.showMessageDialog(frame, "Пожалуйста, выберите ячейку.", "Ошибка", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Сначала скопируйте блок", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Обработчик для очистки данных
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int rowCount = btm.getRowCount();
                int columnCount = btm.getColumnCount();

                for (int i = 0; i < rowCount; i++) {
                    for (int j = 1; j < columnCount; j++) {
                        btm.setValueAt("", i, j);
                        updateNavigationLabel(fileActions, navigationLabel);
                    }
                }
            }
        });

        // Обработчик для поиска в таблице
        byteSearchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame searchWindow = new JFrame("Поиск в таблице");
                JPanel searchPanel = new JPanel(new BorderLayout());

                ButtonUtils.setTable(binTable);

                JPanel inputPanel = ButtonUtils.createInputPanel();
                JPanel buttonPanel = ButtonUtils.createButtonPanel(inputPanel);
                searchPanel.add(inputPanel, BorderLayout.NORTH);
                searchPanel.add(buttonPanel, BorderLayout.CENTER);

                JScrollPane scrollPane = new JScrollPane(searchPanel);
                scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
                searchWindow.add(scrollPane);
                searchWindow.pack();
                searchWindow.setLocationRelativeTo(null);
                searchWindow.setVisible(true);
            }
        });

        emptyButton.setBorderPainted(false);
        emptyButton.setContentAreaFilled(false);
        emptyButton.setFocusPainted(false);
        emptyButton.setEnabled(false);

        // Обработчик для перехода на предыдущую страницу
        previousPageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fileActions.autoSaveFileAtPage(btm);
                fileActions.autoOpenFileAtPage(btm);
                fileActions.previousPage(btm);
                updateNavigationLabel(fileActions, navigationLabel);
            }
        });

        // Обработчик для перехода на следующую страницу
        nextPageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fileActions.autoSaveFileAtPage(btm);
                fileActions.autoOpenFileAtPage(btm);
                fileActions.nextPage(btm);
                updateNavigationLabel(fileActions, navigationLabel);
            }
        });

        // Обработчик для открытия пользователем файла
        openFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                fileActions.openFile(btm, actionEvent);
                updateNavigationLabel(fileActions, navigationLabel);
            }
        });

        // Обработчик для сохранения пользователем файла
        saveFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                fileActions.saveCurrentPageWithContext(actionEvent, btm);
                updateNavigationLabel(fileActions, navigationLabel);
            }
        });

        // Настройка панели с кнопками
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        Component[] components = {editButton, resetButton, copyBlockButton, deleteButton, pasteWithoutShiftButton, byteSearchButton,
                cutBlockWithShiftButton, cutBlockWithResetButton, pasteWithShiftLeftButton, pasteWithShiftRightButton,
                insertCellLeftButton, insertCellLeftComboBox, insertCellRightButton, insertCellRightComboBox,
                clearButton, emptyButton, previousPageButton, nextPageButton,navigationLabel, emptyButton, openFileButton, saveFileButton};
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(1, 1, 1, 1);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Добавление кнопок на панель в две колонки
        for (int i = 0; i < components.length; i++) {
            gbc.gridx = i % 2;
            gbc.gridy = i / 2;
            buttonPanel.add(components[i], gbc);
        }

        return buttonPanel;
    }

    //Метод для обновления панели навигации
    private static void updateNavigationLabel(FileActions fileActions, JLabel navigationLabel) {
        int currentPage = fileActions.getCurrentPage();
        int totalPages = fileActions.getTotalPages();
        navigationLabel.setText("Page: " + currentPage + " of " + totalPages);
    }

}
