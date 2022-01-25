package put.poznan.GUI;

import put.poznan.tools.ConnectionHandler;
import put.poznan.tools.MyView;
import put.poznan.tools.PropertiesHandler;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.concurrent.Semaphore;

import static put.poznan.tools.ConnectionHandler.address;

public class LoadingView extends MyView implements PropertyChangeListener {


    private final CardLayout cardLayout;
    private final JPanel cardPane;
    private JLabel title;
    private JProgressBar progressBar;
    private JPanel buttonPanel;
    private JButton enter;
    private JButton cancel;
    private ConnectionTask connectionTask;
    private Thread progressThread;
    private Semaphore semaphore = new Semaphore(0);
    private Semaphore semaphore2 = new Semaphore(0);

    LoadingView(CardLayout layout, JPanel pane) {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.cardLayout = layout;
        this.cardPane = pane;
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
        System.out.println("on show start");
        semaphore = new Semaphore(0);
        semaphore2 = new Semaphore(0);

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
        this.progressThread.interrupt();
        this.connectionTask.cancel(true);
        this.connectionTask.removePropertyChangeListener(this);
        this.progressBar.setValue(0);
        this.enter.setVisible(false);
        ConnectionHandler.endConnection();
        System.out.println("returning");
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

        @Override
        protected Void doInBackground() {
            setProgress(0);
            progressThread = new Thread(new myThread());
            progressThread.start();


            for (int i = 0; i < 5; i++) {
                if (i == 0) {
                    ConnectionHandler.address = PropertiesHandler.getProperty("serverAddress");
                } else if (i == 1) {
                    ConnectionHandler.port = Integer.parseInt(PropertiesHandler.getProperty("serverPort"));
                } else if (i == 2) {
                    if (!ConnectionHandler.createSocket()) {
                        returnToPreviousView(cardLayout, cardPane);
                        System.out.println("returned to previous view");
                        return null;
                    }
                } else if (i == 3) {
                    String message = ConnectionHandler.sendRequest("GET_LOBBYSIZE_@");
                    if (message == null) {
                        returnToPreviousView(cardLayout, cardPane);
                        System.out.println("returned to previous view");
                        return null;
                    }
                    if (message.contains("RESPONSE_LOBBYSIZE_")) {
                        PropertiesHandler.setProperty("lobbySize", String.valueOf(message.charAt(message.length() - 1)));
                        PropertiesHandler.saveProperties();
                        System.out.println(message);
                    }
                } else {
                    String message = ConnectionHandler.sendRequest("GET_LOBBYCOUNT_@");
                    if (message == null) {
                        returnToPreviousView(cardLayout, cardPane);
                        System.out.println("returned to previous view");
                        return null;
                    }
                }
                try {
                    semaphore2.acquire();
                } catch (InterruptedException e) {
                    //e.printStackTrace();
                    return null;
                }
                semaphore.release();

            }

            System.out.println(address);
            System.out.println(ConnectionHandler.port);

            try {
                progressThread.join();
                System.out.println("joined");
            } catch (InterruptedException e) {
                //e.printStackTrace();
                return null;

            }
            System.out.println("returned");
            return null;
        }

        @Override
        public void done() {
            setCursor(null);
            System.out.println("done " + connectionTask.getProgress());
            if (connectionTask.getProgress() == 100) {

                Toolkit.getDefaultToolkit().beep();
                enter.setVisible(true);

            }
        }

        private class myThread implements Runnable {

            @Override
            public void run() {
                int progress = 0;

                for (int section = 0; section < 5; section++) {
                    System.out.println("in for " + section);
                    for (int i = 0; i < 20; i++) {
                        progress++;
                        try {
                            Thread.sleep(40);
                        } catch (InterruptedException e) {
                            System.out.println("2 " + Thread.currentThread().getName());
                            return;
                        }
                        connectionTask.setProgress(progress);
                    }
                    try {
                        semaphore2.release();
                        semaphore.acquire();

                    } catch (InterruptedException e) {
                        System.out.println("1 " + Thread.currentThread().getName());
                        return;
                    }
                }
            }

        }
    }
}
