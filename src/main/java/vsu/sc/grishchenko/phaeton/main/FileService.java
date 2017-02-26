package vsu.sc.grishchenko.phaeton.main;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import vsu.sc.grishchenko.phaeton.model.Cluster;
import vsu.sc.grishchenko.phaeton.util.logging.Log;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class FileService {
    @Log
    private Logger logger;

    @Autowired
    private FileChooser fileChooser;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ApplicationContext context;

    /**
     * <p>Объект, хранящий ссылку на файл, с которым ассоциирована рабочая область приложения в текущий момент.</p>
     *
     * @see File
     */
    private File currentFile;

    public List<Cluster> open() {
        Scene mainScene = context.getBean("mainScene", Scene.class);
        File file = fileChooser.showOpenDialog(mainScene.getWindow());
        if (file == null) return Collections.emptyList();

        currentFile = file;

        try {
            JavaType type = objectMapper.getTypeFactory().constructCollectionType(List.class, Cluster.class);
            return objectMapper.readValue(file, type);
        } catch (IOException e) {
            logger.log(Level.SEVERE, null, e);
            return Collections.emptyList();
        }
    }

    public void save(List<Cluster> clusters) {
        if (currentFile == null) {
            saveAs(clusters);
        } else {
            save(currentFile, clusters);
        }
    }

    public void saveAs(List<Cluster> clusters) {
        Scene mainScene = context.getBean("mainScene", Scene.class);
        File file = fileChooser.showSaveDialog(mainScene.getWindow());
        if (file == null) return;

        save(file, clusters);
        currentFile = file;
    }

    private void save(File file, List<Cluster> clusters) {
        try {
            objectMapper.writeValue(file, clusters);
        } catch (IOException e) {
            logger.log(Level.SEVERE, null, e);
        }
    }
}
