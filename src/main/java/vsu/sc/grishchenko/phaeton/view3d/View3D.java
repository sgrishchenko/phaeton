package vsu.sc.grishchenko.phaeton.view3d;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Sphere;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static javafx.scene.SceneAntialiasing.BALANCED;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class View3D extends Application {
    @Autowired
    @Qualifier("scene3D")
    private Scene scene3D;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Scene scene = scene3D;
        primaryStage.setTitle("Трехмерная анимированная модель");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
