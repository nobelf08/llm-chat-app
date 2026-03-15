# LLM Chat Application

This project is a simple chat application that interacts with a large language model (LLM) to provide a conversational interface. The application features a graphical user interface (GUI) for users to send messages and receive responses in real-time.

## Project Structure

```
llm-chat-app
├── src
│   ├── Main.java          # Entry point of the application
│   ├── ui
│   │   └── ChatWindow.java # GUI for the chat interface
│   ├── service
│   │   └── LLMService.java  # Handles communication with the LLM
│   └── utils
│       └── StreamHandler.java # Processes streaming output from the LLM
├── pom.xml                # Maven configuration file
└── README.md              # Project documentation
```

## Requirements

- Java Development Kit (JDK) 8 or higher
- Maven for dependency management

## How to Run the Application

1. Clone the repository:
   ```
   git clone https://github.com/yourusername/llm-chat-app.git
   ```

2. Navigate to the project directory:
   ```
   cd llm-chat-app
   ```

3. Build the project using Maven:
   ```
   mvn clean install
   ```

4. Run the application:
   ```
   mvn exec:java -Dexec.mainClass="Main"
   ```

## Usage

- Upon launching the application, a chat window will appear.
- Type your message in the input field and press Enter to send it.
- The application will display the response from the language model in the chat window.

## Contributing

Feel free to submit issues or pull requests if you have suggestions or improvements for the application.

## License

This project is licensed under the MIT License. See the LICENSE file for details.

