import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Collections;

public class LobbyView extends JPanel implements MyView, ChangeListener {

    private String viewName;
    private String nextViewName;

    private JLabel title;

    private JPanel[] lobbies;


    private JPanel buttonPanel;


    private JButton enter;
    private JButton cancel;


    LobbyView() {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setComponents();
        this.addComponents();
    }

    /*public static <T> JPanel<T> safe(JPanel[] panels) {
        return panels == null ? Collections.<T>emptyList() : panels;
    }*/

    private void setComponents() {
        this.viewName = "LobbyView";
        this.nextViewName = "VoteView";
        this.title = new JLabel("Lobby");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setAlignmentY(Component.TOP_ALIGNMENT);
        title.setFont(new Font("Verdana", Font.BOLD, 80));
        title.setBackground(new Color(1.0f, 1.0f, 1.0f, 0.5f));
        title.setForeground(Color.GREEN);
        title.setOpaque(true);

        this.lobbies = new JPanel[5];
        for (int i = 0; i < this.lobbies.length; i++) {
            lobbies[i] = new JPanel();
            if (lobbies[i] != null) {
                System.out.println("dziala2");
                lobbies[i].setLayout(new GridLayout(1, 4));
                lobbies[i].setPreferredSize(new Dimension(400, 60));
                lobbies[i].setMaximumSize(new Dimension(400, 60));
                lobbies[i].add(new JLabel("Lobby "));
                lobbies[i].add(new JLabel("2"));
                lobbies[i].add(new JLabel("/"));
                lobbies[i].add(new JLabel("5"));
                lobbies[i].setForeground(Color.BLACK);
                lobbies[i].setBackground(new Color(1.0f, 1.0f, 1.0f, 0.5f));
                lobbies[i].setOpaque(true);
                lobbies[i].setVisible(true);
            }

        }

        this.enter = new JButton("Enter");
        this.cancel = new JButton("cancel");

        this.buttonPanel = new JPanel();
        this.buttonPanel.setLayout(new GridLayout(1, 3));
        this.buttonPanel.setPreferredSize(new Dimension(400, 60));
        this.buttonPanel.setMaximumSize(new Dimension(400, 60));

        this.buttonPanel.add(this.enter);
        this.buttonPanel.add(Box.createHorizontalGlue());
        this.buttonPanel.add(this.cancel);


    }

    private void addComponents() {
        add(Box.createVerticalGlue());
        add(this.title);
        add(Box.createVerticalGlue());
        for (int i=0;i<this.lobbies.length;i++) {
            if (lobbies[i] != null) {
                add(lobbies[i]);
            }


        }
        add(Box.createVerticalGlue());
        add(this.buttonPanel);
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
        return this.enter;
    }

    @Override
    public JButton getPreviousViewButton() {
        return this.cancel;
    }

    @Override
    public void onShowAction() {

    }


    @Override
    public void returnToPreviousView(CardLayout cardLayout, JPanel cardPane) {
        //#TODO break connection

        cardLayout.show(cardPane, "StartView");
    }

    @Override
    public void moveToNextView(CardLayout cardLayout, JPanel cardPane) {
        cardLayout.show(cardPane, this.nextViewName);
    }

    @Override
    public void stateChanged(ChangeEvent e) {

    }
}
