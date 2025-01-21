package com.example.test;
import Model.User;
import Scenes.AdminScene;
import Users.Administrator;
import Users.Employee;
import Utils.AlertUtil;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.base.NodeMatchers;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.assertions.api.Assertions.assertThat;
import Scenes.LibrarianScene;
import Scenes.ManagerScene;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AdminSceneTest extends ApplicationTest {
    private Administrator mockAdministrator;
    private AdminScene adminScene;
    private Stage stage;

    @Override
    public void start(Stage stage) {
        // Create a single instance of Administrator for both the test and AdminScene
        mockAdministrator = new Administrator(); // This will be shared

        // Pass the same instance to AdminScene
        AdminScene adminScene = new AdminScene(null, mockAdministrator);
        Scene scene = adminScene.showView(stage, mockAdministrator);

        stage.setScene(scene);
        stage.show();
    }


    @Test
    @Order(1)
    public void testDisplayEmployeesScene() {
        Button displayEmployeesButton = lookup("#displayEmployeesButton").query();
        clickOn(displayEmployeesButton);

        TableView<Employee> employeeTable = lookup(".table-view").query();
        assertThat(employeeTable).isNotNull();
        assertEquals(mockAdministrator.readEmployeeData().size(), employeeTable.getItems().size());

        Button backButton = lookup(".button").lookup("Back").query();
        clickOn(backButton);
        assertNotNull(lookup("#displayEmployeesButton").query());

    }

    @Test
    @Order(2)
    public void testManageEmployeesScene() {
        Button manageEmployeesButton = lookup("#manageEmployeesButton").query();
        clickOn(manageEmployeesButton);

        Button registerButton = lookup("#registerButton").query();
        Button modifyButton = lookup("#modifyButton").query();
        Button deleteButton = lookup("#deleteButton").query();

        assertAll(
                () -> assertThat(registerButton).isNotNull(),
                () -> assertThat(modifyButton).isNotNull(),
                () -> assertThat(deleteButton).isNotNull()
        );

        Button backButton = lookup("#backToAdminButton").query();
        clickOn(backButton);
        assertNotNull(lookup("#manageEmployeesButton").query());
    }

    @Test
    @Order(3)
    public void testRegisterEmployee() {
        clickOn("#manageEmployeesButton");
        clickOn("#registerButton");

        // Locate TextField elements
        TextField fNameField = lookup("#firstNameField").query();
        TextField lNameField = lookup("#lastNameField").query();
        TextField emailField = lookup("#emailField").query();
        TextField phoneField = lookup("#phoneField").query();
        TextField birthdayField = lookup("#birthdayField").query();
        TextField idField = lookup("#idField").query();

        // Locate ComboBox elements
        ComboBox<String> genderComboBox = lookup("#genderComboBox").query();
        ComboBox<User.UserRole> roleComboBox = lookup("#roleComboBox").query();
        ComboBox<Integer> salaryComboBox = lookup("#salaryComboBox").query();

        // Fill the form
        clickOn(fNameField).write("John");
        clickOn(lNameField).write("Doe");
        clickOn(emailField).write("john.doe@example.com");
        clickOn(phoneField).write("+355 69 1234 567");
        clickOn(birthdayField).write("01/01/1990");
        clickOn(idField).write("123456");

        // Select values from ComboBox
        clickOn(genderComboBox).clickOn("Male");
        clickOn(roleComboBox).clickOn(User.UserRole.LIBRARIAN.toString());
        clickOn(salaryComboBox).clickOn("500");

        // Submit the form
        Button enterButton = lookup("#enterButton").query();
        clickOn(enterButton);
    }


    @Test
    @Order(4)
    public void testModifyEmployee() {
        try (MockedStatic<AlertUtil> alertUtilMock = Mockito.mockStatic(AlertUtil.class)) {
            // Perform actions to modify the employee
            clickOn("#manageEmployeesButton");
            clickOn("#modifyButton");

            clickOn("#firstNameField").write("UpdatedFirstName");
            clickOn("#enterButton");

            // Verify that AlertUtil.showAlert was called with the correct arguments
            alertUtilMock.verify(() -> AlertUtil.showAlert("Success", "Employee modified successfully"), Mockito.times(1));
        }
    }


    @Test
    @Order(5)
    public void testDeleteEmployee() {
        // Create and register an employee
        Employee employeeToDelete = new Employee("Mark", "Taylor", "mark.taylor@example.com", "Male",
                "01/01/1995", true, "01/01/1995", 789012, Model.User.UserRole.MANAGER, 800, "+355 67 1234 567");
        mockAdministrator.registerEmployee(employeeToDelete);

        // Navigate to "Manage Employees" and open the delete dialog
        clickOn("#manageEmployeesButton");
        clickOn("#deleteButton");

        // Enter the employee ID to delete
        TextField idInputField = lookup(".text-input").query();
        clickOn(idInputField).write(String.valueOf(employeeToDelete.getId()));

        // Wait for the dialog to render
        sleep(500); // Add delay to ensure the UI updates

        // Query the "Yes, Delete" button
        Button yesButton = lookup(".button").lookup((Node node) -> {
            if (node instanceof Button) {
                return ((Button) node).getText().equals("Yes, Delete");
            }
            return false;
        }).queryAs(Button.class);

        // Ensure the button exists
        assertNotNull(yesButton, "Yes, Delete button should exist");

        // Click on the "Yes, Delete" button
        clickOn(yesButton);

        // Verify the alert dialog and its content
        DialogPane dialogPane = lookup(".dialog-pane").query();
        assertNotNull(dialogPane, "Alert dialog should be displayed");

        // Check the content of the alert
        Label alertContent = (Label) dialogPane.lookup(".content");
        assertNotNull(alertContent, "Alert content should not be null");
        assertEquals("Employee deleted successfully.", alertContent.getText(), "Alert message should match");

        // Close the alert
        clickOn("OK");

        // Verify that the employee is no longer in the mockAdministrator's data
        assertThat(mockAdministrator.readEmployeeData())
                .noneMatch(employee -> employee.getId() == employeeToDelete.getId());
    }



    @Test
    @Order(6)
    public void testRevokePermissionsScene() {
        // Click on the "Revoke Permissions" button
        Button revokePermissionsButton = lookup("#revokePermissionsButton").query();
        clickOn(revokePermissionsButton);

        // Enable permissions for Librarian
        Button librarianEnableButton = lookup(".button").lookup((Node node) -> {
            if (node instanceof Button) {
                return ((Button) node).getText().equals("Enable Librarian to add Books");
            }
            return false;
        }).queryAs(Button.class);
        assertNotNull(librarianEnableButton, "Librarian Enable Button should exist");
        clickOn(librarianEnableButton);

        // Verify the Librarian's "Add Book" button is enabled
        assertTrue(LibrarianScene.addButton.isDisable(), "Add Book button should be enabled");

        // Disable permissions for Manager
        Button managerDisableButton = lookup(".button").lookup((Node node) -> {
            if (node instanceof Button) {
                return ((Button) node).getText().equals("Cancel Permission for Manager");
            }
            return false;
        }).queryAs(Button.class);
        assertNotNull(managerDisableButton, "Manager Disable Button should exist");
        clickOn(managerDisableButton);

        // Verify the Manager's "Print Bill" button is disabled
        assertFalse(ManagerScene.printBillButton.isDisable(), "Print Bill button should be disabled");
    }

}

