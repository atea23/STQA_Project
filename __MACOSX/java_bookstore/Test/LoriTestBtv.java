package Test;

public class LoriTestBtv {

    @Test
    public void testGetBillsForPeriodBoundaryValues() {
        String startDate = "20231201_000000";
        String endDate = "20231231_235959";


        String startMin = "20230101_000000";
        String endMin = "20230101_000001";


        assertEquals(expectedBills, getBillsForPeriod(startMin, endMin));


        assertEquals(expectedBills, getBillsForPeriod(startDate, startDate));
    }

}
