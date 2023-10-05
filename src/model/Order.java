package model;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.util.ArrayList;

public class Order {
    private String id;
    private String date;
    private String customerId;
    private ArrayList<OrderDetail> orderDetailArrayList;

    public Order() {
    }

    public Order(String id, String date, String customerId) {
        this.id = id;
        this.date = date;
        this.customerId = customerId;
    }

    public Order(String id, String date, String customerId, ArrayList<OrderDetail> orderDetailArrayList) {
        this.id = id;
        this.date = date;
        this.customerId = customerId;
        this.orderDetailArrayList = orderDetailArrayList;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public ArrayList<OrderDetail> getOrderDetailArrayList() {
        return orderDetailArrayList;
    }

    public void setOrderDetailArrayList(ArrayList<OrderDetail> orderDetailArrayList) {
        this.orderDetailArrayList = orderDetailArrayList;
    }
}
