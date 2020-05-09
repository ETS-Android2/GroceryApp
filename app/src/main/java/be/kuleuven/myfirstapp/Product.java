package be.kuleuven.myfirstapp;

import android.os.Parcel;
import android.os.Parcelable;

import java.net.URL;

public class Product implements Parcelable {

    private long barcode;
    private String name;
    private Double price;
    private URL picture;
    private int quantity;

    public Product(long barcode, String name, Double price, URL picture){
        this.barcode = barcode;
        this.name = name;
        this.price = price;
        this.picture = picture;
    }
    public Product(long barcode, String name, Double price, int quantity){
        this.barcode = barcode;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }
    public Product(long barcode, String name, int quantity){
        this.barcode = barcode;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }
    public Product(long barcode, String name, Double price){
        this.barcode = barcode;
        this.name = name;
        this.price = price;
    }
    public Product(long barcode, String name){
        this.barcode = barcode;
        this.name = name;
    }
    public Product(long barcode, int quantity){
        this.barcode = barcode;
        this.quantity = quantity;
    }

    protected Product(Parcel in) {
        barcode = in.readLong();
        name = in.readString();
        if (in.readByte() == 0) {
            price = null;
        } else {
            price = in.readDouble();
        }
        quantity = in.readInt();
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

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

    public Double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public URL getPicture() {
        return picture;
    }

    public void setPicture(URL picture) {
        this.picture = picture;
    }

    public void setQuantity(int quantity){
        this.quantity = quantity;
    }

    public int getQuantity(){
        return quantity;
    }

    public void setQuantity1(){
        this.quantity = getQuantity()+1;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(barcode);
        dest.writeString(name);
        if (price == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(price);
        }
        dest.writeInt(quantity);
    }
}
