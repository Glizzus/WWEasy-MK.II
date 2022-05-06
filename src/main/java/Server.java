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

    static String httpOK = "HTTP/1.1 \r\n" + "200 OK";

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
        List<String> lines = new ArrayList<>();
        while (!(line = buffRead.readLine()).isBlank()) {
            lines.add(line);
        }
        lines.forEach(System.out::println);
        lines.forEach(x -> {
            if (isPost(x)) System.out.println(getRoute(x));
        });
        sendHTML(client);

    }

    private static boolean isPost(String line) {
        return line.startsWith("POST");
    }

    private static boolean isGet(String line) {
        return line.startsWith("GET");
    }

    private static String getRoute(String line) {
        return line.split("/")[1].split(" ")[0];
    }

    private static void sendHTML(Socket client) throws IOException {
        OutputStream clientOutput = client.getOutputStream();
        clientOutput.write(httpOK.getBytes(StandardCharsets.UTF_8));
        clientOutput.write(("ContentType: " + "text/html" + "\r\n").getBytes(StandardCharsets.UTF_8));
        clientOutput.write("\r\n".getBytes(StandardCharsets.UTF_8));
        clientOutput.write(new HtmlGenerator().parseForCallable(("src/web/views/index.html")).getBytes(StandardCharsets.UTF_8));
        clientOutput.write("\r\n\r\n".getBytes(StandardCharsets.UTF_8));

        clientOutput.flush();
        client.close();
    }




}

