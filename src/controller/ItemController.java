package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
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
import model.Item;
import model.OrderDetail;

import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

public class ItemController {
    @FXML
    private JFXTextField txtCode;

    @FXML
    private TextField txtDate;

    @FXML
    private JFXTextField txtDescription;

    @FXML
    private JFXTextField txtQtyonHand;

    @FXML
    private JFXTextField txtUnitPrice;

    @FXML
    private TableView<Item> tblItem;

    @FXML
    private TableColumn<?, ?> colCode;

    @FXML
    private TableColumn<?, ?> colDescription;

    @FXML
    private TableColumn<?, ?> colQtyonHand;

    @FXML
    private TableColumn<?, ?> colUnitPrice;

    public void initialize(){
        setProperty();
        loadTable();
        setDate();
    }
    public void setProperty(){
        colCode.setCellValueFactory(new PropertyValueFactory<>("code"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colUnitPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        colQtyonHand.setCellValueFactory(new PropertyValueFactory<>("QtyOnHand"));
    }
    public void setDate(){
        txtDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
    }

    private void loadTable() {
        ObservableList<Item> list = FXCollections.observableArrayList();
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            Statement stm = connection.createStatement();
            ResultSet rst = stm.executeQuery("Select * from item");
            while(rst.next()){
                list.add(new Item(rst.getString(1),
                        rst.getString(2),
                        rst.getDouble(3),
                        rst.getInt(4)
                ));
            }
            tblItem.setItems(list);
        } catch (SQLException | ClassNotFoundException e) {
            new Alert(Alert.AlertType.ERROR,e.getMessage());
        }
    }

    @FXML
    void addBtnOnAction(ActionEvent event) {
        Optional<ButtonType> buttonType = new Alert(Alert.AlertType.CONFIRMATION,"Do you want add this Item ?",ButtonType.YES,ButtonType.NO).showAndWait();
        if(buttonType.get() == ButtonType.YES) {
            Item item = new Item(txtCode.getText(), txtDescription.getText(), Double.parseDouble(txtUnitPrice.getText()), Integer.parseInt(txtQtyonHand.getText()));
            try {
                Connection connection = DBConnection.getInstance().getConnection();
                String SQL = "Insert into item Values(?,?,?,?)";
                PreparedStatement pstm = connection.prepareStatement(SQL);
                pstm.setObject(1, item.getCode());
                pstm.setObject(2, item.getDescription());
                pstm.setObject(3, item.getUnitPrice());
                pstm.setObject(4, item.getQtyOnHand());
                if (pstm.executeUpdate() > 0) {
                    new Alert(Alert.AlertType.INFORMATION, "Added Success!").show();
                }else{
                    new Alert(Alert.AlertType.ERROR,"Something went Wrong!").show();
                }
            } catch (SQLException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        loadTable();
        clearFields();
    }

    @FXML
    void clearBtnOnAction(ActionEvent event) {
        clearFields();
    }
    public void clearFields(){
        txtCode.setText("");
        txtDescription.setText("");
        txtUnitPrice.setText("");
        txtQtyonHand.setText("");
    }

    @FXML
    void deleteBtnOnAction(ActionEvent event) {
        Optional<ButtonType> buttonType = new Alert(Alert.AlertType.CONFIRMATION,"Do you want to Delete this Item ?",ButtonType.YES,ButtonType.NO).showAndWait();
        if(buttonType.get() == ButtonType.YES) {
            String code = txtCode.getText();
            try {
                Connection connection = DBConnection.getInstance().getConnection();
                Statement stm = connection.createStatement();
                String SQL = "Delete from item where code ='" + code + "'";
                if (stm.executeUpdate(SQL) > 0) {
                    new Alert(Alert.AlertType.INFORMATION, "Delete Success!").show();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Something went Wrong!").show();
                }
            } catch (SQLException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        loadTable();
        clearFields();
    }

    @FXML
    void txtCodeOnAction(ActionEvent event) {
        String code = txtCode.getText();
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            Statement stm = connection.createStatement();
            ResultSet rst = stm.executeQuery("Select * from item where code ='"+code+"'");
            if(rst.next()){
                txtDescription.setText(rst.getString(2));
                txtQtyonHand.setText(String.valueOf(rst.getInt(3)));
                txtUnitPrice.setText(String.valueOf(rst.getDouble(4)));
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void updateBtnOnAction(ActionEvent event) {
        Optional<ButtonType> buttonType = new Alert(Alert.AlertType.CONFIRMATION,"Do you want to Update this Item ?",ButtonType.YES,ButtonType.NO).showAndWait();
        if(buttonType.get() == ButtonType.YES) {
            String code = txtCode.getText();
            String description = txtDescription.getText();
            double unitPrice = Double.parseDouble(txtUnitPrice.getText());
            int QtyOnHand = Integer.parseInt(txtQtyonHand.getText());
            Item item = new Item(code, description, unitPrice, QtyOnHand);
            try {
                Connection connection = DBConnection.getInstance().getConnection();
                PreparedStatement pstm = connection.prepareStatement("Update item set description = ?,unitPrice = ?,qtyOnHand = ? where code = ?");
                pstm.setObject(1, item.getDescription());
                pstm.setObject(2, item.getUnitPrice());
                pstm.setObject(3, item.getQtyOnHand());
                pstm.setObject(4, item.getCode());
                if (pstm.executeUpdate() > 0) {
                    new Alert(Alert.AlertType.INFORMATION, "Update Success!").show();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Something went Wrong!").show();
                }
            } catch (SQLException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        loadTable();
        clearFields();
    }

    @FXML
    void valueMouseOnClick(MouseEvent event) {
        if(!tblItem.getSelectionModel().isEmpty()){
            Item selectedRow = (Item) tblItem.getSelectionModel().getSelectedItem();
            txtCode.setText(selectedRow.getCode());
            txtDescription.setText(selectedRow.getDescription());
            txtUnitPrice.setText(String.valueOf(selectedRow.getUnitPrice()));
            txtQtyonHand.setText(String.valueOf(selectedRow.getQtyOnHand()));
        }
    }

    @FXML
    void btnHomeOnAction(ActionEvent event) {
        Optional<ButtonType> buttonType = new Alert(Alert.AlertType.CONFIRMATION,"Do you want to go to Home Page ?",ButtonType.YES,ButtonType.NO).showAndWait();
        if(buttonType.get() == ButtonType.YES){
            Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
            try {
                stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../view/HomePage.fxml"))));
                stage.show();
            } catch (IOException e) {
                new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
            }
        }
    }
    public static boolean updateItemQty(ArrayList<OrderDetail> OrderDetailArrayList){
        for (OrderDetail orderDetail : OrderDetailArrayList) {
            boolean isQtyUpdated = updateThisItemQty(orderDetail);
            if (!isQtyUpdated){
                return false;
            }
        }
        return true;
    }
    public static boolean updateThisItemQty(OrderDetail orderDetail){
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            PreparedStatement pstm = connection.prepareStatement("Update item set qtyOnHand = (qtyOnHand - ?) where code = ?");
            pstm.setObject(1,orderDetail.getQty());
            pstm.setObject(2,orderDetail.getItemCode());
            if (pstm.executeUpdate() > 0){
                return true;
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return false;
    }
}
