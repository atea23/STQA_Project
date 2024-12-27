package Test;

public class CoverageTesting {

    @Test
    public void testGetBillsForPeriodCodeCoverage() {
        String startDate = "20231201_000000";
        String endDate = "20231231_235959";

        // Test within the valid period
        ArrayList<Bill> bills = getBillsForPeriod(startDate, endDate);
        assertFalse(bills.isEmpty());

        // Test when startDate equals endDate
        String sameStartEndDate = "20231225_120000";
        ArrayList<Bill> sameDayBills = getBillsForPeriod(sameStartEndDate, sameStartEndDate);
        assertFalse(sameDayBills.isEmpty());

        // Test invalid period (startDate is after endDate)
        String invalidPeriodStart = "20240101_000000";
        ArrayList<Bill> invalidBills = getBillsForPeriod(invalidPeriodStart, endDate);
        assertTrue(invalidBills.isEmpty());
    }

}
