package model;

public class Item {
    private String code;
    private String description;
    private double unitPrice;
    private int QtyOnHand;
    private int qty;
    private double itemTotal;

    public Item() {
    }

    public Item(String description, double unitPrice, int qtyOnHand) {
        this.description = description;
        this.unitPrice = unitPrice;
        QtyOnHand = qtyOnHand;
    }

    public Item(String code, String description, double unitPrice, int QtyOnHand) {
        this.code = code;
        this.description = description;
        this.unitPrice = unitPrice;
        this.QtyOnHand = QtyOnHand;
    }

    public Item(String code, String description, double unitPrice, int QtyOnHand, int qty, double itemTotal) {
        this.code = code;
        this.description = description;
        this.unitPrice = unitPrice;
        this.QtyOnHand = QtyOnHand;
        this.qty = qty;
        this.itemTotal = itemTotal;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int getQtyOnHand() {
        return QtyOnHand;
    }

    public void setQtyOnHand(int qtyOnHand) {
        QtyOnHand = qtyOnHand;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public double getItemTotal() {
        return itemTotal;
    }

    public void setItemTotal(double itemTotal) {
        this.itemTotal = itemTotal;
    }
}
