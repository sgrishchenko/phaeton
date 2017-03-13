package vsu.sc.grishchenko.phaeton;

import javafx.application.Application;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;
import vsu.sc.grishchenko.phaeton.main.Main;

@SpringBootApplication
@EnableAsync
public class PhaetonApplication extends Application {
    private static String[] args;
    private ConfigurableApplicationContext context;

    @Autowired
    private Main main;

    public static void main(String[] args) {
        PhaetonApplication.args = args;
		launch(args);
	}

    @Override
    public void init() throws Exception {
        context = SpringApplication.run(getClass(), args);
        context.getAutowireCapableBeanFactory().autowireBean(this);
    }

	@Override
	public void start(Stage primaryStage) throws Exception {
        main.start(primaryStage);
	}

    @Override
    public void stop() throws Exception {
        super.stop();
        context.stop();
    }
}
