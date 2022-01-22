package put.poznan.GUI;

import put.poznan.tools.MyView;
import put.poznan.tools.Tools;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StartView extends MyView implements ActionListener {

    private JLabel title;

    private JButton connect;
    private JButton exit;

    public StartView() {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        setComponents();
        addComponents();
    }

    private void setComponents() {
        this.viewName = "StartView";
        this.nextViewName = "ConnectingView";
        this.title = new JLabel("Find all Words!");


        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(new Font("Bradley Hand ITC", Font.BOLD, 120));
        title.setBackground(new Color(1.0f, 1.0f, 1.0f, 0.5f));
        title.setForeground(Color.RED);
        title.setOpaque(true);


        this.connect = Tools.createButton("Connect to server", Color.BLUE);
        this.nextViewButton = this.connect;

        this.exit = Tools.createButton("Exit", Color.RED);
        exit.addActionListener(this);

    }

    private void addComponents() {
        add(Box.createVerticalGlue());
        add(this.title);
        add(Box.createVerticalGlue());
        add(this.connect);
        add(Box.createVerticalGlue());
        add(this.exit);
        add(Box.createVerticalGlue());

    }


    @Override
    public void onShowAction() {

    }


    @Override
    public void actionPerformed(ActionEvent e) {

        Object source = e.getSource();

        if (source == this.exit) {
            System.exit(0);
        }
    }
}
