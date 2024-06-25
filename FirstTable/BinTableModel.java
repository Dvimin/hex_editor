package FirstTable;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

public class BinTableModel extends AbstractTableModel {
    private int columnCount = 17;
    private ArrayList<String []> dataArrayList;


    public BinTableModel(){
        dataArrayList = new ArrayList<String []>();
        for (int i = 0; i < dataArrayList.size(); i++){
            dataArrayList.add(new String[getColumnCount()]);
        }
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

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        String []rows = dataArrayList.get(rowIndex);
        return rows[columnIndex];
    }


}

