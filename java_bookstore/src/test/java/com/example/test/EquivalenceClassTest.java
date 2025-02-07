package com.example.test;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.java_bookstore.Bill;

import Users.Librarian;

class EquivalenceClassTest {

	 @BeforeEach
	    void setUp() {
	        Librarian.getBills().clear(); // Clear existing bills in Librarian

	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

	        // Adding sample bills to Librarian's list
	        Bill bill1 = new Bill("978-3-16-148410-0", "Effective Java", 1, 50.0);
	        bill1.setTimestamp("20240114_204027");
	        Librarian.getBills().add(bill1);

	        Bill bill2 = new Bill("978-1-23-456789-0", "Clean Code", 2, 30.0);
	        bill2.setTimestamp("20240115_182014");
	        Librarian.getBills().add(bill2);

	        Bill bill3 = new Bill("978-1-78-328410-1", "The Pragmatic Programmer", 1, 40.0);
	        bill3.setTimestamp("20240116_121204");
	        Librarian.getBills().add(bill3);
	    }

	    @Test
	    void testNormalWeakEquivalenceClass() {
	        // Valid date with bills
	        LocalDate validDateWithBills = LocalDate.of(2024, 1, 14);
	        List<Bill> result = Librarian.getBillsForDay(validDateWithBills);

	        assertEquals(1, result.size(), "Should return 1 bill for the valid date with bills");
	        assertEquals("Effective Java", result.get(0).getTitle(), "Bill title should match");

	        // Valid date without bills
	        LocalDate validDateWithoutBills = LocalDate.of(2024, 2, 1);
	        result = Librarian.getBillsForDay(validDateWithoutBills);

	        assertTrue(result.isEmpty(), "Should return an empty list for the valid date without bills");
	    }

	    @Test
	    void testStrongNormalEquivalenceClass() {
	        // Multiple valid dates
	        LocalDate date1 = LocalDate.of(2024, 1, 14);
	        LocalDate date2 = LocalDate.of(2024, 1, 15);
	        LocalDate date3 = LocalDate.of(2024, 1, 16);

	        assertEquals(1, Librarian.getBillsForDay(date1).size(), "Should return 1 bill for January 14");
	        assertEquals(1, Librarian.getBillsForDay(date2).size(), "Should return 1 bill for January 15");
	        assertEquals(1, Librarian.getBillsForDay(date3).size(), "Should return 1 bill for January 16");
	    } 

	    @Test
	    void testSingleWeakRobustEquivalenceClass() {
	        // Invalid input: null
	        assertThrows(NullPointerException.class, () -> Librarian.getBillsForDay(null), "Null input should throw NullPointerException");
	    }

	    @Test
	    void testStrongRobustEquivalenceClass() {
	        // Invalid date formats
	        try {
	            String invalidDate = "INVALID_DATE";
	            LocalDate.parse(invalidDate, DateTimeFormatter.ofPattern("yyyyMMdd"));
	            fail("Parsing invalid date should throw an exception");
	        } catch (Exception e) {
	            assertTrue(e instanceof DateTimeParseException, "Should throw DateTimeParseException for invalid format");
	        }

	        // Extreme future date
	        LocalDate futureDate = LocalDate.of(9999, 12, 31);
	        List<Bill> result = Librarian.getBillsForDay(futureDate);

	        assertTrue(result.isEmpty(), "Should return an empty list for a far future date");
	    }
}
