package com.example.java_bookstore;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Bill implements Serializable {
    private static final long serialVersionUID = -6015536657969848359L;

    private String isbn;
    private String title;
    private int quantity;
    private double price;
    private Date timestamp;

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }
    public String getTitle(){
        return title;
    }
    private static double totalBillValue = 0.0;  // Initialize totalBillValue

    public Bill() {
        this.isbn = "";
        this.title = "";
        this.quantity = 0;
        this.price = 0.0;
        this.timestamp = new Date();
    }

    public Bill(String isbn, String title, int quantity, double price) {
        this.isbn = isbn;
        this.title = title;
        this.quantity = quantity;
        this.price = price;
        this.timestamp = new Date();
        totalBillValue = quantity * price;
    }

    public static double getTotalBillValue() {
        return totalBillValue;
    }

    @Override
    public String toString() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd_HHmmss");
        return "ISBN: " + isbn + "\n" +
                "Title: " + title + "\n" +
                "Quantity: " + quantity + "\n" +
                "Price: " + price + "\n" +
                "Time: " + formatter.format(timestamp) + "\n" +
                "---------------\n";
    }

    public String getTimestamp() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd_HHmmss");
        return formatter.format(timestamp);
    }

    public void setTimestamp(String timestampString) { //added to simplify equivalence class testing
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd_HHmmss");
            this.timestamp = formatter.parse(timestampString);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid timestamp format: " + timestampString);
        }
    }

}
