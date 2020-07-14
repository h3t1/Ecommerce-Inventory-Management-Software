package app.controllers;

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
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class Order extends Modal implements Initializable {
    private TreeItem<app.models.Order> model;
    private int id;
    @FXML
    private JFXComboBox<Integer> idClient;

    @FXML
    private JFXDatePicker date;

    @FXML
    private JFXTimePicker time;
    public Order(String title, TreeItem<app.models.Order> model) {
        super(title);
        this.model = model;
        if(model!=null){
            this.id = model.getValue().getIdOrder();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

            try {
                ObservableList<Integer> l = (ObservableList<Integer>) app.models.Client.getAll("id");
                idClient.setItems(l.sorted());
                time.set24HourView(true);
                LocalDate ld = LocalDate.now();
                LocalTime lt = LocalTime.now();
                date.setValue(ld);
                time.setValue(lt);

                if (this.id!=0) {
                    int index = l.sorted().indexOf(model.getValue().getIdClient());
                    idClient.getSelectionModel().select(index);
                    String datetime = model.getValue().getDate();
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    date.setValue(LocalDate.parse(datetime,dtf));
                    time.setValue(LocalTime.parse(datetime,dtf));
                }

            } catch (NullPointerException e) {
                TrayNotification tn = new TrayNotification("Error: app.controllers.Order","Unable to fill the form", NotificationType.ERROR);
                tn.showAndDismiss(Duration.millis(5000));
            }
    }

    @FXML
    @Override
    void save(ActionEvent event) throws IOException {
        int idClient = this.idClient.getSelectionModel().getSelectedItem();
        String date = this.date.getValue()+" "+this.time.getValue().toString();
        app.models.Order c = new app.models.Order(this.id, app.models.Client.getClient(idClient), date);
        TrayNotification tn = new TrayNotification();
        if(c.save()){
            tn.setTitle("Succeeded");
            tn.setMessage("Your data have been successfully saved!");
            tn.setNotificationType(NotificationType.SUCCESS);
        }
        else {
            tn.setTitle("Error: app.controlles.Order.save()");
            tn.setNotificationType(NotificationType.ERROR);
        }
        tn.showAndDismiss(Duration.millis(5000));
        cancel(event);
    }
    void delete(StackPane sp, JFXDialog dialog){
        JFXDialogLayout content = new JFXDialogLayout();
        content.setHeading(new Text("DELETE ORDER"));
        content.setBody(new Text("Are you sure you want to delete this order id: "+ this.id +" ?"));
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
            app.models.Order.delete(this.id);
            dialog.close();
        });
        dialog.show();

    }

}
