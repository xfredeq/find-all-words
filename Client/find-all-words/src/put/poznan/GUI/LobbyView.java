package put.poznan.GUI;

import put.poznan.tools.ConnectionHandler;
import put.poznan.tools.MyView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class LobbyView extends MyView implements ActionListener {


    private JLabel title;

    private ArrayList<Lobby> lobbies;


    private JPanel buttonPanel;


    private JButton join;
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
        this.previousViewName = "StartView";
        this.title = new JLabel("Lobby");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setAlignmentY(Component.TOP_ALIGNMENT);
        title.setFont(new Font("Verdana", Font.BOLD, 80));
        title.setBackground(new Color(1.0f, 1.0f, 1.0f, 0.5f));
        title.setForeground(Color.GREEN);
        title.setOpaque(true);

        this.lobbies = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            this.lobbies.add(new Lobby());
            this.lobbies.get(i).getSelect().addActionListener(this);
        }

        //#TODO ButtonPanelFactory

        this.join = new JButton("Join lobby");
        this.join.setVisible(false);
        this.nextViewButton = this.join;

        this.cancel = new JButton("cancel");
        this.previousViewButton = this.cancel;

        this.buttonPanel = new JPanel();
        this.buttonPanel.setLayout(new GridLayout(1, 3));
        this.buttonPanel.setPreferredSize(new Dimension(400, 60));
        this.buttonPanel.setMaximumSize(new Dimension(400, 60));

        this.buttonPanel.add(this.join);
        this.buttonPanel.add(Box.createHorizontalGlue());
        this.buttonPanel.add(this.cancel);


    }

    private void addComponents() {
        add(Box.createVerticalGlue());
        add(this.title);
        add(Box.createVerticalGlue());
        for (Lobby lobby : this.lobbies) {
            this.add(lobby);
        }
        add(Box.createVerticalGlue());
        add(this.buttonPanel);
        add(Box.createVerticalGlue());
    }


    @Override
    public void onShowAction() {

    }


    @Override
    public void returnToPreviousView(CardLayout cardLayout, JPanel cardPane) {
        //#TODO break connection
        for (Lobby l : this.lobbies) {
            l.getSelect().setBackground(Color.YELLOW);
        }
        this.join.setVisible(false);
        ConnectionHandler.endConnection();

        super.returnToPreviousView(cardLayout, cardPane);
    }

    @Override
    public void moveToNextView(CardLayout cardLayout, JPanel cardPane) {
        for (Lobby l : this.lobbies) {
            l.getSelect().setBackground(Color.YELLOW);
        }
        this.join.setVisible(false);
        super.moveToNextView(cardLayout, cardPane);
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        for (Lobby lobby : this.lobbies) {
            if (source == lobby.getSelect()) {
                for (Lobby l : this.lobbies) {
                    l.getSelect().setBackground(Color.YELLOW);
                }
                lobby.getSelect().setBackground(Color.GREEN);
                this.join.setVisible(true);
            }
        }
    }
}
