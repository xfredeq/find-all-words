import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StartView extends JPanel implements MyView, ActionListener {

    private final String viewName;

    private final JLabel title = new JLabel("Find all Words!");

    public JButton connect;
    public JButton exit;

    public StartView() {
        this.viewName = "StartView";
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setComponents();
        addComponents();
    }

    private void setComponents() {

        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(new Font("Bradley Hand ITC", Font.BOLD, 120));
        title.setBackground(new Color(1.0f, 1.0f, 1.0f, 0.5f));
        title.setForeground(Color.RED);
        title.setOpaque(true);

        this.connect = Tools.createButton("Connect to server", Color.BLUE);
        connect.addActionListener(this);

        this.exit = Tools.createButton("Exit", Color.RED);
        exit.addActionListener(this);

    }

    private void addComponents() {
        add(Box.createVerticalGlue());
        add(title);
        add(Box.createVerticalGlue());
        add(connect);
        add(Box.createVerticalGlue());
        add(exit);
        add(Box.createVerticalGlue());

    }

    @Override
    public String getViewName() {
        return this.viewName;
    }

    @Override
    public JButton getNextViewButton() {
        return this.connect;
    }

    @Override
    public void moveToNextView(CardLayout cardLayout, JPanel cardPane) {
        this.connect.setBackground(Color.PINK);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        Object source = e.getSource();

        if (source == this.exit) {
            System.exit(0);
        }
    }
}
