import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Window extends JFrame implements ActionListener {
    private final JPanel cards;


    public Window() {
        super("FindAllWords");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(new Dimension(1024, 720));
        this.setLocationRelativeTo(null);

        this.cards = new JPanel(new CardLayout());
        this.cards.setBackground(Color.LIGHT_GRAY);
        this.add(this.cards);

        this.setVisible(true);
    }


    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
