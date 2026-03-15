import ui.ChatWindow;
import utils.Config;

public class Main {
    public static void main(String[] args) {
        // Parse command line arguments for LLM URL and API key
        if (args.length > 0) {
            String llmUrl = args[0];
            System.setProperty("llm.url", llmUrl);
            System.out.println("Using LLM URL: " + llmUrl);
            
            // Check if API key is provided as second argument
            if (args.length > 1) {
                String apiKey = "your LLM API";
                System.setProperty("your LLM API", apiKey);
                System.out.println("API key provided");
            } else {
                System.out.println("No API key provided - some services may require authentication");
            }
        } else {
            System.out.println("Using default LLM URL: " + Config.getLLMUrl());
            System.out.println("To use a custom URL, run: java -cp target/classes Main <your-llm-url> [api-key]");
            System.out.println("Examples:");
            System.out.println("  For OpenAI: java -cp target/classes Main https://api.openai.com/v1/chat/completions your-openai-key");
            System.out.println("  For Anthropic: java -cp target/classes Main https://api.anthropic.com/v1/messages your-anthropic-key");
            System.out.println("  For custom API: java -cp target/classes Main http://localhost:8000/chat");
        }
        
        // Create and display the chat window
        ChatWindow chatWindow = new ChatWindow();
        chatWindow.initializeUI();
    }
} 
