package com.example.test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.java_bookstore.Author;
import com.example.java_bookstore.Book;
import com.example.java_bookstore.BookController;
import com.example.java_bookstore.Category;
import com.example.java_bookstore.Gender;

import Controller.UserController;
import Model.User;

class IntegrationTesting {
	
	///////////////////////////////////BOOKCONTROLLER INTEGRATION TESTING////////////////////////////////////////

	private BookController bookController;
    private File testFile;
    private UserController userController;
    private File tempUserFile;

    @BeforeEach
    void setUp() {
        testFile = new File("test_books.bin");
        bookController = new BookController(false);
        bookController.setFile(testFile);
        
        tempUserFile = new File("temp_users_test.bin");
        userController = new UserController(tempUserFile);
    }

    @AfterEach
    void tearDown() {
        if (testFile.exists()) {
            testFile.delete();
        }
        
        if (tempUserFile.exists()) {
            tempUserFile.delete();
        }
    }

    @Test
    void testAddBookAndRetrieve() {
        // Correct constructor usage
        Author author = new Author("John", "Doe", Gender.MALE);
        Book book = new Book(
            "12345", "Java Basics", "Supplier A",
            new Date(), 100.0, 120.0, 150.0,
            10, author, 20
        );
        bookController.addBook(book);
        assertEquals(1, bookController.getListOfBooks().size());
        assertEquals("12345", bookController.getListOfBooks().get(0).getIsbn());
        assertEquals(20, bookController.getListOfBooks().get(0).getEnteredStock());
        assertEquals("John Doe", bookController.getListOfBooks().get(0).getAuthor().toString());
    }

    @Test
    void testAddCategoryToBook() {
        Author author = new Author("Jane", "Smith", Gender.FEMALE);
        Book book = new Book(
            "54321", "Advanced Java", "Supplier B",
            new Date(), 200.0, 240.0, 300.0,
            5, author, 10
        );
        Category category = new Category("Programming");
        book.addCategory(category);
        assertTrue(book.getCategory().contains(category));
    }

    @Test
    void testSetSelectedValid() {
        Author author = new Author("Jane", "Smith", Gender.FEMALE);
        Book book = new Book(
            "54321", "Advanced Java", "Supplier B",
            new Date(), 200.0, 240.0, 300.0,
            5, author, 10
        );
        book.setSelected(3);
        assertEquals(3, book.getSelected());
    }

    @Test
    void testSetSelectedInvalid() {
        Author author = new Author("Jane", "Smith", Gender.FEMALE);
        Book book = new Book(
            "54321", "Advanced Java", "Supplier B",
            new Date(), 200.0, 240.0, 300.0,
            5, author, 10
        );
        book.setSelected(6); // Exceeds stock
        assertNotEquals(6, book.getSelected());
    }

    @Test
    void testSaveAndLoadBooks() {
        Author author = new Author("John", "Doe", Gender.MALE);
        Book book = new Book(
            "12345", "Java Basics", "Supplier A",
            new Date(), 100.0, 120.0, 150.0,
            10, author, 20
        );
        bookController.addBook(book);
        bookController.writeBooks();

        BookController newController = new BookController(false);
        newController.setFile(testFile);
        newController.readBooks();

        assertEquals(1, newController.getListOfBooks().size());
        assertEquals("12345", newController.getListOfBooks().get(0).getIsbn());
        assertEquals("John Doe", newController.getListOfBooks().get(0).getAuthor().toString());
    }

    @Test
    void testGetBooksByDateRange() {
        LocalDate today = LocalDate.now();
        Date todayDate = Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Author author = new Author("John", "Doe", Gender.MALE);
        Book book = new Book(
            "12345", "Java Basics", "Supplier A",
            todayDate, 100.0, 120.0, 150.0,
            10, author, 20
        );
        bookController.addBook(book);

        LocalDate startDate = today.minusDays(1);
        LocalDate endDate = today.plusDays(1);

        assertEquals(1, bookController.getBooksBoughtForPeriod(startDate, endDate).size());
    }
    
    
    
///////////////////////////////////USERCONTROLLER INTEGRATION TESTING////////////////////////////////////////


    @Test
    void testAddUserAndRetrieve() {
        User user = new User("John", "Doe", "john.doe@example.com", "password123", "Male", true);
        userController.addUser(user, User.UserRole.ADMIN);

        ArrayList<User> users = userController.getUsers();
        assertEquals(1, users.size());
        assertEquals("John", users.get(0).getFirstName());
        assertEquals(User.UserRole.ADMIN, users.get(0).getRole());
    }

    @Test
    void testSignUpSuccess() {
        boolean isSignedUp = userController.signUp(
            "Jane", "Smith", "jane.smith@example.com", 
            "securePass", "securePass", "Female", 
            false, User.UserRole.LIBRARIAN
        );
        assertTrue(isSignedUp);

        ArrayList<User> users = userController.getUsers();
        assertEquals(1, users.size());
        assertEquals("Jane", users.get(0).getFirstName());
        assertEquals(User.UserRole.LIBRARIAN, users.get(0).getRole());
    }

    @Test
    void testSignUpPasswordMismatch() {
        boolean isSignedUp = userController.signUp(
            "Alice", "Brown", "alice.brown@example.com", 
            "password123", "differentPassword", "Female", 
            true, User.UserRole.MANAGER
        );
        assertFalse(isSignedUp);

        assertEquals(0, userController.getUsers().size());
    }

    @Test
    void testLoginSuccess() {
        User user = new User("Charlie", "Green", "charlie.green@example.com", "manager", "Male", true);
        userController.addUser(user, User.UserRole.MANAGER);

        User loggedInUser = userController.login("charlie.green@example.com", "manager");
        assertNotNull(loggedInUser);
        assertEquals("Charlie", loggedInUser.getFirstName());
        assertEquals(User.UserRole.MANAGER, loggedInUser.getRole());
    }

    @Test
    void testLoginFailure() {
        User user = new User("David", "Lee", "david.lee@example.com", "securePassword", "Male", false);
        userController.addUser(user, User.UserRole.LIBRARIAN);

        User loggedInUser = userController.login("david.lee@example.com", "wrongPassword");
        assertNull(loggedInUser);
    }

    @Test
    void testSetUserRole() {
        User user = new User("Eve", "White", "eve.white@example.com", "admin", "Female", true);
        userController.addUser(user, User.UserRole.ADMIN);

        User loggedInUser = userController.login("eve.white@example.com", "admin");
        assertNotNull(loggedInUser);
        assertEquals(User.UserRole.ADMIN, loggedInUser.getRole());
    }

    @Test
    void testSaveAndLoadUsers() {
        User user = new User("Fiona", "Brown", "fiona.brown@example.com", "password123", "Female", false);
        userController.addUser(user, User.UserRole.LIBRARIAN);
        userController.writeUsers();

        UserController newController = new UserController(tempUserFile);
        ArrayList<User> users = newController.getUsers();

        assertEquals(1, users.size());
        assertEquals("Fiona", users.get(0).getFirstName());
        assertEquals(User.UserRole.LIBRARIAN, users.get(0).getRole());
    }
}

