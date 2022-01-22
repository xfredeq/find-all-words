import put.poznan.GUI.Window;
import put.poznan.tools.PropertiesHandler;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;

public class ApplicationRunner {
    public static void main(String[] args) throws InterruptedException, InvocationTargetException {
        PropertiesHandler.loadProperties();
        SwingUtilities.invokeAndWait(Window::new);
        PropertiesHandler.saveProperties();
    }
}


