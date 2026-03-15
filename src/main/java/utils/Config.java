package utils;

public class Config {
    // LLM Configuration
    public static final String DEFAULT_LLM_URL = "https://integrate.api.nvidia.com/v1/chat/completions";
    
    // You can change this to your actual LLM endpoint
    public static String getLLMUrl() {
        // You can modify this method to read from environment variables or config files
        String url = System.getProperty("llm.url");
        if (url != null && !url.trim().isEmpty()) {
            return url;
        }
        return DEFAULT_LLM_URL;
    }
    
    // API Configuration
    public static final String API_CONTENT_TYPE = "application/json";
    public static final String API_ACCEPT = "application/json";
    
    // Request timeout in milliseconds
    public static final int REQUEST_TIMEOUT = 30000; // 30 seconds
    
    // Debug mode
    public static final boolean DEBUG_MODE = true;
} 