package vsu.sc.grishchenko.phaeton.util.databind;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import javafx.geometry.Point3D;
import javafx.scene.paint.Color;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vsu.sc.grishchenko.phaeton.util.databind.deserializer.ColorDeserializer;
import vsu.sc.grishchenko.phaeton.util.databind.deserializer.Point3DDeserializer;

@Configuration
public class JsonConfiguration {
    @Bean
    public ObjectMapper objectMapper() {
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Color.class, new ColorDeserializer());
        module.addDeserializer(Point3D.class, new Point3DDeserializer());

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(module);
        return objectMapper;
    }
}
