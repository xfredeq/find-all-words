package put.poznan.GUI;

import put.poznan.tools.PropertiesHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameTimer extends JPanel {
    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public int getSeconds() {
        return seconds;
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getTime() {
        return this.time;
    }

    public JLabel getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(JLabel currentTime) {
        this.currentTime = currentTime;
    }

    private int minutes = 0;
    private int seconds = 0;
    private int time = 0;
    private JLabel currentTime = new JLabel("--:--");
    private Timer timer = new Timer(1000, e -> {
        time -= 1000;
        if (time < 0) time = 0;
        minutes = time / 60000;
        seconds = (time / 1000) % 60;
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

    public String getTimer() {
        return String.format("%02d", minutes) + ":" + String.format("%02d", seconds) + "seconds";
    }
}
