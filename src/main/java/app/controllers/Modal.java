package app.controllers;

import app.Main;
import com.jfoenix.controls.JFXDecorator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class Modal {
    protected String title;
    protected TreeItem<?> model;
    public Modal(String title) {
        this.title = title;
    }

    public Stage createForm(String fxml) throws IOException {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(app.Main.class.getResource(fxml+".fxml"));
        // Set it in the FXMLLoader
        loader.setController(this);
        JFXDecorator decorator = new JFXDecorator(stage, loader.load(), false, false, true);
        decorator.getStylesheets().add(Main.class.getResource("modal.css").toExternalForm());
        decorator.setCustomMaximize(true);
        stage.setScene(new Scene(decorator));
        stage.setTitle(title);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
        return stage;
    }
    @FXML
    void cancel(ActionEvent event) {
        final Node source = (Node) event.getSource();
        final Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }
    @FXML
    void save(ActionEvent event) throws IOException {

    }
}
