package vsu.sc.grishchenko.phaeton.view3d;

import javafx.collections.ObservableList;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Sphere;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import vsu.sc.grishchenko.phaeton.model.Cluster;
import vsu.sc.grishchenko.phaeton.model.MotionEquation;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static javafx.application.Platform.runLater;

@Component
@PropertySource("classpath:forms/view3D.properties")
public class Animation {
    @Value("${animation.timeStep}")
    private int timeStep;
    @Value("${animation.scale}")
    private double scale;

    private Map<String, MotionEquation> motionEquationMap;
    private List<AnimationCluster> animationClusters;
    private LinkedList<List<Sphere>> frames = new LinkedList<>();

    private int numberSteps;

    private int timePointer = 0;
    private boolean isPaused = false;
    private boolean isStopped = false;

    @Autowired
    @Qualifier("atoms")
    private Group atoms;
    @Autowired
    private AnimationElementFactory animationElementFactory;

    public void setClusters(List<Cluster> clusters) {
        atoms.getChildren().clear();

        motionEquationMap = clusters
                .stream()
                .flatMap(cluster -> cluster.getMotionEquations().stream())
                .collect(Collectors.toMap(MotionEquation::getLabel, Function.identity()));

        numberSteps = motionEquationMap.values()
                .stream()
                .mapToInt(t -> t.getPath().size())
                .max()
                .orElse(0);

        animationClusters = clusters
                .stream()
                .map(cluster -> new AnimationCluster(getGravityCenterPath(cluster), cluster.getMotionEquations()
                        .stream()
                        .map(equation -> {
                            Sphere atom = animationElementFactory.createAtom(equation.getColor());
                            Text label = animationElementFactory.createLabel(equation.getLabel(), atom);
                            List<Point3D> trajectory = equation.getPath();
                            Map<String, Cylinder> links = equation.getLinkLabels()
                                    .stream()
                                    .collect(Collectors.toMap(Function.identity(),
                                            linkLabel -> animationElementFactory.createLink()));

                            return new AnimationElement(atom, label, trajectory, links);
                        })
                        .peek(animationElement -> atoms.getChildren().add(animationElement.getGroup()))
                        .collect(Collectors.toList())))
                .collect(Collectors.toList());
    }


    private List<Point3D> getGravityCenterPath(Cluster cluster) {
        double weightSum = cluster.getMotionEquations()
                .stream()
                .mapToDouble(MotionEquation::getWeight)
                .sum();

        return IntStream.range(0, numberSteps)
                .mapToObj(index -> cluster.getMotionEquations()
                        .stream()
                        .map(t -> t.getPath().get(index).multiply(t.getWeight() * scale))
                        .reduce(Point3D.ZERO, Point3D::add)
                        .multiply(1 / weightSum))
                .collect(Collectors.toList());
    }

    private List<Sphere> updateAtomsPosition(boolean addTail) {
        List<Sphere> frame = new ArrayList<>();

        for (AnimationCluster animationCluster : animationClusters) {
            for (AnimationElement animationElement : animationCluster.getElements()) {

                Sphere atom = animationElement.getAtom();
                double radius = atom.getRadius();
                List<Point3D> trajectory = animationElement.getTrajectory();

                for (Node node : animationElement.getGroup().getChildren()) {
                    node.setTranslateX(trajectory.get(timePointer).getX() * scale);
                    node.setTranslateY(trajectory.get(timePointer).getY() * scale);
                    node.setTranslateZ(trajectory.get(timePointer).getZ() * scale);
                }

                Text label = animationElement.getLabel();
                label.setTranslateX(label.getTranslateX() + radius);
                label.setTranslateY(label.getTranslateY() + radius);
                label.setTranslateZ(label.getTranslateZ() + radius);

                for (Map.Entry<String, Cylinder> linkEntry : animationElement.getLinks().entrySet()) {
                    Point3D linkedPoint = motionEquationMap.get(linkEntry.getKey()).getPath().get(timePointer).multiply(scale);
                    Point3D point = new Point3D(atom.getTranslateX(), atom.getTranslateY(), atom.getTranslateZ());

                    Cylinder link = linkEntry.getValue();
                    link.getTransforms().clear();
                    link.setHeight(linkedPoint.distance(point));
                    link.setTranslateY(link.getTranslateY() + link.getHeight() / 2);

                    double dx = atom.getTranslateX() - linkedPoint.getX();
                    double dz = atom.getTranslateZ() - linkedPoint.getZ();
                    double dxz = Math.sqrt(dx * dx + dz * dz);
                    double dy = atom.getTranslateY() - linkedPoint.getY();

                    double xAngle = Math.toDegrees(Math.atan2(dy, dxz));
                    double zAngle = Math.toDegrees(Math.atan2(dx, dz));

                    link.getTransforms().add(new Rotate(-90, 0, -link.getHeight() / 2, 0, Rotate.X_AXIS));
                    link.getTransforms().add(new Rotate(zAngle, 0, -link.getHeight() / 2, 0, Rotate.Z_AXIS));
                    link.getTransforms().add(new Rotate(-xAngle, 0, -link.getHeight() / 2, 0, Rotate.X_AXIS));
                }
            }

            if (!addTail) continue;
            Point3D oldGravityCenter = animationCluster.getGravityCenterPath().get(Math.max(0, timePointer - 1));
            Point3D newGravityCenter = animationCluster.getGravityCenterPath().get(timePointer);

            if (!oldGravityCenter.equals(newGravityCenter)) {
                Sphere tail = animationElementFactory.createTail(newGravityCenter);
                atoms.getChildren().add(tail);
                frame.add(tail);
            }
        }

        return frame;
    }

    @Async
    public void start() {
        while (!isStopped) {
            try {
                Thread.sleep(timeStep);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (!isPaused) runLater(this::next);
        }
    }

    public void stop() {
        isStopped = true;
    }

    public void play() {
        isPaused = false;
    }

    public void pause() {
        isPaused = true;
    }

    public void next() {
        if (timePointer >= numberSteps - 1) {
            isPaused = true;
            return;
        }
        timePointer++;

        frames.push(updateAtomsPosition(true));
    }

    public void prev() {
        if (timePointer < 1) return;
        timePointer--;

        updateAtomsPosition(false);
        frames.pop().forEach(atom -> atoms.getChildren().remove(atom));
    }

    public void reset() {
        timePointer = 0;
        frames.stream().flatMap(Collection::stream).forEach(atom -> atoms.getChildren().remove(atom));
        frames.clear();

        updateAtomsPosition(false);
    }
}
