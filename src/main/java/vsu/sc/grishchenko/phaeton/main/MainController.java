package vsu.sc.grishchenko.phaeton.main;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Controller;
import vsu.sc.grishchenko.phaeton.math.Solver;
import vsu.sc.grishchenko.phaeton.model.Cluster;
import vsu.sc.grishchenko.phaeton.model.MotionEquation;
import vsu.sc.grishchenko.phaeton.view3d.View3D;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class MainController implements Initializable {
    @FXML
    public Menu clusterMenu;
    @FXML
    private TreeTableView<Object> table;

    @Autowired
    private FileService fileService;
    @Autowired
    private EditService editService;
    @Autowired
    private ExperimentService experimentService;
    @Autowired
    private SpelExpressionParser parser;
    @Autowired
    private StandardEvaluationContext context;

    private Map<TreeItem<Object>, CheckBox> checkBoxMap = new HashMap<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        /*Point3D r = new Point3D(1, 1, 1);
        context.setVariable("x1", new Point3D(1, 2, 3));
        context.setVariable("x2", new Point3D(2, 2, 2));
        context.setVariable("x3", r);
        context.setVariable("y", new Point3D(3, 2, 1));
        context.setRootObject(r);

         Object res = parser.parseExpression(
                "#c.sum('x', '#x + #this')"
                //"#sum(#c.vars.?[key matches '^x.+'].?[value ne #root].![value + #root])"
        ).getValue(context);*/

        table.setRoot(new TreeItem<>(new ArrayList<>()));
        table.getColumns().forEach(column -> {
            String expression = (String) column.getUserData();
            @SuppressWarnings("unchecked")
            Map<Class<?>, String> fieldMap = (Map<Class<?>, String>) parser.parseExpression(expression).getValue();
            if (fieldMap.isEmpty()) return;

            @SuppressWarnings("unchecked")
            TreeTableColumn<Object, Object> tableColumn = (TreeTableColumn<Object, Object>) column;
            tableColumn.setSortable(false);

            tableColumn.setCellValueFactory(cell -> {
                Object object = cell.getValue().getValue();
                if (!fieldMap.containsKey(object.getClass())) return null;

                String fieldExpression = fieldMap.get(object.getClass());
                Expression parsedExpression = parser.parseExpression(fieldExpression);
                Object value = parsedExpression.getValue(object);
                if (!fieldExpression.contains("#this")) return new SimpleObjectProperty<>(value);

                Node element;
                ObservableValue<?> property = null;
                if (value instanceof Cluster || value instanceof MotionEquation) {
                    element = createCheckBox(cell);
                } else if (value instanceof List) {
                    String list = ((List<?>) value).stream().map(String::valueOf).collect(Collectors.joining(", "));
                    element = new TextField(list);
                    property = ((TextField) element).textProperty();
                } else if (value instanceof Color) {
                    Color color = (Color) value;
                    element = new ColorPicker(color);
                    property = ((ColorPicker) element).valueProperty();
                } else {
                    element = new TextField(value.toString());
                    property = ((TextField) element).textProperty();
                }

                if (property != null) property.addListener((observable, oldValue, newValue) -> {
                    try {
                        parsedExpression.setValue(context, object, newValue);
                        element.getStyleClass().removeAll("error");
                    } catch (Throwable e) {
                        element.getStyleClass().add("error");
                    }
                });

                return new SimpleObjectProperty<>(element);
            });
        });
    }

    public void open() {
        clusterMenu.getItems().clear();
        checkBoxMap.clear();
        List<Cluster> clusters = fileService.open();
        TreeItem<Object> root = new TreeItem<>(clusters);
        table.setRoot(root);

        clusters.forEach(cluster -> {
            TreeItem<Object> clusterItem = editService.addCluster(table, clusterMenu, cluster);

            cluster.getMotionEquations().forEach(motionEquation -> {
                TreeItem<Object> motionEquationItem = new TreeItem<>(motionEquation);
                clusterItem.getChildren().add(motionEquationItem);
            });
        });
    }

    public void save() {
        @SuppressWarnings("unchecked")
        List<Cluster> clusters = (List<Cluster>) table.getRoot().getValue();
        fileService.save(clusters);
    }

    public void saveAs() {
        @SuppressWarnings("unchecked")
        List<Cluster> clusters = (List<Cluster>) table.getRoot().getValue();
        fileService.saveAs(clusters);
    }

    public void addCluster() {
        editService.addCluster(table, clusterMenu);
    }

    public void deleteAll() {
        checkBoxMap.clear();
        editService.deleteAll(table, clusterMenu);
    }

    public void deleteSelected() {
        editService.deleteSelected(clusterMenu, checkBoxMap);
    }

    public void run() throws Exception {
        @SuppressWarnings("unchecked")
        List<Cluster> clusters = (List<Cluster>) table.getRoot().getValue();
        experimentService.run(clusters);
    }

    private CheckBox createCheckBox(TreeTableColumn.CellDataFeatures<Object, Object> cell) {
        CheckBox checkBox = new CheckBox();
        checkBoxMap.put(cell.getValue(), checkBox);
        checkBox.setOnAction(event -> {
            CheckBox parentCheckBox = checkBoxMap.getOrDefault(cell.getValue().getParent(), new CheckBox());
            if (checkBox.isSelected()) {
                if (cell.getValue().getParent().getChildren()
                        .stream()
                        .map(checkBoxMap::get)
                        .filter(neighborCheckBox -> !neighborCheckBox.isSelected())
                        .count() == 0) {

                    parentCheckBox.setSelected(true);
                }
            } else {
                parentCheckBox.setSelected(false);
            }
            cell.getValue().getChildren()
                    .stream()
                    .map(checkBoxMap::get)
                    .forEach(childCheckBox -> childCheckBox.setSelected(checkBox.isSelected()));
        });
        return checkBox;
    }
}
