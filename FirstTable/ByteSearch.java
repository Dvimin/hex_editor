package FirstTable;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;

public class ByteSearch {

    private BinTableModel bintable;

    public ByteSearch(BinTableModel table) {
        this.bintable = table;
    }

    public List<Integer> searchBytes(String[] sequence, boolean exactMatch) {
        List<Integer> results = new ArrayList<>();

        int rowCount = bintable.getRowCount();
        int columnCount = bintable.getColumnCount();

        for (int row = 0; row < rowCount; row++) {
            for (int column = 1; column < columnCount; column++) {
                Object cellValue = bintable.getValueAt(row, column);
                if (cellValue instanceof String) {
                    String value = (String) cellValue;
                    if (exactMatch) {
                        if (value.equals(sequence[column])) {
                            results.add(row);
                            break;
                        }
                    } else {
                        if (value.contains(sequence[column])) {
                            results.add(row);
                            break;
                        }
                    }
                }
            }
        }

        return results;
    }
}

//        for (String b : sequence) {
//            System.out.println(b);
//        }
//        System.out.println("\n");
//        for (int row = 0; row < 1; row++) {
//            for (int column = 0; column < columnCount; column++) {
//                System.out.println(", " + bintable.getValueAt(row, column));
//            }
//            }