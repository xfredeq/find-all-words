import javax.swing.*;
import java.awt.*;

public interface MyView {

    String getViewName();

    JButton getNextViewButton();

    void moveToNextView(CardLayout cardLayout, JPanel cardPane);

}

