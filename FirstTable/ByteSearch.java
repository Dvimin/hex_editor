package FirstTable;

import java.util.regex.Pattern;

public class ByteSearch {
    // Модель таблицы для поиска
    private BinTableModel bintable;

    // Конструктор класса

    public ByteSearch(BinTableModel table) {
        this.bintable = table;
    }

    /**
     * Выполняет поиск последовательности байтов в таблице.
     * @param startRow начальная строка для поиска
     * @param startColumn начальный столбец для поиска
     * @param sequence массив строк, представляющих последовательность для поиска
     * @param exactMatch флаг точного соответствия при сравнении
     * @return массив с координатами найденной последовательности [row, column], либо [-1, -1], если не найдено
     */
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
                            String regex = sequence[seqIndex].replace("?", ".?").replace("*", ".*?");
                            if (!Pattern.matches(regex, value)) {
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
        return new int[]{-1, -1};
    }
}
