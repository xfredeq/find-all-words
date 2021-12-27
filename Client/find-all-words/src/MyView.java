import javax.swing.*;
import java.awt.*;

public interface MyView {

    String getViewName();

    String getNextViewName();

    JButton getNextViewButton();
    JButton getPreviousViewButton();

    void onShowAction();

    void moveToNextView(CardLayout cardLayout, JPanel cardPane);

    void returnToPreviousView(CardLayout cardLayout, JPanel cardPane);

}

