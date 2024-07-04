package FirstTable;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ButtonUtils {
    public static JPanel createInputPanel() {
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JTextField searchField = new JTextField(2);
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
                    inputPanel.remove(inputPanel.getComponentCount() - 1);
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
                JFrame searchWindow = (JFrame) SwingUtilities.getWindowAncestor(inputPanel);
                searchWindow.dispose();
            }
        });
        buttonPanel.add(findButton);

        return buttonPanel;
    }
}