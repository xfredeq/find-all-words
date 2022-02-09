package gui.view;

import gui.helpers.ComparePoints;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class Scoreboard {


    private final JDialog dialog;
    private JLabel title;
    private JLabel desc;
    private JButton cancel;


    public Scoreboard(Map<String, Integer> treeMap) {
        this.dialog = new JDialog();
        this.dialog.setLayout(new GridBagLayout());
        addComponents(treeMap);
    }

    public void addComponents(Map<String, Integer> treeMap) {
        this.dialog.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
        this.dialog.setBounds(500, 300, 500, 400);
        this.title = new JLabel("Final scoreboard.", SwingConstants.CENTER);
        this.title.setFont(new Font("Monospaced", Font.PLAIN, 30));
        this.cancel = new JButton("Return");
        this.desc = new JLabel("Player | Score");
        this.desc.setFont(new Font("Arial", Font.PLAIN, 20));

        this.cancel.addActionListener(e -> dialog.setVisible(false));

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 1;
        constraints.gridy = 0;
        this.dialog.add(this.title, constraints);
        constraints.gridy = 1;
        this.dialog.add(this.desc, constraints);
        for (Map.Entry<String, Integer> string : ComparePoints.entriesSortedByValues(treeMap)) {
            JLabel l = new JLabel(string.getKey() + "   " + string.getValue());
            l.setFont(new Font("Arial", Font.PLAIN, 20));
            l.setAlignmentX(Component.CENTER_ALIGNMENT);

            constraints.weighty = 3;
            constraints.gridy += 1;
            dialog.add(l, constraints);
        }

        this.dialog.add(Box.createVerticalGlue());
        this.cancel.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.cancel.setMaximumSize(new Dimension(80, 40));
        this.cancel.setPreferredSize(new Dimension(80, 40));

        constraints.gridy = 12;
        this.dialog.add(this.cancel, constraints);
        this.dialog.setVisible(true);
    }


}
