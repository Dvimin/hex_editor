package FirstTable;
import javax.swing.*;
import java.awt.*;

public class ButtonSetup {

    public void setupButtons(JFrame frame) {
        JButton addButton = new JButton("Добавить");
        JButton deleteButton = new JButton("Удалить");
        JButton clearButton = new JButton("Очистить");
        JTextField searchText = new JTextField(10);
        JButton searchButton = new JButton("Поиск");

        JPanel buttonPanel = new JPanel(new GridBagLayout());
        Component[] components = {addButton, deleteButton, clearButton, searchText, searchButton};
        for (int i = 0; i < components.length; i++) {
            buttonPanel.add(components[i], new GridBagConstraints(i, 0, 1, 1, 1, 1,
                    GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL,
                    new Insets(1, 1, 1, 1), 0, 0));
        }
        frame.add(buttonPanel, new GridBagConstraints(0, 1, 5, 1, 1, 0,
                GridBagConstraints.SOUTH, GridBagConstraints.HORIZONTAL,
                new Insets(1, 1, 1, 1), 0, 0));
    }
}