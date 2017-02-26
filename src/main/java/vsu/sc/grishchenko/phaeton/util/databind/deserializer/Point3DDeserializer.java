package vsu.sc.grishchenko.phaeton.util.databind.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import javafx.geometry.Point3D;

import java.io.IOException;

public class Point3DDeserializer extends StdDeserializer<Point3D> {

    public Point3DDeserializer() {
        this(null);
    }

    public Point3DDeserializer(Class<?> aClass) {
        super(aClass);
    }

    @Override
    public Point3D deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        JsonNode node = parser.getCodec().readTree(parser);

        double x = node.get("x").asDouble();
        double y = node.get("y").asDouble();
        double z = node.get("z").asDouble();

        return new Point3D(x, y, z);
    }
}