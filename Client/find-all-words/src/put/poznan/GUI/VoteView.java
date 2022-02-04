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

public class VoteView extends MyView implements ActionListener {

    private JLabel title;
    private JPanel buttonPanel;

    private JPanel choicePanel;
    private JRadioButton voteYes;
    private JRadioButton voteNo;

    private JPanel playersPanel;

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
        this.title = new JLabel("Voting...");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        title.setFont(new Font("Verdana", Font.BOLD, 80));
        title.setBackground(new Color(1.0f, 1.0f, 1.0f, 0.5f));
        title.setForeground(Color.BLUE);
        title.setOpaque(true);


        this.playersPanel = new JPanel();
        this.playersPanel.setLayout(new GridLayout(6, 1));
        this.playersPanel.setPreferredSize(new Dimension(400, 100));
        this.playersPanel.setMaximumSize(new Dimension(400, 150));
        this.playersPanel.setAlignmentX(Component.CENTER_ALIGNMENT);


        this.voteYes = new JRadioButton("Yes");
        this.voteYes.setActionCommand("Yes");
        this.voteYes.addActionListener(this);

        this.voteNo = new JRadioButton("No");
        this.voteNo.setActionCommand("No");
        this.voteNo.addActionListener(this);

        this.choicePanel = new JPanel();
        this.choicePanel.setLayout(new GridLayout(1, 3));
        this.choicePanel.setPreferredSize(new Dimension(400, 60));
        this.choicePanel.setMaximumSize(new Dimension(400, 60));


        this.choicePanel.add(this.voteYes);
        this.choicePanel.add(Box.createHorizontalGlue());
        this.choicePanel.add(this.voteNo);


        this.vote = new JButton("Vote");
        this.nextViewButton = this.vote;
        this.cancel = new JButton("cancel");
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
        add(this.playersPanel);
        add(Box.createVerticalGlue());
        add(this.choicePanel);
        add(Box.createVerticalGlue());
        add(this.buttonPanel);
        add(Box.createVerticalGlue());
    }


    @Override
    public void onShowAction() {

        this.updatePlayersList = new UpdatePlayersList();
        this.updatePlayersList.execute();
        System.out.println("List of nicks updater started");
    }


    @Override
    public void returnToPreviousView(CardLayout cardLayout, JPanel cardPane) {
        //#TODO break connection

        super.returnToPreviousView(cardLayout, cardPane);
    }


    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getActionCommand().equals("Yes")) {
            if (this.voteYes.isSelected()) {
                this.voteNo.setSelected(false);
            }
        } else if (ae.getActionCommand().equals("No")) {
            if (this.voteNo.isSelected()) {
                this.voteYes.setSelected(false);
            }
        }


    }

    private class UpdatePlayersList extends SwingWorker<Void, String> {

        @Override
        protected Void doInBackground() {
            for (String response = ConnectionHandler.sendRequest("GET_PLAYERS_@");
                 !isCancelled() && response != null;
                 response = ConnectionHandler.sendRequest("GET_PLAYERS_@")) {
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
            playersPanel.removeAll();
            split = new ArrayList<>(List.of(response.split("_")));
            int count = Integer.parseInt(split.get(3));
            for (int i = 0; i < count; i++) {
                String nick = split.get(4 + i * 2);
                String color = split.get(5 + i * 2);
                Color c = null;
                try {
                    Field field = Class.forName("java.awt.Color").getField(color);
                    c = (Color) field.get(null);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                JLabel l = new JLabel(nick);
                l.setBackground(c);
                l.setAlignmentX(Component.CENTER_ALIGNMENT);
                l.setOpaque(true);
                playersPanel.add(l);

            }
            playersPanel.revalidate();
            validate();
        }
    }

}
