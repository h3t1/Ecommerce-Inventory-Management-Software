package app.controllers;

import animatefx.animation.*;
import app.models.*;
import app.models.Client;
import app.models.Order;
import app.models.OrderLine;
import app.tray.notification.NotificationType;
import app.tray.notification.TrayNotification;
import app.util.HibernateConfig;
import app.util.InternetAvailabilityChecker;
import app.util.JReporter;
import com.jfoenix.controls.*;
import com.jfoenix.controls.cells.editors.TextFieldEditorBuilder;
import com.jfoenix.controls.cells.editors.base.GenericEditableTreeTableCell;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.util.Duration;
import net.sf.jasperreports.engine.JRException;
import javax.persistence.NoResultException;
import java.io.IOException;
import java.net.SocketException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;


public class Main implements Initializable {
    @FXML
    public GridPane productsGP, orderLinesGP, ordersGP, clientsGP, dashboardGP,settingsGP;
    @FXML
    JFXTreeTableView<Client> clients_table;
    @FXML
    JFXTreeTableView<Order> orders_table;
    @FXML
    JFXTreeTableView<OrderLine> orderlines_table;
    @FXML
    JFXTreeTableView<Product> products_table;
    //Tables columns
    @FXML
    TreeTableColumn<Client, String> usernameCol,emailCol,telCol, cityCol, fullNameCol,addressCol;
    @FXML
    TreeTableColumn<Client, Number> idCol;
    @FXML
    TreeTableColumn<Order, Number> idOrderCol,idClientCol;
    @FXML
    TreeTableColumn<Order, String> dateCol;
    @FXML
    TreeTableColumn<OrderLine, Number> idOrderCol2,idProductCol2,quantityCol;
    @FXML
    TreeTableColumn<Product, Number> idProductCol;
    @FXML
    TreeTableColumn<Product, Double> costCol,priceCol;
    @FXML
    TreeTableColumn<Product, String> categoryCol,productNameCol,descriptionCol;
    @FXML
    JFXTextField filterField, host,port,user,password,db;
    @FXML
    JFXPasswordField password1, password2;
    @FXML
    JFXToggleButton useSSL;
    @FXML
    Label nor, date, os,arch, pid, ip, internet,userName,userEmail,userLL;
    @FXML
    JFXProgressBar pb;
    @FXML
    StackPane rootPane;
    @FXML
    private BarChart<?, ?> barChartTac;

    @FXML
    private CategoryAxis xclients;

    @FXML
    private NumberAxis ySpending;

    ObservableList<Client> OLclients;
    ObservableList<Order> OLorders;
    ObservableList<OrderLine> OLorderLines;
    ObservableList<Product> OLproducts;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        getClients();
        getOrders();
        getOrderLines();
        getProducts();
        XYChart.Series set1 = new XYChart.Series<>();
        for(Client c:OLclients)
        set1.getData().add(new XYChart.Data(c.getUsername(), Math.random()*7827));
        barChartTac.getData().addAll(set1);
        LocalDate d = LocalDate.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd, MMMM yyyy");
        date.setText(d.format(dtf));
        userName.setText("Username: "+User.connected.getUsername());
        userEmail.setText("Email: "+User.connected.getEmail());
        userLL.setText("Last time logged: "+User.connected.getLastLogin());
        os.setText(System.getProperty("os.name"));
        arch.setText(System.getProperty("os.arch"));
        pid.setText(String.valueOf(ProcessHandle.current().pid()));
        try {
            ip.setText(InternetAvailabilityChecker.getLocalIp());
        } catch (SocketException e) {
            e.printStackTrace();
        }
        try {
            this.checkInternet();
        } catch (IOException e) {
            e.printStackTrace();
        }
        setDbSettings();

    }
    @FXML
    private void checkInternet() throws IOException {
        if(InternetAvailabilityChecker.isInternetAvailable()){
            internet.setText("UP");
            internet.setTextFill(Paint.valueOf("#1aa275"));
        }
        else{
            internet.setText("DOWN");
            internet.setTextFill(Paint.valueOf("#f1828d"));
        }
    }
    @FXML
    private void testDBC(ActionEvent e){
        String host = HibernateConfig.host,
                port = HibernateConfig.port,
                user = HibernateConfig.user,
                useSSL = HibernateConfig.useSSL,
                password = HibernateConfig.password,
                db = HibernateConfig.db;

        String host2 = this.host.getText(),
                port2 = this.port.getText(),
                useSSL2 = String.valueOf(this.useSSL.isSelected()),
                user2 = this.user.getText(),
                password2 = this.password.getText(),
                db2 = this.db.getText();
        Platform.runLater(()-> this.pb.setVisible(true));
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean hc = HibernateConfig.SetSessionFactory(host2,port2,db2, useSSL2,user2,password2);
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Platform.runLater(() -> {
                    JFXDialogLayout content = new JFXDialogLayout();
                    JFXDialog dialog = new JFXDialog();
                    dialog.setDialogContainer(rootPane);
                    dialog.setContent(content);
                    JFXButton oKButton = new JFXButton("OK");
                    content.setActions(oKButton);
                    oKButton.setOnAction(event -> {
                        dialog.close();
                    });
                    if(hc){
                        Text t = new Text("Connection successful");
                        t.setFill(Paint.valueOf("green"));
                        content.setHeading(t);

                    }else {
                        Text t = new Text("Connection failed");
                        t.setFill(Paint.valueOf("red"));
                        content.setHeading(t);
                    }
                    JFXButton b = (JFXButton) e.getTarget();
                    String bName = b.getText();
                    content.setBody(new Text("host: "+host2+"\nport: "+port2));
                    dialog.show();

                    if(bName.equals("Test connection"))
                        HibernateConfig.SetSessionFactory(host,port,db,useSSL,user,password);
                    });
                   pb.setVisible(false);
            }
        }).start();
    }
    private void getNOR(JFXTreeTableView<?> table){
        nor.textProperty().bind(Bindings.createStringBinding(()->"Number of records: "+table.getCurrentItemsCount(),
                table.currentItemsCountProperty()));
    }

    /** CRUD CLIENT TABLE **/
    @FXML
    void addClient(ActionEvent event){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Platform.runLater(() -> {
                    try {
                        app.controllers.Client controller = new app.controllers.Client("ADD NEW CLIENT", null);
                        controller.createForm("client");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }finally {
                        getClients();
                    }
                });
        }
        }).start();
    }
    @FXML
    void editClient(ActionEvent event) throws IOException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Platform.runLater(()->{
                    try {
                        clients_table.getSelectionModel().getSelectedItem().getValue().getId();
                        app.controllers.Client controller = new app.controllers.Client("EDIT CLIENT", clients_table.getSelectionModel().getSelectedItem());
                        controller.createForm("client");
                    } catch (IOException | NullPointerException e ){
                        TrayNotification tn = new TrayNotification("Warning","Please select a row !", NotificationType.WARNING);
                        tn.showAndDismiss(Duration.millis(5000));
                    }finally {
                        getClients();
                    }
                });
            }
        }).start();
    }
    @FXML
    void deleteClient(ActionEvent event) throws IOException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Platform.runLater(()->{
                    try {
                        JFXDialog dialog = new JFXDialog();
                        clients_table.getSelectionModel().getSelectedItem().getValue().getId();
                        app.controllers.Client controller = new app.controllers.Client("DELETE CLIENT", clients_table.getSelectionModel().getSelectedItem());
                        controller.delete(rootPane, dialog);
                        dialog.setOnDialogClosed(event -> getClients());

                    } catch (NullPointerException e ){
                        TrayNotification tn = new TrayNotification("Warning","Please select a row !", NotificationType.WARNING);
                        tn.showAndDismiss(Duration.millis(5000));
                    }
                });
            }
        }).start();


    }
    private void buildClientTable() {
        idCol.setCellValueFactory(celldata ->
                new SimpleIntegerProperty(celldata.getValue().getValue().getId()));

        usernameCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<Client, String> param) -> {
            return new SimpleStringProperty(String.valueOf(param.getValue().getValue().getUsername()));
        });
        usernameCol.setCellFactory((TreeTableColumn<Client, String> param) -> new GenericEditableTreeTableCell<Client, String>(new TextFieldEditorBuilder()));
        usernameCol.setOnEditCommit((TreeTableColumn.CellEditEvent<Client, String> t) -> {
            t.getTreeTableView().getTreeItem(t.getTreeTablePosition().getRow()).getValue().setUsername(t.getNewValue());
            Platform.runLater(() -> updateClientCell(t));
        });

        emailCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<Client, String> param) -> {
            return new SimpleStringProperty(String.valueOf(param.getValue().getValue().getEmail()));
        });
        emailCol.setCellFactory((TreeTableColumn<Client, String> param) -> new GenericEditableTreeTableCell<Client, String>(new TextFieldEditorBuilder()));
        emailCol.setOnEditCommit((TreeTableColumn.CellEditEvent<Client, String> t) -> {
            t.getTreeTableView().getTreeItem(t.getTreeTablePosition().getRow()).getValue().setEmail(t.getNewValue());
            Platform.runLater(() -> updateClientCell(t));
        });

        telCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<Client, String> param) -> {
            return new SimpleStringProperty(String.valueOf(param.getValue().getValue().getTel()));
        });
        telCol.setCellFactory((TreeTableColumn<Client, String> param) -> new GenericEditableTreeTableCell<Client, String>(new TextFieldEditorBuilder()));
        telCol.setOnEditCommit((TreeTableColumn.CellEditEvent<Client, String> t) -> {
            t.getTreeTableView().getTreeItem(t.getTreeTablePosition().getRow()).getValue().setTel(t.getNewValue());
            Platform.runLater(() -> updateClientCell(t));
        });


        fullNameCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<Client, String> param) -> {
            return new SimpleStringProperty(String.valueOf(param.getValue().getValue().getFullName()));
        });
        fullNameCol.setCellFactory((TreeTableColumn<Client, String> param) -> new GenericEditableTreeTableCell<Client, String>(new TextFieldEditorBuilder()));
        fullNameCol.setOnEditCommit((TreeTableColumn.CellEditEvent<Client, String> t) -> {
            t.getTreeTableView().getTreeItem(t.getTreeTablePosition().getRow()).getValue().setFullName(t.getNewValue());
            Platform.runLater(() -> updateClientCell(t));
        });

        cityCol.setCellValueFactory(celldata ->
                new SimpleStringProperty(celldata.getValue().getValue().getCity().getVille()));
        cityCol.setCellFactory(c -> new GenericEditableTreeTableCell<Client, String>(new TextFieldEditorBuilder()));


        cityCol.setOnEditCommit(celldata -> {
            celldata.getTreeTableView().getTreeItem(celldata.getTreeTablePosition().getRow()).getValue().getCity().setVille(celldata.getNewValue());
            Platform.runLater(() -> updateClientCell(celldata));
        });

        addressCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<Client, String> param) -> {
            return new SimpleStringProperty(String.valueOf(param.getValue().getValue().getAddress()));
        });
        addressCol.setCellFactory((TreeTableColumn<Client, String> param) -> new GenericEditableTreeTableCell<Client, String>(new TextFieldEditorBuilder()));
        addressCol.setOnEditCommit((TreeTableColumn.CellEditEvent<Client, String> t) -> {
            t.getTreeTableView().getTreeItem(t.getTreeTablePosition().getRow()).getValue().setAddress(t.getNewValue());
            Platform.runLater(() -> updateClientCell(t));
        });

        /* Filter results */
        filterField.textProperty().addListener((o, oldVal, newVal) -> {
            clients_table.setPredicate(client -> (String.valueOf(client.getValue().getId()).contains(newVal))
                    || client.getValue().getUsername().toLowerCase().contains(newVal.toLowerCase())
                    || client.getValue().getEmail().toLowerCase().contains(newVal.toLowerCase())
                    || client.getValue().getTel().toLowerCase().contains(newVal.toLowerCase())
                    || client.getValue().getCity().getVille().toLowerCase().contains(newVal.toLowerCase()));
        });

        /* Number of records */
        getNOR(clients_table);
    }
    void getClients() {
        OLclients = (ObservableList<Client>) Client.getAll("*");
        final TreeItem<Client> root = new RecursiveTreeItem<Client>(OLclients, RecursiveTreeObject::getChildren);
        clients_table.getColumns().setAll(idCol,usernameCol,emailCol,telCol,fullNameCol,cityCol,addressCol);
        clients_table.setRoot(root);
        clients_table.setEditable(true);
        clients_table.setShowRoot(false);
    }


    /** CRUD ORDERLINE TABLE **/
    @FXML
    void addOrderLine(ActionEvent event){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Platform.runLater(() -> {
                    try {
                        app.controllers.Order controller = new app.controllers.Order("ADD NEW ORDER", null);
                        controller.createForm("order");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }finally {
                        getOrderLines();
                    }
                });
            }
        }).start();
    }
    @FXML
    void editOrderLine(ActionEvent event) throws IOException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Platform.runLater(()->{
                    try {
                        orders_table.getSelectionModel().getSelectedItem().getValue().getIdOrder();
                        app.controllers.Order controller = new app.controllers.Order("EDIT ORDER", orders_table.getSelectionModel().getSelectedItem());
                        controller.createForm("order");
                    } catch (IOException | NullPointerException e ){
                        e.printStackTrace();
                        TrayNotification tn = new TrayNotification("Warning","Please select a row !", NotificationType.WARNING);
                        tn.showAndDismiss(Duration.millis(5000));
                    }finally {
                        getOrderLines();
                    }
                });
            }
        }).start();
    }
    @FXML
    void deleteOrderLine(ActionEvent event) throws IOException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Platform.runLater(()->{
                    try {
                        JFXDialog dialog = new JFXDialog();
                        orders_table.getSelectionModel().getSelectedItem().getValue().getIdOrder();
                        app.controllers.Order controller = new app.controllers.Order("DELETE ORDER", orders_table.getSelectionModel().getSelectedItem());
                        controller.delete(rootPane, dialog);
                        dialog.setOnDialogClosed(event -> getOrderLines());

                    } catch (NullPointerException e ){
                        TrayNotification tn = new TrayNotification("Warning","Please select a row !", NotificationType.WARNING);
                        tn.showAndDismiss(Duration.millis(4500));
                    }
                });
            }
        }).start();


    }

    private void buildOrderLineTable() {
        idOrderCol2.setCellValueFactory(celldata ->
                new SimpleIntegerProperty(celldata.getValue().getValue().getIdOrder()));

        idProductCol2.setCellValueFactory(celldata ->
         new SimpleIntegerProperty(celldata.getValue().getValue().getIdProduct()));

       quantityCol.setCellValueFactory(celldata ->
               new SimpleIntegerProperty(celldata.getValue().getValue().getQuantity()));
        getNOR(orderlines_table);
        /* Filter results */
        filterField.textProperty().addListener((o, oldVal, newVal) -> {
            orderlines_table.setPredicate(orderline -> (String.valueOf(orderline.getValue().getIdOrder()).contains(newVal))
                    || String.valueOf(orderline.getValue().getIdProduct()).contains(newVal));
        });
    }

    public void getOrderLines() {
        OLorderLines = OrderLine.getAll("*");
        final TreeItem<OrderLine> root = new RecursiveTreeItem<OrderLine>(OLorderLines, RecursiveTreeObject::getChildren);
        orderlines_table.getColumns().setAll(idOrderCol2,idProductCol2,quantityCol);
        orderlines_table.setRoot(root);
        orderlines_table.setEditable(true);
        orderlines_table.setShowRoot(false);
    }

    /** CRUD ORDER TABLE **/
    @FXML
    void addOrder(ActionEvent event){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Platform.runLater(() -> {
                    try {
                        app.controllers.Order controller = new app.controllers.Order("ADD NEW ORDER", null);
                        controller.createForm("order");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }finally {
                        getOrders();
                    }
                });
            }
        }).start();
    }
    @FXML
    void editOrder(ActionEvent event) throws IOException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Platform.runLater(()->{
                    try {
                        orders_table.getSelectionModel().getSelectedItem().getValue().getIdOrder();
                        app.controllers.Order controller = new app.controllers.Order("EDIT ORDER", orders_table.getSelectionModel().getSelectedItem());
                        controller.createForm("order");
                    } catch (IOException | NullPointerException e ){
                        e.printStackTrace();
                        TrayNotification tn = new TrayNotification("Warning","Please select a row !", NotificationType.WARNING);
                        tn.showAndDismiss(Duration.millis(5000));
                    }finally {
                        getOrders();
                    }
                });
            }
        }).start();
    }
    @FXML
    void deleteOrder(ActionEvent event) throws IOException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Platform.runLater(()->{
                    try {
                        JFXDialog dialog = new JFXDialog();
                        orders_table.getSelectionModel().getSelectedItem().getValue().getIdOrder();
                        app.controllers.Order controller = new app.controllers.Order("DELETE ORDER", orders_table.getSelectionModel().getSelectedItem());
                        controller.delete(rootPane, dialog);
                        dialog.setOnDialogClosed(event -> getOrders());

                    } catch (NullPointerException e ){
                        TrayNotification tn = new TrayNotification("Warning","Please select a row !", NotificationType.WARNING);
                        tn.showAndDismiss(Duration.millis(4500));
                    }
                });
            }
        }).start();


    }
    public void getOrders() {
        OLorders = Order.getAll("*");
        final TreeItem<Order> root = new RecursiveTreeItem<Order>(OLorders, RecursiveTreeObject::getChildren);
        orders_table.getColumns().setAll(idOrderCol,idClientCol,dateCol);
        orders_table.setRoot(root);
        orders_table.setEditable(true);
        orders_table.setShowRoot(false);
    }
    private void buildOrderTable() {
        idOrderCol.setCellValueFactory(celldata ->
                new SimpleIntegerProperty(celldata.getValue().getValue().getIdOrder()));

        idClientCol.setCellValueFactory(celldata ->
                new SimpleIntegerProperty(celldata.getValue().getValue().getIdClient()));

        dateCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<Order, String> param) ->
                new SimpleStringProperty(param.getValue().getValue().getDate()));
        getNOR(orders_table);
        /* Filter results */
        filterField.textProperty().addListener((o, oldVal, newVal) -> {
            orders_table.setPredicate(order -> (String.valueOf(order.getValue().getIdOrder()).contains(newVal))
                    || String.valueOf(order.getValue().getIdClient()).contains(newVal)
                    || order.getValue().getDate().contains(newVal));
        });
    }


    /** CRUD Product TABLE **/
    @FXML
    void addProduct(ActionEvent event){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Platform.runLater(() -> {
                    try {
                        app.controllers.Order controller = new app.controllers.Order("ADD NEW ORDER", null);
                        controller.createForm("order");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }finally {
                        getOrders();
                    }
                });
            }
        }).start();
    }
    @FXML
    void editProduct(ActionEvent event) throws IOException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Platform.runLater(()->{
                    try {
                        orders_table.getSelectionModel().getSelectedItem().getValue().getIdOrder();
                        app.controllers.Order controller = new app.controllers.Order("EDIT ORDER", orders_table.getSelectionModel().getSelectedItem());
                        controller.createForm("order");
                    } catch (IOException | NullPointerException e ){
                        e.printStackTrace();
                        TrayNotification tn = new TrayNotification("Warning","Please select a row !", NotificationType.WARNING);
                        tn.showAndDismiss(Duration.millis(5000));
                    }finally {
                        getOrders();
                    }
                });
            }
        }).start();
    }
    @FXML
    void deleteProduct(ActionEvent event) throws IOException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Platform.runLater(()->{
                    try {
                        JFXDialog dialog = new JFXDialog();
                        orders_table.getSelectionModel().getSelectedItem().getValue().getIdOrder();
                        app.controllers.Order controller = new app.controllers.Order("DELETE ORDER", orders_table.getSelectionModel().getSelectedItem());
                        controller.delete(rootPane, dialog);
                        dialog.setOnDialogClosed(event -> getOrders());

                    } catch (NullPointerException e ){
                        TrayNotification tn = new TrayNotification("Warning","Please select a row !", NotificationType.WARNING);
                        tn.showAndDismiss(Duration.millis(4500));
                    }
                });
            }
        }).start();


    }

    private void buildProductTable() {
        idProductCol.setCellValueFactory(celldata ->
                new SimpleIntegerProperty(celldata.getValue().getValue().getIdProduct()));

        categoryCol.setCellValueFactory(celldata -> {
            return new SimpleStringProperty(String.valueOf(celldata.getValue().getValue().getCategory()));
        });
        categoryCol.setCellFactory(celldata -> new GenericEditableTreeTableCell<Product, String>(new TextFieldEditorBuilder()));
        categoryCol.setOnEditCommit(t -> {
            t.getTreeTableView().getTreeItem(t.getTreeTablePosition().getRow()).getValue().setCategory(t.getNewValue());
            Platform.runLater(() -> updateProductCell(t));
        });

        productNameCol.setCellValueFactory(param -> {
            return new SimpleStringProperty(String.valueOf(param.getValue().getValue().getProductName()));
        });
        productNameCol.setCellFactory(param -> new GenericEditableTreeTableCell<Product, String>(new TextFieldEditorBuilder()));
        productNameCol.setOnEditCommit( t-> {
            t.getTreeTableView().getTreeItem(t.getTreeTablePosition().getRow()).getValue().setProductName(t.getNewValue());
            Platform.runLater(() -> updateProductCell(t));
        });

        descriptionCol.setCellValueFactory(param -> {
            return new SimpleStringProperty(String.valueOf(param.getValue().getValue().getDesc()));
        });
        descriptionCol.setCellFactory(param -> new GenericEditableTreeTableCell<Product, String>(new TextFieldEditorBuilder()));
        descriptionCol.setOnEditCommit( t-> {
            t.getTreeTableView().getTreeItem(t.getTreeTablePosition().getRow()).getValue().setDesc(t.getNewValue());
            Platform.runLater(() -> updateProductCell(t));
        });

        costCol.setCellValueFactory(celldata ->
                new SimpleObjectProperty<Double>(celldata.getValue().getValue().getManufacturingCost()));

        priceCol.setCellValueFactory(celldata ->
                new SimpleObjectProperty<Double>(celldata.getValue().getValue().getManufacturingCost()));

        getNOR(products_table);
        /* Filter results */
        filterField.textProperty().addListener((o, oldVal, newVal) -> {
            products_table.setPredicate(p -> (String.valueOf(p.getValue().getIdProduct()).contains(newVal))
                    || p.getValue().getCategory().toLowerCase().contains(newVal.toLowerCase())
                    || p.getValue().getProductName().toLowerCase().contains(newVal.toLowerCase())
                    || p.getValue().getDesc().toLowerCase().contains(newVal.toLowerCase()));
        });
    }
    public void getProducts() {
        OLproducts = (ObservableList<Product>) Product.getAll("*");
        final TreeItem<Product> root = new RecursiveTreeItem<Product>(OLproducts, RecursiveTreeObject::getChildren);
        products_table.getColumns().setAll(idProductCol,categoryCol,productNameCol,descriptionCol,costCol,priceCol);
        products_table.setRoot(root);
        products_table.setEditable(true);
        products_table.setShowRoot(false);
    }



    JFXButton old = null;
    public void setGP(ActionEvent e) {
        JFXButton b = (JFXButton) e.getTarget();
        String bName = b.getText();
        if(!bName.equals("Settings"))
            b.setStyle("-fx-background-color:#e4f1fe");
        if(old==null){
            old=b;
        }
        else if(old!=b && !bName.equals("Settings")){
            old.setStyle("-fx-background-color:white");
            old=b;
        }


        switch(bName) {
            case "Dashboard":
                if(nor.getOpacity()>0.0) {
                    new FlipOutX(nor).play();
                    new FlipOutY(filterField).play();
                }
                dashboardGP.toFront();
                new Bounce(dashboardGP).play();
                break;
            case "Orders":
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Runnable updater = new Runnable() {
                            @Override
                            public void run() {
                                buildOrderTable();
                                getOrders();
                            }
                        };
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException interruptedException) {
                            interruptedException.printStackTrace();
                        }
                        Platform.runLater(updater);
                    }
                }).start();
                ordersGP.toFront();
                if(nor.getOpacity()==0.0) {
                    new FlipInX(nor).play();
                    new FlipInY(filterField).play();
                }
                new FadeInDown(ordersGP).play();
                break;
            case "Order lines":
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Runnable updater = new Runnable() {
                            @Override
                            public void run() {
                                buildOrderLineTable();
                                getOrderLines();
                            }
                        };
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException interruptedException) {
                            interruptedException.printStackTrace();
                        }
                        Platform.runLater(updater);
                    }
                }).start();
                orderLinesGP.toFront();
                if(nor.getOpacity()==0.0) {
                    new FlipInX(nor).play();
                    new FlipInY(filterField).play();
                }
                new JackInTheBox(orderLinesGP).play();
                break;
            case "Products":
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Runnable updater = new Runnable() {
                            @Override
                            public void run() {
                                buildProductTable();
                                getProducts();
                            }
                        };
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException interruptedException) {
                            interruptedException.printStackTrace();
                        }
                        Platform.runLater(updater);
                    }
                }).start();
                if(nor.getOpacity()==0.0) {
                    new FlipOutX(nor).play();
                    new FlipOutY(filterField).play();
                }
                productsGP.toFront();
                new ZoomInRight(productsGP).play();
                break;
            case "Clients":
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Runnable updater = new Runnable() {
                            @Override
                            public void run() {
                                buildClientTable();
                                getClients();
                            }
                        };
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException interruptedException) {
                            interruptedException.printStackTrace();
                        }
                        Platform.runLater(updater);
                    }
                }).start();
                if(nor.getOpacity()==0.0) {
                    new FlipInX(nor).play();
                    new FlipInY(filterField).play();
                }
                clientsGP.toFront();
                new ZoomInLeft(clientsGP).play();
                break;
            case "Settings":
                settingsGP.toFront();
                if(nor.getOpacity()>0.0) {
                    new FlipOutX(nor).play();
                    new FlipOutY(filterField).play();
                }
                new FlipInX(settingsGP).play();
        }
    }
    @FXML
    void exportToCSV(ActionEvent event) throws JRException {

        new JReporter().exportTo("csv",OLclients);
    }

    @FXML
    void exportToHTML(ActionEvent event) throws JRException {

        JReporter jr = new JReporter();
        jr.exportTo("html", OLclients);
    }

    @FXML
    void exportToPDF(ActionEvent event) throws JRException {
        new JReporter().exportTo("pdf",OLclients);

        }

    @FXML
    void exportToXLSX(ActionEvent event) throws JRException {
        new JReporter().exportTo("xlxs",OLclients);
    }

    @FXML
    void exportToXML(ActionEvent event) throws JRException {
        new JReporter().exportTo("xml",OLclients);
    }
    @FXML
    void changePassword(ActionEvent e){
        if(password1.getText().equals(password2.getText())){
            User u = User.connected;
            u.setPassword(password1.getText());
            if(u.save()){
               TrayNotification tn = new TrayNotification("Success","Your password has been successfully updated!",NotificationType.SUCCESS);
               tn.showAndDismiss(Duration.millis(4939));
            }else{
                TrayNotification tn = new TrayNotification("Error","We're unable to update your password!",NotificationType.ERROR);
                tn.showAndDismiss(Duration.millis(4939));
            }
        }else{
            TrayNotification tn = new TrayNotification("Warning","Your password and confirm password do not match!",NotificationType.WARNING);
            tn.showAndDismiss(Duration.millis(4939));
        }
    }
    void updateClientCell(TreeTableColumn.CellEditEvent<Client, String> t){
        TreeItem<Client> model = t.getTreeTableView().getTreeItem(t.getTreeTablePosition().getRow());
        try{
        (new Client(model.getValue().getId(),
                model.getValue().getUsername(),
                model.getValue().getEmail(),
                model.getValue().getTel(),
                model.getValue().getFullName(),
                City.getCity(model.getValue().getCity().getVille()),
                model.getValue().getAddress()
        )).save();
        }catch (NoResultException | NullPointerException | IllegalStateException e){
            TrayNotification tn = new TrayNotification();
            tn.setTitle("Error");
            tn.setMessage("The city you entered does not exist!");
            tn.setNotificationType(NotificationType.ERROR);
            tn.showAndDismiss(Duration.millis(5000));
        }
    }
    void updateProductCell(TreeTableColumn.CellEditEvent<Product, String> t){
        TreeItem<Product> model = t.getTreeTableView().getTreeItem(t.getTreeTablePosition().getRow());
        try{
            (new Product(model.getValue().getIdProduct(),
                    model.getValue().getCategory(),
                    model.getValue().getProductName(),
                    model.getValue().getDesc(),
                    model.getValue().getManufacturingCost(),
                    model.getValue().getBuyingPrice()
            )).save();
        }catch (NoResultException | NullPointerException | IllegalStateException e){
            TrayNotification tn = new TrayNotification();
            tn.setTitle("Error");
            tn.setMessage("The data you entered is not correct!");
            tn.setNotificationType(NotificationType.ERROR);
            tn.showAndDismiss(Duration.millis(5000));
        }
    }

    void setDbSettings(){
        this.host.setText(HibernateConfig.host);
        this.port.setText(HibernateConfig.port);
        this.useSSL.setSelected(Boolean.parseBoolean((HibernateConfig.useSSL)));
        this.db.setText(HibernateConfig.db);
        this.user.setText(HibernateConfig.user);
        this.password.setText(HibernateConfig.password);
    }
}