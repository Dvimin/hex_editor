package FirstTable;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

public class BinTableModel extends AbstractTableModel {
//    private static final long serialVersionUID =


    private int columnCount = 16;
    private ArrayList<String []> dataArrayList;


    public BinTableModel(){
        dataArrayList = new ArrayList<String []>();
        for (int i = 0; i < dataArrayList.size(); i++){
            dataArrayList.add(new String[getColumnCount()]);
        }
    }

    @Override
    public int getRowCount() {
        return dataArrayList.size();
    }

    @Override
    public  String getColumnName(int columnIndex){
        switch (columnIndex){
            case 0: return "00";
            case 1: return "01";
            case 2: return "02";
            case 3: return "03";
            case 4: return "04";
            case 5: return "05";
            case 6: return "06";
            case 7: return "07";
            case 8: return "08";
            case 9: return "09";
            case 10: return "0a";
            case 11: return "0b";
            case 12: return "0c";
            case 13: return "0d";
            case 14: return "0e";
            case 15: return "0f";
        }
        return "";
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
        for (int i = 0; i < columnCount; i++) {
            if (i < data.length) {
                row[i] = String.format("%02X", data[i]);
            } else {
                row[i] = "";
            }
        }
        dataArrayList.add(row);
        fireTableDataChanged();
    }
}

