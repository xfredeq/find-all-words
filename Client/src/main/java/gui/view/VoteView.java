package gui.view;

import gui.helpers.GameTimer;
import tools.ConnectionHandler;
import tools.PropertiesHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class VoteView extends MyView implements ActionListener {
    private CardLayout cardLayout;
    private JPanel cardPane;

    private JLabel title;
    private JPanel buttonPanel;

    private JPanel choicePanel;

    private JButton fakeButton;

    private JPanel playersPanel;
    private JLabel playersListLabel;

    private JButton vote;
    private JButton cancel;

    private JLabel timerLabel;
    private GameTimer timer;

    private UpdatePlayersList updatePlayersList;
    private UpdateTimer updateTimer;

    public VoteView(CardLayout cardLayout, JPanel cardPane) {
        this.cardLayout = cardLayout;
        this.cardPane = cardPane;
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setComponents();
        this.addComponents();
    }

    private void setComponents() {
        this.viewName = "VoteView";
        this.nextViewName = "GameView";
        this.previousViewName = "LobbyView";
        this.title = new JLabel("Vote to start game");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        title.setFont(new Font("Verdana", Font.BOLD, 80));
        title.setBackground(new Color(1.0f, 1.0f, 1.0f, 0.5f));
        title.setForeground(Color.BLUE);
        title.setOpaque(true);


        this.timerLabel = new JLabel("Waiting for players...", SwingConstants.CENTER);
        this.timerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        this.timer = new GameTimer();

        this.playersListLabel = new JLabel("Players in lobby:", SwingConstants.CENTER);
        this.playersListLabel.setAlignmentX(Component.CENTER_ALIGNMENT);


        this.playersPanel = new JPanel();
        this.playersPanel.setLayout(new GridLayout(6, 1));
        this.playersPanel.setPreferredSize(new Dimension(400, 100));
        this.playersPanel.setMaximumSize(new Dimension(400, 150));
        this.playersPanel.setAlignmentX(Component.CENTER_ALIGNMENT);


        this.choicePanel = new JPanel();
        this.choicePanel.setLayout(new GridLayout(1, 3));
        this.choicePanel.setPreferredSize(new Dimension(400, 60));
        this.choicePanel.setMaximumSize(new Dimension(400, 60));


        this.vote = new JButton("Vote");
        this.vote.addActionListener(this);
        this.vote.setActionCommand("Vote");

        this.cancel = new JButton("leave lobby");
        this.previousViewButton = this.cancel;

        this.fakeButton = new JButton();
        this.nextViewButton = this.fakeButton;

        this.buttonPanel = new JPanel();
        this.buttonPanel.setLayout(new GridLayout(1, 3));
        this.buttonPanel.setPreferredSize(new Dimension(400, 60));
        this.buttonPanel.setMaximumSize(new Dimension(400, 60));

        this.buttonPanel.add(this.vote);
        this.buttonPanel.add(Box.createHorizontalGlue());
        this.buttonPanel.add(this.cancel);


    }

    private void addComponents() {
        add(Box.createVerticalGlue());
        add(this.title);
        add(Box.createVerticalGlue());

        add(this.playersListLabel);
        add(Box.createVerticalGlue());
        add(this.playersPanel);
        add(Box.createVerticalGlue());
        add(this.timerLabel);
        add(Box.createVerticalGlue());
        add(this.timer);
        add(Box.createVerticalGlue());
        add(this.choicePanel);
        add(Box.createVerticalGlue());
        add(this.buttonPanel);
        add(Box.createVerticalGlue());
    }


    @Override
    public void onShowAction() {
        System.out.println("List of nicks updater started0");
        this.timer.setVisible(false);

        this.vote.setEnabled(true);
        this.updatePlayersList = new UpdatePlayersList();
        this.updatePlayersList.execute();
        this.updateTimer = new UpdateTimer();
        this.updateTimer.execute();
        System.out.println("List of nicks updater started");
    }


    @Override
    public void returnToPreviousView(CardLayout cardLayout, JPanel cardPane) {
        String response = ConnectionHandler.sendRequest("LOBBY_LEAVE_@", "lobbyLeave");
        if (response == null) {
            this.shutdownAll();
            ConnectionHandler.endConnection();
            this.cardLayout.show(this.cardPane, "StartView");
        }

        this.shutdownAll();
        super.returnToPreviousView(cardLayout, cardPane);
    }

    @Override
    protected void shutdownAll() {
        this.updatePlayersList.cancel(true);
        this.updateTimer.cancel(true);
        this.timer.stop();
    }


    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getActionCommand().equals("Vote")) {
            String response = ConnectionHandler.sendRequest("LOBBY_VOTE_@", "selfVote");
            if (response == null) {
                this.shutdownAll();
                ConnectionHandler.endConnection();
                this.cardLayout.show(this.cardPane, "StartView");
            }
            System.out.println("voted");
        }

    }

    private class UpdateTimer extends SwingWorker<Void, String> {

        @Override
        protected Void doInBackground() {
            while (!isCancelled()) {
                try {
                    publish(ConnectionHandler.responseTable.get("timerStart").messages.poll(100, TimeUnit.SECONDS));
                } catch (InterruptedException ignored) {
                }

                try {
                    publish(ConnectionHandler.responseTable.get("gameStart").messages.poll(100, TimeUnit.SECONDS));
                } catch (InterruptedException ignored) {
                }
            }
            return null;
        }

        @Override
        protected void process(List<String> chunks) {
            String response = chunks.get(chunks.size() - 1);
            if (response == null) {
                shutdownAll();
                ConnectionHandler.endConnection();
                cardLayout.show(cardPane, "StartView");
                return;
            }
            List<String> split;
            split = new ArrayList<>(List.of(response.split("_")));
            System.out.println(split);
            if (response.matches("NOTIFICATION_START_COUNTDOWN_[0-9]+")) {
                timer.setTime(Integer.parseInt(split.get(3)) * 1000);
                timer.start();
                vote.setEnabled(false);
                timerLabel.setText("Game starts in...");

                timer.setVisible(true);
            }
            if (response.matches("NOTIFICATION_START_GAME_[0-9]+")) {
                timer.stop();
                timer.setTime(Integer.parseInt(PropertiesHandler.getProperty("game_duration")));
                timer.setCurrentTime(new JLabel(""));
                PropertiesHandler.setProperty("game_duration", split.get(3));
                PropertiesHandler.saveProperties();
                fakeButton.doClick();
            }

        }

    }

    private class UpdatePlayersList extends SwingWorker<Void, String> {

        @Override
        protected Void doInBackground() {
            String response = ConnectionHandler.sendRequest("LOBBY_PLAYERS_@", "playersVotes");
            System.out.println("RESP: " + response);
            publish(response);
            ConnectionHandler.responseTable.get("playersVotes").messages.clear();
            while (!isCancelled()) {
                try {
                    publish(ConnectionHandler.responseTable.get("playersVotes").messages.poll(100, TimeUnit.SECONDS));
                } catch (InterruptedException ignored) {
                }
            }
            return null;
        }


        @Override
        protected void process(List<String> chunks) {
            String response = chunks.get(chunks.size() - 1);
            if (response == null) {
                shutdownAll();
                ConnectionHandler.endConnection();
                cardLayout.show(cardPane, "StartView");
                return;
            }
            List<String> split;
            playersPanel.removeAll();
            playersPanel.revalidate();
            playersPanel.repaint();
            validate();
            split = new ArrayList<>(List.of(response.split("_")));
            int count = Integer.parseInt(split.get(3));
            for (int i = 0; i < count; i++) {
                String nick = split.get(4 + i * 2);
                String vote = split.get(5 + i * 2);
                JLabel l = new JLabel(nick, SwingConstants.CENTER);
                if ("1".equals(vote)) {
                    l.setBackground(Color.GREEN);
                } else {
                    l.setBackground(Color.RED);
                }

                l.setAlignmentX(Component.CENTER_ALIGNMENT);
                l.setOpaque(true);
                playersPanel.add(l);

            }
            playersPanel.revalidate();
            validate();
        }
    }

}
