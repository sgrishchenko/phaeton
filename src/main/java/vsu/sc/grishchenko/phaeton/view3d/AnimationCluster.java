package vsu.sc.grishchenko.phaeton.view3d;

import javafx.geometry.Point3D;

import java.util.List;

public class AnimationCluster {
    private List<Point3D> gravityCenterPath;
    private List<AnimationElement> elements;

    public AnimationCluster(List<Point3D> gravityCenterPath, List<AnimationElement> elements) {
        this.gravityCenterPath = gravityCenterPath;
        this.elements = elements;
    }

    public List<Point3D> getGravityCenterPath() {
        return gravityCenterPath;
    }

    public List<AnimationElement> getElements() {
        return elements;
    }
}
