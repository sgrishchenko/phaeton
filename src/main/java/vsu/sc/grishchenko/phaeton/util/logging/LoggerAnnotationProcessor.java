package vsu.sc.grishchenko.phaeton.util.logging;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.util.logging.Logger;

@Component
public class LoggerAnnotationProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {

        ReflectionUtils.doWithFields(bean.getClass(), field -> {
            if (!field.isAnnotationPresent(Log.class)) return;

            ReflectionUtils.makeAccessible(field);
            if (field.getType().equals(Logger.class)) {
                field.set(bean, Logger.getLogger(bean.getClass().getName()));
            }
        });

        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}
