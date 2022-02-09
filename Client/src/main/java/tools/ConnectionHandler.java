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
    public static int timeoutTime = 10;
    public static int requestTimeout = 3;
    public static int port = 1313;
    public static String address = "localhost";
    public static Socket socket;
    public static HashMap<String, MessageQueue> responseTable;
    private static PrintWriter out;
    private static BufferedReader in;
    private static MessageGetter messageGetter;


    public static boolean createSocket() {
        try {
            socket = new Socket(address, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            messageGetter = new MessageGetter();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public static void initializeTable() {
        responseTable = new HashMap<>();
        responseTable.put("lobbySize", new MessageQueue("RESPONSE_LOBBYSIZE_[3-9]"));
        responseTable.put("nickname", new MessageQueue("RESPONSE_NICKNAME_.{7}_.*"));
        responseTable.put("lobbiesEntry", new MessageQueue("RESPONSE_LOBBIES_COUNT_[0-9]+_.*"));
        responseTable.put("lobbies", new MessageQueue("NOTIFICATION_LOBBIES_COUNT_[0-9]+_.*"));
        responseTable.put("lobbyJoin", new MessageQueue("RESPONSE_LOBBY_JOIN_.{7}_[0-9]+"));
        responseTable.put("lobbyCreate", new MessageQueue("RESPONSE_LOBBY_CREATE_.{7}_[0-9]+"));
        responseTable.put("lobbyLeave", new MessageQueue("RESPONSE_LOBBY_LEAVE_.{7}_[0-9]+"));
        responseTable.put("playersVotes", new MessageQueue("NOTIFICATION_LOBBY_PLAYERS_[0-9]_.{4,}_[0-1]_.*"));
        responseTable.put("selfVote", new MessageQueue("RESPONSE_LOBBY_VOTE_.{7}_[0-1]"));
        responseTable.put("timerStart", new MessageQueue("NOTIFICATION_START_COUNTDOWN_[0-9]+"));
        responseTable.put("gameStart", new MessageQueue("NOTIFICATION_START_GAME_[0-9]+"));
        responseTable.put("countdownLeave", new MessageQueue("NOTIFICATION_COUNTDOWN_LEAVE"));
        responseTable.put("checkWord", new MessageQueue("RESPONSE_CHECK_WORD_.{7}_[0-9]+"));
        responseTable.put("gameNotification", new MessageQueue("NOTIFICATION_GAME_.*"));
        responseTable.put("roundsNumber", new MessageQueue("RESPONSE_ROUNDS_[0-9]"));
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
        try {
            return ConnectionHandler.responseTable.get(type).messages.poll(requestTimeout, TimeUnit.SECONDS);
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
                try {
                    String message = in.readLine();
                    for (Map.Entry<String, MessageQueue> entry : responseTable.entrySet()) {
                        MessageQueue messageQueue = entry.getValue();
                        if (message.matches(messageQueue.regex)) {
                            messageQueue.messages.add(message);
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
