package vsu.sc.grishchenko.phaeton.main;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vsu.sc.grishchenko.phaeton.math.Solver;
import vsu.sc.grishchenko.phaeton.model.Cluster;
import vsu.sc.grishchenko.phaeton.view3d.View3D;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExperimentService {
    @Autowired
    private View3D view3D;
    @Autowired
    private Solver solver;

    public void run(List<Cluster> clusters) throws Exception {
        solver.solve(clusters
                .stream()
                .flatMap(cluster -> cluster.getMotionEquations().stream())
                .collect(Collectors.toList()), 0, 1000, 0.001);

        view3D.stop();
        view3D.setClusters(clusters);
        view3D.start();
    }
}
