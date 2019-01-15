import com.vk.api.sdk.client.actors.UserActor;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;

public class VkAuthApp extends Application {
    private static final Integer APP_ID = 6815446;
    private static final String REDIRECT_URL = "https://oauth.vk.com/blank.html";
    private static final String VK_AUTH_URL =
            "https://oauth.vk.com/authorize?" +
                    "client_id=" + APP_ID + "&" +
                    "display=page&" +
                    "redirect_uri=" + REDIRECT_URL + "&" +
                    "scope=friends&" +
                    "response_type=token&" +
                    "v=5.59";
    public static UserActor userActor;


    public void start(Stage primaryStage) {
        //userActor = ActorLoader.tryLoadUserActor();
        //if (userActor != null) {
        //    primaryStage.close();
        //} else {
            logIn(primaryStage);
        //}
    }

    private void logIn(Stage stage) {
        WebView view = new WebView();
        WebEngine engine = view.getEngine();
        engine.load(VK_AUTH_URL);
        stage.setScene(new Scene(view));
        stage.show();

        engine.locationProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.startsWith(REDIRECT_URL)) {
                stage.close();
                setUserActor(newValue);
                ActorLoader.dumpUserActor(userActor);
            }
        });
    }


    private void setUserActor(String tokenUrl) {
        Map<String, String> params = URIDeserializer.getParams(tokenUrl);
        Integer userId = Integer.parseInt(params.get("user_id"));
        String accessToken = params.get("access_token");
        userActor = new UserActor(userId, accessToken);
    }
}

