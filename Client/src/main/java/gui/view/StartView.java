package gui.view;

import tools.PropertiesHandler;
import tools.Tools;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class StartView extends MyView implements ActionListener, FocusListener {

    private JLabel title;

    private JLabel nicknameLabel;
    private JLabel nicknameTaken;
    private JTextField nickname;

    private JButton connect;
    private JButton exit;


    public StartView() {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        setComponents();
        addComponents();
    }

    private void setComponents() {
        this.viewName = "StartView";
        this.nextViewName = "LoadingView";
        this.title = new JLabel("Find all words!");


        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(new Font("Bradley Hand ITC", Font.BOLD, 120));
        title.setBackground(new Color(1.0f, 1.0f, 1.0f, 0.5f));
        title.setForeground(Color.RED);
        title.setOpaque(true);

        this.nicknameTaken = new JLabel("This nick is already used!");
        this.nicknameTaken.setVisible(false);
        this.nicknameTaken.setFont(new Font("Arial", Font.BOLD, 20));
        this.nicknameTaken.setForeground(Color.RED);


        this.nicknameLabel = new JLabel("nickname:");
        this.nicknameLabel.setForeground(Color.BLACK);
        this.nicknameLabel.setFont(new Font("Arial", Font.ITALIC, 20));
        this.nicknameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        this.nickname = new JTextField();
        this.nickname.setForeground(Color.BLACK);
        this.nickname.setFont(new Font("Arial", Font.ITALIC, 20));
        this.nickname.setMaximumSize(new Dimension(200, 40));
        this.nickname.addFocusListener(this);

        this.connect = Tools.createButton("Connect to server", Color.BLUE);
        this.connect.addActionListener(this);
        this.connect.setActionCommand("Connect");
        this.nextViewButton = this.connect;

        this.exit = Tools.createButton("Exit", Color.RED);
        this.exit.addActionListener(this);

    }

    private void addComponents() {
        add(Box.createVerticalGlue());
        add(this.title);
        add(Box.createVerticalGlue());
        add(this.connect);
        add(Box.createVerticalGlue());
        add(this.nicknameLabel);
        add(this.nickname);
        add(Box.createVerticalGlue());
        add(this.exit);
        add(Box.createVerticalGlue());

    }

    @Override
    public boolean moveToNextView(CardLayout cardLayout, JPanel cardPane) {
        if (this.nickname.getText().length() < 4) {
            this.nickname.setBackground(Color.RED);
            return false;
        } else {
            PropertiesHandler.setProperty("nickname", this.nickname.getText());
            PropertiesHandler.saveProperties();
            cardLayout.show(cardPane, this.nextViewName);
            return true;
        }

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        Object source = e.getSource();

        if (source == this.exit) {
            System.exit(0);
        }

    }

    @Override
    public void focusGained(FocusEvent e) {
        Object source = e.getSource();

        if (source == this.nickname) {
            this.nickname.setBackground(Color.WHITE);
        }
    }

    @Override
    public void onShowAction() {
    }

    @Override
    public void focusLost(FocusEvent e) {

    }

}
