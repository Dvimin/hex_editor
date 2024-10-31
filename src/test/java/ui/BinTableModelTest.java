package ui;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BinTableModelTest {
    private BinTableModel binTableModel;

    @BeforeEach
    void setUp() {
        binTableModel = new BinTableModel();
    }

    // Проверяем добавление данных в таблицу
    @Test
    void testAddData() {
        byte[] data = {0x1, 0x2, 0x3, 0x4};
        binTableModel.addData(data);

        assertEquals(1, binTableModel.getRowCount());
        assertEquals("01", binTableModel.getValueAt(0, 1));
        assertEquals("02", binTableModel.getValueAt(0, 2));
        assertEquals("03", binTableModel.getValueAt(0, 3));
        assertEquals("04", binTableModel.getValueAt(0, 4));
    }

    // Проверяем очистку данных в таблице
    @Test
    void testClearData() {
        byte[] data = new byte[25];
        for (int i = 0; i < 25; i++) {
            data[i] = (byte) i;
        }

        binTableModel.addData(data);
        assertEquals(2, binTableModel.getRowCount());

        binTableModel.clearData();

        assertEquals(0, binTableModel.getRowCount());

        for (int row = 0; row < binTableModel.getRowCount(); row++) {
            for (int col = 0; col < binTableModel.getColumnCount(); col++) {
                assertEquals("", binTableModel.getValueAt(row, col));
            }
        }
    }

    // Проверяем сдвиг всех ячеек вправо
    @Test
    void testShiftAllRowRight() {
        byte[] data = {0xA, 0xB, 0xC};
        binTableModel.addData(data);

        assertEquals("0A", binTableModel.getValueAt(0, 1));
        assertEquals("0B", binTableModel.getValueAt(0, 2));
        assertEquals("0C", binTableModel.getValueAt(0, 3));

        String lastValue = binTableModel.shiftAllRowRight(0, "FF");

        assertEquals("FF", binTableModel.getValueAt(0, 1));
        assertEquals("0A", binTableModel.getValueAt(0, 2));
        assertEquals("0B", binTableModel.getValueAt(0, 3));
        assertEquals("0C", binTableModel.getValueAt(0, 4));
    }

    // Проверяем сдвиг выделенной строки вправо
    @Test
    void testShiftSelectedRowRight() {
        byte[] data = {0x1, 0x2, 0x3};
        binTableModel.addData(data);

        binTableModel.shiftSelectedRowRight(0, 1);

        assertEquals("01", binTableModel.getValueAt(0, 1));
        assertEquals("", binTableModel.getValueAt(0, 2));
        assertEquals("02", binTableModel.getValueAt(0, 3));
        assertEquals("03", binTableModel.getValueAt(0, 4));
    }

    // Проверяем вставку ячейки вправо и сдвиг
    @Test
    void testInsertCellRightAndShift() {
        byte[] data = {0x1, 0x2, 0x3};
        binTableModel.addData(data);

        binTableModel.insertCellRightAndShift(0, 1);

        assertEquals("01", binTableModel.getValueAt(0, 1));
        assertEquals("", binTableModel.getValueAt(0, 2));
        assertEquals("02", binTableModel.getValueAt(0, 3));
        assertEquals("03", binTableModel.getValueAt(0, 4));
    }

    // Проверяем вставку ячейки вправо и сдвиг с 20 элементами
    @Test
    void testInsertCellRightAndShiftWith20Elements() {
        byte[] data = new byte[20];
        for (int i = 0; i < 20; i++) {
            data[i] = (byte) (i + 1);
        }
        binTableModel.addData(data);

        binTableModel.insertCellRightAndShift(0, 10);
        binTableModel.setValueAt("00", 0, 11);

        byte[] actualData = binTableModel.getAllData();

        byte[] expectedData = new byte[21];
        for (int i = 0; i < 10; i++) {
            expectedData[i] = (byte) (i + 1);
        }
        expectedData[10] = 0x00;
        for (int i = 11; i < 21; i++) {
            expectedData[i] = (byte) (i);
        }

        assertArrayEquals(expectedData, actualData);
    }

    // Проверяем смещение содержимого строки влево
    @Test
    void testShiftAllRowLeft() {
        byte[] data = new byte[17];
        for (int i = 0; i < 17; i++) {
            data[i] = (byte) (i + 1);
        }
        binTableModel.addData(data);

        String lastValue = binTableModel.shiftAllRowLeft(0, "FF");

        assertEquals("FF", binTableModel.getValueAt(0, 16));
        assertEquals("02", binTableModel.getValueAt(0, 1));
    }

    // Проверяем смещение выделенной строки влево
    @Test
    void testShiftSelectedRowLeft() {
        byte[] data = new byte[17];
        for (int i = 0; i < 17; i++) {
            data[i] = (byte) (i + 1);
        }
        binTableModel.addData(data);

        binTableModel.shiftSelectedRowLeft(0, 2, "FF");

        assertEquals("01", binTableModel.getValueAt(0, 1));
        assertEquals("03", binTableModel.getValueAt(0, 2));
        assertEquals("04", binTableModel.getValueAt(0, 3));
        assertEquals("FF", binTableModel.getValueAt(0, 16));

    }

    // Проверяем вставку пустой ячейки слева и сдвиг
    @Test
    void testInsertCellLeftAndShift() {
        byte[] data = {0x1, 0x2, 0x3};
        binTableModel.addData(data);

        binTableModel.insertCellLeftAndShift(0, 1);

        assertEquals("", binTableModel.getValueAt(0, 1));
        assertEquals("01", binTableModel.getValueAt(0, 2));
    }


    // Проверяем удаление ячейки и сдвиг
    @Test
    void testDeleteCellAndShift() {
        byte[] data = {0x1, 0x2, 0x3};
        binTableModel.addData(data);

        binTableModel.deleteCellAndShift(0, 1);

        assertEquals("03", binTableModel.getValueAt(0, 2));
        assertEquals("02", binTableModel.getValueAt(0, 1));
    }

    // Проверяем получение всех данных
    @Test
    void testGetAllData() {
        byte[] data = {0x7, 0x8, 0x9};
        binTableModel.addData(data);

        byte[] resultData = binTableModel.getAllData();
        byte[] expectedData = {0x7, 0x8, 0x9};

        assertArrayEquals(expectedData, resultData);
    }

    @Test
    void testGetNextCell() {
        byte[] data = {0x7, 0x8, 0x9};
        binTableModel.addData(data);

        int row = 0;
        int col = 2;

        int[] nextCell = binTableModel.getNextCell(row, col);

        assertEquals("09", binTableModel.getValueAt(nextCell[0], nextCell[1]));
    }

    @Test
    void testGetBackCelll() {
        byte[] data = {0x7, 0x8, 0x9};
        binTableModel.addData(data);

        int row = 0;
        int col = 2;

        int[] nextCell = binTableModel.getBackCell(row, col);

        assertEquals("07", binTableModel.getValueAt(nextCell[0], nextCell[1]));
    }

    // Проверяем переход на следующую ячейку при переходе на новую строку
    @Test
    void testGetNextCell_NewRowTransition() {
        byte[] data = new byte[34];
        for (int i = 0; i < 34; i++) {
            data[i] = (byte) (i + 1);
        }
        binTableModel.addData(data);
        int row = 0;
        int col = 16;
        int[] nextCell = binTableModel.getNextCell(row, col);

        assertEquals(1, nextCell[0]);
        assertEquals(1, nextCell[1]);
    }

    // Проверяем переход на предыдущую ячейку при переходе на предыдущую строку
    @Test
    void testGetBackCell_PreviousRowTransition() {
        byte[] data = new byte[34];
        for (int i = 0; i < 34; i++) {
            data[i] = (byte) (i + 1);
        }
        binTableModel.addData(data);

        int row = 1;
        int col = 1;

        int[] backCell = binTableModel.getBackCell(row, col);

        assertEquals(0, backCell[0]);
        assertEquals(16, backCell[1]);
    }

    // Проверяем редактируемость ячейки
    @Test
    void testIsCellEditable() {
        byte[] data = {0x1, 0x2, 0x3};
        binTableModel.addData(data);

        // Проверяем, что ячейка по умолчанию редактируема
        assertTrue(binTableModel.isCellEditable(0, 1));
        assertFalse(binTableModel.isCellEditable(0, 0));
    }

    // Проверяем установку значения в ячейку
    @Test
    void testSetValueAt() {
        byte[] data = {0x1, 0x2, 0x3};
        binTableModel.addData(data);

        binTableModel.setValueAt("FF", 0, 1);
        assertEquals("FF", binTableModel.getValueAt(0, 1));
    }

    // Проверяем добавление пустой строки с заданным значением
    @Test
    void testAddEmptyRowWithLogic() {
        binTableModel.addEmptyRowWithLogic("00");
        assertEquals(1, binTableModel.getRowCount());
        assertEquals("00", binTableModel.getValueAt(0, 1));
    }

    // Проверяем удаление строки
    @Test
    void testRemoveRow() {
        byte[] data = {0x1, 0x2, 0x3};
        binTableModel.addData(data);
        binTableModel.removeRow(0);

        assertEquals(0, binTableModel.getRowCount());
    }

}
