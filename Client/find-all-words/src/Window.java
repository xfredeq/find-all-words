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
    private JMenuItem close, connectionSettings;


    public Window() {
        super("FindAllWords");
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
        this.cardLayout.show(cardPane, "StartView");

        this.setVisible(true);
    }

    private void setMyMenuBar() {

        this.menuFile = new JMenu("File");
        this.menuSettings = new JMenu("Settings");
        this.menuHelp = new JMenu("Help");

        this.close = new JMenuItem("Close");
        this.connectionSettings = new JMenuItem("Connection");

        this.close.addActionListener(this);
        this.connectionSettings.addActionListener(this);

        this.menuFile.add(this.close);

        this.menuSettings.add(this.connectionSettings);

        this.menuBar.add(this.menuFile);
        this.menuBar.add(this.menuSettings);
        this.menuBar.add(Box.createHorizontalGlue());
        this.menuBar.add(this.menuHelp);

        this.setJMenuBar(this.menuBar);
    }


    private void addViews() {
        this.views.add(new StartView());
        for (var view : this.views) {
            view.getNextViewButton().addActionListener(this);
            this.cardPane.add((Component) view, view.getViewName());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == this.close) {
            dispose();
        } else if (source == this.connectionSettings) {
            JDialog dialog = new ConnectionDialog(this);
            dialog.setVisible(true);
        }

        for (var view : this.views) {

            if (source == view.getNextViewButton()) {
                view.moveToNextView(this.cardLayout, this.cardPane);
            }
        }
    }


}
