package app.controllers;

import app.models.City;
import app.tray.notification.NotificationType;
import app.tray.notification.TrayNotification;
import com.jfoenix.controls.*;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class Client extends Modal implements Initializable {
    private TreeItem<app.models.Client> model;
    private int id;
    @FXML
    JFXTextField username, email, tel, fullName, city;
    @FXML
    JFXTextArea address;

    public Client(String title, TreeItem<app.models.Client> model) {
        super(title);
        this.model = model;
        if(model!=null){
            this.id = model.getValue().getId();
        }

    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        JFXAutoCompletePopup<String> autoCompletePopup = new JFXAutoCompletePopup<>();
        autoCompletePopup.setSelectionHandler(event -> this.city.setText(event.getObject()));
        ObservableList<String> cities = (ObservableList<String>) City.getAll("ville");

        autoCompletePopup.getSuggestions().addAll(cities);
        this.city.textProperty().addListener(observable ->{
            autoCompletePopup.filter(s -> s.toLowerCase().contains(this.city.getText().toLowerCase()));
            if(!autoCompletePopup.getFilteredSuggestions().isEmpty()){
                autoCompletePopup.show(this.city);
            }else{
                autoCompletePopup.hide();
            }
        });
        if (this.id!=0) {
            try {
                this.username.setText(model.getValue().getUsername());
                this.email.setText(model.getValue().getEmail());
                this.tel.setText(model.getValue().getTel());
                this.fullName.setText(model.getValue().getFullName());
                this.city.setText(model.getValue().getCity().getVille());
                this.address.setText(model.getValue().getAddress());
            } catch (NullPointerException e) {
                TrayNotification tn = new TrayNotification("Error: app.controllers.Client","Unable to fill the form", NotificationType.ERROR);
                tn.showAndDismiss(Duration.millis(5000));
            }
        }
    }

    @FXML
    @Override
    void save(ActionEvent event) throws IOException {

        String username = this.username.getText();
        String email = this.email.getText();
        String tel = this.tel.getText();
        String fullName = this.fullName.getText();
        String city = this.city.getText();
        String address = this.address.getText();
        app.models.Client c = new app.models.Client(this.id, username, email, tel, fullName, City.getCity(city), address);
        TrayNotification tn = new TrayNotification();
        if(c.save()){
            tn.setTitle("Succeeded");
            tn.setMessage("Your data have been successfully saved!");
            tn.setNotificationType(NotificationType.SUCCESS);
        }
        else {
            tn.setTitle("Error: app.controlles.Client.save()");
            tn.setNotificationType(NotificationType.ERROR);
        }
        tn.showAndDismiss(Duration.millis(5000));
        cancel(event);
    }
    @FXML
    void delete(StackPane sp, JFXDialog dialog){
            JFXDialogLayout content = new JFXDialogLayout();
            content.setHeading(new Text("DELETE CLIENT"));
            content.setBody(new Text("Are you sure you want to delete this client id: "+ this.id +" ?"));
            dialog.setDialogContainer(sp);
            dialog.setContent(content);
            JFXButton cancelButton = new JFXButton("Cancel");
            JFXButton deleteButton = new JFXButton("Delete");
            deleteButton.getStyleClass().addAll("delete-button");
            content.setActions(deleteButton, cancelButton);
            cancelButton.setOnAction(event -> {
                dialog.close();
            });
            deleteButton.setOnAction(event -> {
                app.models.Client.delete(this.id);
                dialog.close();
            });
            dialog.show();

    }


}

