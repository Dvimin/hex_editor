package FirstTable;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

public class BinTableModel extends AbstractTableModel {
    private int columnCount = 17;
    private ArrayList<String []> dataArrayList;


    public BinTableModel(){
        dataArrayList = new ArrayList<String []>();
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return column != 0;
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
        String[] rowData = dataArrayList.get(row);
        rowData[col] = (String) value;
        fireTableCellUpdated(row, col);
    }

    @Override
    public int getRowCount() {
        return dataArrayList.size();
    }

    @Override
    public String getColumnName(int columnIndex) {
        if (columnIndex == 0){
            return "";
        } else {
            return String.format("%02X", columnIndex - 1);
        }
    }

    @Override
    public int getColumnCount() {
        return columnCount;
    }

// не используются (не по заданию)
    public void addColumn(byte[] data) {
        columnCount++;
        this.addData(data);
    }

    public void deleteColumn(byte[] data) {
        if (columnCount > 1) {
            columnCount--;
            this.addData(data);
        }
    }

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

    // Смещение целой строки вправо
    public String shiftAllRow(int row, String element) {
        int columnCount = getColumnCount();
        String lastCellValue = getValueAt(row, columnCount - 1).toString();

        for (int j = columnCount - 2; j >= 1; j--) {
            String cellValue = getValueAt(row, j).toString();
            setValueAt(cellValue, row, j + 1);
        }

        setValueAt(element, row, 1);

        fireTableDataChanged();
        return lastCellValue;
    }

    // Смещение выбранной строки
    public String shiftSelectedRow(int row, int column) {
        int columnCount = getColumnCount();
        if (column == columnCount - 1) {
            return "";
        }
        String lastCellValue = getValueAt(row, columnCount - 1).toString();
        for (int j = columnCount - 2; j > column; j--) {
            String cellValue = getValueAt(row, j).toString();
            setValueAt(cellValue, row, j + 1);
        }
        setValueAt("", row, column + 1);
        fireTableDataChanged();
        return lastCellValue;
    }

    public void insertCellRightAndShift(int selectedRow, int selectedColumn) {
        int lastRow = getRowCount() - 1;
        int lastColumn = getColumnCount() - 1;
        if (selectedRow == lastRow) {
            String lastElement = (String) getValueAt(lastRow, lastColumn);
            if (lastElement.equals("") && selectedColumn != lastColumn) {
                shiftSelectedRow(selectedRow, selectedColumn);
            } else {
                String valueToShift = shiftSelectedRow(selectedRow, selectedColumn);
                addEmptyRowWithLogic(valueToShift);
            }
        } else {
            String valueToShift = shiftSelectedRow(selectedRow, selectedColumn);
            for (int i = selectedRow + 1; i < lastRow; i++) {
                valueToShift = shiftAllRow(i, valueToShift);
            }
            String lastElement = (String) getValueAt(lastRow, lastColumn);
            if (lastElement.equals("")) {
                shiftAllRow(lastRow, valueToShift);
            } else {
                valueToShift = shiftAllRow(lastRow, valueToShift);
                addEmptyRowWithLogic(valueToShift);
            }
        }
    }

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


    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        String []rows = dataArrayList.get(rowIndex);
        return rows[columnIndex];
    }


}

