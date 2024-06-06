import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.BorderLayout;


public class MyGUI {
    public static void main(String[] args) {
        JFrame frame = new JFrame("My First GUI");
        String path = "/Images/icon.png";
        // <a target="_blank" href="https://icons8.com/icon/38392/binary-file">Binary File</a> icon by <a target="_blank" href="https://icons8.com">Icons8</a>
        ImageIcon icon = new ImageIcon(MyGUI.class.getResource(path));
        frame.setIconImage(icon.getImage());
        JPanel buttonsPanel = new JPanel();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 300);
        frame.setLocationRelativeTo(null);
        JButton start = new JButton("Старт");
        JButton stop = new JButton("Стоп");
        buttonsPanel.add(start);
        buttonsPanel.add(stop);
        frame.getContentPane().add(BorderLayout.NORTH, buttonsPanel);
        frame.setVisible(true);
    }

}

