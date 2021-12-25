import javax.swing.*;
import java.awt.*;

public class ConnectionDialog extends JDialog {
    private JPanel panel;
    private JLabel server, addressLabel, portLabel;
    private JTextField address, port;
    private JButton confirm, cancel;

    ConnectionDialog(JFrame owner) {
        super(owner, "Connection Settings", true);

        this.setSize(new Dimension(720, 480));
        this.setLocationRelativeTo(null);
        this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));

        this.setComponents();
        this.addComponents();
    }

    private void setComponents() {

        this.confirm = new JButton("confirm");
        this.cancel = new JButton("cancel");

        this.panel = new JPanel();

        this.panel.add(confirm);
        this.panel.add(Box.createHorizontalGlue());
        this.panel.add(cancel);

        this.server = new JLabel("Server options:");
        this.addressLabel = new JLabel("IP address:");
        this.portLabel = new JLabel("Port:");

        this.address = new JTextField("127.0.0.1");
        this.port = new JTextField("1313");

        this.server.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.addressLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.portLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.address.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.port.setAlignmentX(Component.CENTER_ALIGNMENT);

        this.address.setMaximumSize(new Dimension(200, 40));
        this.port.setMaximumSize(new Dimension(100, 40));

        this.server.setForeground(Color.black);
        this.server.setBackground(Color.cyan);
        this.server.setFont(new Font("Arial", Font.BOLD, 25));
        this.server.setOpaque(true);
        this.server.setVisible(true);
    }

    private void addComponents() {
        this.add(Box.createVerticalGlue());
        this.add(this.server);
        this.add(Box.createVerticalGlue());
        this.add(this.addressLabel);
        this.add(this.address);
        this.add(Box.createVerticalGlue());
        this.add(this.portLabel);
        this.add(this.port);
        this.add(Box.createVerticalGlue());
        this.add(this.panel);
        this.add(Box.createVerticalGlue());

    }
}
