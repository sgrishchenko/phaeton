package vsu.sc.grishchenko.phaeton.util.expression;

import javafx.geometry.Point3D;
import org.springframework.expression.spel.support.ReflectivePropertyAccessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.stream.Stream;

@Component
public class Point3DPropertyAccessor extends ReflectivePropertyAccessor {
    @Override
    public Class<?>[] getSpecificTargetClasses() {
        return new Class<?>[]{Point3D.class};
    }

    @Override
    protected Field findField(String name, Class<?> clazz, boolean mustBeStatic) {
        return Stream.of(clazz.getDeclaredFields())
                .filter(field -> field.getName().equals(name))
                .filter(field -> !mustBeStatic || Modifier.isStatic(field.getModifiers()))
                .peek(field -> field.setAccessible(true))
                .findFirst().orElse(null);
    }
}
