import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Server {

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

        String line;
        List<String> lines = new ArrayList<>();
        while (!(line = buffRead.readLine()).isBlank()) {
            lines.add(line);
        }
        lines.forEach(System.out::println);
        UrlRequest request = UrlRequest.requestFromUrl(lines.get(0));
        sendHTML(client, request);
    }

    private static void sendHTML(Socket client, UrlRequest request) throws IOException {
        OutputStream clientOutput = client.getOutputStream();
        clientOutput.write(("HTTP/1.1 \r\n" + "200 OK").getBytes(StandardCharsets.UTF_8));
        clientOutput.write(("ContentType: " + "text/html" + "\r\n").getBytes(StandardCharsets.UTF_8));
        clientOutput.write("\r\n".getBytes(StandardCharsets.UTF_8));

        clientOutput.write(HtmlGenerator.replaceCallable(request.path()).getBytes(StandardCharsets.UTF_8));

        clientOutput.write("\r\n\r\n".getBytes(StandardCharsets.UTF_8));

        clientOutput.flush();
        client.close();
    }
}

