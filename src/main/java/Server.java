import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;



public class Server {

    static String httpOK = "HTTP/1.1 200 OK\r\n\r\n";

    public static void main(String[] args) throws IOException {

        try (ServerSocket serverSocket = new ServerSocket(8080)) {
            while (true) {
                try (Socket client = serverSocket.accept()) {
                    handleClient(client);
                }
            }
        }
    }

    private static void handleClient(Socket client) throws IOException {
        BufferedReader buffRead = new BufferedReader(new InputStreamReader(client.getInputStream()));

        StringBuilder requestBuilder = new StringBuilder();
        String line;
        while (!(line = buffRead.readLine()).isBlank()) {
            System.out.println(line);
        }
        sendHTML(client);


    }

    private static void sendHTML(Socket client) throws IOException {
        OutputStream clientOutput = client.getOutputStream();
        clientOutput.write(("HTTP/1.1 \r\n" + "200 OK").getBytes(StandardCharsets.UTF_8));
        clientOutput.write(("ContentType: " + "text/html" + "\r\n").getBytes(StandardCharsets.UTF_8));
        clientOutput.write("\r\n".getBytes(StandardCharsets.UTF_8));
        clientOutput.write(Files.readAllBytes(Paths.get("src/web/views/index.html")));
        clientOutput.write("\r\n\r\n".getBytes(StandardCharsets.UTF_8));
        clientOutput.flush();
        client.close();

    }
}

