import com.vk.api.sdk.client.actors.UserActor;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.util.Map;

public class VkAuthApp extends Application {
    public static final Integer APP_ID = 6815446;
    public static final String REDIRECT_URL = "https://oauth.vk.com/blank.html";
    public static final String VK_AUTH_URL =
            "https://oauth.vk.com/authorize?" +
                    "client_id=" + APP_ID + "&" +
                    "display=page&" +
                    "redirect_uri=" + REDIRECT_URL + "&" +
                    "scope=friends&" +
                    "response_type=token&" +
                    "v=5.59";
    public static String tokenUrl;
    public static UserActor userActor;


    public void start(Stage primaryStage) {
        WebView view = new WebView();
        WebEngine engine = view.getEngine();
        engine.load(VK_AUTH_URL);
        primaryStage.setScene(new Scene(view));
        primaryStage.show();

        engine.locationProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.startsWith(REDIRECT_URL)) {
                tokenUrl = newValue;
                primaryStage.close();
                setUserActor();
            }
        });
    }

    private void setUserActor(){
        Map<String, String> params = URIDeserializer.getParams(tokenUrl);
        Integer userId = Integer.parseInt(params.get("user_id"));
        String accessToken = params.get("access_token");
        userActor = new UserActor(userId, accessToken);
    }
}

