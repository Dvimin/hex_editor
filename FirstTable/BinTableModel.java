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
    public int getRowCount() {
        return dataArrayList.size();
    }

    @Override
    public String getColumnName(int columnIndex) {
        if (columnIndex == 0){
            return "Row Header";
        } else {
            return String.format("%02X", columnIndex - 1);
        }
    }

    @Override
    public int getColumnCount() {
        return columnCount;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        String []rows = dataArrayList.get(rowIndex);
        return rows[columnIndex];
    }

    public void addData(byte[] data) {
        String[] row = new String[columnCount];
        row[0] = String.format("%X", dataArrayList.size());
        for (int i = 1; i < columnCount; i++) {
            if (i <= data.length) {
                row[i] = String.format("%02X", data[i - 1]);
            } else {
                row[i] = "";
            }
        }
        dataArrayList.add(row);
        fireTableDataChanged();
    }
}

