package FirstTable;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class CustomSelectionListener implements ListSelectionListener {
    private JTable table;

    public CustomSelectionListener(JTable table) {
        this.table = table;
    }

    @Override
    public void valueChanged(ListSelectionEvent event) {
        int[] selectedRows = table.getSelectedRows();
        int[] selectedColumns = table.getSelectedColumns();

        if (selectedRows.length == 1 && (selectedColumns.length == 1 ||selectedColumns.length == 2 || selectedColumns.length == 4 || selectedColumns.length == 8)){
            if (selectedColumns[selectedColumns.length - 1] - selectedColumns[0] != selectedColumns.length - 1){
                table.clearSelection();
            }
        } else {
            table.clearSelection();
        }
    }
}
//public class CustomSelectionListener implements ListSelectionListener {
//    private JTable table;
//
//    public CustomSelectionListener(JTable table) {
//        this.table = table;
//    }
//
//    @Override
//    public void valueChanged(ListSelectionEvent event) {
//        int[] selectedRows = table.getSelectedRows();
//        int[] selectedColumns = table.getSelectedColumns();
//
//        if (selectedColumns.length > 1 || selectedRows.length > 1) {
//            if (selectedColumns.length == 1 || selectedColumns.length == 2 || selectedColumns.length == 4 || selectedColumns.length == 8) {
//                for (int i = 0; i < selectedColumns.length - 1; i++) {
//                    if (selectedColumns[i] != selectedColumns[i+1] - 1) {
//                        table.clearSelection();
//                        break;
//                    }
//                }
//            } else {
//                table.clearSelection();
//            }
//        }
//    }
//}
