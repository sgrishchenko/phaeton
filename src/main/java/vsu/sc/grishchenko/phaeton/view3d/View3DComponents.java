package vsu.sc.grishchenko.phaeton.view3d;

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
import javafx.scene.transform.Rotate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static javafx.scene.SceneAntialiasing.BALANCED;

@Configuration
@PropertySource("classpath:forms/view3D.properties")
public class View3DComponents {

    @Value("${scene.width}")
    private double sceneWidth;
    @Value("${scene.height}")
    private double sceneHeight;
    @Value("${buttons.height}")
    private double buttonsHeight;

    @Value("${camera.near.clip}")
    private double cameraNearClip;
    @Value("${camera.far.clip}")
    private double cameraFarClip;
    @Value("${camera.initial.distance}")
    private double cameraInitialDistance;

    @Value("${axis.length}")
    private double axisLength;

    @Bean
    public Scene scene3D(@Qualifier("subScene3D") SubScene subScene,
                         @Qualifier("buttons") Parent buttons) {

        StackPane stackPane = new StackPane();

        stackPane.getChildren().addAll(subScene, buttons);
        stackPane.setAlignment(Pos.TOP_LEFT);

        Scene scene = new Scene(stackPane, sceneWidth, sceneHeight);
        scene.setFill(Color.WHITE);

        subScene.widthProperty().bind(scene.widthProperty());
        scene.heightProperty().addListener((observable, oldValue, newValue) ->
                subScene.setHeight((double) newValue - buttonsHeight));

        return scene;
    }

    @Bean
    public SubScene subScene3D(@Qualifier("axes") List<Cylinder> axes) {
        Group root = new Group();
        SubScene subScene = new SubScene(root, sceneWidth, sceneHeight - buttonsHeight, true, BALANCED);
        PerspectiveCamera camera = new PerspectiveCamera(true);
        subScene.setCamera(camera);
        subScene.setRoot(root);

        camera.setNearClip(cameraNearClip);
        camera.setFarClip(cameraFarClip);
        camera.setTranslateZ(cameraInitialDistance);

        root.getChildren().addAll(axes);

        return subScene;
    }

    @Bean
    public Parent buttons() {
        HBox buttons = new HBox();
        buttons.setSpacing(5);
        buttons.setPadding(new Insets(5));
        buttons.setAlignment(Pos.BOTTOM_CENTER);

        new LinkedHashMap<String, EventHandler<ActionEvent>>() {{
            put("save", e -> System.out.println("save"));
            put("play", e -> System.out.println("play"));
            put("pause", e -> System.out.println("pause"));
            put("reset", e -> System.out.println("reset"));
            put("prev", e -> System.out.println("prev"));
            put("next", e -> System.out.println("next"));
            put("prevRewind", e -> System.out.println("prevRewind"));
            put("nextRewind", e -> System.out.println("nextRewind"));
        }}.forEach((name, handler) -> {
            Image image = new Image(getClass().getResourceAsStream(String.format("/icons/%s.png", name)));
            Button button = new Button("", new ImageView(image));
            button.setOnAction(handler);
            buttons.getChildren().add(button);
        });

        return buttons;
    }

    @Bean
    public List<Cylinder> axes() {
        List<Cylinder> axisList = new ArrayList<>();
        axisList.add(new Cylinder(1, axisLength));
        axisList.add(new Cylinder(1, axisLength) {{
            setRotate(90);
        }});
        axisList.add(new Cylinder(1, axisLength) {{
            setRotationAxis(Rotate.X_AXIS);
            setRotate(90);
        }});

        axisList.forEach(axis -> axis.setMaterial(new PhongMaterial(Color.BLACK)));
        return axisList;
    }
}
