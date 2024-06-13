package FirstTable;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

public class BinTableModel extends AbstractTableModel {
//    private static final long serialVersionUID =


    private int columnCount = 4;
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
            case 0: return "Name";
            case 1: return "Number";
            case 2: return "str";
            case 3: return "path";
//            case 4: return "4";
//            case 5: return "5";
//            case 6: return "6";
//            case 7: return "7";
//            case 8: return "8";
//            case 9: return "9";
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

    public void addDate(String []row){
        String []rowTable = new String[getColumnCount()];
        rowTable = row;
        dataArrayList.add(rowTable);

    }
}

