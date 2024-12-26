package socket_bot;

import java.io.*;
import java.net.*;
import java.util.*;
import java.text.SimpleDateFormat;

/**
 * A server implementation for the chat bot application that handles
 * multiple client connections and provides automated responses.
 * The server maintains a collection of predefined responses for different
 * types of user inputs and supports basic conversation patterns.
 *
 * Features:
 * - Multi-client support through threaded handlers
 * - Customizable response patterns
 * - Time-aware greetings
 * - Context-based responses
 *
 * @author Ornella Gigante
 * @version 1.0
 */
public class BotServer {
    private ServerSocket serverSocket;
    private final int PORT = 5000;
    private Map<String, String[]> responses;
    
    public BotServer() {
        initializeResponses();
    }
    
    private void initializeResponses() {
        responses = new HashMap<>();
        
        responses.put("hello", new String[]{
            "Hello! How can I help you today?",
            "Hi there! What can I do for you?",
            "Greetings! How may I assist you?",
            "Hello! It's great to hear from you!",
            "Hi! I'm here to help. What's on your mind?"
        });
        
        responses.put("weather", new String[]{
            "The weather is quite pleasant today!",
            "It's a beautiful day outside!",
            "Perfect weather for any activity!",
            "The weather's been lovely lately!",
            "A wonderful day to be outside!"
        });
        
        responses.put("bye", new String[]{
            "Goodbye! Have a great day!",
            "See you later! Take care!",
            "Farewell! Hope to chat again soon!",
            "Bye! It was nice talking to you!",
            "Take care! Come back anytime!"
        });
    }
    
    public void start() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Bot Server started on port " + PORT);
            
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new ClientHandler(clientSocket).start();
            }
        } catch (IOException e) {
            System.out.println("Server Error: " + e.getMessage());
        }
    }
    
    private class ClientHandler extends Thread {
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;
        
        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }
        
        public void run() {
            try {
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    String response = generateBotResponse(inputLine);
                    out.println(response);
                }
            } catch (IOException e) {
                System.out.println("Handler Error: " + e.getMessage());
            }
        }
        
        private String generateBotResponse(String input) {
            String cleanInput = input.toLowerCase().trim();
            
            // Check for greetings
            if (isGreeting(cleanInput)) {
                return getTimeOfDayGreeting() + "! " + getRandomResponse(responses.get("hello"));
            }
            
            // Check for farewells
            if (isFarewell(cleanInput)) {
                return getRandomResponse(responses.get("bye"));
            }
            
            // Time-related responses
            if (cleanInput.contains("time")) {
                String[] timeResponses = {
                    "It's currently " + formatTime(new Date()),
                    "The time right now is " + formatTime(new Date()),
                    "Looking at my clock, it's " + formatTime(new Date()),
                    "It's " + formatTime(new Date()) + ". Time flies, doesn't it?",
                    "According to my internal clock, it's " + formatTime(new Date())
                };
                return getRandomResponse(timeResponses);
            }
            
            // Weather-related responses
            if (cleanInput.contains("weather")) {
                return getRandomResponse(responses.get("weather"));
            }
            
            // Question handling
            if (cleanInput.contains("?")) {
                if (cleanInput.contains("how are you")) {
                    String[] moodResponses = {
                        "I'm doing great, thanks for asking! How about you?",
                        "I'm functioning perfectly! How's your day going?",
                        "All systems operational and in a good mood! How are you?",
                        "I'm having a wonderful day! How about yourself?"
                    };
                    return getRandomResponse(moodResponses);
                }
                
                if (cleanInput.contains("what") || cleanInput.contains("how")) {
                    String[] curiosityResponses = {
                        "That's an interesting question! Could you tell me more about what you'd like to know?",
                        "I'd love to help with that. Could you provide more details?",
                        "Great question! Let me make sure I understand exactly what you're asking.",
                        "I'm curious about your question. Could you elaborate a bit more?"
                    };
                    return getRandomResponse(curiosityResponses);
                }
                
                if (cleanInput.contains("why")) {
                    String[] whyResponses = {
                        "That's a thought-provoking question! Could you give me more context?",
                        "Interesting! What made you think about that?",
                        "I'd need a bit more information to properly answer that. Could you explain further?",
                        "That's quite intriguing! What aspects specifically interest you?"
                    };
                    return getRandomResponse(whyResponses);
                }
            }
            
            // Default responses
            String[] defaultResponses = {
                "I'm not quite sure about that. Could you rephrase?",
                "Interesting! Could you tell me more about what you mean?",
                "I'd like to help, but I need a bit more information.",
                "Could you elaborate on that? I want to make sure I understand correctly.",
                "That's interesting! Would you mind providing more details?"
            };
            return getRandomResponse(defaultResponses);
        }
        
        private boolean isGreeting(String input) {
            String[] greetings = {"hello", "hi", "hey", "good morning", "good afternoon", "good evening"};
            return Arrays.stream(greetings).anyMatch(input::contains);
        }
        
        private boolean isFarewell(String input) {
            String[] farewells = {"bye", "goodbye", "see you", "farewell", "good night"};
            return Arrays.stream(farewells).anyMatch(input::contains);
        }
        
        private String getTimeOfDayGreeting() {
            int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
            if (hour < 12) return "Good morning";
            if (hour < 18) return "Good afternoon";
            return "Good evening";
        }
        
        private String formatTime(Date date) {
            return new SimpleDateFormat("h:mm a").format(date);
        }
        
        private String getRandomResponse(String[] responses) {
            int index = (int) (Math.random() * responses.length);
            return responses[index];
        }
    }
}
