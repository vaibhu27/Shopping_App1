package com.example.farmerhub.pojos;

public class Product {
    String id;
    String img;
    String title;
    String price;
    String desc;
    String farmerId;

    public Product(String id, String img, String title, String price, String desc, String farmerId) {
        this.id = id;
        this.img = img;
        this.title = title;
        this.price = price;
        this.desc = desc;
        this.farmerId = farmerId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getFarmerId() {
        return farmerId;
    }

    public void setFarmerId(String farmerId) {
        this.farmerId = farmerId;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id='" + id + '\'' +
                ", img='" + img + '\'' +
                ", title='" + title + '\'' +
                ", price='" + price + '\'' +
                ", desc='" + desc + '\'' +
                ", farmerId='" + farmerId + '\'' +
                '}';
    }
}
