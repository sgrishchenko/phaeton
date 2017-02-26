package vsu.sc.grishchenko.phaeton.view3d;

import javafx.collections.ObservableList;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Sphere;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
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
public class RunAnimate /*implements Runnable*/ {
    private double radius;
    private double tailRadius;
    private double linkRadius;

    private List<AnimationElement> animationElements;
    private final LinkedList<List<Sphere>> frames = new LinkedList<>();

    private Map<String, Point3D> gravityCenterMap;
    private ObservableList<Node> nodes;

    private int timeStep;
    private long numberSteps;
    private double scale;

    private int timePointer = 0;
    private boolean isPaused = false;
    private boolean isStopped = false;

    public RunAnimate(List<Cluster> clusters, Group root, int timeStep, double scale) {
        this.timeStep = timeStep;
        this.scale = scale;
        nodes = root.getChildren();
        animationElements = new ArrayList<>(trajectories.size());

        radius = 0.4 * scale;
        tailRadius = 0.2 * scale;
        linkRadius = 0.10 * scale;

        Map<String, MotionEquation> motionEquationMap = clusters
                .stream()
                .flatMap(cluster -> cluster.getMotionEquations().stream())
                .collect(Collectors.toMap(MotionEquation::getLabel, Function.identity()));

        List<AnimationCluster> animationClusters = clusters
                .stream()
                .map(cluster -> new AnimationCluster(getGravityCenterPath(cluster), cluster.getMotionEquations()
                        .stream()
                        .map(equation -> {
                            Sphere atom = createAtom(radius, equation.getColor());
                        })))



        trajectoryMap = trajectories
                .stream()
                .collect(Collectors.toMap(Trajectory3D::getLabel, Function.identity()));

        clusterMap = trajectories
                .stream()
                .collect(Collectors.groupingBy(Trajectory3D::getCluster));

        gravityCenterMap = clusterMap.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> getGravityCenter(entry.getValue(), 0)));

        for (Trajectory3D t : trajectories) {
            Point3D point = t.getPath().get(0).multiply(scale);
            Sphere atom = createAtom(point, radius, ColorAdapter.from(t.getColor()));
            Text text = createText(point, t.getLabel());

            Map<String, Cylinder> links = t.getLinkLabels()
                    .stream()
                    .collect(Collectors.toMap(Function.identity(), label -> createLink(point, label)));

            AnimationElement animationElement = new AnimationElement(atom, text, t.getPath(), links);
            animationElements.add(animationElement);
            nodes.add(animationElement.getGroup());
        }
        numberSteps = trajectories.stream().mapToLong(t -> (long) t.getPath().size()).max().getAsLong();
    }

    private Text createText(Point3D point, String label) {
        Text text = new Text(label);
        text.setFont(new Font(12));
        text.setFill(Color.BLACK);

        point = point.add(radius, radius, radius);
        text.setTranslateX(point.getX());
        text.setTranslateY(point.getY());
        text.setTranslateZ(point.getZ());
        return text;
    }

    private Cylinder createLink(Point3D point, String label) {
        Point3D anotherPoint = trajectoryMap.get(label).getPath().get(0).multiply(scale);
        Cylinder link = new Cylinder(linkRadius, anotherPoint.distance(point));

        link.setTranslateX(point.getX());
        link.setTranslateY(point.getY() + link.getHeight() / 2);
        link.setTranslateZ(point.getZ());

        double dx = point.getX() - anotherPoint.getX();
        double dz = point.getZ() - anotherPoint.getZ();
        double dxz = Math.sqrt(dx * dx + dz * dz);
        double dy = point.getY() - anotherPoint.getY();

        double xAngle = Math.toDegrees(Math.atan2(dy, dxz));
        double zAngle = Math.toDegrees(Math.atan2(dx, dz));

        link.getTransforms().add(new Rotate(-90, 0, -link.getHeight() / 2, 0, Rotate.X_AXIS));
        link.getTransforms().add(new Rotate(zAngle, 0, -link.getHeight() / 2, 0, Rotate.Z_AXIS));
        link.getTransforms().add(new Rotate(-xAngle, 0, -link.getHeight() / 2, 0, Rotate.X_AXIS));

        PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(Color.GRAY);
        material.setSpecularColor(Color.GRAY.darker());
        link.setMaterial(material);

        return link;
    }

    private Sphere createAtom(double radius, Color color) {
        final PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(color);
        material.setSpecularColor(color.darker());

        Sphere atom = new Sphere(radius);
        atom.setMaterial(material);

        return atom;
    }

    private List<Point3D> getGravityCenterPath(Cluster cluster) {
        double weightSum = cluster.getMotionEquations()
                .stream()
                .mapToDouble(MotionEquation::getWeight)
                .sum();


        return IntStream.range(0, cluster.getMotionEquations().size())
                .mapToObj(index -> cluster.getMotionEquations()
                        .stream()
                        .map(t -> t.getPath().get(index).multiply(t.getWeight() * scale))
                        .reduce(Point3D.ZERO, Point3D::add)
                        .multiply(1 / weightSum))
                .collect(Collectors.toList());
    }

    /**
     * <p>Метод, позволяющий запустить процесс воспроизведения.</p>
     *//*
    @Override
    public void run() {
        while (!isStopped) {
            try {
                Thread.sleep(timeStep);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (!isPaused) runLater(this::next);
        }
    }

    *//**
     * <p>Метод, который возобновляет воспроизведение.</p>
     *//*
    public void play() {
        isPaused = false;
    }

    *//**
     * <p>Метод, который приостанавливает воспроизведение.</p>
     *//*
    public void pause() {
        isPaused = true;
    }

    *//**
     * <p>Метод, который обновляет положения отображаемых частиц при воспроизведении.</p>
     *
     * @param addTail следует ли в рамках метода создавать частицы,
     *                которые символизируют следы траекторий, оставленные частицами модели.
     * @return список созданных в рамках обновления частиц.
     * Частицы будут созданы только, если параметр <code>addTail</code> будет иметь значение <code>true</code>.
     * В противном случае метод вернет пустой список.
     *//*
    private List<Sphere> updateAtomsPosition(boolean addTail) {
        List<Sphere> frame = new ArrayList<>();

        for (AnimationElement animationElement : animationElements) {
            Sphere atom = animationElement.getAtom();
            List<Point3D> trajectory = animationElement.getTrajectory();

            for (Node node : animationElement.getGroup().getChildren()) {
                node.setTranslateX(trajectory.get(timePointer).getX() * scale);
                node.setTranslateY(trajectory.get(timePointer).getY() * scale);
                node.setTranslateZ(trajectory.get(timePointer).getZ() * scale);
            }

            Text text = animationElement.getText();
            text.setTranslateX(text.getTranslateX() + radius);
            text.setTranslateY(text.getTranslateY() + radius);
            text.setTranslateZ(text.getTranslateZ() + radius);

            for (Map.Entry<String, Cylinder> linkEntry : animationElement.getLinks().entrySet()) {
                Point3D linkedPoint = trajectoryMap.get(linkEntry.getKey()).getPath().get(timePointer).multiply(scale);
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

        if (!addTail) return frame;
        for (Map.Entry<String, List<Trajectory3D>> cluster : clusterMap.entrySet()) {
            Point3D oldGravityCenter = gravityCenterMap.get(cluster.getKey());
            Point3D newGravityCenter = getGravityCenter(cluster.getValue(), timePointer);

            if (!oldGravityCenter.equals(newGravityCenter)) {
                Sphere tail = createAtom(oldGravityCenter, tailRadius, Color.GREY);
                gravityCenterMap.put(cluster.getKey(), newGravityCenter);
                nodes.add(tail);
                frame.add(tail);
            }
        }

        return frame;
    }

    *//**
     * <p>Метод, который переводит модель в следующее состояние.
     * Используется для пошагового воспроизведения.</p>
     *//*
    public void next() {
        if (timePointer >= numberSteps - 1) {
            isPaused = true;
            return;
        }
        timePointer++;

        frames.push(updateAtomsPosition(true));
    }

    *//**
     * <p>Метод, который переводит модель в предыдущее состояние.
     * Используется для пошагового воспроизведения.</p>
     *//*
    public void prev() {
        if (timePointer < 1) return;
        timePointer--;

        updateAtomsPosition(false);
        frames.pop().forEach(nodes::remove);
    }

    *//**
     * <p>Метод, который приостанавливает воспроизведение
     * и переводит модель в начальное положение.</p>
     *//*
    public void reset() {
        timePointer = 0;
        frames.stream().flatMap(Collection::stream).forEach(nodes::remove);
        frames.clear();

        gravityCenterMap = clusterMap.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> getGravityCenter(entry.getValue(), 0)));

        updateAtomsPosition(false);
    }

    *//**
     * <p>Метод, который полностью останавливает воспроизведение.
     * После его вызова дальнейшее воспроизведение невозможно.</p>
     *//*
    public void stop() {
        isStopped = true;
    }
    */
}
