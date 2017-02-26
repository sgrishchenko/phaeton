package vsu.sc.grishchenko.phaeton.math;

import javafx.geometry.Point3D;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import vsu.sc.grishchenko.phaeton.model.MotionEquation;
import vsu.sc.grishchenko.phaeton.util.expression.SpelConfiguration.EvaluationContextFactory;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class Solver {
    @Autowired
    private ExpressionParser parser;
    @Autowired
    private EvaluationContextFactory contextFactory;

    public List<MotionEquation> solve(List<MotionEquation> motionEquations,
                                      double initialTime,
                                      int countSteps,
                                      double stepSize) {

        StandardEvaluationContext context = contextFactory.getContext();

        List<MotionEquationFeatures> features = motionEquations
                .stream()
                .map(this::features)
                .collect(Collectors.toList());

        //first step
        context.setVariable("t", initialTime);
        context.setVariables(features.stream().collect(Collectors.toMap(
                equation -> equation.getValue().getLabel(),
                MotionEquationFeatures::getLastButOnePosition))
        );

        features.forEach(equation -> {
            Point3D initialAcceleration = (Point3D) equation.getExpression().getValue(context);

            equation.setLastPosition(equation.getLastButOnePosition()
                    .add(equation.getValue().getInitialVelocity().multiply(stepSize))
                    .add(initialAcceleration.multiply(Math.pow(stepSize, 2) / 2)));

            equation.getValue().getPath().add(equation.getLastButOnePosition());
            equation.getValue().getPath().add(equation.getLastPosition());
        });

        //iteration
        initialTime += 2 * stepSize;
        for (int i = 2; i < countSteps; i++, initialTime += stepSize) {
            context.setVariable("t", initialTime);

            context.setVariable("t", initialTime);
            context.setVariables(features.stream().collect(Collectors.toMap(
                    equation -> equation.getValue().getLabel(),
                    MotionEquationFeatures::getLastPosition))
            );

            features.forEach(equation ->
                    equation.setAcceleration((Point3D) equation.getExpression().getValue(context)));

            features.forEach(equation -> {
                Point3D newPosition = equation.getLastPosition().multiply(2)
                        .subtract(equation.getLastButOnePosition())
                        .add(equation.getAcceleration().multiply(Math.pow(stepSize, 2)));

                equation.getValue().getPath().add(newPosition);

                equation.setLastButOnePosition(equation.getLastPosition());
                equation.setLastPosition(newPosition);
            });
        }

        return motionEquations;
    }

    private MotionEquationFeatures features(MotionEquation motionEquation) {
        Expression expression = parser.parseExpression(motionEquation.getAccelerationEquation());
        return new MotionEquationFeatures(motionEquation, expression);
    }

    private static class MotionEquationFeatures {
        private final MotionEquation value;
        private final Expression expression;

        private Point3D lastButOnePosition;
        private Point3D lastPosition;
        private Point3D acceleration;

        private MotionEquationFeatures(MotionEquation value, Expression expression) {
            this.value = value;
            this.lastButOnePosition = value.getInitialPosition();
            this.expression = expression;
        }

        MotionEquation getValue() {
            return value;
        }

        Expression getExpression() {
            return expression;
        }

        Point3D getLastButOnePosition() {
            return lastButOnePosition;
        }

        void setLastButOnePosition(Point3D lastButOnePosition) {
            this.lastButOnePosition = lastButOnePosition;
        }

        Point3D getLastPosition() {
            return lastPosition;
        }

        void setLastPosition(Point3D lastPosition) {
            this.lastPosition = lastPosition;
        }

        Point3D getAcceleration() {
            return acceleration;
        }

        void setAcceleration(Point3D acceleration) {
            this.acceleration = acceleration;
        }
    }

}
