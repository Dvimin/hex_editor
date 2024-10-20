package utils;

import actions.FileActions;
import ui.BinTableModel;

import javax.swing.*;
import java.io.IOException;
import java.util.regex.Pattern;

public class ByteSearch {
    private BinTableModel bintable;
    private FileActions fileActions;
    private int totalPages;

    // Конструктор для инициализации ByteSearch
    public ByteSearch(BinTableModel table, FileActions fileActions) {
        this.bintable = table;
        this.fileActions = fileActions;
        this.totalPages = fileActions.getTotalPages();
    }

    // Поиск байтов по всем страницам
    public int[] searchBytesInAllPages(int startRow, int startColumn, String[] sequence, boolean exactMatch, JLabel navigationLabel) throws IOException {
        int[] result = searchBytes(startRow, startColumn, sequence, exactMatch);

        while (result[0] == -1 && fileActions.hasNextPage()) {
            fileActions.nextPage(bintable);
            fileActions.updateNavigationLabel(navigationLabel);
            result = searchBytes(0, 1, sequence, exactMatch);
        }

        return result;
    }

    // Поиск байтов на текущей странице
    public int[] searchBytes(int startRow, int startColumn, String[] sequence, boolean exactMatch) {
        int rowCount = bintable.getRowCount();
        int columnCount = bintable.getColumnCount();
        int sequenceLength = sequence.length;

        int currentRow = startRow;
        int currentColumn = startColumn;

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
            break;
        }
        return new int[]{-1, -1};
    }
}
