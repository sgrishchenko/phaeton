package vsu.sc.grishchenko.phaeton.view3d;

import javafx.collections.ListChangeListener;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

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
    @Autowired
    @Qualifier("atoms")
    private Group atoms;

    @Value("${camera.initial.angle.x}")
    private double cameraInitialXAngle;
    @Value("${camera.initial.angle.y}")
    private double cameraInitialYAngle;
    @Value("${camera.initial.angle.z}")
    private double cameraInitialZAngle;

    @Value("${control.multiplier}")
    private double controlMultiplier;
    @Value("${shift.multiplier}")
    private double shiftMultiplier;

    @PostConstruct
    private void handleMouse() throws NonInvertibleTransformException {
        Group root = (Group) subScene.getRoot();
        Translate translate = new Translate();
        Rotate xRotate = new Rotate(cameraInitialXAngle, Rotate.X_AXIS);
        Rotate yRotate = new Rotate(cameraInitialYAngle, Rotate.Y_AXIS);
        Rotate zRotate = new Rotate(cameraInitialZAngle, Rotate.Z_AXIS);
        Rotate xTextRotate = (Rotate) xRotate.createInverse();
        Rotate yTextRotate = (Rotate) yRotate.createInverse();
        Rotate zTextRotate = (Rotate) zRotate.createInverse();
        root.getTransforms().addAll(translate, xRotate, yRotate, zRotate);

        atoms.getChildren().addListener((ListChangeListener<Node>) change -> {
            change.next();
            change.getAddedSubList()
                    .stream()
                    .flatMap(node -> node.lookupAll("Label").stream())
                    .forEach(node -> node.getTransforms().addAll(zTextRotate, yTextRotate, xTextRotate));
        });

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
                xRotate.setAngle(xRotate.getAngle() - deltaY);
                xTextRotate.setAngle(xTextRotate.getAngle() + deltaY);

                double sign = Math.signum(180 - Math.abs(xRotate.getAngle() % 360))
                        * Math.signum(xRotate.getAngle());

                zRotate.setAngle(zRotate.getAngle() - deltaX * sign);
                zTextRotate.setAngle(zTextRotate.getAngle() + deltaX * sign);
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
