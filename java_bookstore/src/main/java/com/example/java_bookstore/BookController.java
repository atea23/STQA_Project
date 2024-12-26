package com.example.java_bookstore;

import java.io.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BookController {
    private ArrayList<Book> books;
    private File file;

    public BookController() {
        books = new ArrayList<>();
        file = new File("books.bin");
        if (file.exists()) {
            readBooks();
        }
    }

    private void readBooks() {
        try {
            FileInputStream fileIS = new FileInputStream(file);
            ObjectInputStream objectIS = new ObjectInputStream(fileIS);
            books = (ArrayList<Book>) objectIS.readObject();
            fileIS.close();
            objectIS.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void addBook(Book book) {
        books.add(book);
        writeBooks();
    }

    public List<Book> getBooksBoughtForPeriod(LocalDate startDate, LocalDate endDate) {
        List<Book> booksBought = new ArrayList<>();

        for (Book book : books) {
            Date purchasedDate = book.getPurchasedDate();

            if (purchasedDate != null) {
                // Convert Date to LocalDate (if necessary, depending on your use case)
                LocalDate purchasedLocalDate = purchasedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

                if (purchasedLocalDate.isAfter(startDate.minusDays(1)) &&
                        purchasedLocalDate.isBefore(endDate.plusDays(1))) {
                    booksBought.add(book);
                }
            }
        }

        return booksBought;
    }


    public void writeBooks() {
        try {
            FileOutputStream fileOS = new FileOutputStream(file);
            ObjectOutputStream objectOS = new ObjectOutputStream(fileOS);
            objectOS.writeObject(books);
            objectOS.close();
            fileOS.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public ArrayList<Book> getListOfBooks() {
        return books;
    }

    public boolean categoryExists(Category category) {
        for (Book book : books) {
            if (book.getCategory().equals(category)) {
                return true;
            }
        }
        return false;
    }

    public void addCategory(Category...category) {
        for(Category categories : category)
            this.addCategory(categories);
    }

    public void updateBookStock(Book updatedBook) {
        for (Book book : books) {
            if (book.getIsbn().equals(updatedBook.getIsbn())) {
                // Update the stock value
                book.setStock(updatedBook.getStock());
                // Save the changes to the file
                writeBooks();
                return; // Assuming ISBN is unique; if not, remove this line
            }
        }
    }

}