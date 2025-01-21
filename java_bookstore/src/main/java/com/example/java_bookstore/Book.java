package com.example.java_bookstore;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Book implements Serializable {
    @Serial
    private static final long serialVersionUID = 5296705482940410483L;
    private String isbn;
    private String title;
    private String supplier;
    private Date purchasedDate;
    private double purchasedPrice;
    private double originalPrice;
    private double sellingPrice;
    private int stock;
    private Author author;
    private ArrayList<Category> category = new ArrayList<>();
    private int selected;
    private final int enteredStock;


    public Book(String isbn, String title, String supplier, Date purchasedDate, double purchasedPrice, double originalPrice, double sellingPrice, int stock, Author author, int enteredStock) {
        this.isbn = isbn;
        this.title = title; 
        this.supplier = supplier;
        this.purchasedDate = purchasedDate;
        this.purchasedPrice = purchasedPrice;
        this.originalPrice = originalPrice; 
        this.sellingPrice = sellingPrice;
        this.author = author;
        this.stock = stock;
        this.enteredStock = enteredStock;

    }
    public int getEnteredStock() {
        return enteredStock;
    }

    public String getIsbn() {
        return isbn;
    }
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getSupplier() {
        return supplier;
    }
    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }
    public Date getPurchasedDate() {
        return purchasedDate;
    }
    public double getPurchasedPrice() {
        return purchasedPrice;
    }
    public void setPurchasedPrice(double purchasedPrice) {
        this.purchasedPrice = purchasedPrice;
    }
    public double getOriginalPrice() {
        return originalPrice;
    }
    public void setOriginalPrice(double originalPrice) {
        this.originalPrice = originalPrice;
    }
    public double getSellingPrice() {
        return sellingPrice;
    }
    public void setSellingPrice(double sellingPrice) {
        this.sellingPrice = sellingPrice;
    }
    public int getStock() {
        return stock;
    }
    public void setStock(int stock) {
        this.stock = stock;
    }
    public Author getAuthor() {
        return author;
    }
    public void setAuthor(Author author) {
        this.author = author;
    }
    public ArrayList<Category> getCategory() {
        return category;
    }
    public void addCategory(Category category) {
        this.category.add(category);
    }
    public void addCategory(Category...category) {
        for(Category categories : category)
            this.addCategory(categories);
    }
    public int getSelected() {
        return selected;
    }

    public void setSelected(int selected) {
        if (selected > 0 && selected <= stock) {
            this.selected = selected;
        } else {
            System.out.println("Error!");
        }
    }

    @Override
    public String toString() {
        return this.title + " by " + this.author.toString() + "costs " + this.sellingPrice + " leke" + " and there are " + this.stock + " copies available. ";
    }
}
