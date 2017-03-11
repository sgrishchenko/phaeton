package vsu.sc.grishchenko.phaeton.util.expression;

import javafx.geometry.Point3D;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.Operation;
import org.springframework.expression.OperatorOverloader;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.DoubleSummaryStatistics;
import java.util.List;

import static org.springframework.expression.Operation.*;

@Component
public class Point3DOperatorOverloader implements OperatorOverloader {

    private static final List<Operation> OPERATIONS_FOR_SCALAR = Arrays.asList(MULTIPLY, ADD, SUBTRACT);

    @Override
    public boolean overridesOperation(Operation operation, Object leftOperand, Object rightOperand)
            throws EvaluationException {

        if (leftOperand == null || rightOperand == null) return false;

        List<Object> operands = Arrays.asList(leftOperand, rightOperand);

        boolean allVectors = operands.stream().allMatch(o -> o instanceof Point3D);
        boolean anyScalar = operands.stream().anyMatch(o -> o instanceof Point3D)
                && operands.stream().anyMatch(o -> o instanceof Number);

        return (anyScalar && OPERATIONS_FOR_SCALAR.contains(operation)) || allVectors;
    }

    @Override
    public Object operate(Operation operation, Object leftOperand, Object rightOperand) throws EvaluationException {
        List<Object> operands = Arrays.asList(leftOperand, rightOperand);
        if (operands.stream().allMatch(o -> o instanceof Point3D)) {
            Point3D left = (Point3D) leftOperand;
            Point3D right = (Point3D) rightOperand;

            switch (operation) {
                case ADD:
                    return left.add(right);
                case SUBTRACT:
                    return left.subtract(right);
                case MODULUS:
                    return left.distance(right);
                case DIVIDE:
                    return left.midpoint(right);
                case MULTIPLY:
                    return left.crossProduct(right);
                case POWER:
                    return left.angle(right);
                default:
                    throw new UnsupportedOperationException();
            }
        } else {
            Point3D vector = filterOperands(operands, Point3D.class);
            Double scalar = Double.parseDouble(filterOperands(operands, Number.class).toString());

            switch (operation) {
                case ADD:
                    return vector.add(new Point3D(scalar, scalar, scalar));
                case SUBTRACT:
                    return leftOperand.equals(vector)
                            ? vector.subtract(new Point3D(scalar, scalar, scalar))
                            : new Point3D(scalar, scalar, scalar).subtract(vector);
                case MULTIPLY:
                    return vector.multiply(Double.parseDouble(scalar.toString()));
                default:
                    throw new UnsupportedOperationException();
            }
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T filterOperands(List<Object> operands, Class<T> tClass) {
        return (T) operands
                .stream()
                .filter(o -> tClass.isAssignableFrom(o.getClass()))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
