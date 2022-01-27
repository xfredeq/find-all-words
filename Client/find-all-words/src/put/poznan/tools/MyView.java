package put.poznan.tools;

import javax.swing.*;
import java.awt.*;

public abstract class MyView extends JPanel {
    protected String viewName = "";
    protected String nextViewName = "";
    protected String previousViewName = "";

    protected JButton nextViewButton;
    protected JButton previousViewButton;

    public String getViewName() {
        return this.viewName;
    }


    public String getNextViewName() {
        return this.nextViewName;
    }

    public String getPreviousViewName() {
        return this.previousViewName;
    }


    public JButton getNextViewButton() {
        return this.nextViewButton;
    }


    public JButton getPreviousViewButton() {
        return this.previousViewButton;
    }


    public abstract void onShowAction();

    public boolean moveToNextView(CardLayout cardLayout, JPanel cardPane) {
        cardLayout.show(cardPane, this.nextViewName);
        return true;
    }


    public void returnToPreviousView(CardLayout cardLayout, JPanel cardPane) {
        if (this.previousViewName != null){
            cardLayout.show(cardPane, this.previousViewName);
        }

    }

}

