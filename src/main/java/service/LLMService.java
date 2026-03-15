package service;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.CompletableFuture;
import utils.Config;
import ui.ChatWindow;

public class LLMService {
    private final String llmUrl;
    private final CloseableHttpClient httpClient;
    private final Gson gson;
    private final String apiKey;
    
    public LLMService() {
        // Use the configured LLM URL
        this.llmUrl = Config.getLLMUrl();
        this.httpClient = createHttpClient();
        this.gson = new Gson();
        this.apiKey = "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";
    }
    
    public LLMService(String llmUrl) {
        this.llmUrl = llmUrl;
        this.httpClient = createHttpClient();
        this.gson = new Gson();
        this.apiKey = "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";
    }
    
    private CloseableHttpClient createHttpClient() {
        try {
            // Create SSL context that trusts all certificates (for development)
            SSLContext sslContext = new SSLContextBuilder()
                .loadTrustMaterial(null, (chain, authType) -> true)
                .build();
            
            // Create SSL socket factory
            SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(
                sslContext,
                new String[]{"TLSv1.2", "TLSv1.3"},
                null,
                NoopHostnameVerifier.INSTANCE
            );
            
            // Create registry for socket factories
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.getSocketFactory())
                .register("https", sslSocketFactory)
                .build();
            
            // Create connection manager
            PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
            connectionManager.setMaxTotal(20);
            connectionManager.setDefaultMaxPerRoute(10);
            
            // Create request config with timeout
            RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(Config.REQUEST_TIMEOUT)
                .setSocketTimeout(Config.REQUEST_TIMEOUT)
                .setConnectionRequestTimeout(Config.REQUEST_TIMEOUT)
                .build();
            
            // Create HTTP client
            return HttpClients.custom()
                .setSSLSocketFactory(sslSocketFactory)
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(requestConfig)
                .build();
                
        } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException e) {
            System.err.println("Error creating HTTP client: " + e.getMessage());
            // Fallback to default client
            return HttpClients.createDefault();
        }
    }
    
    public void processRequest(String request) {
        // Log the request for debugging
        System.out.println("Processing LLM request: " + request);
        
        // Simulate processing time
        try {
            Thread.sleep(1000); // Simulate 1 second processing time
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    public CompletableFuture<String> processRequestAsync(String request) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return callLLMAPI(request);
            } catch (Exception e) {
                System.err.println("Error calling LLM API: " + e.getMessage());
                e.printStackTrace();
                return "Sorry, I encountered an error while processing your request. Please check your LLM server connection and try again.";
            }
        });
    }
    
    public CompletableFuture<String> processStreamingRequestAsync(String request, ChatWindow chatWindow) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return callStreamingLLMAPI(request, chatWindow);
            } catch (Exception e) {
                System.err.println("Error calling streaming LLM API: " + e.getMessage());
                e.printStackTrace();
                return "Sorry, I encountered an error while processing your request. Please check your LLM server connection and try again.";
            }
        });
    }
    
    private String callLLMAPI(String message) throws Exception {
        // Determine the API type based on URL
        if (llmUrl.contains("openai.com") || llmUrl.contains("api.openai.com")) {
            return callOpenAI(message);
        } else if (llmUrl.contains("anthropic.com") || llmUrl.contains("api.anthropic.com")) {
            return callAnthropic(message);
        } else if (llmUrl.contains("nvidia.com") || llmUrl.contains("integrate.api.nvidia.com")) {
            return callNVIDIA(message);
        } else {
            return callCustomAPI(message);
        }
    }
    
    private String callStreamingLLMAPI(String message, ChatWindow chatWindow) throws Exception {
        // Determine the API type based on URL
        if (llmUrl.contains("openai.com") || llmUrl.contains("api.openai.com")) {
            return callStreamingOpenAI(message, chatWindow);
        } else if (llmUrl.contains("anthropic.com") || llmUrl.contains("api.anthropic.com")) {
            return callStreamingAnthropic(message, chatWindow);
        } else if (llmUrl.contains("nvidia.com") || llmUrl.contains("integrate.api.nvidia.com")) {
            return callStreamingNVIDIA(message, chatWindow);
        } else {
            return callStreamingCustomAPI(message, chatWindow);
        }
    }
    
    private String callStreamingOpenAI(String message, ChatWindow chatWindow) throws Exception {
        // For now, fall back to regular OpenAI call
        return callOpenAI(message);
    }
    
    private String callStreamingAnthropic(String message, ChatWindow chatWindow) throws Exception {
        // For now, fall back to regular Anthropic call
        return callAnthropic(message);
    }
    
    private String callStreamingCustomAPI(String message, ChatWindow chatWindow) throws Exception {
        // For now, fall back to regular custom API call
        return callCustomAPI(message);
    }
    
    private String callOpenAI(String message) throws Exception {
        JsonObject requestPayload = new JsonObject();
        requestPayload.addProperty("model", "gpt-3.5-turbo");
        
        JsonObject messageObj = new JsonObject();
        messageObj.addProperty("role", "user");
        messageObj.addProperty("content", message);
        
        JsonObject[] messages = new JsonObject[]{messageObj};
        requestPayload.add("messages", gson.toJsonTree(messages));
        
        HttpPost httpPost = new HttpPost(llmUrl);
        httpPost.setHeader("Content-Type", Config.API_CONTENT_TYPE);
        httpPost.setHeader("Authorization", "Bearer " + apiKey);
        
        String jsonPayload = gson.toJson(requestPayload);
        httpPost.setEntity(new StringEntity(jsonPayload, "UTF-8"));
        
        if (Config.DEBUG_MODE) {
            System.out.println("Sending OpenAI request to: " + llmUrl);
            System.out.println("Request payload: " + jsonPayload);
        }
        
        return executeRequest(httpPost);
    }
    
    private String callAnthropic(String message) throws Exception {
        JsonObject requestPayload = new JsonObject();
        requestPayload.addProperty("model", "claude-3-sonnet-20240229");
        requestPayload.addProperty("max_tokens", 1000);
        
        JsonObject messageObj = new JsonObject();
        messageObj.addProperty("role", "user");
        messageObj.addProperty("content", message);
        
        JsonObject[] messages = new JsonObject[]{messageObj};
        requestPayload.add("messages", gson.toJsonTree(messages));
        
        HttpPost httpPost = new HttpPost(llmUrl);
        httpPost.setHeader("Content-Type", Config.API_CONTENT_TYPE);
        httpPost.setHeader("x-api-key", apiKey);
        httpPost.setHeader("anthropic-version", "2023-06-01");
        
        String jsonPayload = gson.toJson(requestPayload);
        httpPost.setEntity(new StringEntity(jsonPayload, "UTF-8"));
        
        if (Config.DEBUG_MODE) {
            System.out.println("Sending Anthropic request to: " + llmUrl);
            System.out.println("Request payload: " + jsonPayload);
        }
        
        return executeRequest(httpPost);
    }
    
    private String callNVIDIA(String message) throws Exception {
        JsonObject requestPayload = new JsonObject();
        requestPayload.addProperty("model", "qwen/qwen2.5-coder-32b-instruct");
        requestPayload.addProperty("max_tokens", 512);
        requestPayload.addProperty("temperature", 1.00);
        requestPayload.addProperty("top_p", 1.00);
        requestPayload.addProperty("frequency_penalty", 0.00);
        requestPayload.addProperty("presence_penalty", 0.00);
        requestPayload.addProperty("stream", true);

        JsonObject messageObj = new JsonObject();
        messageObj.addProperty("role", "user");
        messageObj.addProperty("content", message);

        JsonObject[] messages = new JsonObject[]{messageObj};
        requestPayload.add("messages", gson.toJsonTree(messages));

        HttpPost httpPost = new HttpPost(llmUrl);
        httpPost.setHeader("Content-Type", Config.API_CONTENT_TYPE);
        httpPost.setHeader("Authorization", "Bearer " + apiKey);
        httpPost.setHeader("Accept", "text/event-stream");

        String jsonPayload = gson.toJson(requestPayload);
        httpPost.setEntity(new StringEntity(jsonPayload, "UTF-8"));

        if (Config.DEBUG_MODE) {
            System.out.println("Sending NVIDIA request to: " + llmUrl);
            System.out.println("Request payload: " + jsonPayload);
        }

        return executeRequest(httpPost);
    }
    
    private String callStreamingNVIDIA(String message, ChatWindow chatWindow) throws Exception {
        JsonObject requestPayload = new JsonObject();
        requestPayload.addProperty("model", "qwen/qwen2.5-coder-32b-instruct");
        requestPayload.addProperty("max_tokens", 512);
        requestPayload.addProperty("temperature", 1.00);
        requestPayload.addProperty("top_p", 1.00);
        requestPayload.addProperty("frequency_penalty", 0.00);
        requestPayload.addProperty("presence_penalty", 0.00);
        requestPayload.addProperty("stream", true);

        JsonObject messageObj = new JsonObject();
        messageObj.addProperty("role", "user");
        messageObj.addProperty("content", message);

        JsonObject[] messages = new JsonObject[]{messageObj};
        requestPayload.add("messages", gson.toJsonTree(messages));

        HttpPost httpPost = new HttpPost(llmUrl);
        httpPost.setHeader("Content-Type", Config.API_CONTENT_TYPE);
        httpPost.setHeader("Authorization", "Bearer " + apiKey);
        httpPost.setHeader("Accept", "text/event-stream");

        String jsonPayload = gson.toJson(requestPayload);
        httpPost.setEntity(new StringEntity(jsonPayload, "UTF-8"));

        if (Config.DEBUG_MODE) {
            System.out.println("Sending streaming NVIDIA request to: " + llmUrl);
            System.out.println("Request payload: " + jsonPayload);
        }

        return executeStreamingRequest(httpPost, chatWindow);
    }
    
    private String callCustomAPI(String message) throws Exception {
        // Create the request payload for custom APIs
        JsonObject requestPayload = new JsonObject();
        requestPayload.addProperty("message", message);
        requestPayload.addProperty("model", "default");
        
        // Create HTTP POST request
        HttpPost httpPost = new HttpPost(llmUrl);
        httpPost.setHeader("Content-Type", Config.API_CONTENT_TYPE);
        httpPost.setHeader("Accept", Config.API_ACCEPT);
        httpPost.setHeader("User-Agent", "LLM-Chat-App/1.0");
        
        // Add API key if provided
        if (apiKey != null && !apiKey.trim().isEmpty()) {
            httpPost.setHeader("Authorization", "Bearer " + apiKey);
        }
        
        // Add the request body
        String jsonPayload = gson.toJson(requestPayload);
        httpPost.setEntity(new StringEntity(jsonPayload, "UTF-8"));
        
        if (Config.DEBUG_MODE) {
            System.out.println("Sending custom API request to: " + llmUrl);
            System.out.println("Request payload: " + jsonPayload);
        }
        
        return executeRequest(httpPost);
    }
    
    private String executeRequest(HttpPost httpPost) throws Exception {
        // Execute the request
        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
            HttpEntity entity = response.getEntity();
            String responseBody = EntityUtils.toString(entity);
            
            if (Config.DEBUG_MODE) {
                System.out.println("Response status: " + response.getStatusLine());
                System.out.println("Response body: " + responseBody);
            }
            
            // Parse the response
            if (response.getStatusLine().getStatusCode() == 200) {
                return parseLLMResponse(responseBody);
            } else {
                return "Error: HTTP " + response.getStatusLine().getStatusCode() + " - " + responseBody;
            }
        }
    }
    
    private String executeStreamingRequest(HttpPost httpPost, ChatWindow chatWindow) throws Exception {
        // Execute the streaming request
        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
            if (Config.DEBUG_MODE) {
                System.out.println("Response status: " + response.getStatusLine());
                System.out.println("Response headers: " + response.getAllHeaders());
            }
            
            if (response.getStatusLine().getStatusCode() == 200) {
                return parseStreamingResponse(response, chatWindow);
            } else {
                String errorBody = EntityUtils.toString(response.getEntity());
                return "Error: HTTP " + response.getStatusLine().getStatusCode() + " - " + errorBody;
            }
        }
    }
    
    private String parseStreamingResponse(CloseableHttpResponse response, ChatWindow chatWindow) throws Exception {
        StringBuilder fullResponse = new StringBuilder();
        String currentLine = "";
        
        if (Config.DEBUG_MODE) {
            System.out.println("Starting to parse streaming response...");
        }
        
        try (java.io.BufferedReader reader = new java.io.BufferedReader(
                new java.io.InputStreamReader(response.getEntity().getContent()))) {
            
            String line;
            int lineCount = 0;
            while ((line = reader.readLine()) != null) {
                lineCount++;
                if (Config.DEBUG_MODE) {
                    System.out.println("Line " + lineCount + ": " + line);
                }
                
                if (line.startsWith("data: ")) {
                    String data = line.substring(6);
                    
                    if (Config.DEBUG_MODE) {
                        System.out.println("Processing data: " + data);
                    }
                    
                    if (data.equals("[DONE]")) {
                        if (Config.DEBUG_MODE) {
                            System.out.println("Stream completed with [DONE]");
                        }
                        break;
                    }
                    
                    try {
                        JsonObject jsonData = JsonParser.parseString(data).getAsJsonObject();
                        
                        if (jsonData.has("choices")) {
                            JsonObject choice = jsonData.getAsJsonArray("choices").get(0).getAsJsonObject();
                            
                            if (choice.has("delta") && choice.getAsJsonObject("delta").has("content")) {
                                JsonElement contentElement = choice.getAsJsonObject("delta").get("content");
                                if (!contentElement.isJsonNull()) {
                                    String content = contentElement.getAsString();
                                    fullResponse.append(content);
                                    
                                    if (Config.DEBUG_MODE) {
                                        System.out.println("Streaming content: " + content);
                                        System.out.println("Full response so far: " + fullResponse.toString());
                                    }
                                    
                                    // Update the chat window with streaming content
                                    chatWindow.updateStreamingMessage("LLM", fullResponse.toString());
                                    
                                    // 添加延迟以减慢流式输出速度
                                    try {
                                        Thread.sleep(50); // 50ms 延迟，可以根据需要调整
                                    } catch (InterruptedException e) {
                                        Thread.currentThread().interrupt();
                                        break;
                                    }
                                } else {
                                    if (Config.DEBUG_MODE) {
                                        System.out.println("Content is null, skipping");
                                    }
                                }
                            } else {
                                if (Config.DEBUG_MODE) {
                                    System.out.println("No content in delta, skipping");
                                }
                            }
                        } else {
                            if (Config.DEBUG_MODE) {
                                System.out.println("No choices in response");
                            }
                        }
                    } catch (Exception e) {
                        // Skip malformed JSON lines
                        if (Config.DEBUG_MODE) {
                            System.err.println("Skipping malformed line: " + data);
                            e.printStackTrace();
                        }
                    }
                } else {
                    if (Config.DEBUG_MODE) {
                        System.out.println("Line doesn't start with 'data: ', skipping");
                    }
                }
            }
        }
        
        if (Config.DEBUG_MODE) {
            System.out.println("Final streaming response: " + fullResponse.toString());
        }
        
        return fullResponse.toString();
    }
    
    private String parseLLMResponse(String responseBody) {
        try {
            // Try to parse as JSON first
            JsonObject jsonResponse = JsonParser.parseString(responseBody).getAsJsonObject();
            
            // Handle OpenAI response format
            if (jsonResponse.has("choices")) {
                JsonObject choice = jsonResponse.getAsJsonArray("choices").get(0).getAsJsonObject();
                JsonObject message = choice.getAsJsonObject("message");
                return message.get("content").getAsString();
            }
            
            // Handle Anthropic response format
            if (jsonResponse.has("content")) {
                JsonObject content = jsonResponse.getAsJsonArray("content").get(0).getAsJsonObject();
                return content.get("text").getAsString();
            }
            
            // Common response field names for custom APIs
            if (jsonResponse.has("response")) {
                return jsonResponse.get("response").getAsString();
            } else if (jsonResponse.has("message")) {
                return jsonResponse.get("message").getAsString();
            } else if (jsonResponse.has("content")) {
                return jsonResponse.get("content").getAsString();
            } else if (jsonResponse.has("text")) {
                return jsonResponse.get("text").getAsString();
            } else if (jsonResponse.has("result")) {
                return jsonResponse.get("result").getAsString();
            } else {
                // If no standard field found, return the whole response
                return responseBody;
            }
        } catch (Exception e) {
            // If JSON parsing fails, return the raw response
            System.err.println("Failed to parse JSON response: " + e.getMessage());
            return responseBody;
        }
    }
    

    
    public void close() {
        try {
            httpClient.close();
        } catch (Exception e) {
            System.err.println("Error closing HTTP client: " + e.getMessage());
        }
    }
} 