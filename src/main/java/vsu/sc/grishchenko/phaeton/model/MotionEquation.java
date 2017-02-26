package vsu.sc.grishchenko.phaeton.model;

import javafx.geometry.Point3D;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Класс для описания информации о частице моделируемой системы.</p>
 *
 * @author Грищенко Сергей
 */
public class MotionEquation {
    /**
     * <p>Текстовая метка частицы.</p>
     */
    private String label;
    /**
     * <p>Модуль суммы сил, действующих на частицу, описанный аналитическим выражением.</p>
     */
    private String accelerationEquation = "0";
    /**
     * <p>Вектор начального положения частицы.</p>
     */
    private Point3D initialPosition = Point3D.ZERO;
    /**
     * <p>Вектор начальной скорости частицы.</p>
     */
    private Point3D initialVelocity = Point3D.ZERO;
    /**
     * <p>Цвет частицы.</p>
     */
    private Color color = new Color(0.6, 0, 0, 1);
    /**
     * <p>Список частиц, с которыми необходимо отображать визуальную связь.</p>
     */
    private List<String> linkLabels = new ArrayList<>();
    /**
     * <p>Масса частицы.</p>
     */
    private Double weight = 0.;
    /**
     * <p>Список объектов {@link Point3D}, описывающих точки трехмерного пространства
     * траектории движения частицы.</p>
     */
    private List<Point3D> path = new ArrayList<>();

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getAccelerationEquation() {
        return accelerationEquation;
    }

    public void setAccelerationEquation(String accelerationEquation) {
        this.accelerationEquation = accelerationEquation;
    }

    public Point3D getInitialPosition() {
        return initialPosition;
    }

    public void setInitialPosition(Point3D initialPosition) {
        this.initialPosition = initialPosition;
    }

    public Point3D getInitialVelocity() {
        return initialVelocity;
    }

    public void setInitialVelocity(Point3D initialVelocity) {
        this.initialVelocity = initialVelocity;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public List<String> getLinkLabels() {
        return linkLabels;
    }

    public void setLinkLabels(List<String> linkLabels) {
        this.linkLabels = linkLabels;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public List<Point3D> getPath() {
        return path;
    }

    public void setPath(List<Point3D> path) {
        this.path = path;
    }
}
