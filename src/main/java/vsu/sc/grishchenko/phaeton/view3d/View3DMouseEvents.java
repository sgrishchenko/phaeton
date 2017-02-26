package vsu.sc.grishchenko.phaeton.view3d;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.layout.StackPane;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@PropertySource("classpath:forms/view3D.properties")
public class View3DMouseEvents {
    private double mouseX;
    private double mouseY;
    private double mouseOldX;
    private double mouseOldY;

    @Autowired
    @Qualifier("scene3D")
    private Scene scene;
    @Autowired
    @Qualifier("subScene3D")
    private SubScene subScene;

    @Value("${camera.initial.x.angle}")
    private double cameraInitialXAngle;
    @Value("${camera.initial.y.angle}")
    private double cameraInitialYAngle;

    @Value("${control.multiplier}")
    private double controlMultiplier;
    @Value("${shift.multiplier}")
    private double shiftMultiplier;

    @PostConstruct
    private void handleMouse() {
        Group root = (Group) subScene.getRoot();
        Translate translate = new Translate();
        Rotate xRotate = new Rotate(cameraInitialXAngle, Rotate.X_AXIS);
        Rotate yRotate = new Rotate(cameraInitialYAngle, Rotate.Y_AXIS);
        root.getTransforms().addAll(translate, xRotate, yRotate);

        Set<Node> textNodes = root.lookupAll("Text");
        Map<Node, Rotate> yTextRotateMap = textNodes.stream()
                .collect(Collectors.toMap(Function.identity(), node -> new Rotate(-cameraInitialYAngle, Rotate.Y_AXIS)));
        yTextRotateMap.forEach((node, rotate) -> node.getTransforms().add(rotate));

        Map<Node, Rotate> xTextRotateMap = textNodes.stream()
                .collect(Collectors.toMap(Function.identity(), node -> new Rotate(-cameraInitialYAngle, Rotate.X_AXIS)));
        xTextRotateMap.forEach((node, rotate) -> node.getTransforms().add(rotate));

        scene.setOnMousePressed(event -> {
            mouseOldX = event.getSceneX();
            mouseOldY = event.getSceneY();
            mouseX = event.getSceneX();
            mouseY = event.getSceneY();
        });
        scene.setOnMouseDragged(event -> {
            mouseOldX = mouseX;
            mouseOldY = mouseY;
            mouseX = event.getSceneX();
            mouseY = event.getSceneY();

            double modifier = 1.0;
            if (event.isControlDown()) modifier = controlMultiplier;
            if (event.isShiftDown()) modifier = shiftMultiplier;

            double deltaX = (mouseOldX - mouseX) * modifier;
            double deltaY = (mouseOldY - mouseY) * modifier;

            if (event.isPrimaryButtonDown()) {
                xTextRotateMap.values().forEach(rotate -> rotate.setAngle(rotate.getAngle() + deltaY));
                yTextRotateMap.values().forEach(rotate -> rotate.setAngle(rotate.getAngle() - deltaX));

                xRotate.setAngle(xRotate.getAngle() - deltaY);
                yRotate.setAngle(yRotate.getAngle() + deltaX);
            } else if (event.isSecondaryButtonDown()) {
                double delta = Math.abs(deltaX) > Math.abs(deltaY) ? deltaX : deltaY;
                root.setTranslateZ(root.getTranslateZ() + delta * modifier);
            } else if (event.isMiddleButtonDown()) {
                translate.setX(translate.getX() - deltaX * modifier);
                translate.setY(translate.getY() - deltaY * modifier);
            }
        });
    }
}