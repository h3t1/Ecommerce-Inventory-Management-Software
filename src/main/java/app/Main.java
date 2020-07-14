package app;
import com.jfoenix.controls.JFXDecorator;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;


public class Main extends Application {

    public static JFXDecorator decorator;
    public static Stage window;

    @Override
    public void start(Stage stage) throws IOException {
        window = stage;
        decorator = new JFXDecorator(stage , loadFXML("login"));
        decorator.setCustomMaximize(true);
        decorator.getStylesheets().add(getClass().getResource("main.css").toExternalForm());
        window.setScene(new Scene(decorator));
        window.setResizable(false);
        window.setTitle("Login");
        window.show();
    }
    public static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }
    public static void main(String[] args) {
        launch();
    }
}
