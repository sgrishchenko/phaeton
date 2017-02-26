package vsu.sc.grishchenko.phaeton.util.expression;

import javafx.geometry.Point3D;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

@Configuration
public class SpelConfiguration {
    @Bean
    public SpelExpressionParser parser() {
        return new SpelExpressionParser();
    }

    @Component
    public static abstract class EvaluationContextFactory {
        @Lookup
        public abstract StandardEvaluationContext getContext();
    }
}
