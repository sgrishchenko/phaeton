package vsu.sc.grishchenko.phaeton.util.databind.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import javafx.scene.paint.Color;

import java.io.IOException;

public class ColorDeserializer extends StdDeserializer<Color> {

    public ColorDeserializer() {
        this(null);
    }

    public ColorDeserializer(Class<?> aClass) {
        super(aClass);
    }

    @Override
    public Color deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        JsonNode node = parser.getCodec().readTree(parser);

        double red = node.get("red").asDouble();
        double green = node.get("green").asDouble();
        double blue = node.get("blue").asDouble();
        double opacity = node.get("opacity").asDouble();

        return new Color(red, green, blue, opacity);
    }
}