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
    public String shiftRowAndInsert(int row, String element) {
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
    public String shiftColumnAndInsert(int row, int column) {
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

    public void insertCellAndShift(int selectedRow, int selectedColumn) {
        int lastRow = getRowCount() - 1;
        int lastColumn = getColumnCount() - 1;
        if (selectedRow == lastRow && selectedColumn == lastColumn) {
            addEmptyRowWithLogic("");
        } else {
            String valueToShift = shiftColumnAndInsert(selectedRow, selectedColumn);
            for (int i = selectedRow + 1; i < lastRow; i++) {
                valueToShift = shiftRowAndInsert(i, valueToShift);
            }
            String lastElement = (String) getValueAt(lastRow, lastColumn);
            if (lastElement.equals("")) {
                shiftRowAndInsert(lastRow, valueToShift);
            } else {
                valueToShift = shiftRowAndInsert(lastRow, valueToShift);
                addEmptyRowWithLogic(valueToShift);
            }
        }
    }




    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        String []rows = dataArrayList.get(rowIndex);
        return rows[columnIndex];
    }


}

