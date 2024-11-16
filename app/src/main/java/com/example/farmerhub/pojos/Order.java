package com.example.farmerhub.pojos;

public class Order {
    public String id;
    public String buyerId;
    public String farmerId;
    public String productId;
    public String qty;
    public String status;
    public String productTitle;
    public String productPrice;



    public Order(String id, String buyerId, String farmerId, String productId, String qty, String status, String productTitle, String productPrice) {
        this.id = id;
        this.buyerId = buyerId;
        this.farmerId = farmerId;
        this.productId = productId;
        this.qty = qty;
        this.status = status;
        this.productTitle = productTitle;
        this.productPrice = productPrice;
    }
    public String getId() {
        return id;
    }
}
