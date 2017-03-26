package vsu.sc.grishchenko.phaeton.view3d;

import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Sphere;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class AnimationElementFactory {
    @Value("#{${animation.scale} * 0.4}")
    private double radius;
    @Value("#{${animation.scale} * 0.2}")
    private double tailRadius;
    @Value("#{${animation.scale} * 0.1}")
    private double linkRadius;
    @Value("#{${animation.scale} * 0.5}")
    private double textSize;

    public Sphere createAtom(Color color) {
        PhongMaterial material = new PhongMaterial(color);
        material.setSpecularColor(color.darker());

        Sphere atom = new Sphere(radius);
        atom.setMaterial(material);

        return atom;
    }

    public Sphere createTail(Point3D translate) {
        PhongMaterial material = new PhongMaterial(Color.GRAY);
        material.setSpecularColor(Color.GRAY.darker());

        Sphere atom = new Sphere(tailRadius);
        atom.setMaterial(material);
        atom.setTranslateX(translate.getX());
        atom.setTranslateY(translate.getY());
        atom.setTranslateZ(translate.getZ());

        return atom;
    }


    public Label createLabel(String labelText, Sphere atom) {
        Label label = new Label(labelText);
        label.setLabelFor(atom);
        label.setFont(new Font(textSize));
        label.setTextFill(Color.BLACK);
        return label;
    }

    public Cylinder createLink() {
        PhongMaterial material = new PhongMaterial(Color.GRAY);
        material.setSpecularColor(Color.GRAY.darker());

        Cylinder link = new Cylinder(linkRadius, 0);
        link.setMaterial(material);

        return link;
    }

}
