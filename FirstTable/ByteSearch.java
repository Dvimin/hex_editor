package FirstTable;
import javax.swing.JTable;
import java.util.List;
import java.util.ArrayList;
public class ByteSearch {

    private BinTableModel bintable;

    public ByteSearch(BinTableModel table) {
        this.bintable = table;
    }

    public int[] searchBytes(int startRow, int startColumn, String[] sequence, boolean exactMatch) {
        int rowCount = bintable.getRowCount();
        int columnCount = bintable.getColumnCount();
        int sequenceLength = sequence.length;

        for (int row = startRow; row < rowCount; row++) {
            for (int column = (row == startRow ? startColumn : 1); column < columnCount; column++) {
                boolean match = true;
                int currentRow = row;
                int currentColumn = column;

                for (int seqIndex = 0; seqIndex < sequenceLength; seqIndex++) {
                    Object cellValue = bintable.getValueAt(currentRow, currentColumn);

                    if (!(cellValue instanceof String)) {
                        match = false;
                        break;
                    }
                    String value = (String) cellValue;
                    if (exactMatch) {
                        if (!value.equals(sequence[seqIndex])) {
                            match = false;
                            break;
                        }
                    } else {
                        if (!value.contains(sequence[seqIndex])) {
                            match = false;
                            break;
                        }
                    }
                    int[] nextCell = bintable.getNextCell(currentRow, currentColumn);
                    currentRow = nextCell[0];
                    currentColumn = nextCell[1];
                }

                if (match) {
                    return new int[] { row, column };
                }
            }
            startColumn = 1;
        }

        return null;
    }
}
