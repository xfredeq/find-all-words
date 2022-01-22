package put.poznan.networking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

// https://www.baeldung.com/a-guide-to-java-sockets
public class SocketClient {

    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public void startConnection(String ip, int port) throws IOException {
        clientSocket = new Socket(ip, port);
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    public void sendMessage(String msg) throws IOException {
        out.println(msg);
    }

    public String getMessage() throws IOException {
        return this.in.readLine();
    }

    public void stopConnection() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
    }

    public static void main(String[] args) {
        SocketClient socketClient = new SocketClient();
        try {
            socketClient.startConnection("127.0.0.1", 1100);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            socketClient.sendMessage("wiadomosc");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            System.out.println(socketClient.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            socketClient.stopConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
