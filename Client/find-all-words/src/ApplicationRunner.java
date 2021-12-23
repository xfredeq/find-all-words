import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class ApplicationRunner {
    public static void main (String[] args) throws InterruptedException, InvocationTargetException, IOException {

        //SwingUtilities.invokeAndWait(Window::new);

        Scanner scanner = new Scanner(System.in);

        Socket clientSocket = new Socket(InetAddress.getLocalHost(), 1313);
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        int i = 0;
        while (i < 100) {
            String msg = scanner.nextLine();
            out.println(msg);
            String response = in.readLine();
            System.out.println(response);
            i++;
        }

        in.close();
        out.close();
        clientSocket.close();
    }
}


