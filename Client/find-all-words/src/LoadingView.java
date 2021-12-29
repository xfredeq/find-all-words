import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

public class LoadingView extends JPanel implements MyView, ChangeListener {

    private String viewName;
    private String nextViewName;

    private JLabel title;

    private JProgressBar progressBar;

    private JPanel buttonPanel;
    private JButton enter;
    private JButton cancel;


    private Thread progressThread;

    LoadingView() {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setComponents();
        this.addComponents();
    }

    private void setComponents() {
        this.viewName = "ConnectingView";
        this.nextViewName = "TODO";
        this.title = new JLabel("Connecting...");

        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(new Font("Verdana", Font.BOLD, 80));
        title.setBackground(new Color(1.0f, 1.0f, 1.0f, 0.5f));
        title.setForeground(Color.RED);
        title.setOpaque(true);

        this.progressBar = new JProgressBar();
        this.progressBar.setPreferredSize(new Dimension(500, 100));
        this.progressBar.setMaximumSize(new Dimension(500, 100));
        this.progressBar.setStringPainted(true);
        this.progressBar.addChangeListener(this);

        this.enter = new JButton("Enter");
        this.enter.setVisible(false);
        this.cancel = new JButton("cancel");

        this.buttonPanel = new JPanel();
        this.buttonPanel.setLayout(new GridLayout(1, 3));
        this.buttonPanel.setPreferredSize(new Dimension(400, 60));
        this.buttonPanel.setMaximumSize(new Dimension(400, 60));

        this.buttonPanel.add(this.enter);
        this.buttonPanel.add(Box.createHorizontalGlue());
        this.buttonPanel.add(this.cancel);


    }

    private void addComponents() {
        add(Box.createVerticalGlue());
        add(this.title);
        add(Box.createVerticalGlue());
        add(this.progressBar);
        add(Box.createVerticalGlue());
        add(this.buttonPanel);
        add(Box.createVerticalGlue());
    }


    @Override
    public String getViewName() {
        return this.viewName;
    }

    @Override
    public String getNextViewName() {
        return this.nextViewName;
    }

    @Override
    public JButton getNextViewButton() {
        return this.enter;
    }

    @Override
    public JButton getPreviousViewButton() {
        return this.cancel;
    }

    @Override
    public void onShowAction() {
        this.progressBar.setValue(0);
        this.progressThread = new Thread(() -> {
            for (int i = 1; i <= 100; i++) {
                progressBar.setValue(i);
                try{
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    return;
                }
            }
        }, "progressThread");

        progressThread.start();


    }

    @Override
    public void moveToNextView(CardLayout cardLayout, JPanel cardPane) {

    }

    @Override
    public void returnToPreviousView(CardLayout cardLayout, JPanel cardPane) {
        //#TODO break connection
        this.progressThread.interrupt();
        this.enter.setVisible(false);
        cardLayout.show(cardPane, "StartView");
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        Object source = e.getSource();
        if (source == this.progressBar) {
            if (this.progressBar.getValue() == 100) {
                this.enter.setVisible(true);
            }
        }
    }
}
