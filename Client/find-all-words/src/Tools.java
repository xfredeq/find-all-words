import javax.swing.*;
import java.awt.*;

public class Tools {
    public static JButton createButton(String text, Color color, Font font){
        JButton button = new JButton(text);

        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setBackground(color);
        button.setForeground(Color.BLACK);
        button.setFont(font);
        button.setPreferredSize(new Dimension(200, 80));

        return button;
    }

    public static JButton createButton(String text, Color color){
        JButton button = new JButton(text);

        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setBackground(color);
        button.setForeground(Color.BLACK);
        button.setFont(new Font("Arial", Font.BOLD, 25));
        button.setPreferredSize(new Dimension(200, 80));

        return button;
    }
}
