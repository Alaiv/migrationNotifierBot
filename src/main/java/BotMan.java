import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class BotMan {
    private static final String CHAT_ID = "{ENTER UR CHAT ID}";
    private static final String TOKEN = "{ENTER UR BOT TOKEN}";

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
            int errCount = db.getBadJobStateCount();

            String message = "Возникла ошибка создания среза!";
            boolean isTime = false;
            boolean isFinished = false;

            //send notifications every 10 min
            if (currentTime - startTime >= 600000) {
                //get jobs count with state = 0 (waiting)
                int waitingCount = db.getWaitJobStateCount();
                //get jobs count with state = 1 (process)
                int progressCount = db.getInProgressJobStateCount();
                //check if no jobs active
                isFinished = errCount == 0 && waitingCount == 0 && progressCount == 0;

                String err = errCount > 0 ? "Возникла ошибка создания среза!" : "Ошибок нет";
                message = isFinished ? "Все работы завершены." : String.format("""
                        - Количество дорог в ожидании: %s
                        - Количество дорог в процессе: %s
                        - %s""", waitingCount, progressCount, err);

                startTime = currentTime;
                isTime = true;
            }

            //send bot message
            sendMessage(message, errCount, isTime, client);
            if (isFinished) break;

            //wait for 2 min
            Thread.sleep(120000);
        }
    }

    private static void sendMessage(String message, int errCount, boolean isTime, HttpClient client)
            throws IOException, InterruptedException
    {
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
        if (errCount > 0 || isTime) {
            client.send(request, HttpResponse.BodyHandlers.ofString());
        }
    }
}
