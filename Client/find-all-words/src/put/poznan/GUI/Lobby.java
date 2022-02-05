package put.poznan.GUI;

import put.poznan.tools.PropertiesHandler;

import javax.swing.*;
import java.awt.*;

public class Lobby extends JPanel {

    private final int nr;

    private JLabel name;
    private JLabel players;
    private JLabel capacity;



    private JButton select;


    Lobby(String number, String players) {
        //#TODO get lobby nr from server
        this.nr = Integer.parseInt(number);
        this.setLayout(new GridLayout(1, 5));
        this.setPreferredSize(new Dimension(400, 60));
        this.setMaximumSize(new Dimension(600, 60));

        this.setForeground(Color.BLACK);
        this.setBackground(Color.LIGHT_GRAY);
        this.setOpaque(true);
        this.setVisible(true);

        this.setComponents(players);
        this.addComponents();
    }

    private void setComponents(String players) {

        this.name = new JLabel("Lobby " + this.nr);
        //#TODO get number of players in lobby
        this.players = new JLabel(players);
        this.capacity = new JLabel(PropertiesHandler.getProperty("lobbySize"));

        this.select = new JButton("select lobby");
        this.select.setBackground(Color.YELLOW);
    }

    private void addComponents() {
        this.add(this.name);
        this.add(this.players);
        this.add(new JLabel("/"));
        this.add(this.capacity);
        this.add(this.select);

    }

    public JButton getSelect() {
        return select;
    }

    public void updatePlayersNumber(String number) {
        this.players.setText(number);
    }

    public int getNumber() {return this.nr;}

}
