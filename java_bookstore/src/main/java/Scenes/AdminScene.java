package Scenes;

import Users.Administrator;
import Users.Employee;
import Users.Librarian;
import Utils.AlertUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import Model.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static Scenes.LibrarianScene.disableAddBookButton;
import static Scenes.LibrarianScene.enableAddBookButton;
//import static Scenes.ManagerScene.disablePrintBillButton;
import static Scenes.ManagerScene.enablePrintBillButton;

public class AdminScene  {
    private User user;
    private Stage stage;
    private TableView<Employee> employeeListTable;
    private Administrator administrator;
    private ObservableList<Employee> data;
    //private Librarian librarian = new Librarian();
    //private LibrarianScene librarianScene = new LibrarianScene(user);
    public AdminScene(User user, Administrator administrator){
        this.user = user;
        this.administrator = administrator;
        this.employeeListTable = new TableView<>();
    }


    public Scene showView(Stage stage, Administrator administrator) {
        this.stage = stage;
        this.administrator = administrator;

        VBox firstVBox = new VBox(10);

        firstVBox.setStyle("-fx-background-color: #ADD8E6;");

        Label adminLabel = new Label("Welcome to the Admin scene!");
        adminLabel.setStyle("-fx-text-fill: #000080;");
        firstVBox.getChildren().add(adminLabel);

        Button displayEmplButton = new Button("Display employees list");
        displayEmplButton.setStyle("-fx-background-color: #87CEEB; -fx-text-fill: #000080;");
        displayEmplButton.setMinWidth(200);
        displayEmplButton.setOnAction(e -> DisplayEmployeesScene());

        Button manageEmplButton = new Button("Manage Employees");
        manageEmplButton.setStyle("-fx-background-color: #87CEEB; -fx-text-fill: #000080;");
        manageEmplButton.setMinWidth(200);
        manageEmplButton.setOnAction(e -> ManageEmployeesScene());

        Button revokePermissionsButton = new Button("Revoke permissions");
        revokePermissionsButton.setStyle("-fx-background-color: #87CEEB; -fx-text-fill: #000080;");
        revokePermissionsButton.setMinWidth(200);
        revokePermissionsButton.setOnAction(e -> revokePermissionsScene());

        Button goToLibrarianButton = new Button("Go to Librarian Scene");
        goToLibrarianButton.setStyle("-fx-background-color: #87CEEB; -fx-text-fill: #000080;");
        goToLibrarianButton.setMinWidth(200);
        goToLibrarianButton.setOnAction(e -> {
            LibrarianScene librarianScene = new LibrarianScene(user);
            librarianScene.implementOtherLibrarian();
            Scene librarianSceneView = librarianScene.showView(stage);
            stage.setScene(librarianSceneView);
        });

        Button goToManagerButton = new Button("Go to Manager Scene");
        goToManagerButton.setStyle("-fx-background-color: #87CEEB; -fx-text-fill: #000080;");
        goToManagerButton.setMinWidth(200);
        goToManagerButton.setOnAction(e -> {
            ManagerScene managerScene = new ManagerScene(user);
            managerScene.implementOtherManager();
            Scene managerSceneView = managerScene.showView(stage);
            stage.setScene(managerSceneView);
        });

        Button viewSalariesButton = new Button("View Financials");
        viewSalariesButton.setStyle("-fx-background-color: #87CEEB; -fx-text-fill: #000080;");
        viewSalariesButton.setMinWidth(200);
        viewSalariesButton.setOnAction(e -> viewFinancials());

        firstVBox.getChildren().addAll(goToLibrarianButton, goToManagerButton, displayEmplButton, manageEmplButton, viewSalariesButton, revokePermissionsButton);
        firstVBox.setPadding(new Insets(20));

        return new Scene(firstVBox, 400, 300);
    }
    private void viewFinancials() {
        Librarian librarian1 = new Librarian();
        ManagerScene managerScene=new ManagerScene(new User());
        DatePicker startDatePicker = new DatePicker();
        DatePicker endDatePicker = new DatePicker();

        // Show a dialog to the user to input start and end dates
        Alert dateInputDialog = new Alert(Alert.AlertType.NONE);
        dateInputDialog.setTitle("Enter Date Range");
        dateInputDialog.setHeaderText("Please select the start and end dates");

        dateInputDialog.getDialogPane().setContent(new javafx.scene.layout.HBox(10, startDatePicker, endDatePicker));

        dateInputDialog.getButtonTypes().add(javafx.scene.control.ButtonType.OK);

        dateInputDialog.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
                // Get start and end dates from DatePickers
                LocalDate startDate = startDatePicker.getValue();
                LocalDate endDate = endDatePicker.getValue();

                if (startDate != null && endDate != null) {
                    // Convert LocalDate to LocalDateTime with a specific time (midnight in this case)
                    LocalDateTime startDateTime = startDate.atStartOfDay();
                    LocalDateTime endDateTime = endDate.atStartOfDay();

                    // Convert LocalDateTime to String format "yyyyMMdd_HHmmss"
                    String formattedStartDate = startDateTime.format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
                    String formattedEndDate = endDateTime.format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

                    // Access salaries for the specified period
                    List<Double> salaries = administrator.accessSalaryForPeriod(startDate, endDate);

                    // Calculate the total salary
                    double totalSalary = salaries.stream().mapToDouble(Double::doubleValue).sum();

                    // Calculate the total income for the selected period
                    double totalIncome = librarian1.calculateTotalRevenueForPeriod(formattedStartDate, formattedEndDate);

                    double totalCost = managerScene.calculateTotalCost(startDate, endDate);

                    double grandTotal = totalSalary + totalCost;

                    // Display total employees salaries, total income, and grand total for the selected period
                    StringBuilder financialInfo = new StringBuilder("Information about the total incomes and costs: ");
                    //financialInfo.append("\nTotal employees salaries for the selected period: $").append(totalSalary);
                    financialInfo.append("\nTotal income for the selected period: $").append(totalIncome);
                    // financialInfo.append("\nTotal book costs for the selected period: $").append(totalCost);
                    financialInfo.append("\nGrand Total for the selected period(salaries+book costs): $").append(grandTotal);

                    AlertUtil.showAlert("Financials", financialInfo.toString());
                } else {
                    showAlert("Invalid Input", "Please select both start and end dates.");
                }
            }
        });
    }


    private void DisplayEmployeesScene() {
        data = FXCollections.observableArrayList(administrator.readEmployeeData());
        employeeListTable = new TableView<>(data);

        TableColumn<Employee, String> fNameColumn = new TableColumn<>("First Name");
        fNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));

        TableColumn<Employee, String> lNameColumn = new TableColumn<>("Last Name");
        lNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));

        TableColumn<Employee, String> emailColumn = new TableColumn<>("Email");
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        TableColumn<Employee, String> phoneNoColumn = new TableColumn<>("Phone");
        phoneNoColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));

        TableColumn<Employee, Double> salaryColumn = new TableColumn<>("Salary");
        salaryColumn.setCellValueFactory(new PropertyValueFactory<>("salary"));

        TableColumn<Employee, String> birthdayColumn = new TableColumn<>("Birthday");
        birthdayColumn.setCellValueFactory(new PropertyValueFactory<>("birthday"));

        TableColumn<Employee, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Employee, String> roleColumn = new TableColumn<>("Role");
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));

        TableColumn<Employee, LocalDate> lastSalaryDateColumn = new TableColumn<>("Last Salary Date");
        lastSalaryDateColumn.setCellValueFactory(new PropertyValueFactory<>("lastSalaryPaymentDate"));

        employeeListTable.getColumns().addAll(fNameColumn, lNameColumn, emailColumn,
                phoneNoColumn, salaryColumn, birthdayColumn, idColumn, roleColumn, lastSalaryDateColumn);

        Button backButton = new Button("Back");
        backButton.setStyle("-fx-background-color: #87CEEB; -fx-text-fill: #000080;");
        VBox.setMargin(backButton, new Insets(10, 0, 0, 10));
        backButton.setOnAction(e -> {
            AdminScene adminScene = new AdminScene(user, administrator);
            Scene scene = adminScene.showView(stage, administrator);
            stage.setScene(scene);
        });

        VBox vbox = new VBox();
        vbox.getChildren().addAll(employeeListTable, backButton);
        vbox.setAlignment(Pos.CENTER);
        vbox.setStyle("-fx-background-color: #ADD8E6;");

        fNameColumn.setStyle("-fx-text-fill: #000080;");
        lNameColumn.setStyle("-fx-text-fill: #000080;");
        emailColumn.setStyle("-fx-text-fill: #000080;");
        phoneNoColumn.setStyle("-fx-text-fill: #000080;");
        salaryColumn.setStyle("-fx-text-fill: #000080;");
        birthdayColumn.setStyle("-fx-text-fill: #000080;");
        idColumn.setStyle("-fx-text-fill: #000080;");
        roleColumn.setStyle("-fx-text-fill: #000080;");
        lastSalaryDateColumn.setStyle("-fx-text-fill: #000080;");

        refreshEmployeeTableView();

        Scene employeesListScene = new Scene(vbox, 800, 600);
        stage.setScene(employeesListScene);

        //qe te hapet ne mes te ekranit
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX((screenBounds.getWidth() - stage.getWidth()) / 2);
        stage.setY((screenBounds.getHeight() - stage.getHeight()) / 2);
    }

    private void ManageEmployeesScene() {
        VBox manageEmVBox = new VBox(10);
        manageEmVBox.setStyle("-fx-background-color: #ADD8E6;");


        Button registerButton = new Button("Register");
        registerButton.setStyle("-fx-background-color: #87CEEB; -fx-text-fill: #000080;");
        registerButton.setPrefWidth(150);
        registerButton.setOnAction(e -> registerEmployee());

        Button modifyButton = new Button("Modify");
        modifyButton.setStyle("-fx-background-color: #87CEEB; -fx-text-fill: #000080;");
        modifyButton.setPrefWidth(150);
        modifyButton.setOnAction(e -> modifyEmployee());

        Button deleteButton = new Button("Delete");
        deleteButton.setStyle("-fx-background-color: #87CEEB; -fx-text-fill: #000080;");
        deleteButton.setPrefWidth(150);
        deleteButton.setOnAction(e -> deleteEmployee());

        Button backButton = new Button("Back to Admin");
        backButton.setStyle("-fx-background-color: #87CEEB; -fx-text-fill: #000080;");
        backButton.setOnAction(e -> {
            AdminScene adminScene = new AdminScene(user, administrator);
            Scene scene = adminScene.showView(stage, administrator);
            stage.setScene(scene);
        });

        manageEmVBox.getChildren().addAll(registerButton, modifyButton, deleteButton,backButton);
        manageEmVBox.setPadding(new Insets(20)); // Add padding around the VBox

        Scene manageEmplScene = new Scene(manageEmVBox, 400, 300);
        stage.setScene(manageEmplScene);
    }


    private void registerEmployee() {
        GridPane registerPane = new GridPane();
        registerPane.setAlignment(Pos.CENTER);
        registerPane.setVgap(15);
        registerPane.setHgap(10);
        registerPane.setStyle("-fx-background-color: #ADD8E6;");

        Label fNameLabel = new Label("First Name:");
        TextField fNameField = new TextField();

        Label lNameLabel = new Label("Last Name:");
        TextField lNameField = new TextField();

        Label emailLabel=new Label("Email");
        TextField emailField = new TextField();

        Label genLabel = new Label("Gender:");
        ComboBox<String> genComboBox = new ComboBox<>();
        genComboBox.getItems().addAll("Female", "Male");

        Label birthdayLabel = new Label("Birthday:");
        TextField birthdayField = new TextField();

        Label EmIdLabel = new Label("ID:");
        TextField EmIdField = new TextField();

        Label roleLabel = new Label("Role:");
        ComboBox<User.UserRole> roleComboBox = new ComboBox<>();
        roleComboBox.getItems().addAll(User.UserRole.values());

        Label salLabel = new Label("Salary:");
        ComboBox<Integer> salComboBox = new ComboBox<>();
        salComboBox.getItems().addAll(500, 600, 700, 800, 900);

        Label phoneLabel = new Label("Phone:");
        TextField phoneField = new TextField();

        Button enterButton = new Button("Enter");
        enterButton.setStyle("-fx-background-color: #87CEEB; -fx-text-fill: #000080;");
        enterButton.setOnAction(e -> {
            // Validation
            if (!fNameField.getText().matches("[a-zA-Z]+")) {
                showAlert("Error", "First Name should contain only letters.");
                return;
            }

            if (!lNameField.getText().matches("[a-zA-Z]+")) {
                showAlert("Error", "Last Name should contain only letters.");
                return;
            }

            if (!emailField.getText().matches("^[a-zA-Z0-9_+&-]+(?:\\.[a-zA-Z0-9_+&-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$")) {
                showAlert("Error", "Invalid email format.");
                return;
            }

            if (!birthdayField.getText().matches("\\d{2}/\\d{2}/\\d{4}")) {
                showAlert("Error", "Birthday should be in the format DD/MM/YYYY.");
                return;
            }

            if (!EmIdField.getText().matches("\\d{6}")) {
                showAlert("Error", "ID should contain 6 digits.");
                return;
            }

            if (!phoneField.getText().matches("\\+355 (69|68|67) \\d{4} \\d{3}")) {
                showAlert("Error", "Phone should be in the format +355 6X XXXX XXX.");
                return;
            }


            if (genComboBox.getValue() == null) {
                showAlert("Error", "Please select a gender.");
                return;
            }

            if (roleComboBox.getValue() == null) {
                showAlert("Error", "Please select a role.");
                return;
            }

            if (salComboBox.getValue() == null) {
                showAlert("Error", "Please select a salary.");
                return;
            }

            int enteredId = Integer.parseInt(EmIdField.getText());
            if (employeeExists(enteredId)) {
                showAlert("Error", "Employee with ID " + enteredId + " already exists.");
                return;
            }

            String enteredPhone = phoneField.getText();
            if (isPhoneAlreadyUsed(enteredPhone)) {
                showAlert("Error", "Phone number " + enteredPhone + " is already registered.");
                return;
            }

            Employee newEmpl = new Employee(
                    fNameField.getText(), lNameField.getText(), emailField.getText(), genComboBox.getValue(),
                    "", false, birthdayField.getText(),
                    Integer.parseInt(EmIdField.getText()), roleComboBox.getValue(),
                    salComboBox.getValue(), phoneField.getText()
            );

            boolean successful = administrator.registerEmployee(newEmpl); // if user registered correctly, successful returns true

            if (successful) {
                AlertUtil.showAlert("Registration Successful", "Employee registered successfully!");
                refreshEmployeeTableView();
            } else {
                AlertUtil.showAlert("Registration Failed", "Failed to register employee. Please try again.");
            }

            // Go back to adminScene
            AdminScene adminScene = new AdminScene(user, administrator);
            Scene scene = adminScene.showView(stage, administrator);
            stage.setScene(scene);
        });

        Button backButton = new Button("Back");
        backButton.setStyle("-fx-background-color: #87CEEB; -fx-text-fill: #000080;");
        backButton.setOnAction(e -> {
            ManageEmployeesScene(); // back to Manage Employees scene
        });

        registerPane.add(backButton, 0, 12);

        registerPane.add(fNameLabel, 0, 0);
        registerPane.add(fNameField, 1, 0);
        registerPane.add(lNameLabel, 0, 1);
        registerPane.add(lNameField, 1, 1);
        registerPane.add(emailLabel, 0, 2);
        registerPane.add(emailField, 1, 2);
        registerPane.add(genLabel, 0, 4);
        registerPane.add(genComboBox, 1, 4);
        registerPane.add(birthdayLabel, 0, 7);
        registerPane.add(birthdayField, 1, 7);
        registerPane.add(EmIdLabel, 0, 8);
        registerPane.add(EmIdField, 1, 8);
        registerPane.add(roleLabel, 0, 9);
        registerPane.add(roleComboBox, 1, 9);
        registerPane.add(salLabel, 0, 10);
        registerPane.add(salComboBox, 1, 10);
        registerPane.add(phoneLabel, 0, 11);
        registerPane.add(phoneField, 1, 11);
        registerPane.add(enterButton, 1, 12);

        Scene regEmplScene = new Scene(registerPane, 400, 500);
        stage.setScene(regEmplScene);
    }

    private boolean isPhoneAlreadyUsed(String phone) {
        List<Employee> employees = administrator.readEmployeeData();
        for (Employee employee : employees) {
            if (employee.getPhone().equals(phone)) {
                return true; // Phone number already used
            }
        }
        return false; // Phone number is unique
    }

    public boolean employeeExists(int id) {
        List<Employee> employees = administrator.readEmployeeData();
        return employees.stream().anyMatch(employee -> employee.getId() == id);
    }


    private void refreshEmployeeTableView() {
        data.clear();
        data.addAll(administrator.readEmployeeData());
        employeeListTable.setItems(data);
    }

    private void modifyEmployee() {
        TextInputDialog employeeidDialog = new TextInputDialog();
        employeeidDialog.setTitle("Modify Employee data");
        employeeidDialog.setHeaderText("Enter Employee ID of the Employee to modify");
        employeeidDialog.setContentText("ID:");

        Optional<String> result = employeeidDialog.showAndWait();
        result.ifPresent(id -> {
            try {
                int employeeId = Integer.parseInt(id);
                Employee employeeToEdit = findEmplById(employeeId);

                if (employeeToEdit != null) {
                    showEditEmplScene(employeeToEdit);
                } else {
                    showAlert("Error", "Employee ID not found. Please enter existing ID.");
                    modifyEmployee();
                }
            } catch (NumberFormatException e) {
                showAlert("Error", "Invalid ID format. Please enter a valid ID.");
                modifyEmployee();
            }
        });
    }

    private Employee findEmplById(int id) {
        List<Employee> employees = administrator.readEmployeeData();
        for (Employee employee : employees) {
            if (employee.getId() == id) {
                return employee;
            }
        }
        return null;
    }

    private void showEditEmplScene(Employee employee) {
        GridPane editPane = new GridPane();
        editPane.setAlignment(Pos.CENTER);
        editPane.setVgap(15);
        editPane.setHgap(10);
        editPane.setStyle("-fx-background-color: #ADD8E6;");

        TextField fNameField = new TextField(employee.getFirstName());
        fNameField.setEditable(false);
        Button editFNameButton = createEditInfoButton(fNameField);
        editFNameButton.setStyle("-fx-background-color: #87CEEB; -fx-text-fill: #000080;");
        addRow(editPane, "First Name:", fNameField, editFNameButton, 0);

        TextField lNameField = new TextField(employee.getLastName());
        lNameField.setEditable(false);
        Button editLNameButton = createEditInfoButton(lNameField);
        editLNameButton.setStyle("-fx-background-color: #87CEEB; -fx-text-fill: #000080;");
        addRow(editPane, "Last Name:", lNameField, editLNameButton, 1);

        TextField emailField = new TextField(employee.getEmail());
        emailField.setEditable(false);
        Button editEmailButton = createEditInfoButton(emailField);
        editEmailButton.setStyle("-fx-background-color: #87CEEB; -fx-text-fill: #000080;");
        addRow(editPane, "Email:", emailField, editEmailButton, 2);

        ComboBox<String> genComboBox = new ComboBox<>();
        genComboBox.getItems().addAll("Female", "Male");
        genComboBox.setValue(employee.getGenre());
        addRow(editPane, "Gender:", genComboBox, null, 3);

        TextField bdField = new TextField(employee.getBirthday());
        bdField.setEditable(false);
        Button editBirthdayButton = createEditInfoButton(bdField);
        editBirthdayButton.setStyle("-fx-background-color: #87CEEB; -fx-text-fill: #000080;");
        addRow(editPane, "Birthday:", bdField, editBirthdayButton, 4);

        TextField phField = new TextField(employee.getPhone());
        phField.setEditable(false);
        Button editPhoneButton = createEditInfoButton(phField);
        editPhoneButton.setStyle("-fx-background-color: #87CEEB; -fx-text-fill: #000080;");
        addRow(editPane, "Phone:", phField, editPhoneButton, 5);

        ComboBox<User.UserRole> roleComboBox = new ComboBox<>();
        roleComboBox.getItems().addAll(User.UserRole.values());
        roleComboBox.setValue(employee.getRole());
        addRow(editPane, "Role:", roleComboBox, null, 6);

        ComboBox<Double> salComboBox = new ComboBox<>();
        salComboBox.getItems().addAll(500.0, 600.0, 700.0, 800.0, 900.0);
        salComboBox.setValue(employee.getSalary());
        addRow(editPane, "Salary:", salComboBox, null, 7);

        Button backButton = new Button("Back");
        backButton.setStyle("-fx-background-color: #87CEEB; -fx-text-fill: #000080;");
        backButton.setOnAction(e -> ManageEmployeesScene());
        editPane.add(backButton, 0, 9);

        Button doneButton = new Button("Done");
        doneButton.setStyle("-fx-background-color: #87CEEB; -fx-text-fill: #000080;");
        doneButton.setOnAction(e -> {
            // Validation

            if (!fNameField.getText().matches("[a-zA-Z]+")) {
                showAlert("Error", "First Name should contain only letters.");
                return;
            }

            if (!lNameField.getText().matches("[a-zA-Z]+")) {
                showAlert("Error", "Last Name should contain only letters.");
                return;
            }
            if (!bdField.getText().matches("\\d{2}/\\d{2}/\\d{4}")) {
                showAlert("Error", "Birthday should be in the format DD/MM/YYYY.");
                return;
            }
            if (!emailField.getText().matches("^[a-zA-Z0-9_+&-]+(?:\\.[a-zA-Z0-9_+&-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$")) {
                showAlert("Error", "Invalid email format.");
                return;
            }

            if (genComboBox.getValue() == null) {
                showAlert("Error", "Please select a gender.");
                return;
            }

            if (roleComboBox.getValue() == null) {
                showAlert("Error", "Please select a role.");
                return;
            }

            if (salComboBox.getValue() == null) {
                showAlert("Error", "Please select a salary.");
                return;
            }

            if (!phField.getText().matches("\\+355 (69|68|67) \\d{4} \\d{3}")) {
                showAlert("Error", "Phone should be in the format +355 6X XXXX XXX.");
                return;
            }

            employee.setFirstName(fNameField.getText());
            employee.setLastName(lNameField.getText());
            employee.setEmail(emailField.getText());
            employee.setPhone(phField.getText());
            employee.setGenre(genComboBox.getValue());
            employee.setBirthday(bdField.getText());
            employee.setRole(roleComboBox.getValue());
            employee.setSalary(salComboBox.getValue());

            if (administrator.modifyEmployee(employee.getId(), employee)) {
                showAlert("Success", "Employee modified successfully.");
                ManageEmployeesScene();
            } else {
                showAlert("Error", "Failed to modify employee.");
            }
        });
        editPane.add(doneButton, 1, 9);

        Scene editScene = new Scene(editPane, 400, 500);
        stage.setScene(editScene);
    }


    private Button createEditInfoButton(TextField field) {
        Button editInfoButton = new Button("Edit");
        editInfoButton.setOnAction(e -> field.setEditable(true));
        return editInfoButton;
    }

    private void addRow(GridPane pane, String labelText, Node control, Button button, int rowIndex) {
        pane.add(new Label(labelText), 0, rowIndex);
        pane.add(control, 1, rowIndex);

        if (button != null) {
            pane.add(button, 2, rowIndex);
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void deleteEmployee() {
        TextInputDialog idDialog = new TextInputDialog();
        idDialog.setTitle("Delete Employee");
        idDialog.setHeaderText("Enter Employee ID to Delete");
        idDialog.setContentText("ID:");

        Optional<String> result = idDialog.showAndWait();
        result.ifPresent(id -> {
            try {
                int employeeId = Integer.parseInt(id);
                Employee employeeToDelete = findEmployeeById(employeeId);

                if (employeeToDelete != null) {
                    showConfirmDeleteScene(employeeToDelete);
                } else {
                    showAlert("Error", "Employee ID not found. Please try again.");
                    deleteEmployee();
                }
            } catch (NumberFormatException e) {
                showAlert("Error", "Invalid ID format. Please enter a valid ID.");
                deleteEmployee();
            }
        });
    }

    private Employee findEmployeeById(int id) {
        List<Employee> employees = administrator.readEmployeeData();
        for (Employee employee : employees) {
            if (employee.getId() == id) {
                return employee;
            }
        }
        return null;
    }

    private void showConfirmDeleteScene(Employee employee) {
        VBox layout = new VBox(10);
        Label clarityLabel = new Label("Are you sure you want to delete the following employee?\n" +
                "ID: " + employee.getId() + "\n" +
                "Name: " + employee.getFirstName() + " " + employee.getLastName() + "\n" +
                "Email: " + employee.getEmail() + "\n" +
                "");
        Button yesButton = new Button("Yes, Delete");
        Button noButton = new Button("No, Insert another ID");
        Button backButton = new Button("Back to Manage Employees");

        yesButton.setOnAction(e -> {
            if (administrator.deleteEmployee(employee.getId())) {
                showAlert("Success", "Employee deleted successfully.");
                ManageEmployeesScene();
            } else {
                showAlert("Error", "Failed to delete employee.");
            }
        });

        noButton.setOnAction(e -> deleteEmployee());
        backButton.setOnAction(e -> ManageEmployeesScene());

        layout.getChildren().addAll(clarityLabel, yesButton, noButton,backButton);
        Scene confirmDeleteScene = new Scene(layout, 300, 200);
        stage.setScene(confirmDeleteScene);
    }

    private void revokePermissionsScene() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Revoke Permissions");
        alert.setHeaderText("Choose role to revoke permissions");
        alert.setContentText("Choose your option:");

        ButtonType librarianButton = new ButtonType("Enable Librarian to add Books");
        ButtonType managerButton = new ButtonType("Enable Manager to print Bill");
        ButtonType cancelLibrarianButton = new ButtonType("Cancel Permission for Librarian");
        ButtonType cancelManagerButton = new ButtonType("Cancel Permission for Manager");


        alert.getButtonTypes().setAll(librarianButton, managerButton,cancelLibrarianButton, cancelManagerButton);

        Optional<ButtonType> result = alert.showAndWait();
        result.ifPresent(buttonType -> {
            if (buttonType == librarianButton) {
                enableAddBookButton();
                Scene mainMenuScene = showView(stage,administrator);
                stage.setScene(mainMenuScene);


            } else if (buttonType == managerButton) {
                // Revoke permissions for Manager
                enablePrintBillButton();
                Scene mainMenuScene = showView(stage,administrator);
                stage.setScene(mainMenuScene);
            }
            else if (buttonType == cancelLibrarianButton) {
                disableAddBookButton();
                Scene mainMenuScene = showView(stage, administrator);
                stage.setScene(mainMenuScene);
            }
            else if(buttonType==cancelManagerButton){
                ManagerScene managerScene = new ManagerScene(user);
                managerScene.disablePrintBillButton();
                Scene mainMenuScene = showView(stage,administrator); // Navigate back to main menu view
                stage.setScene(mainMenuScene);
            }
        });
    }
}