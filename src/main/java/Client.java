import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {

    public static void main(String[] args) {

        try (Socket clientSocket = new Socket("127.0.0.1",8989);
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
        ) {
            String word = "бизнес хранить";
            out.println(word);
            String answer = in.readLine();
            System.out.println(answer);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
