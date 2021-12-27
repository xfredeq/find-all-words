import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StartView extends JPanel implements MyView, ActionListener {

    private String viewName;
    private String nextViewName;


    private JLabel title;

    private JButton connect;
    private JButton exit;

    public StartView() {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        setComponents();
        addComponents();
    }

    private void setComponents() {
        this.viewName = "StartView";
        this.nextViewName = "ConnectingView";
        this.title = new JLabel("Find all Words!");


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
        add(this.title);
        add(Box.createVerticalGlue());
        add(this.connect);
        add(Box.createVerticalGlue());
        add(this.exit);
        add(Box.createVerticalGlue());

    }

    @Override
    public String getViewName() {
        return this.viewName;
    }

    @Override
    public String getNextViewName() {
        return this.nextViewName;
    }

    @Override
    public JButton getNextViewButton() {
        return this.connect;
    }

    @Override
    public JButton getPreviousViewButton() {
        return null;
    }

    @Override
    public void onShowAction() {

    }

    @Override
    public void moveToNextView(CardLayout cardLayout, JPanel cardPane) {
        cardLayout.show(cardPane, this.nextViewName);
    }

    @Override
    public void returnToPreviousView(CardLayout cardLayout, JPanel cardPane) {

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        Object source = e.getSource();

        if (source == this.exit) {
            System.exit(0);
        }
    }
}
