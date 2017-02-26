package vsu.sc.grishchenko.phaeton.view3d;

import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Sphere;
import javafx.scene.text.Text;

import java.util.List;
import java.util.Map;

public class AnimationElement {
    private Sphere atom;
    private Text text;
    private List<Point3D> trajectory;
    private Map<String, Cylinder> links;
    private Group group = new Group();

    public AnimationElement(Sphere atom, Text text, List<Point3D> trajectory, Map<String, Cylinder> links) {
        this.atom = atom;
        this.text = text;
        this.trajectory = trajectory;
        this.links = links;

        group.getChildren().addAll(atom, text);
        group.getChildren().addAll(links.values());
    }

    public Sphere getAtom() {
        return atom;
    }

    public void setAtom(Sphere atom) {
        this.atom = atom;
    }

    public Text getText() {
        return text;
    }

    public void setText(Text text) {
        this.text = text;
    }

    public List<Point3D> getTrajectory() {
        return trajectory;
    }

    public void setTrajectory(List<Point3D> trajectory) {
        this.trajectory = trajectory;
    }

    public Map<String, Cylinder> getLinks() {
        return links;
    }

    public void setLinks(Map<String, Cylinder> links) {
        this.links = links;
    }

    public Group getGroup() {
        return group;
    }
}