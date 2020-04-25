package be.kuleuven.myfirstapp;

import java.net.URL;

public class Product {

    private long barcode;
    private String name;
    private int price;
    private URL picture;

    public Product(long barcode, String name, int price, URL picture){
        this.barcode = barcode;
        this.name = name;
        this.price = price;
        this.picture = picture;
    }
    public Product(long barcode, String name, int price){
        this.barcode = barcode;
        this.name = name;
        this.price = price;
        this.picture = picture;
    }
    public Product(long barcode, String name){
        this.barcode = barcode;
        this.name = name;
    }

    public long getBarcode() {
        return barcode;
    }

    public void setBarcode(long barcode) {
        this.barcode = barcode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public URL getPicture() {
        return picture;
    }

    public void setPicture(URL picture) {
        this.picture = picture;
    }
}
