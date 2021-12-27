import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.net.Inet4Address;
import java.net.UnknownHostException;


public class ConnectionDialog extends JDialog implements ActionListener, FocusListener {

    private JPanel panel;
    private JLabel server, addressLabel, portLabel;
    private JTextField address, port;
    private JButton confirm, cancel, dflt;

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
        this.dflt = new JButton("default");

        this.confirm.addActionListener(this);
        this.cancel.addActionListener(this);
        this.dflt.addActionListener(this);

        this.panel = new JPanel();
        this.panel.setLayout(new GridLayout(1, 5));
        this.panel.setPreferredSize(new Dimension(400, 60));
        this.panel.setMaximumSize(new Dimension(400, 60));

        this.panel.add(confirm);
        this.panel.add(Box.createHorizontalGlue());
        this.panel.add(dflt);
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

        this.server.setForeground(Color.BLACK);
        this.server.setFont(new Font("Arial", Font.BOLD, 25));


        this.addressLabel.setForeground(Color.BLACK);
        this.addressLabel.setFont(new Font("Arial", Font.ITALIC, 20));

        this.address.setHorizontalAlignment(JTextField.CENTER);
        this.address.addFocusListener(this);

        this.portLabel.setForeground(Color.BLACK);
        this.portLabel.setFont(new Font("Arial", Font.ITALIC, 20));

        this.port.setHorizontalAlignment(JTextField.CENTER);
        this.port.addFocusListener(this);
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

    private void setDefault() {
        this.address.setText("127.0.0.1");
        this.port.setText("1313");
        this.address.setBackground(Color.WHITE);
        this.port.setBackground(Color.WHITE);
    }

    private boolean validateAddress() {
        try {
            Inet4Address.getByName(this.address.getText());
            return true;
        } catch (UnknownHostException ex) {
            return false;
        }
    }

    private boolean validatePort() {
        int port;
        try {
            port = Integer.parseInt(this.port.getText());
        } catch (IllegalArgumentException e) {
            return false;
        }
        return port > 1024 && port < 49152;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == this.dflt) {
            this.setDefault();
        } else if (source == cancel) {
            this.dispose();
            this.setDefault();
        } else if (source == this.confirm) {
            if (validateAddress() && validatePort()) {
                //#TODO save to properties
                dispose();
            }
            //#TODO warning?
            if (!validateAddress()) {
                this.address.setBackground(Color.red);
            }
            if (!validatePort()) {
                this.port.setBackground(Color.red);
            }
        }
    }

    @Override
    public void focusGained(FocusEvent e) {
        Object source = e.getSource();

        if (source == this.address) {
            this.address.setBackground(Color.WHITE);
        } else if (source == this.port) {
            this.port.setBackground(Color.WHITE);
        }
    }

    @Override
    public void focusLost(FocusEvent e) {

    }
}
