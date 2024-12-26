package Users;


import Exceptions.ManagerException;
import Model.User;
import Scenes.ManagerScene;
import com.example.java_bookstore.*;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

public class Manager extends Employee{
    private BookController bookController;
    private Librarian librarian = new Librarian();
    private static ArrayList<Bill> bills = new ArrayList<>();
    private static ArrayList<Book> books = new ArrayList<>();

    public Manager() {
        this.bookController = new BookController();
    }

    public Manager(Librarian librarian) {
        this.librarian = librarian;
    }
    private ArrayList<Author> authors = new ArrayList<>();

    public static ArrayList<String> getBookSoldStatistics(String filter) {
        ArrayList<String> statistics = new ArrayList<>();

        switch (filter.toLowerCase()) {
            case "daily":
                statistics = getDailyStatisticsBills();
                break;
            case "monthly":
                statistics = getMonthlyStatisticsBills();
                break;
            case "total":
                statistics = getTotalStatisticsBills();
                break;
        }
        return statistics;
    }

    private static ArrayList<String> getDailyStatisticsBills() {
        ArrayList<String> dailyStatistics = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");

        for (Bill bill : bills) {
            try {
                Date billTimestamp = dateFormat.parse(bill.getTimestamp());
                LocalDateTime billDate = LocalDateTime.ofInstant(billTimestamp.toInstant(), ZoneId.systemDefault());

                LocalDateTime todayDateTime = LocalDateTime.now();

                if (billDate.toLocalDate().isEqual(todayDateTime.toLocalDate())) {
                    System.out.println("Added to dailyStatistics");

                    dailyStatistics.add(formatBookInfoBill(bill));
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return dailyStatistics;
    }

    private static ArrayList<String> getMonthlyStatisticsBills() {
        ArrayList<String> monthlyStatistics = new ArrayList<>();
        SimpleDateFormat monthFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");

        for (Bill bill : bills) {
            try {
                String billTimestampString = bill.getTimestamp();

                Date billTimestamp = monthFormat.parse(billTimestampString);

                String billMonth = new SimpleDateFormat("yyyyMM").format(billTimestamp);
                String currentMonth = new SimpleDateFormat("yyyyMM").format(new Date());

                System.out.println("Bill Timestamp: " + billTimestampString);
                System.out.println("Formatted Bill Month: " + billMonth);
                System.out.println("Current Month: " + currentMonth);

                if (billMonth.equals(currentMonth)) {
                    System.out.println("Added to Monthly Statistics");
                    monthlyStatistics.add(formatBookInfoBill(bill));
                }
            } catch (ParseException | NullPointerException e) {
                e.printStackTrace(); // Handle the exception appropriately
            }
        }
        return monthlyStatistics;
    }

    private static ArrayList<String> getTotalStatisticsBills() {
        ArrayList<String> totalStatistics = new ArrayList<>();

        for (Bill bill : bills) {
            totalStatistics.add(formatBookInfoBill(bill));
        }
        return totalStatistics;
    }

    private static String formatBookInfoBill(Bill bill) {
        return String.format("Book: %s, Quantity: %d, Price: %.2f",
                bill.getTitle(), bill.getQuantity(), bill.getPrice());
    }


    public static ArrayList<String> getBookBoughtStatistics(String filter) {
        ArrayList<String> statistics = new ArrayList<>();

        switch (filter.toLowerCase()) {
            case "daily":
                statistics = getDailyStatistics();
                break;
            case "monthly":
                statistics = getMonthlyStatistics();
                break;
            case "total":
                statistics = getTotalStatistics();
                break;
        }
        return statistics;
    }

    private static ArrayList<String> getDailyStatistics() {
        ArrayList<String> dailyStatistics = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

        for (Book book : books) {
            Date bookPurchasedDate = book.getPurchasedDate();

            System.out.println(book);

            if (bookPurchasedDate != null) {
                LocalDateTime purchasedLocalDateTime = LocalDateTime.ofInstant(bookPurchasedDate.toInstant(), ZoneId.systemDefault());
                LocalDateTime todayDateTime = LocalDateTime.now();

                if (purchasedLocalDateTime.toLocalDate().isEqual(todayDateTime.toLocalDate())) {
                    System.out.println("Added to dailyStatistics");
                    dailyStatistics.add(formatBookInfo(book));
                }
            }
        }
        return dailyStatistics;
    }

    private static ArrayList<String> getMonthlyStatistics() {
        ArrayList<String> monthlyStatistics = new ArrayList<>();
        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

        for (Book book : books) {
            Date purchasedDate = book.getPurchasedDate();

            if (purchasedDate != null) {
                LocalDateTime purchasedLocalDateTime = LocalDateTime.ofInstant(purchasedDate.toInstant(), ZoneId.systemDefault());

                YearMonth bookYearMonth = YearMonth.from(purchasedLocalDateTime);
                YearMonth currentYearMonth = YearMonth.from(LocalDateTime.now());

                if (bookYearMonth.equals(currentYearMonth)) {
                    System.out.println("Added to monthlyStatistics");
                    monthlyStatistics.add(formatBookInfo(book));
                }
            } else {
                System.out.println("Book has no purchased date: " + book.getTitle());
            }
        }
        return monthlyStatistics;
    }

    private static ArrayList<String> getTotalStatistics() {
        ArrayList<String> totalStatistics = new ArrayList<>();

        for (Book book : books) {
            totalStatistics.add(formatBookInfo(book));
        }
        return totalStatistics;
    }
    private static String formatBookInfo(Book book) {
        return String.format("Book: %s, Quantity: %d, Price: %.2f",
                book.getTitle(), book.getEnteredStock(), book.getPurchasedPrice());
    }

    public static void loadBillsFromFileManager() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("bills.bin"))) {
            bills = (ArrayList<Bill>) ois.readObject();
        } catch (FileNotFoundException e) {
            System.err.println("File not found. Creating a new file.");
            saveBillsToFile();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void saveBillsToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("bills.bin"))) {
            oos.writeObject(bills);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadBooksFromFileManager() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("books.bin"))) {
            books = (ArrayList<Book>) ois.readObject();
        } catch (FileNotFoundException e) {
            System.err.println("File not found. Creating a new file.");
            saveBooksToFile();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void saveBooksToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("books.bin"))) {
            oos.writeObject(books);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}