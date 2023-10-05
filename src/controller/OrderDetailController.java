package controller;

import db.DBConnection;
import model.OrderDetail;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

public class OrderDetailController {
    public static boolean addOrderDetail(ArrayList<OrderDetail> OrderDetailArrayList){
        for (OrderDetail orderDetail: OrderDetailArrayList) {
            boolean isAdded = addThisOrderDetail(orderDetail);
            if (!isAdded){
                return false;
            }
        }
        return true;
    }
    public static boolean addThisOrderDetail(OrderDetail orderDetail){
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            PreparedStatement pstm = connection.prepareStatement("Insert into orderDetail Values(?, ?, ?, ?)");
            pstm.setObject(1,orderDetail.getOrderId());
            pstm.setObject(2,orderDetail.getItemCode());
            pstm.setObject(3,orderDetail.getQty());
            pstm.setObject(4,orderDetail.getUnitPrice());
            if (pstm.executeUpdate() > 0){
                return true;
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return false;
    }
}
