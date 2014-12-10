import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by Earlviktor on 10.12.2014.
 */
public class FacebookAuth {

    final static String authURL = "https://www.facebook.com/dialog/oauth?" +
            "client_id=1575232952710375&redirect_uri=http://127.0.0.1:1234/code" +
            "&response_type=code&scope=public_profile,email";

    public static void main(String[] args) {
        CodeServer server = new CodeServer();
        Thread serverThread = new Thread(server);
        serverThread.start();
        if(Desktop.isDesktopSupported()){
            try {
                Desktop.getDesktop().browse(new URI(authURL));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }
}
