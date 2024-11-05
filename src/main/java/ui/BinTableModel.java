package ui;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

public class BinTableModel extends AbstractTableModel {
    private final int columnCount = 17;
    private final ArrayList<String[]> dataArrayList;


    public BinTableModel() {
        dataArrayList = new ArrayList<String[]>();
    }

    // Указывает, редактируема ли ячейка в таблице
    @Override
    public boolean isCellEditable(int row, int column) {
        return column != 0;
    }

    // Устанавливает значение ячейки по заданной строке и столбцу
    @Override
    public void setValueAt(Object value, int row, int col) {
        String[] rowData = dataArrayList.get(row);
        rowData[col] = (String) value;
        fireTableCellUpdated(row, col);
    }

    // Возвращает количество строк в таблице
    @Override
    public int getRowCount() {
        return dataArrayList.size();
    }

    // Возвращает название колонки в таблице
    @Override
    public String getColumnName(int columnIndex) {
        if (columnIndex == 0) {
            return "";
        } else {
            return String.format("%02X", columnIndex - 1);
        }
    }

    // Возвращает количество колонок в таблице
    @Override
    public int getColumnCount() {
        return columnCount;
    }


    // Очищает данные таблицы
    public void clearData() {
        dataArrayList.clear();
        fireTableDataChanged();
    }

    // Возвращает все данные таблицы в виде массива байтов
    public byte[] getAllData() {
        ArrayList<Byte> byteList = new ArrayList<>();
        for (String[] row : dataArrayList) {
            for (int i = 1; i < columnCount; i++) {
                if (row[i] != null && !row[i].isEmpty()) {
                    try {
                        byte value = (byte) Integer.parseInt(row[i], 16);
                        byteList.add(value);
                    } catch (NumberFormatException e) {
                        System.err.println("Ошибка при преобразовании: " + row[i]);
                    }
                }
            }
        }

        byte[] byteArray = new byte[byteList.size()];
        for (int i = 0; i < byteList.size(); i++) {
            byteArray[i] = byteList.get(i);
        }

        return byteArray;
    }

    // Добавляет данные в таблицу из массива байтов
    public void addData(byte[] data) {
        int dataIndex = 0;
        while (dataIndex < data.length) {
            String[] row = new String[columnCount];
            row[0] = String.format("%08X", dataArrayList.size());
            for (int i = 1; i < columnCount && dataIndex < data.length; i++) {
                row[i] = String.format("%02X", data[dataIndex++]);
            }
            dataArrayList.add(row);
        }
        fireTableDataChanged();
    }

    // Добавляет пустую строку с заданным значением в таблицу
    public void addEmptyRowWithLogic(String value) {
        String[] row = new String[getColumnCount()];
        row[0] = String.format("%08X", dataArrayList.size());
        row[1] = value;
        for (int i = 2; i < getColumnCount(); i++) {
            row[i] = "";
        }
        dataArrayList.add(row);
        fireTableRowsInserted(dataArrayList.size() - 1, dataArrayList.size() - 1);
    }

    // Удаляет строку из таблицы по индексу
    public void removeRow(int row) {
        if (row >= 0 && row < dataArrayList.size()) {
            dataArrayList.remove(row);
            fireTableRowsDeleted(row, row);
        }
    }


    // Смещает содержимое строки вправо, начиная с первого элемента
    public String shiftAllRowRight(int row, String element) {
        int columnCount = getColumnCount();
        String lastCellValue = getValueAt(row, columnCount - 1);

        for (int j = columnCount - 2; j >= 1; j--) {
            String cellValue = getValueAt(row, j);
            setValueAt(cellValue, row, j + 1);
        }

        setValueAt(element, row, 1);

        fireTableDataChanged();
        return lastCellValue;
    }

    // Смещает выбранную строку вправо от указанного столбца
    public String shiftSelectedRowRight(int row, int column) {
        int columnCount = getColumnCount();
        if (column == columnCount - 1) {
            return "";
        }
        String lastCellValue = getValueAt(row, columnCount - 1);
        for (int j = columnCount - 2; j > column; j--) {
            String cellValue = getValueAt(row, j);
            setValueAt(cellValue, row, j + 1);
        }
        setValueAt("", row, column + 1);
        fireTableDataChanged();
        return lastCellValue;
    }

    // Вставляет ячейку справа с логикой сдвига
    public void insertCellRightAndShift(int selectedRow, int selectedColumn) {
        int lastRow = getRowCount() - 1;
        int lastColumn = getColumnCount() - 1;
        if (selectedRow == lastRow) {
            String lastElement = getValueAt(lastRow, lastColumn);
            if (lastElement.equals("") && selectedColumn != lastColumn) {
                shiftSelectedRowRight(selectedRow, selectedColumn);
            } else {
                String valueToShift = shiftSelectedRowRight(selectedRow, selectedColumn);
                addEmptyRowWithLogic(valueToShift);
            }
        } else {
            String valueToShift = shiftSelectedRowRight(selectedRow, selectedColumn);
            for (int i = selectedRow + 1; i < lastRow; i++) {
                valueToShift = shiftAllRowRight(i, valueToShift);
            }
            String lastElement = getValueAt(lastRow, lastColumn);
            if (lastElement.equals("")) {
                shiftAllRowRight(lastRow, valueToShift);
            } else {
                valueToShift = shiftAllRowRight(lastRow, valueToShift);
                addEmptyRowWithLogic(valueToShift);
            }
        }
    }

    // Вставляет пустую ячейку слева с логикой сдвига
    public void insertCellLeftAndShift(int selectedRow, int selectedColumn) {
        if (selectedRow == 0 && selectedColumn == 1) {
            insertCellRightAndShift(0, 0);
            return;
        }
        int[] backCell = getBackCell(selectedRow, selectedColumn);
        selectedRow = backCell[0];
        selectedColumn = backCell[1];
        insertCellRightAndShift(selectedRow, selectedColumn);
    }

    // Смещает содержимое строки влево с заданным элементом
    public String shiftAllRowLeft(int row, String element) {
        int columnCount = getColumnCount();
        Object firstCellValueObj = getValueAt(row, 1);
        String firstCellValue = firstCellValueObj != null ? firstCellValueObj.toString() : "";

        for (int j = 1; j < columnCount - 1; j++) {
            Object nextCellValueObj = getValueAt(row, j + 1);
            String cellValue = nextCellValueObj != null ? nextCellValueObj.toString() : "";
            setValueAt(cellValue, row, j);
        }

        setValueAt(element, row, columnCount - 1);

        fireTableDataChanged();
        return firstCellValue;
    }

    // Смещает выбранную строку влево с логикой замещения
    public void shiftSelectedRowLeft(int row, int column, String element) {
        int columnCount = getColumnCount();
        if (column > 0) {
            for (int j = column; j < columnCount - 1; j++) {
                setValueAt(getValueAt(row, j + 1), row, j);
            }
            setValueAt(element, row, columnCount - 1);

            fireTableDataChanged();
        }
    }

    // Удаляет ячейку и сдвигает остальные ячейки
    public void deleteCellAndShift(int selectedRow, int selectedColumn) {
        int lastRow = getRowCount() - 1;
        int lastColumn = getColumnCount() - 1;
        String element = "";
        for (int row = lastRow; row > selectedRow; row--) {
            element = shiftAllRowLeft(row, element);
        }
        shiftSelectedRowLeft(selectedRow, selectedColumn, element);
    }


    // Возвращает следующую ячейку по порядку
    public int[] getNextCell(int currentRow, int currentColumn) {
        int lastRow = getRowCount() - 1;
        int lastColumn = getColumnCount() - 1;

        if (currentColumn == lastColumn) {
            if (currentRow == lastRow) {
                return new int[]{0, 1};
            } else {
                return new int[]{currentRow + 1, 1};
            }
        } else {
            return new int[]{currentRow, currentColumn + 1};
        }
    }

    // Возвращает предыдущую ячейку по порядку
    public int[] getBackCell(int currentRow, int currentColumn) {
        int lastRow = getRowCount() - 1;
        int lastColumn = getColumnCount() - 1;
        if (currentColumn == 1) {
            if (currentRow == 0) {
                return new int[]{lastRow, lastColumn};
            } else {
                return new int[]{currentRow - 1, lastColumn};
            }
        } else {
            return new int[]{currentRow, currentColumn - 1};
        }
    }

    // Возвращает значение ячейки по строке и столбцу
    @Override
    public String getValueAt(int rowIndex, int columnIndex) {
        String[] rows = dataArrayList.get(rowIndex);
        return (rows[columnIndex] != null) ? rows[columnIndex] : "";
    }


}

