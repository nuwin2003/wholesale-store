package controller;

import com.jfoenix.controls.JFXComboBox;
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
import javafx.stage.Stage;
import model.Item;
import model.Order;
import model.OrderDetail;

import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

public class OrderController {

    @FXML
    private Label lblOrderId;

    @FXML
    private JFXComboBox<String> cmbCustId;

    @FXML
    private JFXComboBox<String> cmbItemCode;

    @FXML
    private Label lblDescription;

    @FXML
    private Label lblQtyOnHand;

    @FXML
    private Label lblUnitPrice;

    @FXML
    private TextField txtItemQty;

    @FXML
    private TableView<Item> tblOrder;

    @FXML
    private TableColumn<?, ?> colItemCode;

    @FXML
    private TableColumn<?, ?> colDescription;

    @FXML
    private TableColumn<?, ?> colUnitPrice;

    @FXML
    private TableColumn<?, ?> colQty;

    @FXML
    private TableColumn<?, ?> colItemTotal;

    @FXML
    private Label lblCustName;

    @FXML
    private TextField txtDate;

    @FXML
    private Label lblTotal;

    ObservableList<Item> list = FXCollections.observableArrayList();
    double total = 0;

    public void initialize(){
        setOrderId();
        setDate();
        loadAllCustomerIds();
        loadAllItemCodes();
        setProperty();
        tblOrder.setItems(list);
        setTotal();
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
    public void setOrderId(){
        String lastId = getLastOrderId();
        if(lastId != null){
            lastId = lastId.split("[A-Z]")[1];
            lastId = String.format("D%03d", (Integer.parseInt(lastId) + 1));
            lblOrderId.setText(lastId);
        }else{
            lblOrderId.setText("D001");
        }
    }

    public String getLastOrderId(){
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            Statement stm = connection.createStatement();
            ResultSet rst = stm.executeQuery("Select id from orders ORDER BY id DESC Limit 1");
            return rst.next() ? rst.getString("id") : null;

        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    public void setDate(){
        txtDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
    }

    public void loadAllCustomerIds(){
        ObservableList<String> observableList = FXCollections.observableArrayList(getAllCustomerIds());
        cmbCustId.setItems(observableList);
    }
    public ArrayList<String> getAllCustomerIds(){
        try {
            ArrayList<String> idList = new ArrayList<>();
            Connection connection = DBConnection.getInstance().getConnection();
            Statement stm = connection.createStatement();
            ResultSet rst = stm.executeQuery("Select id from customer");
            while(rst.next()){
                idList.add(rst.getString(1));
            }
            return idList;
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    public void loadAllItemCodes(){
        ObservableList<String> observableList = FXCollections.observableArrayList(getAllItemCodes());
        cmbItemCode.setItems(observableList);
    }
    public ArrayList<String> getAllItemCodes(){
        ArrayList<String> codeList = new ArrayList<>();
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            Statement stm = connection.createStatement();
            ResultSet rst = stm.executeQuery("Select code from item");
            while(rst.next()){
                codeList.add(rst.getString(1));
            }
            return codeList;
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void CustomerIdStateChanged(ActionEvent event) {
        String CustomerId = cmbCustId.getValue();
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            Statement stm = connection.createStatement();
            ResultSet rst = stm.executeQuery("Select name from customer where id='" + CustomerId + "'");
            if(rst.next()){
                lblCustName.setText(rst.getString("name"));
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void itemCodeStateChanged(ActionEvent event) {
        String code = cmbItemCode.getValue();
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            Statement stm = connection.createStatement();
            ResultSet rst = stm.executeQuery("Select * from item where code='" + code + "'");
            if(rst.next()){
                lblDescription.setText(rst.getString(2));
                lblUnitPrice.setText(String.format("%.2f",rst.getDouble(3)));
                lblQtyOnHand.setText(String.valueOf(rst.getInt(4)));
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void btnAddOnAction(ActionEvent event) {
        String code = cmbItemCode.getValue();
        String description = lblDescription.getText();
        double unitPrice =  Double.parseDouble(lblUnitPrice.getText());
        int QtyOnHand = Integer.parseInt(lblQtyOnHand.getText());
        int qty = Integer.parseInt(txtItemQty.getText());
        double itemTotal = Integer.parseInt(txtItemQty.getText()) * unitPrice;
        if(qty > QtyOnHand) {
            new Alert(Alert.AlertType.ERROR,"Qty is not Available!").show();
        } else {
            int row = isAlreadyExist(code);
            if (row == -1) {
                Item item = new Item(code, description, unitPrice, QtyOnHand, qty, itemTotal);
                total += itemTotal;
                list.add(item);
                setTotal();
                txtItemQty.setText("");
            } else {
                Item item = tblOrder.getItems().get(row);
                double newItemTotal = item.getItemTotal() + itemTotal;
                item.setQty(item.getQty() + qty);
                item.setItemTotal(newItemTotal);
                tblOrder.refresh();
                total += itemTotal;
                setTotal();
                txtItemQty.setText("");
            }
        }
    }

    public int isAlreadyExist(String code){
        for(int i=0; i < tblOrder.getItems().size(); i++){
            Item itemRow = tblOrder.getItems().get(i);
            if(code.equals(itemRow.getCode())){
                return i;
            }
        }
        return -1;
    }
    public void setProperty(){
        colItemCode.setCellValueFactory(new PropertyValueFactory<>("code"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colUnitPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        colQty.setCellValueFactory(new PropertyValueFactory<>("qty"));
        colItemTotal.setCellValueFactory(new PropertyValueFactory<>("itemTotal"));
    }
    public void setTotal(){
        lblTotal.setText(String.format("%.2f",total));
    }

    @FXML
    void btnRemoveOnAction(ActionEvent event) {
        if(!tblOrder.getSelectionModel().isEmpty()) {
            Item selectedRow = tblOrder.getSelectionModel().getSelectedItem();
            list.remove(selectedRow);
            total -= selectedRow.getItemTotal();
            setTotal();
        }
    }
    @FXML
    void btnPlaceOrderOnAction(ActionEvent event) throws SQLException, ClassNotFoundException {
        Optional<ButtonType> buttonType = new Alert(Alert.AlertType.CONFIRMATION,"Do you want to Place this Order ?",ButtonType.YES,ButtonType.NO).showAndWait();
        if(buttonType.get() == ButtonType.YES) {
            Connection connection = DBConnection.getInstance().getConnection();
            try {
                connection.setAutoCommit(false);
                ArrayList<OrderDetail> OrderDetailsArrayList = new ArrayList<>();
                for (int i = 0; i < tblOrder.getItems().size(); i++) {
                    String orderId = lblOrderId.getText();
                    String itemCode = tblOrder.getItems().get(i).getCode();
                    int qty = tblOrder.getItems().get(i).getQty();
                    double unitPrice = tblOrder.getItems().get(i).getUnitPrice();
                    OrderDetail orderDetail = new OrderDetail(orderId, itemCode, qty, unitPrice);
                    OrderDetailsArrayList.add(orderDetail);
                }
                Order order = new Order(lblOrderId.getText(), txtDate.getText(), cmbCustId.getValue(), OrderDetailsArrayList);
                PreparedStatement pstm = connection.prepareStatement("Insert into orders Values(?, ?, ?)");
                pstm.setObject(1, order.getId());
                pstm.setObject(2, order.getDate());
                pstm.setObject(3, order.getCustomerId());
                boolean isOrderAdded = pstm.executeUpdate() > 0;
                if (isOrderAdded) {
                    boolean isOrderDetailAdded = OrderDetailController.addOrderDetail(order.getOrderDetailArrayList());
                    if (isOrderDetailAdded) {
                        boolean isItemQtyUpdated = ItemController.updateItemQty(order.getOrderDetailArrayList());
                        if (isItemQtyUpdated) {
                            new Alert(Alert.AlertType.INFORMATION, "Placed Successfully!").show();
                            connection.commit();
                            setOrderId();
                            clearFieldValues();
                            clearTable();
                        } else {
                            new Alert(Alert.AlertType.ERROR, "Something went Wrong!").show();
                        }
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }
    public void clearFieldValues(){
        cmbCustId.getSelectionModel().clearSelection();
        lblCustName.setText("");
        cmbItemCode.getSelectionModel().clearSelection();
        lblDescription.setText("");
        lblQtyOnHand.setText("");
        lblUnitPrice.setText("");
    }
    public void clearTable(){
        list.clear();
    }
}

