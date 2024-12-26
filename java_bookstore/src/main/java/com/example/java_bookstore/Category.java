package com.example.java_bookstore;

import java.io.Serializable;

public class Category implements Serializable {
    private String categoryName;

    public Category(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public static Category fromString(String categoryName) {
        return new Category(categoryName);
    }
    @Override
    public String toString() {
        return categoryName;
    }
}

