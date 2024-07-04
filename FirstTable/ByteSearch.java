package FirstTable;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;

public class ByteSearch {
    private JTable bintable;

    public ByteSearch(JTable table) {
        this.bintable = table;
    }

    public List<Integer> searchBytes(byte[] sequence, boolean exactMatch) {
        List<Integer> results = new ArrayList<>();

        DefaultTableModel model = (DefaultTableModel) bintable.getModel();
        int rowCount = model.getRowCount();
        int columnCount = model.getColumnCount();

        for (int row = 0; row < rowCount; row++) {
            for (int column = 0; column < columnCount; column++) {
                Object cellValue = model.getValueAt(row, column);
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