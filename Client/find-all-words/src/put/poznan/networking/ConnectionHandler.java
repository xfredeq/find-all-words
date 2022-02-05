package put.poznan.networking;

import put.poznan.tools.Triplet;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.KeyPair;
import java.util.HashMap;
import java.util.Map;


public class ConnectionHandler {
    public static int port;
    public static String address;
    public static Socket socket;
    public static HashMap<String, Triplet> responseTable;
    private static PrintWriter out;
    private static BufferedReader in;
    private static MessageGetter messageGetter;

    public static boolean createSocket() {
        try {
            socket = new Socket(address, port);
            //socket.setSoTimeout(10_000);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            messageGetter = new MessageGetter();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static void initializeTable() {
        responseTable = new HashMap<>();
        responseTable.put("lobbySize", new Triplet("RESPONSE_LOBBYSIZE_[3-9]"));
        responseTable.put("nickname", new Triplet("RESPONSE_NICKNAME_.{4,}"));
    }

    @SuppressWarnings("unused")
    public static String getMessage() {
        try {
            return in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String sendRequest(String request) {
        System.out.println("request in send: " + request);
        out.print(request);
        out.flush();
        try {
            String response = in.readLine();
            System.out.println("response: " + response);
            while (!validateResponse(request, response)) {
                response = in.readLine();
            }
            return response;
        } catch (IOException t) {
            return null;
        }
    }

    public static void endConnection() {
        try {
            stopReader();

            out.close();

            in.close();

            socket.close();

        } catch (Exception e) {
            // e.printStackTrace();
        }
    }

    private static boolean validateResponse(String request, String response) {
        switch (request) {
            case "GET_LOBBYSIZE_@":
                return response.matches("RESPONSE_LOBBYSIZE_[3-9]");
            case "LOBBY_CREATE_@":
                return response.matches("RESPONSE_LOBBY_CREATE_SUCCESS_[0-9]+");
            case "GET_LOBBIES_@":
                return response.matches("RESPONSE_LOBBIES_COUNT_[0-9]+_.*") || "RESPONSE_BAD_REQUEST".equals(response);
            case "LOBBY_LEAVE_@":
                return response.matches("RESPONSE_LOBBY_LEAVE_SUCCESS");
            default:
                if (request.matches("SET_NICKNAME_.{4,}_@")) {
                    return response.matches("RESPONSE_NICKNAME_.{4,}");
                } else if (request.matches("LOBBY_JOIN_[1-9]+_@")) {
                    return response.matches("RESPONSE_LOBBY_JOIN_[1-9]+_SUCCESS");
                }
        }
        return false;
    }

    public static void readMessages() {
        messageGetter.execute();
    }

    public static void stopReader() {
        messageGetter.cancel(true);
    }

    public static void send(String m) {
        out.print(m);
        out.flush();
    }

    private static class MessageGetter extends SwingWorker<Void, Void> {

        @Override
        protected Void doInBackground() {
            System.out.println("reader started");
            while (!isCancelled()) {
                try {
                    String m = in.readLine();
                    System.out.println("response in bg: " + m);

                    for (Map.Entry<String, Triplet> entry : responseTable.entrySet()) {
                        System.out.println("keyyy: " + entry.getKey());
                        Triplet triplet = entry.getValue();
                        if (m.matches(triplet.regex)) {
                            synchronized (triplet.lock) {
                                triplet.response = m;
                                triplet.lock.notifyAll();
                            }
                            break;
                        }
                    }

                } catch (IOException e) {
                    //e.printStackTrace();
                }
            }
            return null;
        }
    }
}
