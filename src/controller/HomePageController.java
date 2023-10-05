package controller;

import db.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import model.Item;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

public class HomePageController {

    @FXML
    private AnchorPane anchorPaneHome;

    @FXML
    private TextField txtDate;

    @FXML
    private TableView<Item> tblPriceList;

    @FXML
    private TableColumn<?, ?> colItemDesc;

    @FXML
    private TableColumn<?, ?> colUnitPrice;

    @FXML
    private TableColumn<?, ?> colQtyOnHand;


    public void initialize(){
        setDate();
        setProperties();
        loadTable();
    }
    public void setDate(){
        txtDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
    }
    public void setProperties(){
        colItemDesc.setCellValueFactory(new PropertyValueFactory<>("description"));
        colUnitPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        colQtyOnHand.setCellValueFactory(new PropertyValueFactory<>("qtyOnHand"));
    }
    public void loadTable(){
        ObservableList<Item> itemObservableList = FXCollections.observableArrayList();
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            Statement stm = connection.createStatement();
            ResultSet rst = stm.executeQuery("Select description,unitPrice,qtyOnHand from item");
            while(rst.next()){
                itemObservableList.add(new Item(
                        rst.getString(1),
                        rst.getDouble(2),
                        rst.getInt(3)
                ));
            }
            tblPriceList.setItems(itemObservableList);
        } catch (SQLException | ClassNotFoundException e) {
            new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
        }
    }

    public void btnCustomerFormOnAction(ActionEvent actionEvent) throws IOException {
        URL resource = this.getClass().getResource("../view/CustomerForm.fxml");

        assert resource != null;

        Parent load = (Parent)FXMLLoader.load(resource);
        this.anchorPaneHome.getChildren().clear();
        this.anchorPaneHome.getChildren().add(load);
    }


    public void btnItemFormOnAction(ActionEvent actionEvent) throws IOException {
        URL resource = this.getClass().getResource("../view/ItemForm.fxml");

        assert resource != null;

        Parent load = (Parent) FXMLLoader.load(resource);
        this.anchorPaneHome.getChildren().clear();
        this.anchorPaneHome.getChildren().add(load);
    }

    public void btnOrderFormOnAction(ActionEvent actionEvent) throws IOException {
        URL resource = this.getClass().getResource("../view/OrderForm.fxml");

        assert resource != null;

        Parent load = (Parent) FXMLLoader.load(resource);
        this.anchorPaneHome.getChildren().clear();
        this.anchorPaneHome.getChildren().add(load);
    }
    @FXML
    void btnViewOrdersOnAction(ActionEvent event) throws IOException {
        URL resource = this.getClass().getResource("../view/ViewOrders.fxml");

        assert  resource != null;

        Parent load = (Parent) FXMLLoader.load(resource);
        this.anchorPaneHome.getChildren().clear();
        this.anchorPaneHome.getChildren().add(load);
    }
    @FXML
    void btnAboutUsOnAction(ActionEvent event) {

    }
    @FXML
    void btnExitOnAction(ActionEvent event) {
        Optional<ButtonType> buttonType = new Alert(Alert.AlertType.CONFIRMATION,"Do you want to Exit ?",ButtonType.YES,ButtonType.NO).showAndWait();
        if(buttonType.get() == ButtonType.YES) {
            System.exit(0);
        }
    }
}
