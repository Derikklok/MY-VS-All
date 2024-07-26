import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import com.google.gson.Gson;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.awt.Desktop;

public class SimpleHttpServer {
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/chatbot", new ChatbotHandler());
        server.createContext("/", new StaticFileHandler("C:\\Users\\User\\Desktop\\Coding\\JAVA\\AI\\ChatBot-V1.0\\src\\main\\java\\index.html"));
        server.setExecutor(null); // creates a default executor
        server.start();
        System.out.println("Server started on port 8080");

        openBrowser("http://localhost:8080/");
    }

    private static void openBrowser(String url) {
        try {
            URI uri = new URI(url);
            Desktop desktop = Desktop.getDesktop();
            if (desktop.isSupported(Desktop.Action.BROWSE)) {
                desktop.browse(uri);
            } else {
                System.err.println("BROWSE action not supported");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static class StaticFileHandler implements HttpHandler {
        private final String filePath;

        public StaticFileHandler(String filePath) {
            this.filePath = filePath;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            File file = new File(filePath);
            if (!file.exists()) {
                String response = "404 (Not Found)\n";
                exchange.sendResponseHeaders(404, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            } else {
                exchange.sendResponseHeaders(200, file.length());
                OutputStream os = exchange.getResponseBody();
                Files.copy(file.toPath(), os);
                os.close();
            }
        }
    }

    static class ChatbotHandler implements HttpHandler {
        private final ChatBot chatBot = new ChatBot();

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                handlePostRequest(exchange);
            } else {
                exchange.sendResponseHeaders(405, -1); // Method Not Allowed
            }
        }

        private void handlePostRequest(HttpExchange exchange) throws IOException {
            // Read request body
            InputStream requestBody = exchange.getRequestBody();
            String body = new BufferedReader(new InputStreamReader(requestBody))
                    .lines()
                    .collect(Collectors.joining("\n"));

            System.out.println("Received request: " + body);

            Gson gson = new Gson();
            Message message = gson.fromJson(body, Message.class);

            String response = chatBot.getResponse(message.getMessage());

            // Send response
            exchange.sendResponseHeaders(200, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();

            System.out.println("Sent response: " + response);
        }
    }

    private static class Message {
        private String message;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    private void handlePostRequest(HttpExchange exchange) throws IOException {
        // Read request body
        InputStream requestBody = exchange.getRequestBody();
        String body = new BufferedReader(new InputStreamReader(requestBody))
                .lines()
                .collect(Collectors.joining("\n"));

        System.out.println("Received request: " + body);

        Gson gson = new Gson();
        Message message = gson.fromJson(body, Message.class);

        String response = ChatBot.getResponse(message.getMessage());

        // Send response with HTML content
        exchange.getResponseHeaders().set("Content-Type", "text/html");
        exchange.sendResponseHeaders(200, response.getBytes(StandardCharsets.UTF_8).length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes(StandardCharsets.UTF_8));
        os.close();

        System.out.println("Sent response: " + response);
    }

}
