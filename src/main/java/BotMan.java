import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class BotMan {
    private static final String CHAT_ID = "-4047469857";
    private static final String TOKEN = "6615790550:AAFXfrI7G9ndWOeOasFV-H6BnCDqdWlHytU";

    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("Бот успешно запущен.");
        DB db = new DB();

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .version(HttpClient.Version.HTTP_2)
                .build();

        long startTime = System.currentTimeMillis();


        while (true) {
            //get current time
            long currentTime = System.currentTimeMillis();
            //get jobs count with state = 3 (error)
            int count = db.getBadJobStateCount();
            String message = "**Возникла ошиба создания среза**";
            boolean isTime = false;

            //send notifications every 10 min
            if (currentTime - startTime >= 600000) {
                //get jobs count with state = 0 (waiting)
                int count2 = db.getGoodJobStateCount();
                String err = count > 0 ? "**Возникла ошибка создания среза**" : "Ошибок создания среза нет";
                message = "Количество дорог в ожидании: " + count2 + "\n" + err;
                startTime = currentTime;
                isTime = true;
            }

            //build URI for telegram bot
            UriBuilder builder = UriBuilder
                    .fromUri("https://api.telegram.org")
                    .path("/{token}/sendMessage")
                    .queryParam("chat_id", CHAT_ID)
                    .queryParam("text", message);

            //build get request with URI
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(builder.build("bot" + TOKEN))
                    .timeout(Duration.ofSeconds(5))
                    .build();

            //send request (notification)
            if (count > 0 || isTime) {
                client.send(request, HttpResponse.BodyHandlers.ofString());
            }

            //wait for 2 min
            Thread.sleep(120000);
        }
    }
}
