package utils;

import actions.FileActions;
import ui.BinTableModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;

public class ButtonUtils {
    private static ArrayList<JTextField> searchFields = new ArrayList<>();
    private static JTable binTable;
    private static ByteSearch byteSearch;

    // Установка таблицы и обработчика файлов для поиска
    public static void setTableAndFileActions(JTable table, FileActions fileActions) {
        binTable = table;
        byteSearch = new ByteSearch((BinTableModel) binTable.getModel(), fileActions);
    }

    // Создание панели ввода для поиска
    public static JPanel createInputPanel() {
        searchFields.clear();
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JTextField searchField = new JTextField(2);
        searchFields.add(searchField);
        inputPanel.add(searchField);
        return inputPanel;
    }

    // Создание панели кнопок для управления полем поиска
    public static JPanel createButtonPanel(JPanel inputPanel, JLabel navigationLabel) {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        // Кнопка добавления нового поля для поиска
        JButton addButton = new JButton("+");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JTextField newField = new JTextField(2);
                searchFields.add(newField);
                inputPanel.add(newField);
                inputPanel.revalidate();
                inputPanel.repaint();
            }
        });
        buttonPanel.add(addButton);

        // Кнопка удаления последнего поля для поиска
        JButton removeButton = new JButton("-");
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (inputPanel.getComponentCount() > 1) {
                    JTextField removedField = searchFields.remove(searchFields.size() - 1);
                    inputPanel.remove(removedField);
                    inputPanel.revalidate();
                    inputPanel.repaint();
                }
            }
        });
        buttonPanel.add(removeButton);

        // Кнопка поиска по заданным значениям
        JButton findButton = new JButton("Найти");
        findButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (validateInput()) {
                    // Сбор значений для поиска
                    String[] searchByte = new String[searchFields.size()];
                    for (int i = 0; i < searchFields.size(); i++) {
                        String value = searchFields.get(i).getText();
                        searchByte[i] = value;
                    }

                    int startRow = binTable.getSelectedRow();
                    int startColumn = binTable.getSelectedColumn();
                    if (startRow == -1 || startColumn == -1) {
                        startRow = 0;
                        startColumn = 1;
                    } else {
                        int[] nextCell = getNextCell(startRow, startColumn);
                        startRow = nextCell[0];
                        startColumn = nextCell[1];
                    }

                    // Выполнение поиска по всем страницам и получение результатов
                    try {
                        int[] foundCoordinates = byteSearch.searchBytesInAllPages(startRow, startColumn, searchByte, false, navigationLabel);
                        int foundRow = foundCoordinates[0];
                        int foundColumn = foundCoordinates[1];
                        if (foundRow == -1 && foundColumn == -1) {
                            JOptionPane.showMessageDialog(null, "Совпадений не найдено", "Ошибка", JOptionPane.ERROR_MESSAGE);
                        } else {
                            binTable.changeSelection(foundRow, foundColumn, false, false);
                        }
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(null, "Ошибка при доступе к файлу: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Введите двузначное число в формате 16-ричной системы (0-9, A-F), либо символы '*' или '?'", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        buttonPanel.add(findButton);

        return buttonPanel;
    }

    // Валидация введенных значений в полях поиска
    private static boolean validateInput() {
        for (JTextField field : searchFields) {
            String text = field.getText().trim();
            if (!text.matches("[0-9A-F*?]*")) {
                return false;
            }
        }
        return true;
    }

    // Получение следующей ячейки в таблице для поиска
    public static int[] getNextCell(int currentRow, int currentColumn) {
        int rowCount = binTable.getRowCount();
        int columnCount = binTable.getColumnCount();

        if (currentColumn < columnCount - 1) {
            currentColumn++;
        } else {
            currentColumn = 1;
            currentRow++;
            if (currentRow >= rowCount) {
                currentRow = 0;
            }
        }

        return new int[]{currentRow, currentColumn};
    }
}
