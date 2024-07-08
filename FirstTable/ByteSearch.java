package FirstTable;

import javax.swing.JTable;

public class ByteSearch {

    private BinTableModel bintable;

    public ByteSearch(BinTableModel table) {
        this.bintable = table;
    }

    public int[] searchBytes(int startRow, int startColumn, String[] sequence, boolean exactMatch) {
        int rowCount = bintable.getRowCount();
        int columnCount = bintable.getColumnCount();
        int sequenceLength = sequence.length;

        int currentRow = startRow;
        int currentColumn = startColumn;
        boolean firstPass = true;
        while (true) {
            for (int row = currentRow; row < rowCount; row++) {
                for (int column = (row == currentRow ? currentColumn : 0); column < columnCount; column++) {
                    boolean match = true;
                    int tempRow = row;
                    int tempColumn = column;

                    for (int seqIndex = 0; seqIndex < sequenceLength; seqIndex++) {
                        Object cellValue = bintable.getValueAt(tempRow, tempColumn);

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

                        int[] nextCell = bintable.getNextCell(tempRow, tempColumn);
                        tempRow = nextCell[0];
                        tempColumn = nextCell[1];
                    }

                    if (match) {
                        return new int[]{row, column};
                    }
                }
                currentColumn = 1;
            }
            int[] nextCell = bintable.getNextCell(currentRow, currentColumn);
            if (!firstPass || (nextCell[0] == startRow && nextCell[1] == startColumn)) {
                break;
            }

            firstPass = false;
            currentRow = 0;
            currentColumn = 1;
        }

        System.out.println("Совпадений не найдено");
        return new int[]{-1, -1};
    }
}
