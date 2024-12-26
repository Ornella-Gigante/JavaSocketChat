package socket_bot;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import javax.swing.plaf.basic.BasicScrollBarUI;

/**
 * 
 * ChatBotGUI class that implements a graphical user interface for the chat bot.
 * This class provides a modern, user-friendly interface with split-pane layout,
 * featuring a chat area and commands panel. It includes customized UI components
 * with a dark theme color scheme and responsive design elements.
 * 
 * @author Ornella Gigante
 * @version 1.0
 */

public class ChatBotGUI extends JFrame {
    private JTextArea chatArea;
    private JTextArea commandsArea;
    private JTextField inputField;
    private JButton sendButton;
    private JButton clearButton;
    private JButton disconnectButton;
    private JComboBox<String> questionOptions;
    private BotClient client;
    
    // Color scheme
    private final Color PRIMARY_COLOR = new Color(60, 63, 65);
    private final Color SECONDARY_COLOR = new Color(43, 43, 43);
    private final Color ACCENT_COLOR = new Color(0, 122, 255);
    private final Color TEXT_COLOR = new Color(187, 187, 187);
    private final Color BACKGROUND_COLOR = new Color(50, 50, 50);
    
    public ChatBotGUI() {
        setTitle("Interactive Chat Bot");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BACKGROUND_COLOR);
        
        // Set modern look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            UIManager.put("Button.arc", 10);
            UIManager.put("Component.arc", 10);
            UIManager.put("TextComponent.arc", 10);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        client = new BotClient();
        setupComponents();
        
        try {
            client.connect();
            appendMessage("System: Connected to server");
            displayAvailableCommands();
        } catch (IOException e) {
            showError("Could not connect to server");
        }
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                client.disconnect();
            }
        });
    }
    
    private void setupComponents() {
        // Main panel with split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerSize(3);
        splitPane.setBorder(null);
        splitPane.setBackground(BACKGROUND_COLOR);
        
        // Left panel - Chat area
        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
        leftPanel.setBackground(BACKGROUND_COLOR);
        leftPanel.setBorder(new EmptyBorder(10, 10, 10, 5));
        
        // Chat area setup
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        chatArea.setBackground(PRIMARY_COLOR);
        chatArea.setForeground(TEXT_COLOR);
        chatArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        
        JScrollPane chatScrollPane = new JScrollPane(chatArea);
        chatScrollPane.setBorder(BorderFactory.createLineBorder(ACCENT_COLOR, 1));
        chatScrollPane.getVerticalScrollBar().setUI(new ModernScrollBarUI());
        
        // Right panel - Commands area
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel.setBackground(BACKGROUND_COLOR);
        rightPanel.setBorder(new EmptyBorder(10, 5, 10, 10));
        
        // Commands area setup
        commandsArea = new JTextArea();
        commandsArea.setEditable(false);
        commandsArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        commandsArea.setBackground(SECONDARY_COLOR);
        commandsArea.setForeground(TEXT_COLOR);
        commandsArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        commandsArea.setLineWrap(true);
        commandsArea.setWrapStyleWord(true);
        
        JScrollPane commandsScrollPane = new JScrollPane(commandsArea);
        commandsScrollPane.setBorder(BorderFactory.createLineBorder(ACCENT_COLOR, 1));
        commandsScrollPane.getVerticalScrollBar().setUI(new ModernScrollBarUI());
        
        JLabel commandsLabel = new JLabel("Available Commands", SwingConstants.CENTER);
        commandsLabel.setForeground(TEXT_COLOR);
        commandsLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        commandsLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        // Options panel
        JPanel optionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        optionsPanel.setBackground(BACKGROUND_COLOR);
        
        String[] questions = {
            "Select a question...",
            "Hello!",
            "What's the weather like?",
            "What time is it?",
            "Goodbye"
        };
        
        questionOptions = new JComboBox<>(questions);
        questionOptions.setBackground(PRIMARY_COLOR);
        questionOptions.setForeground(TEXT_COLOR);
        questionOptions.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        disconnectButton = createStyledButton("Disconnect");
        
        // Input panel
        JPanel inputPanel = new JPanel(new BorderLayout(10, 0));
        inputPanel.setBackground(BACKGROUND_COLOR);
        inputPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        inputField = new JTextField();
        inputField.setBackground(PRIMARY_COLOR);
        inputField.setForeground(TEXT_COLOR);
        inputField.setCaretColor(TEXT_COLOR);
        inputField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT_COLOR, 1),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        inputField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        
        sendButton = createStyledButton("Send");
        clearButton = createStyledButton("Clear");
        
        // Action listeners
        sendButton.addActionListener(e -> sendMessage());
        clearButton.addActionListener(e -> chatArea.setText(""));
        inputField.addActionListener(e -> sendMessage());
        disconnectButton.addActionListener(e -> disconnect());
        questionOptions.addActionListener(e -> handleQuestionSelection());
        
        // Assembly
        optionsPanel.add(questionOptions);
        optionsPanel.add(disconnectButton);
        
        buttonPanel.add(sendButton);
        buttonPanel.add(clearButton);
        
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(buttonPanel, BorderLayout.EAST);
        
        leftPanel.add(optionsPanel, BorderLayout.NORTH);
        leftPanel.add(chatScrollPane, BorderLayout.CENTER);
        leftPanel.add(inputPanel, BorderLayout.SOUTH);
        
        rightPanel.add(commandsLabel, BorderLayout.NORTH);
        rightPanel.add(commandsScrollPane, BorderLayout.CENTER);
        
        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(rightPanel);
        splitPane.setDividerLocation(600);
        
        add(splitPane);
    }
    
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(ACCENT_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setPreferredSize(new Dimension(100, 30));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(ACCENT_COLOR.brighter());
            }
            
            public void mouseExited(MouseEvent e) {
                button.setBackground(ACCENT_COLOR);
            }
        });
        
        return button;
    }
    
    private void displayAvailableCommands() {
        StringBuilder commands = new StringBuilder();
        commands.append("Bot understands these commands:\n\n");
        commands.append("1. Hello! - Start a conversation\n");
        commands.append("2. What's the weather like? - Check weather\n");
        commands.append("3. What time is it? - Get current time\n");
        commands.append("4. Goodbye - End conversation\n\n");
        commands.append("Tips:\n");
        commands.append("- Select from the dropdown menu or type your question\n");
        commands.append("- The bot will understand variations of these phrases\n");
        commandsArea.setText(commands.toString());
    }
    
    private void handleQuestionSelection() {
        String selectedQuestion = (String) questionOptions.getSelectedItem();
        if (!selectedQuestion.equals("Select a question...")) {
            inputField.setText(selectedQuestion);
            inputField.requestFocus();
        }
    }
    
    private void disconnect() {
        client.disconnect();
        appendMessage("System: Disconnected from server");
        disconnectButton.setEnabled(false);
        sendButton.setEnabled(false);
        inputField.setEnabled(false);
        questionOptions.setEnabled(false);
    }
    
    private void sendMessage() {
        String message = inputField.getText().trim();
        if (!message.isEmpty()) {
            appendMessage("You: " + message);
            String response = client.sendMessage(message);
            appendMessage("Bot: " + response);
            inputField.setText("");
            questionOptions.setSelectedIndex(0);
        }
    }
    
    private void appendMessage(String message) {
        chatArea.append(message + "\n");
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }
    
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    // Custom ScrollBarUI class for modern look
    private class ModernScrollBarUI extends BasicScrollBarUI {
        protected void configureScrollBarColors() {
            this.thumbColor = ACCENT_COLOR;
            this.trackColor = PRIMARY_COLOR;
        }
        
        @Override
        protected JButton createDecreaseButton(int orientation) {
            return createZeroButton();
        }
        
        @Override
        protected JButton createIncreaseButton(int orientation) {
            return createZeroButton();
        }
        
        private JButton createZeroButton() {
            JButton button = new JButton();
            button.setPreferredSize(new Dimension(0, 0));
            return button;
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ChatBotGUI().setVisible(true);
        });
        
        new Thread(() -> {
            new BotServer().start();
        }).start();
    }
}
