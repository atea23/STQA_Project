package com.example.test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import Controller.UserController;
import Model.User;
import Model.User.UserRole;
import org.mockito.Mockito;
import org.mockito.InjectMocks;


class UserControllerTest {

	@InjectMocks
    private UserController userController;

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
        userController = spy(new UserController());
        userController.getUsers().clear(); // Reset the user list
        doNothing().when(userController).readUsers();
        doNothing().when(userController).writeUsers(); // Mock writeUsers globally
    }

    @Test
    void testAddUser() throws Exception {
        User user = new User("John", "Doe", "john@example.com", "password", "Male", true);
        doNothing().when(userController).writeUsers();

        userController.addUser(user, User.UserRole.MANAGER);

        assertEquals(1, userController.getUsers().size());
        assertEquals(User.UserRole.MANAGER, userController.getUsers().get(0).getRole());
    }

    @Test
    void testSignUpSuccess() throws Exception {
        boolean result = userController.signUp("Jane", "Doe", "jane@example.com", "pass123", "pass123", "Female", false, User.UserRole.ADMIN);

        assertTrue(result);
        assertEquals(1, userController.getUsers().size());
        assertEquals("jane@example.com", userController.getUsers().get(0).getEmail());
    }

    @Test
    void testSignUpPasswordMismatch() {
        boolean result = userController.signUp("Jane", "Doe", "jane@example.com", "pass123", "wrongpass", "Female", false, User.UserRole.ADMIN);

        assertFalse(result);
        assertEquals(0, userController.getUsers().size());
    }

    @Test
    void testLoginSuccess() {
        User user = new User("John", "Doe", "john@example.com", "password", "Male", true);
        userController.addUser(user, User.UserRole.MANAGER);

        User loggedInUser = userController.login("john@example.com", "password");

        assertNotNull(loggedInUser);
        assertEquals("john@example.com", loggedInUser.getEmail());
    }

    @Test
    void testLoginFailure() {
        User loggedInUser = userController.login("nonexistent@example.com", "password");

        assertNull(loggedInUser);
    }


    @Test
    void testWriteUsers() throws Exception {
        User user = new User("John", "Doe", "john@example.com", "password", "Male", true);
        userController.addUser(user, User.UserRole.MANAGER);

        doNothing().when(userController).writeUsers();
        userController.writeUsers();

        verify(userController, times(2)).writeUsers();
    }

    @Test
    void testReadUsers() throws Exception {
        // Prepare mock users to be returned by the mocked ObjectInputStream
        ArrayList<User> mockUsers = new ArrayList<>();
        mockUsers.add(new User("Alice", "Johnson", "alice@example.com", "password1", "Female", false));
        mockUsers.add(new User("Bob", "Smith", "bob@example.com", "password2", "Male", true));

        // Mock the behavior of file input and object streams
        when(userController.getFileInputStream()).thenReturn(mockFileInputStream);
        when(userController.getObjectInputStream(mockFileInputStream)).thenReturn(mockObjectInputStream);
        when(mockObjectInputStream.readObject()).thenReturn(mockUsers);

        // Call the readUsers method
        userController.readUsers();

        // Verify the users list in UserController is updated
        assertEquals(2, userController.getUsers().size());
        assertEquals("Alice", userController.getUsers().get(0).getFirstName());
        assertEquals("Bob", userController.getUsers().get(1).getFirstName());

        // Verify that the streams were used correctly
        verify(mockFileInputStream, times(1)).close();
        verify(mockObjectInputStream, times(1)).close();
    }
}



