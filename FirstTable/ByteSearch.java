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

    public List<String> searchBytes(String[] sequence, boolean exactMatch) {
        List<String> results = new ArrayList<>();

        int rowCount = bintable.getRowCount();
        int columnCount = bintable.getColumnCount();
        int sequenceLength = sequence.length;

        for (int row = 0; row < rowCount; row++) {
            for (int column = 1; column < columnCount; column++) {
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
                    results.add("Совпадение найдено начиная с строки: " + row + ", столбца: " + column);
                    System.out.println("Совпадение найдено начиная с строки: " + row + ", столбца: " + column);
                }
            }
        }

        if (results.isEmpty()) {
            System.out.println("Совпадений не найдено");
        }

        return results;
    }

}