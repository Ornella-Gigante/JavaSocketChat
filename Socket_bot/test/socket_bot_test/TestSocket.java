package socket_bot_test;

import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import socket_bot.BotClient;
import socket_bot.BotServer;
import socket_bot.ChatBotGUI;

class TestSocket {
    private BotServer server;
    private BotClient client;
    
    @BeforeEach
    void setUp() throws InterruptedException {
        server = new BotServer();
        client = new BotClient();
        Thread serverThread = new Thread(() -> server.start());
        serverThread.setDaemon(true);
        serverThread.start();
        Thread.sleep(1000);
    }
    
    @AfterEach
    void tearDown() throws InterruptedException {
        if (client != null) {
            client.disconnect();
        }
        Thread.sleep(500);
    }

    @Test
    void testClientConnection() {
        try {
            client.connect();
            String response = client.sendMessage("hello");
            assertNotNull(response);
            assertTrue(response.contains("Hello") || response.contains("Hi"));
        } catch (IOException e) {
            fail("Connection test failed: " + e.getMessage());
        }
    }

    @Test
    void testGUIBasicFunctionality() {
        ChatBotGUI gui = new ChatBotGUI();
        try {
            assertNotNull(gui);
            assertTrue(gui.isVisible());
        } finally {
            gui.dispose();
        }
    }

    @Test
    void testServerResponse() {
        try {
            client.connect();
            String response = client.sendMessage("weather");
            assertNotNull(response);
            assertTrue(response.toLowerCase().contains("weather"));
        } catch (IOException e) {
            fail("Server response test failed: " + e.getMessage());
        }
    }
}
