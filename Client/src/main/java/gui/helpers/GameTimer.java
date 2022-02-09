package gui.helpers;

import javax.swing.*;
import java.awt.*;

public class GameTimer extends JPanel {

    public void setTime(int time) {
        this.time = time;
    }

    public void setCurrentTime(JLabel currentTime) {
        this.currentTime = currentTime;
    }

    private int time = 0;
    private JLabel currentTime = new JLabel("--:--");
    private final Timer timer = new Timer(1000, e -> {
        time -= 1000;
        if (time < 0) time = 0;
        int minutes = time / 60000;
        int seconds = (time / 1000) % 60;
        currentTime.setText(String.format("%02d", minutes) + ":" + String.format("%02d", seconds));
    });

    public GameTimer() {
        this.currentTime.setText("--:--");

        this.currentTime.setForeground(Color.BLACK);
        this.currentTime.setFont(new Font("Monospaced", Font.BOLD, 20));
        setOpaque(false);

        add(currentTime);
    }


    public void start() {
        timer.start();
    }

    public void stop() {
        timer.stop();
    }

}
