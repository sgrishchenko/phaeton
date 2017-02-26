package vsu.sc.grishchenko.phaeton.main;

import javafx.scene.control.*;
import org.springframework.stereotype.Service;
import vsu.sc.grishchenko.phaeton.model.Cluster;
import vsu.sc.grishchenko.phaeton.model.MotionEquation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EditService {

    public void addCluster(TreeTableView<Object> table, Menu clusterMenu) {
        @SuppressWarnings("unchecked")
        List<Cluster> clusters = (List<Cluster>) table.getRoot().getValue();
        Cluster cluster = new Cluster();
        cluster.setName("Кластер " + (clusters.size() + 1));
        clusters.add(cluster);

        addCluster(table, clusterMenu, cluster);
    }

    public TreeItem<Object> addCluster(TreeTableView<Object> table, Menu clusterMenu, Cluster cluster) {
        TreeItem<Object> clusterItem = new TreeItem<>(cluster);
        clusterItem.setExpanded(true);
        table.getRoot().getChildren().add(clusterItem);

        MenuItem menuItem = new MenuItem(cluster.getName());
        menuItem.setUserData(cluster);
        menuItem.setOnAction(event -> {
            MotionEquation motionEquation = new MotionEquation();
            String label = String.format("r%d-%d",
                    table.getRoot().getChildren().indexOf(clusterItem) + 1,
                    clusterItem.getChildren().size() + 1);
            motionEquation.setLabel(label);
            clusterItem.getChildren().add(new TreeItem<>(motionEquation));

            ((Cluster) clusterItem.getValue()).getMotionEquations().add(motionEquation);
        });
        clusterMenu.getItems().add(menuItem);
        return clusterItem;
    }

    public void deleteAll(TreeTableView<Object> table, Menu clusterMenu) {
        @SuppressWarnings("unchecked")
        List<Cluster> clusters = (List<Cluster>) table.getRoot().getValue();
        clusters.clear();
        table.getRoot().getChildren().clear();
        clusterMenu.getItems().clear();
    }

    public void deleteSelected(Menu clusterMenu, Map<TreeItem<Object>, CheckBox> checkBoxMap) {
        new HashMap<>(checkBoxMap).entrySet()
                .stream()
                .filter(entry -> entry.getValue().isSelected())
                .map(Map.Entry::getKey)
                .forEach(item -> {
                    checkBoxMap.remove(item);
                    if (item.getParent() == null) return;
                    Object value = item.getParent().getValue();
                    if (value instanceof List) {
                        ((List<?>) value).remove(item.getValue());
                        clusterMenu.getItems().removeIf(menuItem -> item.getValue().equals(menuItem.getUserData()));
                    } else if (value instanceof Cluster) {
                        MotionEquation motionEquation = (MotionEquation) item.getValue();
                        ((Cluster) value).getMotionEquations().remove(motionEquation);
                    }
                    item.getParent().getChildren().remove(item);
                });
    }
}
