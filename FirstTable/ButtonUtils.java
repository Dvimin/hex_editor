package FirstTable;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
public class ButtonUtils {
    private static ArrayList<JTextField> searchFields = new ArrayList<>();
    private static BinTableModel bintable;
    public static void setTable(BinTableModel table) {
        bintable = table;
    }
    public static JPanel createInputPanel() {
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JTextField searchField = new JTextField(2);
        searchFields.add(searchField);
        inputPanel.add(searchField);
        return inputPanel;
    }
    public static JPanel createButtonPanel(JPanel inputPanel) {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton addButton = new JButton("+");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JTextField newField = new JTextField(2);
                searchFields.add(newField);
                inputPanel.add(newField);
                inputPanel.revalidate();
                inputPanel.repaint();
            }
        });
        buttonPanel.add(addButton);
        JButton removeButton = new JButton("-");
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (inputPanel.getComponentCount() > 1) {
                    JTextField removedField = searchFields.remove(searchFields.size() - 1);
                    inputPanel.remove(removedField);
                    inputPanel.revalidate();
                    inputPanel.repaint();
                }
            }
        });
        buttonPanel.add(removeButton);
        JButton findButton = new JButton("Найти");
        findButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                byte[] searchBytes = new byte[searchFields.size()];
                for (int i = 0; i < searchFields.size(); i++) {
                    String value = searchFields.get(i).getText();
                    searchBytes[i] = Byte.parseByte(value);
                }
                ButtonUtils.setTable(bintable);
                ByteSearch byteSearch = new ByteSearch(bintable);
                List<Integer> results = byteSearch.searchBytes(searchBytes, true);
                if (results.isEmpty()) {
                    System.out.println("Последовательность байт не найдена.");
                } else {
                    System.out.println("Последовательность байт найдена в следующих строках:");
                    for (int row : results) {
                        System.out.println("Строка " + (row + 1));
                    }
                }
            }
        });
        buttonPanel.add(findButton);
        return buttonPanel;
    }
}