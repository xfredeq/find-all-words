package put.poznan.GUI;

import put.poznan.networking.ConnectionHandler;
import put.poznan.tools.MyView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.lang.*;

public class GameView extends MyView implements ActionListener {

    private final GridBagConstraints c;

    private JPanel enterPanel;
    private JLabel enterTitle;
    private JTextField enterTextField;
    private JPanel submitPanel;


    private JPanel wordsPanel;
    private JLabel wordsTitle;
    private JPanel playersPanel;
    private JLabel playersTitle;

    private JPanel timerPanel;
    private JPanel lettersPanel;
    private JPanel lettersTable;

    private JButton submit;

    private JLabel submitLabel;
    private JLabel timerLabel;
    private JLabel timeElapsed;
    private JLabel letterLabel;

    private JPanel letters;

    private UpdateData updateData;

    private ArrayList<Character> lettersList = new ArrayList<Character>(List.of(
            'w', 'o', 'r', 'd', 'w', 'o',
            'r', 'd', 'a', 'a', 'b', 'b',
            'b', 'b', 'b', 'b', 'b', 'b',
            'b', 'b', 'b', 'b', 'b', 'b',
            'b', 'b', 'b', 'b', 'b', 'b',
            'b', 'b', 'b', 'b', 'b', 'b'));


    public GameView() {
        this.setLayout(new GridBagLayout());
        this.c = new GridBagConstraints();

        this.setComponents();
        this.addComponents();

    }

    private void addLetters(){
        int k = 0;
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 6; j++) {

                GridBagConstraints l = new GridBagConstraints();
                l.gridx = i;
                l.gridy = j;
                l.weightx = 1;
                l.weighty = 1;


                System.out.println(this.lettersList);
                this.letters.add(new JLabel(this.lettersList.get(k).toString()), l);
                k++;
                this.letters.revalidate();

                this.lettersTable.add(new JScrollPane(this.letters), BorderLayout.CENTER);
            }

        }
    }

    private void setComponents() {
        this.viewName = "GameView";
        this.nextViewName = "LobbyView";
        this.previousViewName = "LobbyView";

        this.wordsPanel = new JPanel();
        this.wordsPanel.setBackground(new Color(255, 199, 211));
        this.wordsPanel.setMaximumSize(new Dimension(180, 40));
        this.wordsPanel.setPreferredSize(new Dimension(80, 40));
        this.wordsPanel.setLayout(new BoxLayout(this.wordsPanel, BoxLayout.Y_AXIS));

        this.wordsTitle = new JLabel("Words guessed:", SwingConstants.CENTER);
        this.wordsTitle.setAlignmentX(Box.CENTER_ALIGNMENT);
        this.wordsTitle.setMaximumSize(new Dimension(180, 40));
        this.wordsTitle.setPreferredSize(new Dimension(180, 40));
        this.wordsTitle.setFont(new Font("Arial", Font.BOLD, 20));
        this.wordsTitle.setBackground(new Color(255, 199, 211));

        this.playersPanel = new JPanel();
        this.playersPanel.setBackground(new Color(172, 240, 248));
        this.playersPanel.setLayout(new BoxLayout(this.playersPanel, BoxLayout.Y_AXIS));

        this.playersTitle = new JLabel("Opponents:", SwingConstants.CENTER);
        this.playersTitle.setAlignmentX(Box.CENTER_ALIGNMENT);
        this.playersTitle.setMaximumSize(new Dimension(180, 40));
        this.playersTitle.setPreferredSize(new Dimension(120, 40));
        this.playersTitle.setFont(new Font("Arial", Font.BOLD, 20));
        this.playersTitle.setBackground(new Color(172, 240, 248));

        this.enterPanel = new JPanel();
        this.enterPanel.setBackground(new Color(134, 159, 255));
        this.enterPanel.setAlignmentX(Box.CENTER_ALIGNMENT);
        this.enterPanel.setLayout(new BoxLayout(this.enterPanel, BoxLayout.Y_AXIS));


        this.enterTitle = new JLabel("Enter your word...", SwingConstants.CENTER);
        this.enterTitle.setAlignmentX(Box.CENTER_ALIGNMENT);
        this.enterTitle.setMaximumSize(new Dimension(360, 40));
        this.enterTitle.setPreferredSize(new Dimension(180, 40));
        this.enterTitle.setFont(new Font("Arial", Font.BOLD, 20));
        this.enterTitle.setBackground(new Color(134, 159, 255));


        this.enterTextField = new JTextField("Enter word here...");
        this.enterTextField.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.enterTextField.setMaximumSize(new Dimension(200, 40));
        this.enterTextField.setPreferredSize(new Dimension(200, 40));
        //this.enterTextField.add();


        this.submitPanel = new JPanel();
        this.submitPanel.setBackground(new Color(148, 148, 148));
        this.submitPanel.setLayout(new BoxLayout(this.submitPanel, BoxLayout.Y_AXIS));

        this.submit = new JButton("Submit");
        this.submit.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.submit.setMaximumSize(new Dimension(80, 40));
        this.submit.setPreferredSize(new Dimension(80, 40));
        this.submit.setActionCommand("Submit");
        this.submit.addActionListener(this);

        this.submitLabel = new JLabel("Submit the word!");
        this.submitLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.submitLabel.setMaximumSize(new Dimension(180, 40));
        this.submitLabel.setPreferredSize(new Dimension(80, 40));
        this.submitLabel.setFont(new Font("Arial", Font.BOLD, 20));
        this.submitLabel.setBackground(new Color(148, 148, 148));
        this.submitLabel.setOpaque(true);

        this.timerPanel = new JPanel();
        this.timerPanel.setBackground(new Color(221, 207, 255));
        this.timerPanel.setMaximumSize(new Dimension(180, 40));
        this.timerPanel.setPreferredSize(new Dimension(180, 40));
        this.timerPanel.setLayout(new BoxLayout(this.timerPanel, BoxLayout.Y_AXIS));

        this.timerLabel = new JLabel("Remaining time:", SwingConstants.CENTER);
        this.timerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.timerLabel.setMaximumSize(new Dimension(180, 40));
        this.timerLabel.setPreferredSize(new Dimension(80, 40));
        this.timerLabel.setFont(new Font("Monospaced", Font.BOLD, 20));
        this.timerPanel.setBackground(new Color(221, 207, 255));

        this.timeElapsed = new JLabel(LocalTime.now().toString(), SwingConstants.CENTER);
        this.timeElapsed.setMaximumSize(new Dimension(180, 40));
        this.timeElapsed.setPreferredSize(new Dimension(80, 40));
        this.timeElapsed.setFont(new Font("Monospaced", Font.BOLD, 20));
        this.timeElapsed.setBackground(new Color(221, 207, 255));

        this.lettersPanel = new JPanel(new BorderLayout(8, 8));
        this.lettersPanel.setBackground(new Color(207, 206, 220));
        this.lettersPanel.setMaximumSize(new Dimension(180, 40));
        this.lettersPanel.setPreferredSize(new Dimension(180, 40));

        this.lettersTable = new JPanel(new BorderLayout(7, 7));
        this.lettersTable.setBackground(new Color(159, 158, 171));
        this.lettersPanel.setMaximumSize(new Dimension(180, 40));
        this.lettersPanel.setPreferredSize(new Dimension(180, 40));

        this.letterLabel = new JLabel("Available letters:", SwingConstants.CENTER);
        this.letterLabel.setMaximumSize(new Dimension(200, 40));
        this.letterLabel.setPreferredSize(new Dimension(80, 40));
        this.letterLabel.setFont(new Font("Monospaced", Font.BOLD, 14));
        this.letterLabel.setBackground(new Color(207, 206, 220));

        this.letters = new JPanel(new GridBagLayout());
        this.letters.setBackground(new Color(207, 206, 220));
        this.letters.setMaximumSize(new Dimension(180, 180));
        this.letters.setPreferredSize(new Dimension(180, 40));


    }

    private void addComponents() {

        this.submitPanel.add(Box.createVerticalGlue());
        this.submitPanel.add(this.submitLabel);
        this.submitPanel.add(Box.createVerticalGlue());
        this.submit.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.submitPanel.add(this.submit);
        this.submitPanel.add(Box.createVerticalGlue());

        this.timerPanel.add(Box.createVerticalGlue());
        this.timerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.timerPanel.add(this.timerLabel);
        this.timerPanel.add(Box.createVerticalGlue());
        this.timeElapsed.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.timerPanel.add(this.timeElapsed);
        this.timerPanel.add(Box.createVerticalGlue());


        this.lettersPanel.add(this.letterLabel, BorderLayout.NORTH);

        this.lettersPanel.add(Box.createVerticalGlue());
        this.lettersPanel.add(this.lettersTable);

        this.enterPanel.add(Box.createVerticalGlue());
        this.enterPanel.add(this.enterTitle);
        this.enterPanel.add(Box.createVerticalGlue());
        this.enterTextField.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.enterPanel.add(this.enterTextField);
        this.enterPanel.add(Box.createVerticalGlue());


        this.wordsPanel.add(this.wordsTitle);


        this.playersPanel.add(this.playersTitle);

        addLetters();


        this.c.fill = GridBagConstraints.BOTH;
        this.c.gridheight = 2;
        this.c.weightx = 1;
        this.c.weighty = 2;
        this.c.gridx = 0;
        this.c.gridy = 0;
        this.add(wordsPanel, this.c);

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
        this.add(playersPanel, this.c);


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

        this.c.fill = GridBagConstraints.BOTH;
        this.c.gridwidth = 1;
        this.c.gridheight = 1;
        this.c.weightx = 1;
        this.c.weighty = 1;
        this.c.gridx = 1;
        this.c.gridy = 0;
        this.add(timerPanel, this.c);

        this.c.fill = GridBagConstraints.BOTH;
        this.c.gridwidth = 1;
        this.c.gridheight = 1;
        this.c.weightx = 1;
        this.c.weighty = 1;
        this.c.gridx = 1;
        this.c.gridy = 1;
        this.add(lettersPanel, this.c);
    }


    @Override
    public void onShowAction() {

        //ConnectionHandler.createSocket();
        //this.updateData = new UpdateData();
        //this.updateData.execute();
        System.out.println("Game data updated");
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        String response;
        if (ae.getActionCommand().equals("Submit")) {
            response = ConnectionHandler.sendRequest(this.enterTextField.getText());

            if (response.equals("word proper")) {

                wordsPanel.add(new JLabel(enterTextField.getText()));
                for (int i = 0; i < enterTextField.getText().length(); i++) {
                    System.out.println("letter removed" + enterTextField.getText().charAt(i));
                    this.lettersList.remove((Character) enterTextField.getText().charAt(i));
                    this.lettersList.add('x');
                    System.out.println(this.lettersList);
                }
                this.letters.removeAll();
                this.lettersTable.removeAll();
                addLetters();

                System.out.println("word proper");
                wordsPanel.revalidate();
            }

        }
    }

    private class UpdateData extends SwingWorker<Void, String> {

        @Override
        protected Void doInBackground() {
            for (String response = ConnectionHandler.getMessage();
                 !isCancelled() && response != null;
                 response = ConnectionHandler.getMessage()) {
                publish(response);

            }
            return null;
        }

        @Override
        protected void process(List<String> chunks) {
            String response = chunks.get(chunks.size() - 1);
            if (response.equals("word proper")) {
                wordsPanel.add(new JLabel(enterTextField.getText()));
                System.out.println("word proper");
                letters.removeAll();
                lettersTable.removeAll();
                addLetters();
                lettersPanel.revalidate();

            } else if (response.equals("")) {

                playersPanel.add(new JLabel());
            }
            wordsPanel.revalidate();
            validate();
        }

    }
}
