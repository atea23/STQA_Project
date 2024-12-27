package Test;

public class EvaluationTesting {

    @Test
    public void testGetBookTitlesForDay() {
        // Valid date with book entries
        LocalDate validDate = LocalDate.of(2023, 12, 25);
        ArrayList<String> titles = getBookTitlesForDay(validDate);
        assertFalse(titles.isEmpty());

        // Invalid date with no books
        LocalDate invalidDate = LocalDate.of(2024, 1, 1);
        ArrayList<String> emptyTitles = getBookTitlesForDay(invalidDate);
        assertTrue(emptyTitles.isEmpty());
    }

}
