package Test;

import Users.Librarian;
import com.example.java_bookstore.Bill;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class AteaTest {

    /// //////////////////////////////////BVT///////////////////////////////////////////////////////
    @BeforeAll
    static void setup() {
        // Add some test data to the bills list
        Bill bill1 = new Bill("12345", "Book Title 1", 1, 10.0);
        bill1.setTimestamp("20240101_000000");
        Bill bill2 = new Bill("67890", "Book Title 2", 2, 20.0);
        bill2.setTimestamp("20240101_120000");
        Bill bill3 = new Bill("11223", "Book Title 3", 1, 15.0);
        bill3.setTimestamp("20240102_000000");

        Librarian.getBills().clear();
        Librarian.getBills().add(bill1);
        Librarian.getBills().add(bill2);
        Librarian.getBills().add(bill3);
    }

    @Test
    void testGetBookTitlesForDay_NoBills() {
        // Boundary condition: Date with no bills
        LocalDate date = LocalDate.of(2024, 1, 3); // No bills on this date
        ArrayList<String> bookTitles = Librarian.getBookTitlesForDay(date);
        assertTrue(bookTitles.isEmpty(), "Expected no book titles for the date with no bills");
    }

    @Test
    void testGetBookTitlesForDay_OneBill() {
        // Boundary condition: Date with exactly one bill
        LocalDate date = LocalDate.of(2024, 1, 2); // One bill on this date
        ArrayList<String> bookTitles = Librarian.getBookTitlesForDay(date);
        assertEquals(1, bookTitles.size(), "Expected exactly one book title for the date with one bill");
        assertEquals("Book Title 3", bookTitles.get(0), "Unexpected book title found");
    }

    @Test
    void testGetBookTitlesForDay_MultipleBills() {
        // Boundary condition: Date with multiple bills
        LocalDate date = LocalDate.of(2024, 1, 1); // Two bills on this date
        ArrayList<String> bookTitles = Librarian.getBookTitlesForDay(date);
        assertEquals(2, bookTitles.size(), "Expected two book titles for the date with multiple bills");
        assertTrue(bookTitles.contains("Book Title 1"), "Book Title 1 is missing");
        assertTrue(bookTitles.contains("Book Title 2"), "Book Title 2 is missing");
    }

    @Test
    void testGetBookTitlesForDay_NullDate() {
        assertThrows(NullPointerException.class,
                () -> Librarian.getBookTitlesForDay(null),
                "NullPointerException expected for null date");
    }



}

