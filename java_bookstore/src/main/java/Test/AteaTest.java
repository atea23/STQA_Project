package Test;
import Users.Librarian;
import com.example.java_bookstore.Bill;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class AteaTest {
    /*@Test
    void testNormalBVTForGetBillsForPeriod() {
        String startDate = "20220101_000000"; // Start of 2022
        String endDate = "20221231_235959";  // End of 2022

        Bill bill1 = new Bill("978-3-16-148410-0", "Effective Java", 1, 45.0);
        Bill bill2 = new Bill("978-0-13-468599-1", "Clean Code", 1, 30.0);

        bill1.setTimestamp("20220315_120000"); // March 2022
        bill2.setTimestamp("20221010_150000"); // October 2022

        ArrayList<Bill> bills = new ArrayList<>();
        bills.add(bill1);
        bills.add(bill2);
        Librarian.bills = bills;

        ArrayList<Bill> result = Librarian.getBillsForPeriod(startDate, endDate);
        assertEquals(2, result.size(), "Should return 2 bills for the period");
    }
*/
    @Test
    void testRobustBVTForGetBillsForPeriod() {
        String extremePastDate = "19000101_000000";  // Extreme past
        String extremeFutureDate = "30001231_235959";  // Extreme future

        assertThrows(IllegalArgumentException.class, () -> Librarian.getBillsForPeriod(null, extremeFutureDate),
                "Null startDate should throw exception");
        assertThrows(IllegalArgumentException.class, () -> Librarian.getBillsForPeriod(extremePastDate, null),
                "Null endDate should throw exception");

        ArrayList<Bill> result = Librarian.getBillsForPeriod(extremePastDate, extremeFutureDate);
        assertEquals(0, result.size(), "Extreme date range should return 0 bills if no bills exist");
    }

    /*@Test
    void testWorstCaseBVTForGetBillsForPeriod() {
        String earliestDate = "19000101_000000";  // Earliest possible date
        String latestDate = "30001231_235959";  // Latest possible date

        Bill bill = new Bill("978-3-16-148410-0", "Effective Java", 1, 50.0);
        bill.setTimestamp("20220115_123456");  // A valid timestamp

        ArrayList<Bill> bills = new ArrayList<>();
        bills.add(bill);
        Librarian.bills = bills;

        ArrayList<Bill> result = Librarian.getBillsForPeriod(earliestDate, latestDate);
        assertEquals(1, result.size(), "Should include all bills within the extreme date range");
    }

    @Test
    void testRobustWorstCaseBVTForGetBillsForPeriod() {
        String invalidDate1 = "INVALID_DATE";  // Malformed date
        String invalidDate2 = "20221301_000000";  // Invalid month
        String invalidDate3 = "";  // Empty string

        assertThrows(ParseException.class, () -> Librarian.getBillsForPeriod(invalidDate1, invalidDate2),
                "Malformed dates should throw ParseException");
        assertThrows(ParseException.class, () -> Librarian.getBillsForPeriod(invalidDate3, invalidDate3),
                "Empty dates should throw ParseException");
    }*/
}
