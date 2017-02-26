package vsu.sc.grishchenko.phaeton.util.expression;

import javafx.geometry.Point3D;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class Point3DEvaluationContext extends StandardEvaluationContext {
    @Autowired
    private ExpressionParser parser;
    @Autowired
    private Point3DPropertyAccessor point3DPropertyAccessor;
    @Autowired
    private Point3DOperatorOverloader point3DOperatorOverloader;

    private final Map<String, Object> variables = new HashMap<>();

    public Point3DEvaluationContext() {
        super();
        setVariable("c", this);
        registerFunction("sum", ReflectionUtils.findMethod(getClass(), "sum", List.class));
    }

    @Override
    public void setVariable(String name, Object value) {
        this.variables.put(name, value);
        super.setVariable(name, value);
    }

    @Override
    public void setVariables(Map<String, Object> variables) {
        this.variables.putAll(variables);
        super.setVariables(variables);
    }

    public Point3D sum(String filter, String expression) {
        return sum(new HashMap<>(variables).entrySet()
                .stream()
                .filter(var -> var.getKey().startsWith(filter))
                .filter(var -> !getRootObject().getValue().equals(var.getValue()))
                .map(Map.Entry::getValue)
                .map(value -> {
                    Object hidden = this.lookupVariable(filter);
                    this.setVariable(filter, value);
                    Point3D result = (Point3D) parser.parseExpression(expression).getValue(this);
                    this.setVariable(filter, hidden);
                    return result;
                })
                .collect(Collectors.toList()));
    }

    @PostConstruct
    public void init() {
        addPropertyAccessor(point3DPropertyAccessor);
        setOperatorOverloader(point3DOperatorOverloader);
    }

    public Map<String, Object> getVars() {
        return variables;
    }

    private static Point3D sum(List<Point3D> points) {
        return points.stream().reduce(Point3D.ZERO, Point3D::add);
    }
}
