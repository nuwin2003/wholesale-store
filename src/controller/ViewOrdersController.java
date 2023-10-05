package controller;

import com.jfoenix.controls.JFXButton;
import db.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import model.Order;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

public class ViewOrdersController {

    @FXML
    private TableView<Order> tblOrder;

    @FXML
    private TableColumn<?, ?> clmOrderId;

    @FXML
    private TableColumn<?, ?> clmDate;

    @FXML
    private TableColumn<?, ?> clmCustomerId;

    @FXML
    private TextField txtDate;

    @FXML
    private Label lblOrderId;

    @FXML
    private Label lblOrderDate;

    @FXML
    private Label lblCustomerId;

    public void initialize() {
        setProperties();
        loadTable();
        setDate();
    }
    public void setDate(){
        txtDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
    }
    public void setProperties(){
        clmOrderId.setCellValueFactory(new PropertyValueFactory<>("id"));
        clmDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        clmCustomerId.setCellValueFactory(new PropertyValueFactory<>("customerId"));
    }
    public void loadTable() {
        ObservableList<Order> orderObservableList = FXCollections.observableArrayList();
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            Statement stm = connection.createStatement();
            ResultSet rst = stm.executeQuery("Select * from orders");
            while(rst.next()){
                orderObservableList.add(new Order(
                        rst.getString(1),
                        rst.getString(2),
                        rst.getString(3)
                ));
            }
            tblOrder.setItems(orderObservableList);
        } catch (SQLException | ClassNotFoundException e) {
            new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
        }
    }
    public void clearFields(){
        lblOrderId.setText("");
        lblOrderDate.setText("");
        lblCustomerId.setText("");
    }
    @FXML
    void ValueMouseOnClick(MouseEvent event) {
        if(!tblOrder.getSelectionModel().isEmpty()) {
            Order selectedRow = tblOrder.getSelectionModel().getSelectedItem();
            lblOrderId.setText(selectedRow.getId());
            lblOrderDate.setText(selectedRow.getDate());
            lblCustomerId.setText(selectedRow.getCustomerId());
        }
    }
    @FXML
    void deleteBtnOnAction(ActionEvent event) {
        Optional<ButtonType> buttonType = new Alert(Alert.AlertType.CONFIRMATION,"Do you want to Delete this Order ?",ButtonType.YES,ButtonType.NO).showAndWait();
        if(buttonType.get() == ButtonType.YES) {
            try {
                Connection connection = DBConnection.getInstance().getConnection();
                Statement stm = connection.createStatement();
                String SQL = "Delete from orders where id = '" + lblOrderId.getText() + "'";
                if (stm.executeUpdate(SQL) > 0) {
                    new Alert(Alert.AlertType.INFORMATION, "Delete Success!").show();
                }
            } catch (SQLException | ClassNotFoundException e) {
                new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
            }
        }
        loadTable();
        clearFields();
    }

    @FXML
    void btnHomeOnAction(ActionEvent event) {
        Optional<ButtonType> buttonType = new Alert(Alert.AlertType.CONFIRMATION,"Do you want to go to Home Page ?",ButtonType.YES,ButtonType.NO).showAndWait();
        if(buttonType.get() == ButtonType.YES) {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            try {
                stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../view/HomePage.fxml"))));
                stage.show();
            } catch (IOException e) {
                new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
            }
        }
    }

}
