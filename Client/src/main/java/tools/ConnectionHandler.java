package tools;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class ConnectionHandler {
    public static int port = 1313;
    public static String address = "localhost";
    public static Socket socket;
    public static HashMap<String, Triplet> responseTable;
    private static PrintWriter out;
    private static BufferedReader in;
    private static MessageGetter messageGetter;


    public static boolean createSocket() {
        try {
            socket = new Socket(address, port);
            //socket.setSoTimeout(5_000);
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
        responseTable.put("nickname", new Triplet("RESPONSE_NICKNAME_.{7}_.*"));
        responseTable.put("lobbiesEntry", new Triplet("RESPONSE_LOBBIES_COUNT_[0-9]+_.*"));
        responseTable.put("lobbies", new Triplet("NOTIFICATION_LOBBIES_COUNT_[0-9]+_.*"));
        responseTable.put("lobbyJoin", new Triplet("RESPONSE_LOBBY_JOIN_.{7}_[0-9]+"));
        responseTable.put("lobbyCreate", new Triplet("RESPONSE_LOBBY_CREATE_.{7}_[0-9]+"));
        responseTable.put("lobbyLeave", new Triplet("RESPONSE_LOBBY_LEAVE_.{7}_[0-9]+"));
        responseTable.put("playersVotes", new Triplet("NOTIFICATION_LOBBY_PLAYERS_[0-9]_.{4,}_[0-1]_.*"));
        responseTable.put("selfVote", new Triplet("RESPONSE_LOBBY_VOTE_.{7}_[0-1]"));
        responseTable.put("timerStart", new Triplet("NOTIFICATION_START_COUNTDOWN_[0-9]+"));
        responseTable.put("gameStart", new Triplet("NOTIFICATION_START_GAME_[0-9]+"));
        responseTable.put("checkWord", new Triplet("RESPONSE_CHECK_WORD_.{7}_[0-9]+"));
        responseTable.put("newLetter", new Triplet("NOTIFICATION_GAME_LETTER_[0-9]+"));
        responseTable.put("wordsList", new Triplet("NOTIFICATION_GAME_WORD_.{7}_.+"));
        responseTable.put("playersList", new Triplet("NOTIFICATION_GAME_PLAYERS_[0-9]_.{4,}_[0-9]+.*"));
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

    public static void readMessages() {
        messageGetter.execute();
    }

    public static void stopReader() {
        messageGetter.cancel(true);
    }

    public static String sendRequest(String request, String type) {
        out.print(request);
        out.flush();
        Object lock = ConnectionHandler.responseTable.get(type).lock;
        try {
            return ConnectionHandler.responseTable.get(type).messages.poll(2, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static class MessageGetter extends SwingWorker<Void, Void> {

        @Override
        protected Void doInBackground() {
            System.out.println("reader started");
            while (!isCancelled()) {
                System.out.println("while");
                try {
                    String message = in.readLine();
                    System.out.println(message);
                    for (Map.Entry<String, Triplet> entry : responseTable.entrySet()) {
                        Triplet triplet = entry.getValue();
                        if (message.matches(triplet.regex)) {
                            triplet.messages.add(message);
                            break;
                        }
                    }
                } catch (IOException ignored) {
                }
            }
            return null;
        }
    }
}