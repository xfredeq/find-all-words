package gui.helpers;


import gui.view.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class Window extends JFrame implements ActionListener {

    private final JPanel cardPane;
    private final CardLayout cardLayout;
    private final ArrayList<MyView> views;

    private final JMenuBar menuBar;
    private JMenu menuFile, menuSettings, menuHelp;
    private JMenuItem close, connectionSettings, about;

    private ConnectionDialog connectionDialog;
    private int windowWidth;
    private int windowHeight;

    public Window() {


        super("FindAllWords");
        System.out.println("window " + Thread.currentThread().getName());
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setWindowWidth(1024);
        this.setWindowHeight(720);
        this.setSize(new Dimension(windowWidth, windowHeight));
        this.setLocationRelativeTo(null);


        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            SwingUtilities.updateComponentTreeUI(this);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        this.menuBar = new JMenuBar();
        setMyMenuBar();

        this.cardLayout = new CardLayout();
        this.views = new ArrayList<>();
        this.cardPane = new JPanel(this.cardLayout);

        this.cardPane.setBackground(Color.LIGHT_GRAY);

        this.addViews();

        this.add(this.cardPane);
        this.cardLayout.show(cardPane, "put.poznan.GUI.StartView");

        this.setVisible(true);
    }

    public void setWindowWidth(int windowWidth) {
        this.windowWidth = windowWidth;
    }

    public void setWindowHeight(int windowHeight) {
        this.windowHeight = windowHeight;
    }

    private void setMyMenuBar() {

        this.menuFile = new JMenu("File");
        this.close = new JMenuItem("Close");
        this.close.addActionListener(this);
        this.close.setAccelerator(KeyStroke.getKeyStroke("ctrl X"));

        this.menuFile.add(this.close);


        this.menuSettings = new JMenu("Settings");
        this.connectionSettings = new JMenuItem("Connection");
        this.connectionSettings.addActionListener(this);

        this.menuSettings.add(this.connectionSettings);


        this.menuHelp = new JMenu("Help");
        this.about = new JMenuItem("About");
        this.about.addActionListener(this);

        this.menuHelp.add(this.about);


        this.menuBar.add(this.menuFile);
        this.menuBar.add(this.menuSettings);
        this.menuBar.add(Box.createHorizontalGlue());
        this.menuBar.add(this.menuHelp);

        this.setJMenuBar(this.menuBar);
    }


    private void addViews() {
        this.views.add(new StartView());
        this.views.add(new LoadingView(this.cardLayout, this.cardPane));
        this.views.add(new LobbyView(this.cardLayout, this.cardPane));
        this.views.add(new VoteView(this.cardLayout, this.cardPane));
        this.views.add(new GameView(this.cardLayout, this.cardPane));
        for (var view : this.views) {
            if (view.getNextViewButton() != null) {
                view.getNextViewButton().addActionListener(this);
            }
            if (view.getSecondaryNextViewButton() != null) {
                view.getSecondaryNextViewButton().addActionListener(this);
            }
            if (view.getPreviousViewButton() != null) {
                view.getPreviousViewButton().addActionListener(this);
            }

            this.cardPane.add(view, view.getViewName());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == this.close) {
            int result = JOptionPane.showConfirmDialog(null, "Are you sure?", "Exit confirmation", JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                dispose();
            }

        } else if (source == this.about) {
            JOptionPane.showMessageDialog(this, """
                    This is an online game called "Find all words".\s
                    The goal is to assemble as many words as possible from letters sent by server.\s
                    Points are awarded for words that are in the system dictionary.\s
                    Penalty is applied when the word is not present in the dictionary.\s
                    Letters are taken away when assembled word is proper.\s
                    """, "About", JOptionPane.INFORMATION_MESSAGE);

        } else if (source == this.connectionSettings) {
            if (this.connectionDialog == null) {
                connectionDialog = new ConnectionDialog(this);
            }
            connectionDialog.setVisible(true);
        }

        for (var view : this.views) {
            MyView nextView = null;

            if (source == view.getNextViewButton()) {
                if (view.getViewName().equals("StartView")) {
                    this.menuSettings.setVisible(false);
                }
                for (var v : this.views) {
                    if (view.getNextViewName().equals(v.getViewName())) {
                        nextView = v;
                        break;
                    }
                }
                System.out.println("moooveee");
                if (view.moveToNextView(this.cardLayout, this.cardPane)) {
                    System.out.println("action");
                    nextView.onShowAction();
                    System.out.println("after action");
                }
            } else if (view.getSecondaryNextViewButton() != null && source == view.getSecondaryNextViewButton()) {
                for (var v : this.views) {
                    if (view.getNextViewName().equals(v.getViewName())) {
                        nextView = v;
                        break;
                    }
                }
                nextView.onShowAction();
            } else if (source == view.getPreviousViewButton()) {
                MyView previousView = null;
                if (view.getViewName().equals("LoadingView") || view.getViewName().equals("LobbyView")) {
                    this.menuSettings.setVisible(true);
                }
                for (var v : this.views) {
                    if (view.getPreviousViewName().equals(v.getViewName())) {
                        previousView = v;
                        break;
                    }
                }
                view.returnToPreviousView(this.cardLayout, this.cardPane);
                previousView.onShowAction();
            }
        }
    }


}
