package gui.view;

import gui.helpers.Lobby;
import tools.ConnectionHandler;
import tools.PropertiesHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class LobbyView extends MyView implements ActionListener {

    private final JPanel cardPane;
    private final CardLayout cardLayout;

    private final ActionListener listener = this;
    private JLabel title;
    private Map<String, Lobby> lobbies;
    private JPanel buttonPanel;
    private JPanel lobbyPanel;

    private JLabel nickname;

    private JButton join;
    private JButton create;
    private JButton cancel;

    private UpdateLobbyInfo updater;

    private Lobby selectedLobby;

    public LobbyView(CardLayout cardLayout, JPanel cardPane) {
        this.cardLayout = cardLayout;
        this.cardPane = cardPane;

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setComponents();
        this.addComponents();
    }

    private void setComponents() {
        this.viewName = "LobbyView";
        this.nextViewName = "VoteView";
        this.previousViewName = "StartView";
        this.title = new JLabel("Lobbies");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        //title.setAlignmentY(Component.TOP_ALIGNMENT);
        title.setFont(new Font("Verdana", Font.BOLD, 80));
        title.setBackground(new Color(1.0f, 1.0f, 1.0f, 0.5f));
        title.setForeground(Color.GREEN);
        title.setOpaque(true);

        this.nickname = new JLabel();
        nickname.setAlignmentX(Component.CENTER_ALIGNMENT);
        nickname.setFont(new Font("Arial", Font.ITALIC, 25));
        nickname.setForeground(Color.BLACK);

        this.lobbies = new HashMap<>();

        this.join = new JButton("Join lobby");
        this.join.setVisible(false);
        this.nextViewButton = this.join;

        this.create = new JButton("Create lobby");
        this.create.addActionListener(this);
        this.secondaryNextViewButton = this.create;

        this.cancel = new JButton("Leave game");
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
        //add(Box.createVerticalGlue());
        add(this.nickname);
        add(Box.createVerticalGlue());
        add(this.lobbyPanel);
        add(Box.createVerticalGlue());
        add(this.buttonPanel);
        add(Box.createVerticalGlue());
    }


    @Override
    public void onShowAction() {
        this.lobbies.clear();
        this.lobbyPanel.removeAll();
        this.selectedLobby = null;

        for (Lobby l : this.lobbies.values()) {
            l.getSelect().setBackground(Color.YELLOW);
        }
        this.join.setVisible(false);

        String nick = PropertiesHandler.getProperty("nickname");
        this.nickname.setText("Hi " + nick + ", join or create the game:");


        this.updater = new UpdateLobbyInfo();
        this.updater.execute();
        System.out.println("updater started");

    }

    @Override
    public void returnToPreviousView(CardLayout cardLayout, JPanel cardPane) {
        this.updater.cancel(true);

        ConnectionHandler.endConnection();

        super.returnToPreviousView(cardLayout, cardPane);
    }

    @Override
    public boolean moveToNextView(CardLayout cardLayout, JPanel cardPane) {
        this.updater.cancel(true);

        int nr = this.selectedLobby.getNumber();
        String response = ConnectionHandler.sendRequest(
                "LOBBY_JOIN_" + nr + "_@", "lobbyJoin");
        if (response == null) {
            this.returnToPreviousView(cardLayout, cardPane);
            return false;
        }

        String[] split = response.split("_");
        System.out.println(Arrays.toString(split));
        if ("SUCCESS".equals(split[split.length - 2])) {
            return super.moveToNextView(cardLayout, cardPane);
        } else {
            System.out.println("LOBBY JOIN FAILURE");
            return false;
        }

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
                this.selectedLobby = lobby;
                this.join.setVisible(true);
            }
        }

        if (source == this.create) {
            this.updater.cancel(true);
            String response = ConnectionHandler.sendRequest(
                    "LOBBY_CREATE_@", "lobbyCreate");
            if (response == null) {
                returnToPreviousView(cardLayout, cardPane);
            } else {
                String[] split = response.split("_");
                System.out.println(Arrays.toString(split));
                if ("SUCCESS".equals(split[split.length - 2])) {
                    cardLayout.show(cardPane, this.nextViewName);
                } else {
                    System.out.println("LOBBY CREATE FAILURE");
                }
            }


        }
    }


    private class UpdateLobbyInfo extends SwingWorker<Void, String> {

        @Override
        protected Void doInBackground() {

            publish(ConnectionHandler.sendRequest("GET_LOBBIES_@", "lobbiesEntry"));

            while (!isCancelled()) {
                try {
                    publish(ConnectionHandler.responseTable.get("lobbies").messages.poll(100, TimeUnit.SECONDS));
                } catch (InterruptedException ignored) {
                }
            }
            return null;
        }

        @Override
        protected void process(List<String> chunks) {
            String response = chunks.get(chunks.size() - 1);
            System.out.println("lobbies message: " + response);
            List<String> split;
            split = new ArrayList<>(List.of(response.split("_")));
            int count = Integer.parseInt(split.get(3));
            lobbies.clear();
            lobbyPanel.removeAll();
            lobbyPanel.revalidate();
            validate();
            for (int i = 0; i < count; i++) {
                String number = split.get(5 + 4 * i);
                String players = split.get(7 + 4 * i);

                Lobby l = new Lobby(number, players);
                lobbies.put(number, l);
                l.getSelect().addActionListener(listener);
                lobbyPanel.add(l);
            }
            lobbyPanel.revalidate();
            validate();
        }
    }
}

