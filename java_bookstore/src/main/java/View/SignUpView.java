package View;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import Controller.UserController;
import Model.User;

public class SignUpView {

    public Scene showView(Stage stage) {
        GridPane pane = new GridPane();
        pane.setHgap(10);
        pane.setVgap(10);
        pane.setPadding(new Insets(10, 10, 10, 10));

        pane.setStyle("-fx-background-color: #ADD8E6;"); // Light blue background

        // First Name
        Label firstNameL = new Label("First Name");
        firstNameL.setId("firstNameLabel"); // Set fx:id
        TextField firstNameField = new TextField();
        firstNameField.setId("firstNameField"); // Set fx:id
        firstNameL.setStyle("-fx-text-fill: #000080;");
        pane.add(firstNameL, 0, 0);
        pane.add(firstNameField, 1, 0);

        // Last Name
        Label lastNameL = new Label("Last Name");
        lastNameL.setId("lastNameLabel"); // Set fx:id
        TextField lastNameField = new TextField();
        lastNameField.setId("lastNameField"); // Set fx:id
        lastNameL.setStyle("-fx-text-fill: #000080;");
        pane.add(lastNameL, 0, 1);
        pane.add(lastNameField, 1, 1);

        // Email
        Label emailL = new Label("Email");
        emailL.setId("emailLabel"); // Set fx:id
        TextField emailField = new TextField();
        emailField.setId("emailField"); // Set fx:id
        emailL.setStyle("-fx-text-fill: #000080;");
        pane.add(emailL, 0, 2);
        pane.add(emailField, 1, 2);

        // Password
        Label passwordL = new Label("Password");
        passwordL.setId("passwordLabel"); // Set fx:id
        PasswordField passF = new PasswordField();
        passF.setId("passwordField"); // Set fx:id
        passwordL.setStyle("-fx-text-fill: #000080;");
        pane.add(passwordL, 0, 3);
        pane.add(passF, 1, 3);

        // Verify Password
        Label vPasswordL = new Label("Verify Password");
        vPasswordL.setId("verifyPasswordLabel"); // Set fx:id
        PasswordField vPassF = new PasswordField();
        vPassF.setId("verifyPasswordField"); // Set fx:id
        vPasswordL.setStyle("-fx-text-fill: #000080;");
        pane.add(vPasswordL, 0, 4);
        pane.add(vPassF, 1, 4);

        // Gender
        Label genderL = new Label("Gender");
        genderL.setId("genderLabel"); // Set fx:id
        RadioButton male = new RadioButton("Male");
        male.setId("maleRadioButton"); // Set fx:id
        RadioButton female = new RadioButton("Female");
        female.setId("femaleRadioButton"); // Set fx:id
        RadioButton other = new RadioButton("Other");
        other.setId("otherRadioButton"); // Set fx:id

        ToggleGroup tg = new ToggleGroup();
        male.setToggleGroup(tg);
        female.setToggleGroup(tg);
        other.setToggleGroup(tg);

        genderL.setStyle("-fx-text-fill: #000080;");
        pane.add(genderL, 0, 5);
        HBox genderBox = new HBox(10, male, female, other);
        pane.add(genderBox, 1, 5);

        // Status (Profession)
        Label profL = new Label("Select your status:");
        profL.setId("statusLabel"); // Set fx:id
        ComboBox<String> prof = new ComboBox<>();
        prof.setId("statusComboBox"); // Set fx:id
        prof.getItems().addAll("Librarian", "Administrator", "Manager");
        profL.setStyle("-fx-text-fill: #000080;");
        pane.add(profL, 0, 6);
        pane.add(prof, 1, 6);

        // Remember Me
        CheckBox remember = new CheckBox("Remember Me");
        remember.setId("rememberCheckBox"); // Set fx:id
        remember.setStyle("-fx-text-fill: #000080;");
        pane.add(remember, 1, 7);

        // Sign Up Button
        Button signUpButton = new Button("Sign Up");
        signUpButton.setId("signUpButton"); // Set fx:id
        signUpButton.setStyle("-fx-background-color: #87CEEB; -fx-text-fill: #000080;");
        pane.add(signUpButton, 1, 8);

        // Back Button
        Button backButton = new Button("Back");
        backButton.setId("backButton"); // Set fx:id
        backButton.setStyle("-fx-background-color: #87CEEB; -fx-text-fill: #000080;");
        backButton.setOnAction(e -> {
            LogInView logInView = new LogInView();
            Scene loginScene = logInView.showView(stage);
            stage.setScene(loginScene);
        });
        backButton.setMinWidth(120);
        pane.add(backButton, 1, 9);

        // Sign-Up Action
        signUpButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String firstName = firstNameField.getText();
                String lastName = lastNameField.getText();
                String email = emailField.getText();
                String password = passF.getText();
                String verifyPassword = vPassF.getText();
                String gender = tg.getSelectedToggle() == male ? "Male" : tg.getSelectedToggle() == female ? "Female" : "Other";
                boolean isRememberMe = remember.isSelected();

                UserController uc = new UserController();
                boolean isRegistered = uc.signUp(firstName, lastName, email, password, verifyPassword, gender, isRememberMe, User.role);

                if (!isRegistered) {
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setHeaderText("Error");
                    errorAlert.setContentText("Registration failed. Please check your information.");
                    errorAlert.show();
                } else {
                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                    successAlert.setHeaderText("Registration Successful");
                    successAlert.setContentText("You have successfully registered!");
                    successAlert.showAndWait();
                    LogInView logInView = new LogInView();
                    stage.setScene(logInView.showView(stage));
                }
            }
        });

        return new Scene(pane, 400, 400);
    }
}