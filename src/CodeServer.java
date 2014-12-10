import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Map;

/**
 * Created by Earlviktor on 10.12.2014.
 */
public class CodeServer implements Runnable {

    final String getURL = "https://graph.facebook.com/oauth/access_token?" +
            "client_id=1575232952710375" +
            "&redirect_uri=http://127.0.0.1:1234/code" +
            "&client_secret=db10e94c6eb6513440ff3b90ca6d11cd" +
            "&code=";
    final String meURL = "https://graph.facebook.com/v2.2/me?access_token=";

    HttpServer server;

    @Override
    public void run() {

        try {
            server = HttpServer.create(new InetSocketAddress(1234),0);
            server.createContext("/code", new CodeHandler());
            server.setExecutor(null);
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    class CodeHandler implements HttpHandler{

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            URI uri = httpExchange.getRequestURI();
            System.out.println("Successfully connected to URI: "+uri.toString());
            String code = uri.getQuery().split("=")[1];
            if(code.equals("user_denied")) {
                System.out.println("Error: user refused to log in");
                return;
            }
            String response = "Everything's ok! Return to your app";
            httpExchange.sendResponseHeaders(200, response.length());
            OutputStream os = httpExchange.getResponseBody();
            os.write(response.getBytes());
            os.close();

            HttpClient client = HttpClients.createDefault();
            HttpGet get = new HttpGet(getURL+code);
            HttpResponse getResponse = client.execute(get);
            System.out.println(getResponse.getStatusLine());
            String responseText = new BufferedReader(new InputStreamReader(getResponse.getEntity().getContent())).readLine();
            System.out.println(responseText);
            String token = responseText.split("[=&]")[1];

            System.out.println("\nNow will get some authorized info!\n");

            get = new HttpGet(meURL+token);
            getResponse = client.execute(get);
            BufferedReader reader = new BufferedReader(new InputStreamReader(getResponse.getEntity().getContent()));
            System.out.println(getResponse.getStatusLine());
            System.out.println(reader.readLine());
            System.out.println("That's all, folks!");

            CodeServer.this.server.stop(0);

        }
    }
}
