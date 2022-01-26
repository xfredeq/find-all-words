package put.poznan.GUI;

import put.poznan.networking.ConnectionHandler;
import put.poznan.tools.MyView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

public class LobbyView extends MyView implements ActionListener {

    private JLabel title;

    private Map<String, Lobby> lobbies;

    private ActionListener listener = this;


    private JPanel buttonPanel;
    private JPanel lobbyPanel;


    private JButton join;
    private JButton create;
    private JButton cancel;

    private UpdateLobbyInfo updater;


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
        this.title = new JLabel("Lobbies");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setAlignmentY(Component.TOP_ALIGNMENT);
        title.setFont(new Font("Verdana", Font.BOLD, 80));
        title.setBackground(new Color(1.0f, 1.0f, 1.0f, 0.5f));
        title.setForeground(Color.GREEN);
        title.setOpaque(true);

        this.lobbies = new HashMap<>();

        //#TODO ButtonPanelFactory

        this.join = new JButton("Join lobby");
        this.join.setVisible(false);
        this.nextViewButton = this.join;

        this.create = new JButton("Create lobby");
        this.create.addActionListener(this);

        this.cancel = new JButton("cancel");
        this.previousViewButton = this.cancel;

        this.buttonPanel = new JPanel();
        this.buttonPanel.setLayout(new GridLayout(1, 5));
        this.buttonPanel.setPreferredSize(new Dimension(600, 60));
        this.buttonPanel.setMaximumSize(new Dimension(600, 60));

        this.buttonPanel.add(this.join);
        this.buttonPanel.add(Box.createHorizontalGlue());
        this.buttonPanel.add(this.create);
        this.buttonPanel.add(Box.createHorizontalGlue());
        this.buttonPanel.add(this.cancel);

        this.lobbyPanel = new JPanel();
        this.lobbyPanel.setLayout(new BoxLayout(this.lobbyPanel, BoxLayout.Y_AXIS));

    }

    private void addComponents() {
        add(Box.createVerticalGlue());
        add(this.title);
        add(Box.createVerticalGlue());
        add(this.lobbyPanel);
        add(Box.createVerticalGlue());
        add(this.buttonPanel);
        add(Box.createVerticalGlue());
    }


    @Override
    public void onShowAction() {
        /*this.lobbyPanel.removeAll();
        for (int i = 0; i < 5; i++) {
            Lobby lobby = new Lobby();
            this.lobbies.add(lobby);
            lobby.getSelect().addActionListener(this);
            this.lobbyPanel.add(lobby);
        }
        this.lobbyPanel.revalidate();
        validate();*/

        this.updater = new UpdateLobbyInfo();
        this.updater.execute();

    }

    @Override
    public void returnToPreviousView(CardLayout cardLayout, JPanel cardPane) {
        this.updater.cancel(true);
        //#TODO break connection
        for (Lobby l : this.lobbies.values()) {
            l.getSelect().setBackground(Color.YELLOW);
        }
        this.join.setVisible(false);
        ConnectionHandler.endConnection();

        super.returnToPreviousView(cardLayout, cardPane);
    }

    @Override
    public void moveToNextView(CardLayout cardLayout, JPanel cardPane) {
        for (Lobby l : this.lobbies.values()) {
            l.getSelect().setBackground(Color.YELLOW);
        }
        this.join.setVisible(false);
        super.moveToNextView(cardLayout, cardPane);
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        for (Lobby lobby : this.lobbies.values()) {
            if (source == lobby.getSelect()) {
                for (Lobby l : this.lobbies.values()) {
                    l.getSelect().setBackground(Color.YELLOW);
                }
                lobby.getSelect().setBackground(Color.GREEN);
                this.join.setVisible(true);
            }
        }

        if (source == this.create) {
            String response = ConnectionHandler.sendRequest("CREATE_LOBBY_@");
            System.out.println(Arrays.toString(response.split("_")));
        }
    }


    private class UpdateLobbyInfo extends SwingWorker<Void, String> {

        @Override
        protected Void doInBackground() {
            for (String response = ConnectionHandler.sendRequest("GET_LOBBIES_@");
                 !isCancelled() && response != null;
                 response = ConnectionHandler.sendRequest("GET_LOBBIES_@")) {

                publish(response);
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ignore) {
                }
            }
            return null;
        }

        @Override
        protected void process(List<String> chunks) {
            String response = chunks.get(chunks.size() - 1);
            List<String> split;
            if (response.contains("RESPONSE_LOBBIES_")) {
                split = new ArrayList<>(List.of(response.split("_")));
                System.out.println(split);
                int count = Integer.parseInt(split.get(3));
                for (int i = 0; i < count; i++) {
                    String number = split.get(5 + 4 * i);
                    String players = split.get(7 + 4 * i);

                    System.out.println(number + "  " + players);

                    Lobby lobby = lobbies.get(number);
                    if (lobby != null) {
                        lobby.updatePlayersNumber(players);
                    } else {
                        Lobby l = new Lobby(players);
                        lobbies.put(number, l);
                        l.getSelect().addActionListener(listener);
                        lobbyPanel.add(l);
                        lobbyPanel.revalidate();
                        validate();
                    }
                    System.out.println(lobbies);
                }


            }
        }
    }
}
