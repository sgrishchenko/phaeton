package vsu.sc.grishchenko.phaeton.util;

import javafx.fxml.FXMLLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SpringFXMLLoader {
    @Autowired
    private ApplicationContext context;

    public <T> T load(String location) throws IOException {
        FXMLLoader loader = new FXMLLoader(context.getResource(location).getURL());
        loader.setControllerFactory(param -> context.getBean(param));
        return loader.load();
    }
}
