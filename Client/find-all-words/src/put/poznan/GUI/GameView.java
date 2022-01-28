package put.poznan.GUI;

import put.poznan.tools.MyView;
import put.poznan.GUI.Window;

import javax.swing.*;
import java.awt.*;

public class GameView extends MyView {

    private final GridBagConstraints c;

    private JPanel enterPanel;
    private JPanel submitPanel;

    private JPanel wordsList;
    private JPanel playersList;

    private JButton submit;

    private JLabel submitLabel;


    public GameView() {
        this.setLayout(new GridBagLayout());
        this.c = new GridBagConstraints();

        this.setComponents();
        this.addComponents();

    }

    private void setComponents() {
        this.viewName = "GameView";
        this.nextViewName = "LobbyView";
        this.previousViewName = "LobbyView";

        this.wordsList = new JPanel();
        this.wordsList.setBackground(Color.RED);
        this.wordsList.setMaximumSize(new Dimension(180, 40));
        this.wordsList.setPreferredSize(new Dimension(80, 40));

        this.playersList = new JPanel();
        this.playersList.setBackground(Color.GREEN);

        this.enterPanel = new JPanel();
        this.enterPanel.setBackground(Color.BLUE);


        this.submitPanel = new JPanel();
        this.submitPanel.setBackground(Color.YELLOW);
        this.submitPanel.setLayout(new BoxLayout(this.submitPanel, BoxLayout.Y_AXIS));

        this.submit = new JButton("Submit");
        this.submit.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.submit.setMaximumSize(new Dimension(80, 40));
        this.submit.setPreferredSize(new Dimension(80, 40));

        this.submitLabel = new JLabel("Submit the word!");
        this.submitLabel.setAlignmentX(Box.CENTER_ALIGNMENT);
        this.submitLabel.setMaximumSize(new Dimension(180, 40));
        this.submitLabel.setPreferredSize(new Dimension(80, 40));
        this.submitLabel.setFont(new Font("Arial", Font.BOLD, 20));
        this.submitLabel.setBackground(new Color(1.0f, 1.0f, 1.0f, 0.5f));
        this.submitLabel.setOpaque(true);
    }

    private void addComponents() {

        this.submitPanel.add(Box.createVerticalGlue());
        this.submitPanel.add(this.submitLabel);
        this.submitPanel.add(Box.createVerticalGlue());
        this.submit.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.submitPanel.add(this.submit);
        this.submitPanel.add(Box.createVerticalGlue());

        this.c.fill = GridBagConstraints.BOTH;
        this.c.gridheight = 2;
        this.c.weightx = 1;
        this.c.weighty = 2;
        this.c.gridx = 0;
        this.c.gridy = 0;
        this.add(wordsList, this.c);

        this.c.fill = GridBagConstraints.HORIZONTAL;
        this.c.gridheight = 1;
        this.c.weightx = 1;
        this.c.weighty = 1;
        this.c.gridx = 0;
        this.c.gridy = 1;
        this.add(new JPanel(), this.c);


        this.c.fill = GridBagConstraints.BOTH;
        this.c.gridheight = 2;
        this.c.weightx = 1;
        this.c.weighty = 2;
        this.c.gridx = 2;
        this.c.gridy = 0;
        this.add(playersList, this.c);

        this.c.fill = GridBagConstraints.HORIZONTAL;
        this.c.gridheight = 1;
        this.c.weightx = 1;
        this.c.weighty = 1;
        this.c.gridx = 1;
        this.c.gridy = 1;
        this.add(new JPanel(), this.c);

        this.c.fill = GridBagConstraints.BOTH;
        this.c.gridwidth = 2;
        this.c.gridheight = 1;
        this.c.weightx = 2;
        this.c.weighty = 1;
        this.c.gridx = 0;
        this.c.gridy = 2;
        this.add(enterPanel, this.c);

        this.c.fill = GridBagConstraints.BOTH;
        this.c.gridwidth = 1;
        this.c.gridheight = 1;
        this.c.weightx = 0.5;
        this.c.weighty = 1;
        this.c.gridx = 2;
        this.c.gridy = 2;
        this.add(submitPanel, this.c);
    }


    @Override
    public void onShowAction() {

    }
}
