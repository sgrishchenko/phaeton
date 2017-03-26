package vsu.sc.grishchenko.phaeton.view3d;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static javafx.scene.SceneAntialiasing.BALANCED;

@Configuration
@PropertySource("classpath:forms/view3D.properties")
public class View3DComponents {
    private boolean isButtonPressed;

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
    @Value("${animation.timeStep}")
    private int timeStep;

    @Autowired
    private Animation animation;

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
    public SubScene subScene3D(@Qualifier("axes") List<Cylinder> axes,
                               @Qualifier("atoms") Group atoms) {
        Group root = new Group();
        SubScene subScene = new SubScene(root, sceneWidth, sceneHeight - buttonsHeight, true, BALANCED);
        PerspectiveCamera camera = new PerspectiveCamera(true);
        subScene.setCamera(camera);
        subScene.setRoot(root);

        camera.setNearClip(cameraNearClip);
        camera.setFarClip(cameraFarClip);
        camera.setTranslateZ(cameraInitialDistance);

        root.getChildren().addAll(axes);
        root.getChildren().add(atoms);

        return subScene;
    }

    @Bean
    public Parent buttons() {
        HBox buttons = new HBox();
        buttons.setSpacing(5);
        buttons.setPadding(new Insets(5));
        buttons.setAlignment(Pos.BOTTOM_CENTER);

        new LinkedHashMap<String, Consumer<Button>>() {{
            put("prev-rewind", button -> rewindHandler(button, stopper -> animation.prevRewind(stopper)));
            put("prev", button -> simpleHandler(button, animation::prev));
            put("play", button -> simpleHandler(button, animation::play));
            put("pause", button -> simpleHandler(button, animation::pause));
            put("reset", button -> simpleHandler(button, animation::reset));
            put("next", button -> simpleHandler(button, animation::next));
            put("next-rewind", button -> rewindHandler(button, stopper -> animation.nextRewind(stopper)));
            put("save", button -> simpleHandler(button, () -> System.out.println("save")));
            put("info", button -> simpleHandler(button, () -> System.out.println("info")));
        }}.forEach((name, handler) -> {
            Image image = new Image(String.format("/icons/%s.png", name));
            Button button = new Button("", new ImageView(image));
            handler.accept(button);
            buttons.getChildren().add(button);
        });

        return buttons;
    }

    private void simpleHandler(Button button, Runnable action) {
        button.setOnAction(e -> action.run());
    }

    private void rewindHandler(Button button, Consumer<Supplier<Boolean>> rewindAction) {
        button.setOnMousePressed(e -> {
            isButtonPressed = true;
            rewindAction.accept(() -> isButtonPressed);
        });

        button.setOnMouseReleased(e -> isButtonPressed = false);
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

    @Bean
    public Group atoms() {
        return new Group();
    }
}
