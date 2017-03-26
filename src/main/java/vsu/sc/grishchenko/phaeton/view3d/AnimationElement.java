package vsu.sc.grishchenko.phaeton.view3d;

import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Sphere;

import java.util.List;
import java.util.Map;

public class AnimationElement {
    private Sphere atom;
    private Label label;
    private List<Point3D> trajectory;
    private Map<String, Cylinder> links;
    private Group group = new Group();

    public AnimationElement(Sphere atom, Label label, List<Point3D> trajectory, Map<String, Cylinder> links) {
        this.atom = atom;
        this.label = label;
        this.trajectory = trajectory;
        this.links = links;

        group.getChildren().addAll(atom, label);
        group.getChildren().addAll(links.values());
    }

    public Sphere getAtom() {
        return atom;
    }

    public Label getLabel() {
        return label;
    }

    public List<Point3D> getTrajectory() {
        return trajectory;
    }

    public Map<String, Cylinder> getLinks() {
        return links;
    }

    public Group getGroup() {
        return group;
    }
}
