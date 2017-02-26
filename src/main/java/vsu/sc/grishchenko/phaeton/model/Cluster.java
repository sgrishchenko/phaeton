package vsu.sc.grishchenko.phaeton.model;

import java.util.ArrayList;
import java.util.List;

public class Cluster {
    private Long id;
    private String name;
    private List<MotionEquation> motionEquations = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<MotionEquation> getMotionEquations() {
        return motionEquations;
    }

    public void setMotionEquations(List<MotionEquation> motionEquations) {
        this.motionEquations = motionEquations;
    }
}
