package put.poznan.GUI;

import put.poznan.networking.ConnectionHandler;
import put.poznan.tools.MyView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import java.util.*;
import java.util.List;
import java.util.Arrays;

public class VoteView extends MyView implements ActionListener {

    private JLabel title;
    private JPanel buttonPanel;

    private JPanel choicePanel;

    private JPanel playersPanel;
    private JLabel playersListLabel;

    private JButton vote;
    private JButton cancel;

    private UpdatePlayersList updatePlayersList;

    VoteView() {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setComponents();
        this.addComponents();
    }

    private void setComponents() {
        this.viewName = "VoteView";
        this.nextViewName = "GameView";
        this.previousViewName = "LobbyView";
        this.title = new JLabel("Vote to start the game");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        title.setFont(new Font("Verdana", Font.BOLD, 80));
        title.setBackground(new Color(1.0f, 1.0f, 1.0f, 0.5f));
        title.setForeground(Color.BLUE);
        title.setOpaque(true);

        this.playersListLabel = new JLabel("Players in lobby:");
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
        add(this.choicePanel);
        add(Box.createVerticalGlue());
        add(this.buttonPanel);
        add(Box.createVerticalGlue());
    }


    @Override
    public void onShowAction() {
        System.out.println("List of nicks updater started0");
        this.updatePlayersList = new UpdatePlayersList();
        this.updatePlayersList.execute();
        System.out.println("List of nicks updater started");
    }


    @Override
    public void returnToPreviousView(CardLayout cardLayout, JPanel cardPane) {
        String response = ConnectionHandler.sendRequest2("LOBBY_LEAVE_@", "lobbyLeave");
        System.out.println(Arrays.toString(response.split("_")));
        super.returnToPreviousView(cardLayout, cardPane);
    }


    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getActionCommand().equals("Vote")) {
            String response = ConnectionHandler.sendRequest2("LOBBY_VOTE_@", "selfVote");
            System.out.println("voted");
        }

    }

    private class UpdatePlayersList extends SwingWorker<Void, String> {

        @Override
        protected Void doInBackground() {
            publish(ConnectionHandler.sendRequest2("GET_PLAYERS_@", "playersVotes"));
            while (!isCancelled()) {
                Object lock = ConnectionHandler.responseTable.get("playersVotes").lock;
                synchronized (lock) {
                    try {
                        lock.wait();
                        publish(ConnectionHandler.responseTable.get("playersVotes").response);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            }
            return null;
        }


        @Override
        protected void process(List<String> chunks) {
            String response = chunks.get(chunks.size() - 1);
            List<String> split;
            playersPanel.removeAll();
            split = new ArrayList<>(List.of(response.split("_")));
            int count = Integer.parseInt(split.get(3));
            for (int i = 0; i < count; i++) {
                String nick = split.get(4 + i * 2);
                String vote = split.get(5 + i * 2);
                Color c = null;
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
