package Users;
import Exceptions.LibrarianException;
import com.example.java_bookstore.*;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
public class Librarian extends Employee{
    private double totalRevenue;
    private static ArrayList<Book> selectedBooksStatistics = new ArrayList<>();
    private ArrayList<Book> selectedBooks = new ArrayList<>();
    public static ArrayList<Bill> bills = new ArrayList<>();
    private BookController bookController;
    public void addToSelectedBooks(Book book) {
        try {
            selectedBooks.add(book);
            selectedBooksStatistics.add(book);
        } catch (Exception e) {
            throw new LibrarianException("Error adding book to selected books.", e);
        }
    }

    public Librarian() {
        this.bookController = new BookController();
    }

    public void resetPerformance() {
        selectedBooks.clear();
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }

    public void reset(){
        selectedBooks.clear();
    }

    public void printBill() {
        double totalBillValue = 0.0;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String timestamp = dateFormat.format(new Date());

        try (PrintWriter writer = new PrintWriter(new FileWriter("bill" + timestamp + ".txt"))) {
            for (Book selectedBook : selectedBooks) {
                Bill bill = new Bill(selectedBook.getIsbn(), selectedBook.getTitle(),
                        selectedBook.getSelected(), selectedBook.getSellingPrice());
                bills.add(bill);
                String billEntry = bill.toString();
                System.out.println(billEntry);
                writer.println(billEntry);

                totalBillValue += selectedBook.getSellingPrice() * selectedBook.getSelected();
                selectedBook.setStock(selectedBook.getStock() - selectedBook.getSelected());
                bookController.updateBookStock(selectedBook);
            }
            String totalValue = "Total Bill Value: " + totalBillValue + "\n";
            System.out.println(totalValue);
            writer.println(totalValue);
            System.out.println("Bill details saved.");
        } catch (IOException e) {
            throw new LibrarianException("Error printing bill. Could not write to file.", e);
        } finally {
            saveBillsToFile();
            reset();
        }
    }

    public static double calculateTotalRevenueForPeriod(String startDate, String endDate) {
        double totalRevenueForPeriod = 0.0;

        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd_HHmmss");
            Date startDateTime = formatter.parse(startDate);
            Date endDateTime = formatter.parse(endDate);

            for (Bill bill : getBillsForPeriod(startDate, endDate)) {
                Date billDateTime = formatter.parse(bill.getTimestamp());
                if (((billDateTime.compareTo(startDateTime) >= 0) && (billDateTime.compareTo(endDateTime) <= 0)) || (startDate.equals(endDate) && (billDateTime.compareTo(startDateTime) == 0))) {
                    totalRevenueForPeriod += bill.getPrice() * bill.getQuantity();
                }
            }
        } catch (ParseException e) {
            System.err.println("Error parsing date: " + e.getMessage());
        }
        return totalRevenueForPeriod;
    }

    public static ArrayList<Bill> getBillsForPeriod(String startDate, String endDate) {
        ArrayList<Bill> billsForPeriod = new ArrayList<>();
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd_HHmmss");
            Date startDateTime = formatter.parse(startDate);
            Date endDateTime = formatter.parse(endDate);
            for (Bill bill : getBills()) {
                Date billDateTime = formatter.parse(bill.getTimestamp());
                if (((billDateTime.compareTo(startDateTime) >= 0) && (billDateTime.compareTo(endDateTime) <= 0)) || (startDate.equals(endDate) && (billDateTime.compareTo(startDateTime) == 0))) {
                    billsForPeriod.add(bill);
                }
            }
        } catch (ParseException e) {
            System.err.println("Error parsing date: " + e.getMessage());
        }
        return billsForPeriod;
    }

    public static ArrayList<String> getBookTitlesForPeriod(String startDate, String endDate) {
        ArrayList<String> bookTitles = new ArrayList<>();
        ArrayList<Bill> billsForPeriod = getBillsForPeriod(startDate, endDate);
        for (Bill bill : billsForPeriod) {
            bookTitles.add(bill.getTitle());
        }
        return bookTitles;
    }

    public static ArrayList<Bill> getBillsForDay(LocalDate date) {
        ArrayList<Bill> billsForDay = new ArrayList<>();
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
            for (Bill bill : getBills()) {
                String billTimestamp = bill.getTimestamp();
                LocalDateTime billDateTime = LocalDateTime.parse(billTimestamp, formatter);

                if (billDateTime.toLocalDate().isEqual(date)) {
                    billsForDay.add(bill);
                }
            }
        } catch (DateTimeParseException e) {
            System.err.println("Error parsing date: " + e.getMessage());
        }
        return billsForDay;
    }

    public static ArrayList<String> getBookTitlesForDay(LocalDate date) {
        ArrayList<String> bookTitles = new ArrayList<>();
        ArrayList<Bill> billsForDay = getBillsForDay(date);

        for (Bill bill : billsForDay) {
            bookTitles.add(bill.getTitle());
        }
        return bookTitles;
    }

    public static double calculateTotalRevenueForDay(LocalDate date) {
        double totalRevenueForDay = 0.0;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
            for (Bill bill : getBillsForDay(date)) {
                String billTimestamp = bill.getTimestamp();
                LocalDateTime billDateTime = LocalDateTime.parse(billTimestamp, formatter);

                if (billDateTime.toLocalDate().isEqual(date)) {
                    totalRevenueForDay += bill.getPrice() * bill.getQuantity();
                }
            }
        } catch (DateTimeParseException e) {
            System.err.println("Error parsing date: " + e.getMessage());
        }
        return totalRevenueForDay;
    }

    public static ArrayList<Bill> getBills() {
        return bills;
    }

    public static ArrayList<Book> getBooksSold() {
        return selectedBooksStatistics;
    }

    public static void saveBillsToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("bills.bin"))) {
            oos.writeObject(bills);
        } catch (IOException e) {
            throw new LibrarianException("Error saving bills to file.", e);
        }
    }

    public static void loadBillsFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("bills.bin"))) {
            bills = (ArrayList<Bill>) ois.readObject();
        } catch (FileNotFoundException e) {
            System.err.println("File not found. Creating a new file.");
            saveBillsToFile();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}