package socket_bot;

import java.io.*;
import java.net.*;

/**
 * A client-side socket implementation for the chat bot application.
 * This class handles the connection to the bot server, message sending,
 * and connection management.
 *
 * @author Ornella Gigante
 * @version 1.0
 */
public class BotClient {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private final String HOST = "localhost";
    private final int PORT = 5000;
    
    public void connect() throws IOException {
        socket = new Socket(HOST, PORT);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }
    
    public String sendMessage(String message) {
        try {
            out.println(message);
            return in.readLine();
        } catch (IOException e) {
            return "Error: " + e.getMessage();
        }
    }
    
    public void disconnect() {
        try {
            if (socket != null && !socket.isClosed()) {
                out.println("bye");
                socket.close();
            }
        } catch (IOException e) {
            System.out.println("Error closing connection: " + e.getMessage());
        }
    }
}
