package FirstTable;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.util.Arrays;

public class CustomSelectionListener implements ListSelectionListener {
    private JTable table;

    public CustomSelectionListener(JTable table) {
        this.table = table;
    }

    @Override
    public void valueChanged(ListSelectionEvent event) {
        int[] selectedRows = table.getSelectedRows();
        int[] selectedColumns = table.getSelectedColumns();
        if (table.getSelectedColumn() == 0) {
            table.removeColumnSelectionInterval(0, 0);
            selectedColumns = Arrays.stream(selectedColumns).filter(col -> col != 0).toArray();
        }

        if (selectedRows.length == 1 && (selectedColumns.length == 1 ||selectedColumns.length == 2 || selectedColumns.length == 4 || selectedColumns.length == 8)){
            if (selectedColumns[selectedColumns.length - 1] - selectedColumns[0] != selectedColumns.length - 1){
                table.clearSelection();
            }
        } else {
            table.clearSelection();
        }
    }
}

