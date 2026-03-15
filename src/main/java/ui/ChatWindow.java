package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class ChatWindow {
    private JFrame frame;
    private JTextArea chatArea;
    private JTextField inputField;
    private JButton sendButton;
    private service.LLMService llmService;

    public void initializeUI() {
        // Ensure we're on the Event Dispatch Thread
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(this::initializeUI);
            return;
        }
        
        // Initialize LLM service
        llmService = new service.LLMService();
        
        // Create the main frame
        frame = new JFrame("LLM Chat Application");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 500);
        frame.setLocationRelativeTo(null);
        frame.setResizable(true);
        frame.setAlwaysOnTop(false);
        
        // Create main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Create chat area (top)
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        chatArea.setFont(new Font("Arial", Font.PLAIN, 14));
        
        JScrollPane scrollPane = new JScrollPane(chatArea);
        scrollPane.setPreferredSize(new Dimension(580, 400));
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Create input panel (bottom)
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        inputField = new JTextField();
        inputField.setFont(new Font("Arial", Font.PLAIN, 14));
        inputField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}
            
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendMessage();
                }
            }
            
            @Override
            public void keyReleased(KeyEvent e) {}
        });
        
        sendButton = new JButton("Send");
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });
        
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        
                       // Add test buttons panel
               JPanel testPanel = new JPanel();
               testPanel.setLayout(new FlowLayout());
               
               
               inputPanel.add(testPanel, BorderLayout.WEST);
        
        mainPanel.add(inputPanel, BorderLayout.SOUTH);
        
        // Add main panel to frame
        frame.add(mainPanel);
        
        // Show welcome message
        appendMessage("System", "Welcome to LLM Chat! Type your message and press Enter or click Send.");
        
        // Make frame visible and ensure it's on top
        frame.pack();
        frame.setVisible(true);
        frame.toFront();
        frame.requestFocus();
        
        // Ensure window is not minimized
        frame.setExtendedState(JFrame.NORMAL);
        
        // Focus on input field
        inputField.requestFocus();
        
        // Debug: Print to console to confirm window is created
        System.out.println("Chat window GUI initialized and should be visible!");
        System.out.println("Window title: " + frame.getTitle());
        System.out.println("Window size: " + frame.getSize());
        System.out.println("Window location: " + frame.getLocation());
    }
    
    private void sendMessage() {
        String message = inputField.getText().trim();
        if (!message.isEmpty()) {
            // Display user message
            appendMessage("You", message);
            
            // Clear input field
            inputField.setText("");
            
            // Process with streaming LLM service
            processWithStreamingLLM(message);
        }
    }
    
    private void processWithLLM(String message) {
        // Show typing indicator
        appendMessage("LLM", "Thinking...");
        
        // Process message with LLM service asynchronously
        llmService.processRequestAsync(message).thenAccept(response -> {
            // Update the "Thinking..." message with actual response
            updateLastMessage("LLM", response);
        });
    }
    
    public void processWithStreamingLLM(String message) {
        // Show typing indicator
        appendMessage("LLM", "Thinking...");
        
        // Process message with streaming LLM service asynchronously
        llmService.processStreamingRequestAsync(message, this).thenAccept(response -> {
            // Final update when streaming is complete
            updateLastMessage("LLM", response);
        });
    }
    
    public void updateStreamingMessage(String sender, String message) {
       
            try {
                // Get the current text and find the last LLM line
                String currentText = chatArea.getText();
                
                // Find the last occurrence of any LLM line (including numbered lists)
                int lastLLMIndex = -1;
                String[] patterns = {"LLM: Thinking...", "LLM: "};
                
                for (String pattern : patterns) {
                    int index = currentText.lastIndexOf(pattern);
                    if (index != -1) {
                        lastLLMIndex = index;
                        break;
                    }
                }
                
                // If no standard pattern found, look for lines that start with "LLM:" followed by numbers or bullets
                if (lastLLMIndex == -1) {
                    String[] lines = currentText.split("\n");
                    for (int i = lines.length - 1; i >= 0; i--) {
                        String line = lines[i].trim();
                        if (line.startsWith("LLM:")) {
                            // Found an LLM line, find its position in the original text
                            int lineStart = currentText.lastIndexOf(line);
                            if (lineStart != -1) {
                                lastLLMIndex = lineStart;
                                break;
                            }
                        }
                    }
                }
                
                if (lastLLMIndex != -1) {
                    // Find the start of the line (beginning of text or after newline)
                    int lineStart = lastLLMIndex;
                    
                    // Find the end of the line (next newline or end of text)
                    int lineEnd = currentText.indexOf('\n', lastLLMIndex);
                    if (lineEnd == -1) {
                        lineEnd = currentText.length();
                    } else {
                        // Include the newline character
                        lineEnd++;
                    }
                    
                    // Replace the entire line
                    String beforeLine = currentText.substring(0, lineStart);
                    
                    
                    // Ensure consistent formatting by removing any trailing newlines from the message
                    String cleanedMessage = message.trim().replaceAll("\\n+$", "");
                    
                    String newText = beforeLine + sender + ": " + cleanedMessage + "\n" ;
                    
                    // Set the new text and maintain caret position
                    chatArea.setText(newText);
                    chatArea.setCaretPosition(chatArea.getText().length());
                    
                    // Force repaint to ensure UI updates
                    chatArea.revalidate();
                    chatArea.repaint();
                    frame.revalidate();
                    frame.repaint();
                    
                    System.out.println("Updated streaming message: " + cleanedMessage);
                } else {
                    // If no LLM line found, append a new one
                    String cleanedMessage = message.trim().replaceAll("\\n+$", "");
                    chatArea.append(sender + ": " + cleanedMessage + "\n");
                    chatArea.setCaretPosition(chatArea.getDocument().getLength());
                    
                    // Force repaint to ensure UI updates
                    chatArea.revalidate();
                    chatArea.repaint();
                    frame.revalidate();
                    frame.repaint();
                    
                    System.out.println("Appended streaming message: " + cleanedMessage);
                }
            } catch (Exception e) {
                System.err.println("Error updating streaming message: " + e.getMessage());
                e.printStackTrace();
            }
        
    }
    
    // Test method to verify streaming updates work
    public void testStreamingUpdate() {
        appendMessage("LLM", "Testing streaming...");
        
        // Simulate streaming updates
        new Thread(() -> {
            try {
                Thread.sleep(1000);
                updateStreamingMessage("LLM", "Testing streaming... Hello");
                Thread.sleep(500);
                updateStreamingMessage("LLM", "Testing streaming... Hello World");
                Thread.sleep(500);
                updateStreamingMessage("LLM", "Testing streaming... Hello World! This is a test.");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
    
    // Test method to verify bullet point streaming updates work
    public void testBulletPointStreaming() {
        appendMessage("LLM", "Testing bullet points...");
        
        // Simulate bullet point streaming updates
        new Thread(() -> {
            try {
                Thread.sleep(1000);
                updateStreamingMessage("LLM", "1. First bullet point");
                Thread.sleep(500);
                updateStreamingMessage("LLM", "1. First bullet point\n2. Second bullet point");
                Thread.sleep(500);
                updateStreamingMessage("LLM", "1. First bullet point\n2. Second bullet point\n3. Third bullet point");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
    
    private void appendMessage(String sender, String message) {
        SwingUtilities.invokeLater(() -> {
            chatArea.append(sender + ": " + message + "\n");
            chatArea.setCaretPosition(chatArea.getDocument().getLength());
        });
    }
    
    private void updateLastMessage(String sender, String message) {
        SwingUtilities.invokeLater(() -> {
            String currentText = chatArea.getText();
            String[] lines = currentText.split("\n");
            
            // Find and replace the last "Thinking..." message
            for (int i = lines.length - 1; i >= 0; i--) {
                if (lines[i].startsWith("LLM: Thinking...")) {
                    lines[i] = sender + ": " + message;
                    break;
                }
            }
            
            // Rebuild the text
            StringBuilder newText = new StringBuilder();
            for (String line : lines) {
                if (!line.isEmpty()) {
                    newText.append(line).append("\n");
                }
            }
            
            chatArea.setText(newText.toString());
            chatArea.setCaretPosition(chatArea.getDocument().getLength());
        });
    }
    

} 