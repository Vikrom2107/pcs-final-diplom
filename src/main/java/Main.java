import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        BooleanSearchEngine engine = new BooleanSearchEngine(new File("pdfs"));
//        System.out.println(engine.search("бизнес"));
        // здесь создайте сервер, который отвечал бы на нужные запросы
        // слушать он должен порт 8989
        // отвечать на запросы /{word} -> возвращённое значение метода search(word) в JSON-формате
        try (ServerSocket serverSocket = new ServerSocket(8989);) {
            while (true) {
                try (
                        Socket socket = serverSocket.accept();
                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        PrintWriter out = new PrintWriter(socket.getOutputStream());
                ) {
                    final String word = in.readLine();
//                    List<PageEntry> resultSearch = engine.search(word); // проверка только одного слова
                    List<PageEntry> resultSearch = engine.searchWithStopWords(word);
                    GsonBuilder builder = new GsonBuilder();
                    Gson gson = builder.create();
                    String answer = gson.toJson(resultSearch);
                    out.println(answer);
                }
            }
        } catch (Exception e) {
            System.out.println("Не могу стартовать сервер");
            e.printStackTrace();
        }
    }
}