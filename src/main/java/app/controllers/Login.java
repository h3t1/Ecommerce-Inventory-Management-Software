package app.controllers;

import animatefx.animation.ZoomIn;
import app.Main;
import app.models.User;
import app.tray.notification.NotificationType;
import app.tray.notification.TrayNotification;
import app.util.HibernateConfig;
import app.util.InternetAvailabilityChecker;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXProgressBar;
import com.jfoenix.controls.JFXTextField;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static java.lang.Thread.sleep;

public class Login implements Initializable {
    @FXML
    JFXProgressBar pb;
    @FXML
    JFXTextField username;
    @FXML
    JFXPasswordField password;
    Parent main;
    private static boolean auth=false;
    @FXML
    public void switchToMainApp() throws IOException {
        Platform.runLater(() -> pb.setVisible(true));
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Login.auth = checkLogin();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(auth)
                Platform.runLater(() -> {
                    try {
                        main = Main.loadFXML("main");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Main.window.setOpacity(0);
                    Main.decorator.setContent(main);
                    Main.window.sizeToScene();
                    Main.window.centerOnScreen();
                    Timeline tick0 = new Timeline();
                    tick0.setCycleCount(Timeline.INDEFINITE);

                    tick0.getKeyFrames().add(
                            new KeyFrame(new Duration(15), new EventHandler<ActionEvent>() {
                                public void handle(ActionEvent t) {
                                    Main.window.setOpacity(Main.window.getOpacity()+0.01);
                                    if(Main.window.getOpacity()>0.99){
                                        Main.window.setOpacity(1);
                                        tick0.stop();
                                    }
                                }}));
                    tick0.play();
                    new ZoomIn(Main.decorator).play();

                    Main.decorator.setTitle("Product Manager");
                    TrayNotification tn = new TrayNotification();
                    tn.setTitle("Login succeeded!");
                    tn.setMessage("Welcome Hamza");
                    tn.setNotificationType(NotificationType.SUCCESS);
                    tn.showAndDismiss(Duration.millis(4500));
                });
                else{
                    Platform.runLater(() ->
                    {
                        pb.setVisible(false);
                        try {
                            if(InternetAvailabilityChecker.isInternetAvailable()){
                                TrayNotification tn = new TrayNotification("Error!","Unable to login. please check your login / password",NotificationType.ERROR);
                                tn.showAndDismiss(Duration.millis(4000));
                            }
                            else{
                                TrayNotification tn = new TrayNotification("Error!","Please check your internet connection and try again", NotificationType.ERROR);
                                tn.showAndDismiss(Duration.millis(4000));
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                    });

                }

            }
        }).start();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public boolean checkLogin() throws IOException {

         if(User.check(this.username.getText(),this.password.getText())){
             String  host="hamza.heliohost.org",
                     db="th_test",
                     port="3306",
                     useSSL="false",
                     username="th_test",
                     password="test123";
             return HibernateConfig.SetSessionFactory(host,port,db,useSSL,username,password);
         }

        return false;
    }
}