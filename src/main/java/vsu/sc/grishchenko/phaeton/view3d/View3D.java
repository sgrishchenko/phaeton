package vsu.sc.grishchenko.phaeton.view3d;

import javafx.application.Application;
import javafx.scene.*;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import vsu.sc.grishchenko.phaeton.model.Cluster;

import java.util.*;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class View3D extends Application {
    @Autowired
    @Qualifier("scene3D")
    private Scene scene3D;
    @Autowired
    private Animation animation;

    public void start() throws Exception {
        start(new Stage());
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Scene scene = scene3D;
        primaryStage.setTitle("Трехмерная анимированная модель");
        primaryStage.setScene(scene);
        primaryStage.show();

        animation.run();
        primaryStage.setOnCloseRequest(event -> animation.stop());
    }

    public void setClusters(List<Cluster> clusters) {
        animation.setClusters(clusters);
    }
}
