package com.example.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.java_bookstore.Author;
import com.example.java_bookstore.Book;
import com.example.java_bookstore.BookController;
import com.example.java_bookstore.Category;
import com.example.java_bookstore.Gender;

class BookControllerTest {

	@InjectMocks
    private BookController bookController;

    @Mock
    private File mockFile;

    @Mock
    private FileInputStream mockFileInputStream;

    @Mock
    private ObjectInputStream mockObjectInputStream;

    @Mock
    private FileOutputStream mockFileOutputStream; 

    @Mock
    private ObjectOutputStream mockObjectOutputStream;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        bookController = spy(new BookController(false)); // Skip loading books

        // Mock file operations
//        doNothing().when(bookController).readBooks();
//        doNothing().when(bookController).writeBooks();

        // Clear the books list
        bookController.getListOfBooks().clear();
    }


    @Test
    void testAddBook() {
        Author author = new Author("John", "Doe", Gender.MALE);
        Book book = new Book("12345", "Test Book", "Supplier", new Date(), 100.0, 120.0, 150.0, 10, author, 5);

        // Add the book
        bookController.addBook(book);

        // Assert the book was added
        assertEquals(1, bookController.getListOfBooks().size());
        assertEquals("12345", bookController.getListOfBooks().get(0).getIsbn());

        // Verify writeBooks() was called
        verify(bookController, times(1)).writeBooks();
    }


    @Test
    void testGetBooksBoughtForPeriod() {
        Author author = new Author("Jane", "Doe", Gender.FEMALE);
        Book book1 = new Book("11111", "Book One", "Supplier1", new Date(2024 - 1900, 0, 1), 50.0, 60.0, 80.0, 5, author, 2); // Jan 1, 2024
        Book book2 = new Book("22222", "Book Two", "Supplier2", new Date(2024 - 1900, 5, 15), 60.0, 80.0, 100.0, 10, author, 3); // Jun 15, 2024
        Book book3 = new Book("33333", "Book Three", "Supplier3", null, 70.0, 90.0, 110.0, 8, author, 4); // No purchased date

        bookController.addBook(book1);
        bookController.addBook(book2);
        bookController.addBook(book3);

        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 6, 30);

        List<Book> result = bookController.getBooksBoughtForPeriod(startDate, endDate);

        // Verify correct books were returned
        assertEquals(2, result.size());
        assertTrue(result.contains(book1));
        assertTrue(result.contains(book2));
    }

    @Test
    void testCategoryExists() {
        Author author = new Author("Jane", "Doe", Gender.FEMALE);
        Category fictionCategory = new Category("Fiction");
        Category scienceCategory = new Category("Science");

        Book book = new Book("12345", "Test Book", "Supplier", new Date(), 100.0, 120.0, 150.0, 10, author, 5);
        book.addCategory(fictionCategory);

        bookController.addBook(book);

        assertTrue(bookController.categoryExists(fictionCategory)); // Should pass
        assertFalse(bookController.categoryExists(scienceCategory)); // Should pass
    }

    @Test
    void testUpdateBookStock() {
        Author author = new Author("Jane", "Doe", Gender.FEMALE);
        Book book = new Book("12345", "Test Book", "Supplier", new Date(), 100.0, 120.0, 150.0, 10, author, 5);

        bookController.addBook(book);

        // Create an updated book with new stock value
        Book updatedBook = new Book("12345", "Test Book", "Supplier", new Date(), 100.0, 120.0, 150.0, 20, author, 10);

        bookController.updateBookStock(updatedBook);

        // Verify stock was updated
        assertEquals(20, bookController.getListOfBooks().get(0).getStock());

        // Verify writeBooks() was called
        verify(bookController, times(2)).writeBooks();
    }

    @Test
    void testReadBooks() throws Exception {
        // Mock file and object input streams
        doReturn(mockObjectInputStream).when(bookController).getObjectInputStream();

        // Prepare mock data
        Author author = new Author("Jane", "Doe", Gender.FEMALE);
        ArrayList<Book> mockBooks = new ArrayList<>();
        mockBooks.add(new Book("12345", "Mock Book", "Mock Supplier", new Date(), 100.0, 120.0, 150.0, 10, author, 5));

        // Mock the behavior of readObject() to return the prepared list
        when(mockObjectInputStream.readObject()).thenReturn(mockBooks);
        System.out.println("before read");
        System.out.println(mockBooks);
        // Call readBooks explicitly
        bookController.readBooks();

        // Verify the books list is populated
        assertEquals(1, bookController.getListOfBooks().size());
        assertEquals("12345", bookController.getListOfBooks().get(0).getIsbn());
    }



    @Test
    void testWriteBooks() throws Exception {
        // Mock the ObjectOutputStream
        doReturn(mockObjectOutputStream).when(bookController).getObjectOutputStream();

        // Create a book and add it to the BookController
        Author author = new Author("Jane", "Doe", Gender.FEMALE);
        Book book = new Book("12345", "Test Book", "Supplier", new Date(), 100.0, 120.0, 150.0, 10, author, 5);
        bookController.addBook(book);

        // Verify that writeBooks() was called
        verify(bookController, times(1)).writeBooks();

        // Verify that the correct data was written to the ObjectOutputStream
        List<Book> expectedBooks = new ArrayList<>();
        expectedBooks.add(book);
        verify(mockObjectOutputStream).writeObject(expectedBooks);
    }

}

