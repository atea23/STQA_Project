package com.example.test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import com.example.java_bookstore.Bill;

import Users.Librarian;

class AteaTestCoverage {

	    @BeforeEach
	    public void setup() {
	        Librarian.getBills().clear();
	        try {
	            // Adding some sample bills to test various conditions
	            Bill bill1 = new Bill("123", "Book1", 2, 50.0);  
	            bill1.setTimestamp("20230101_120000");
	            Librarian.getBills().add(bill1);

	            Bill bill2 = new Bill("456", "Book2", 1, 30.0); 
	            bill2.setTimestamp("20230102_130000");
	            Librarian.getBills().add(bill2);

	            Bill bill3 = new Bill("789", "Book3", 3, 20.0);
	            bill3.setTimestamp("20230103_140000");
	            Librarian.getBills().add(bill3);
	        } catch (Exception e) {
	            fail("Setup failed: " + e.getMessage());
	        }
	    }

	    @Test
	    public void testGetBillsForPeriod_StatementCoverage() {
	        ArrayList<Bill> bills = Librarian.getBillsForPeriod("20230101_120000", "20230103_140000");
	        assertEquals(3, bills.size());
	    }

	    @Test
	    public void testGetBillsForPeriod_BranchCoverage() {
	        // Case: Start date equals end date, one bill matches
	        ArrayList<Bill> bills = Librarian.getBillsForPeriod("20230102_130000", "20230102_130000");
	        assertEquals(1, bills.size());
	        assertEquals("Book2", bills.get(0).getTitle());

	        // Case: Start date and end date cover multiple bills
	        bills = Librarian.getBillsForPeriod("20230101_120000", "20230102_130000");
	        assertEquals(2, bills.size());
	    }

	    @Test
	    public void testGetBillsForPeriod_ConditionCoverage() {
	        // Case: No bills fall within the period
	        ArrayList<Bill> bills = Librarian.getBillsForPeriod("20220101_120000", "20220102_130000");
	        assertTrue(bills.isEmpty());

	        // Case: All bills fall within the period
	        bills = Librarian.getBillsForPeriod("20230101_120000", "20230103_140000");
	        assertEquals(3, bills.size());
	    }

	    @Test
	    public void testGetBillsForPeriod_MC_DC() {
	        // Testing individual conditions and their effects
	        // Condition 1: billDateTime >= startDateTime
	        ArrayList<Bill> bills = Librarian.getBillsForPeriod("20230103_140000", "20230104_140000");
	        assertEquals(1, bills.size());
	        assertEquals("Book3", bills.get(0).getTitle());

	        // Condition 2: billDateTime <= endDateTime
	        bills = Librarian.getBillsForPeriod("20230101_120000", "20230101_120000");
	        assertEquals(1, bills.size());
	        assertEquals("Book1", bills.get(0).getTitle());

	        // Combined conditions
	        bills = Librarian.getBillsForPeriod("20230101_120000", "20230103_140000");
	        assertEquals(3, bills.size());
	    }
	    
	    @Test
	    public void testGetBillsForPeriod_ParseException() {
	        // Trigger ParseException by passing invalid date format
	        ArrayList<Bill> bills = Librarian.getBillsForPeriod("invalid_date", "20230101_120000");
	        assertTrue(bills.isEmpty()); // Should not return any bills
	    }

	    @Test
	    public void testGetBillsForPeriod_EqualDates() {
	        // Test where startDate equals endDate
	        ArrayList<Bill> bills = Librarian.getBillsForPeriod("20230101_120000", "20230101_120000");
	        assertEquals(1, bills.size()); // Should return only the matching bill
	    }
	    
	    

}

