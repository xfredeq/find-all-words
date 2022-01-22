package put.poznan.GUI;

import put.poznan.tools.MyView;
import put.poznan.tools.PropertiesHandler;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.Socket;
import java.util.concurrent.Semaphore;

public class LoadingView extends MyView implements PropertyChangeListener {


    private JLabel title;

    private JProgressBar progressBar;

    private JPanel buttonPanel;
    private JButton enter;
    private JButton cancel;

    private ConnectionTask connectionTask;

    private Thread progressThread;

    private final Semaphore semaphore = new Semaphore(0);
    private final Semaphore semaphore2 = new Semaphore(0);

    LoadingView() {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setComponents();
        this.addComponents();
    }

    private void setComponents() {
        this.viewName = "ConnectingView";
        this.nextViewName = "LobbyView";
        this.previousViewName = "StartView";
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

        this.enter = new JButton("Enter");
        this.enter.setVisible(false);
        this.nextViewButton = this.enter;

        this.cancel = new JButton("cancel");
        this.previousViewButton = this.cancel;

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
    public void onShowAction() {

        System.out.println("inside");
        //#TODO get lobby size from server
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        this.connectionTask = new ConnectionTask();
        this.connectionTask.addPropertyChangeListener(this);
        this.connectionTask.execute();

    }

    @Override
    public void moveToNextView(CardLayout cardLayout, JPanel cardPane) {
        this.enter.setVisible(false);
        super.moveToNextView(cardLayout, cardPane);
    }

    @Override
    public void returnToPreviousView(CardLayout cardLayout, JPanel cardPane) {
        //#TODO break connection
        this.connectionTask.cancel(true);
        this.progressBar.setValue(0);
        this.enter.setVisible(false);
        super.returnToPreviousView(cardLayout, cardPane);
    }


    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("progress".equals(evt.getPropertyName())) {
            int progress = (Integer) evt.getNewValue();
            progressBar.setValue(progress);
        }
    }

    private class ConnectionTask extends SwingWorker<Void, Void> {

        private class myThread implements Runnable {


            @Override
            public void run() {
                int progress = 0;
                for (int i = 0; i < 20; i++) {
                    progress++;
                    try {
                        Thread.sleep(40);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    setProgress(progress);
                }
                try {
                    semaphore2.release();
                    semaphore.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                for (int i = 0; i < 20; i++) {
                    progress++;
                    try {
                        Thread.sleep(40);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    setProgress(progress);
                }
            }
        }

        @Override
        protected Void doInBackground() {
            setProgress(0);

            progressThread = new Thread(new myThread());

            progressThread.start();

            String address = PropertiesHandler.getProperty("serverAddress");
            System.out.println(address);
            try {
                semaphore2.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            semaphore.release();

            Integer.parseInt(PropertiesHandler.getProperty("serverPort"));
            try {
                progressThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public void done() {
            Toolkit.getDefaultToolkit().beep();
            enter.setVisible(true);
            setCursor(null);
        }
    }
}
