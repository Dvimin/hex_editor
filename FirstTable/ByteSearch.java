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

    public List<Integer> searchBytes(byte[] sequence, boolean exactMatch) {
        List<Integer> results = new ArrayList<>();

        int rowCount = bintable.getRowCount();
        int columnCount = bintable.getColumnCount();

        for (int row = 0; row < rowCount; row++) {
            for (int column = 0; column < columnCount; column++) {
                Object cellValue = bintable.getValueAt(row, column);
                if (cellValue instanceof Byte) {
                    byte value = (Byte) cellValue;
                    if (exactMatch) {
                        if (value == sequence[column]) {
                            results.add(row);
                            break;
                        }
                    } else {
                        if ((value & sequence[column]) == sequence[column]) {
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