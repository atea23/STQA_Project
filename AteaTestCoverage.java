package Test;

import org.junit.BeforeClass;
import org.junit.Test;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import com.example.java_bookstore.Bill;
import Users.Librarian;

import java.util.ArrayList;
import java.util.List;

public class AteaTestCoverage {

   private static List<Bill> bills;

    @BeforeClass
    public static void setup() {
        // Initialize the shared list of bills
        bills = new ArrayList<>();

        Bill bill1 = new Bill("123", "Book A", 2, 10.0);
        bill1.setTimestamp("20240101_120000");
        bills.add(bill1);

        Bill bill2 = new Bill("124", "Book B", 3, 15.0);
        bill2.setTimestamp("20240102_120000");
        bills.add(bill2);

        Bill bill3 = new Bill("125", "Book C", 1, 20.0);
        bill3.setTimestamp("20240103_120000");
        bills.add(bill3);
    }


    @Test
    public void testStatementCoverage() {
        // Mock the Librarian class and its getBillsForPeriod method
        Librarian librarianMock = mock(Librarian.class);
        when(librarianMock.getBillsForPeriod(anyString(), anyString())).thenReturn(bills);

        // Valid date range with matching bills
        double result = librarianMock.calculateTotalRevenueForPeriod("20240101_120000", "20240103_120000");
        assertEquals(95.0, result, 0.01);

        // Valid date range with no matching bills
        when(librarianMock.getBillsForPeriod(anyString(), anyString())).thenReturn(new ArrayList<>());
        result = librarianMock.calculateTotalRevenueForPeriod("20230101_120000", "20230102_120000");
        assertEquals(0.0, result, 0.01);

        // Invalid date format
        result = librarianMock.calculateTotalRevenueForPeriod("INVALID_DATE", "20230102_120000");
        assertEquals(0.0, result, 0.01); // Expect 0.0 as error is caught

        // Edge case: startDate == endDate
        when(librarianMock.getBillsForPeriod("20240102_120000", "20240102_120000")).thenReturn(bills.subList(1, 2));
        result = librarianMock.calculateTotalRevenueForPeriod("20240102_120000", "20240102_120000");
        assertEquals(45.0, result, 0.01);
    }

    @Test
    public void testBranchCoverage() {
        // Mock the Librarian class and its getBillsForPeriod method
        Librarian librarianMock = mock(Librarian.class);
        when(librarianMock.getBillsForPeriod(anyString(), anyString())).thenReturn(bills);

        // True: billDateTime >= startDateTime && billDateTime <= endDateTime
        double result = librarianMock.calculateTotalRevenueForPeriod("20240101_120000", "20240103_120000");
        assertEquals(95.0, result, 0.01);

        // False: billDateTime >= startDateTime && billDateTime <= endDateTime
        when(librarianMock.getBillsForPeriod(anyString(), anyString())).thenReturn(new ArrayList<>());
        result = librarianMock.calculateTotalRevenueForPeriod("20230101_120000", "20230102_120000");
        assertEquals(0.0, result, 0.01);

        // True: startDate.equals(endDate) && billDateTime.compareTo(startDateTime) == 0
        when(librarianMock.getBillsForPeriod("20240102_120000", "20240102_120000")).thenReturn(bills.subList(1, 2));
        result = librarianMock.calculateTotalRevenueForPeriod("20240102_120000", "20240102_120000");
        assertEquals(45.0, result, 0.01);

        // False: startDate.equals(endDate) && billDateTime.compareTo(startDateTime) == 0
        result = librarianMock.calculateTotalRevenueForPeriod("20240101_120000", "20240103_120000");
        assertEquals(95.0, result, 0.01);
    }

    @Test
    public void testConditionCoverage() {
        // Mock the Librarian class and its getBillsForPeriod method
        Librarian librarianMock = mock(Librarian.class);
        when(librarianMock.getBillsForPeriod(anyString(), anyString())).thenReturn(bills);

        // Test true and false for billDateTime.compareTo(startDateTime) >= 0
        double result1 = librarianMock.calculateTotalRevenueForPeriod("20240101_120000", "20240101_120000");
        assertEquals(20.0, result1, 0.01); // Only Bill A

        double result2 = librarianMock.calculateTotalRevenueForPeriod("20230101_120000", "20230101_120000");
        assertEquals(0.0, result2, 0.01); // No matching bills

        // Test true and false for billDateTime.compareTo(endDateTime) <= 0
        result1 = librarianMock.calculateTotalRevenueForPeriod("20240101_120000", "20240102_120000");
        assertEquals(65.0, result1, 0.01); // Bill A and Bill B

        result2 = librarianMock.calculateTotalRevenueForPeriod("20240103_120000", "20240103_120000");
        assertEquals(20.0, result2, 0.01); // Only Bill C
    }

    @Test
    public void testMCDC() {
        // Mock the Librarian class and its getBillsForPeriod method
        Librarian librarianMock = mock(Librarian.class);
        when(librarianMock.getBillsForPeriod(anyString(), anyString())).thenReturn(bills);

        // Independently test billDateTime.compareTo(startDateTime) >= 0
        double result1 = librarianMock.calculateTotalRevenueForPeriod("20240101_120000", "20240101_120000");
        assertEquals(20.0, result1, 0.01); // Only Bill A

        double result2 = librarianMock.calculateTotalRevenueForPeriod("20230101_120000", "20230101_120000");
        assertEquals(0.0, result2, 0.01); // No matching bills

        // Independently test billDateTime.compareTo(endDateTime) <= 0
        result1 = librarianMock.calculateTotalRevenueForPeriod("20240101_120000", "20240103_120000");
        assertEquals(95.0, result1, 0.01); // All bills

        result2 = librarianMock.calculateTotalRevenueForPeriod("20240101_120000", "20240101_120000");
        assertEquals(20.0, result2, 0.01); // Only Bill A
    }

}
