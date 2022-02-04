package put.poznan.networking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class ConnectionHandler {
    public static int port = 1313;
    public static String address = "localhost";
    public static Socket socket;

    private static PrintWriter out;
    private static BufferedReader in;


    public static boolean createSocket() {
        try {
            socket = new Socket(address, port);
            socket.setSoTimeout(100_000);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
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
        System.out.println("request: " + request);
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
            in.close();
            out.close();
            socket.close();
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    private static boolean validateResponse(String request, String response) {
        switch (request) {
            case "GET_LOBBYSIZE_@":
                return response.matches("RESPONSE_LOBBYSIZE_[3-9]");
            case "CREATE_LOBBY_@":
                return response.matches("RESPONSE_CREATE_LOBBY_SUCCES_[0-9]+");
            case "GET_LOBBIES_@":
                return response.matches("RESPONSE_LOBBIES_COUNT_[0-9]+_.*");
            case "GET_PLAYERS_@":
                return response.matches("RESPONSE_PLAYERS_COUNT_[1-9]_.{4,}_.+");
            case "word":
                return true;
            default:
                if (request.matches("SET_NICKNAME_.{4,}_@")) {
                    return response.matches("RESPONSE_NICKNAME_.{4,}");
                }
        }

        return false;
    }
}
