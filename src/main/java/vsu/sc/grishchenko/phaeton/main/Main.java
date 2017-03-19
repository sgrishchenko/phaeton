package vsu.sc.grishchenko.phaeton.main;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import vsu.sc.grishchenko.phaeton.util.logging.Log;
import vsu.sc.grishchenko.phaeton.util.SpringFXMLLoader;

import java.io.IOException;
import java.util.logging.Logger;

@Component
@Configuration
public class Main extends Application {
    @Autowired
    @Qualifier("mainScene")
    private Scene mainScene;


    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Молекулярные кластеры");
        primaryStage.setScene(mainScene);
        primaryStage.show();
    }

    @Bean
    public Scene mainScene(SpringFXMLLoader loader) throws IOException {
        Parent root = loader.load("forms/main.fxml");
        return new Scene(root);
    }
}
