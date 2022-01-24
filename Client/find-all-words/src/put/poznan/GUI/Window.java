package put.poznan.GUI;

import put.poznan.tools.MyView;

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

    public Window() {

        super("FindAllWords");
        System.out.println("window " + Thread.currentThread().getName());
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(new Dimension(1024, 720));
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
        this.views.add(new LoadingView(cardLayout, cardPane));
        this.views.add(new LobbyView());
        this.views.add(new VoteView());
        for (var view : this.views) {
            if (view.getNextViewButton() != null) {
                view.getNextViewButton().addActionListener(this);
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

        } else if (source == this.about) { // #TODO text from properties
            JOptionPane.showMessageDialog(this, "TODO", "About", JOptionPane.INFORMATION_MESSAGE);

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
                view.moveToNextView(this.cardLayout, this.cardPane);
                System.out.println("action");
                nextView.onShowAction();
                System.out.println("after action");
            } else if (source == view.getPreviousViewButton()) {
                if (view.getViewName().equals("ConnectingView")) {
                    this.menuSettings.setVisible(true);
                }
                view.returnToPreviousView(this.cardLayout, this.cardPane);
            }
        }
    }


}
