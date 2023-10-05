package controller;

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
import model.Customer;

import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

public class CustomerController {
    @FXML
    private JFXTextField txtId;

    @FXML
    private TableView<Customer> tblCustomer;

    @FXML
    private TableColumn<?, ?> colId;

    @FXML
    private TableColumn<?, ?> colName;

    @FXML
    private TableColumn<?, ?> colAddress;

    @FXML
    private TableColumn<?, ?> colSalary;

    @FXML
    private JFXTextField txtName;

    @FXML
    private JFXTextField txtAddress;

    @FXML
    private JFXTextField txtSalary;

    @FXML
    private TextField txtDate;

    public void initialize(){
        txtDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        loadTable();
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
    public void loadTable(){
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colSalary.setCellValueFactory(new PropertyValueFactory<>("salary"));

        ObservableList<Customer> list = FXCollections.observableArrayList();
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            Statement stm = connection.createStatement();
            ResultSet rst = stm.executeQuery("Select * from customer");
            while(rst.next()){
                list.add(new Customer(rst.getString(1),
                        rst.getString(2),
                        rst.getString(3),
                        rst.getDouble(4)
                ));
            }
            tblCustomer.setItems(list);
        } catch (SQLException | ClassNotFoundException e) {
            new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
        }
    }

    @FXML
    void addBtnOnAction(ActionEvent event) {
        Optional<ButtonType> buttonType = new Alert(Alert.AlertType.CONFIRMATION,"Do you want to add this Customer?",ButtonType.YES,ButtonType.NO).showAndWait();
        if(buttonType.get() == ButtonType.YES) {
            String id = txtId.getText();
            String name = txtName.getText();
            String address = txtAddress.getText();
            double salary = Double.parseDouble(txtSalary.getText());

            Customer customer = new Customer(id, name, address, salary);
            try {
                Connection connection = DBConnection.getInstance().getConnection();
                PreparedStatement pstm = connection.prepareStatement("Insert into customer Values(?,?,?,?)");
                pstm.setObject(1, customer.getId());
                pstm.setObject(2, customer.getName());
                pstm.setObject(3, customer.getAddress());
                pstm.setObject(4, customer.getSalary());
                if (pstm.executeUpdate() > 0) {
                    new Alert(Alert.AlertType.INFORMATION, "Added Success!").show();
                }else{
                    new Alert(Alert.AlertType.ERROR, "Something went Wrong!").show();
                }
            } catch (SQLException | ClassNotFoundException e) {
                new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
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
        txtId.setText("");
        txtName.setText("");
        txtAddress.setText("");
        txtSalary.setText("");
    }

    @FXML
    void deleteBtnOnAction(ActionEvent event) {
        Optional<ButtonType> buttonType = new Alert(Alert.AlertType.CONFIRMATION,"Do you want to delete this Customer?",ButtonType.YES,ButtonType.NO).showAndWait();
        if(buttonType.get() == ButtonType.YES) {
            String id = txtId.getText();
            try {
                Connection connection = DBConnection.getInstance().getConnection();
                Statement stm = connection.createStatement();
                String SQL = "Delete from customer where id='" + id + "'";
                if (stm.executeUpdate(SQL) > 0) {
                    new Alert(Alert.AlertType.INFORMATION, "Delete Success!").show();
                }else{
                    new Alert(Alert.AlertType.ERROR, "Something went Wrong!").show();
                }
            } catch (SQLException | ClassNotFoundException e) {
                new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
            }
        }
        loadTable();
        clearFields();
    }

    @FXML
    void updateBtnOnAction(ActionEvent event) {
        Optional<ButtonType> buttonType = new Alert(Alert.AlertType.CONFIRMATION,"Do you want to Update this Contact ?",ButtonType.YES,ButtonType.NO).showAndWait();
        if(buttonType.get() == ButtonType.YES) {
            Customer customer = new Customer(txtId.getText(), txtName.getText(), txtAddress.getText(), Double.parseDouble(txtSalary.getText()));
            try {
                Connection connection = DBConnection.getInstance().getConnection();
                PreparedStatement pstm = connection.prepareStatement("Update customer set name= ? ,address= ? , salary= ? where id= ?");
                pstm.setObject(1, customer.getName());
                pstm.setObject(2, customer.getAddress());
                pstm.setObject(3, customer.getSalary());
                pstm.setObject(4, customer.getId());
                if (pstm.executeUpdate() > 0) {
                    new Alert(Alert.AlertType.INFORMATION, "Update Success!").show();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Something went Wrong!").show();
                }
            } catch (SQLException | ClassNotFoundException e) {
                new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
            }
        }
        loadTable();
        clearFields();
    }
    @FXML
    void txtIdOnAction(ActionEvent event) {
        String id = txtId.getText();
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            ResultSet rst = connection.createStatement().executeQuery("Select * from customer where id='" + id + "'");
            Customer customer = null;
            if (rst.next()) {
                customer = new Customer(id, rst.getString(2), rst.getString(3), rst.getDouble(4));
            }
            if (customer != null) {
                txtName.setText(customer.getName());
                txtAddress.setText(customer.getAddress());
                txtSalary.setText(String.valueOf(customer.getSalary()));
            }else{
                new Alert(Alert.AlertType.ERROR,"Something went Wrong!").show();
            }
        } catch (SQLException | ClassNotFoundException e) {
            new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
        }
    }

    @FXML
    void valueMouseOnClick(MouseEvent event) {
        if (!tblCustomer.getSelectionModel().isEmpty()) {
            Customer selectedRow = tblCustomer.getSelectionModel().getSelectedItem();
            txtId.setText(selectedRow.getId());
            txtName.setText(selectedRow.getName());
            txtAddress.setText(selectedRow.getAddress());
            txtSalary.setText(String.valueOf(selectedRow.getSalary()));
        }
    }
}
