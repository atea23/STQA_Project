package View;

import Users.Administrator;
import Users.Librarian;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import Controller.UserController;
import Model.User;
import Scenes.AdminScene;
import Scenes.ManagerScene;
import Scenes.LibrarianScene;

public class LogInView {

    public Scene showView(Stage stage) {
        GridPane p = new GridPane();
        p.setHgap(10);
        p.setVgap(10);
        p.setPadding(new Insets(10, 10, 10, 10));
        p.setAlignment(Pos.CENTER);

        p.setStyle("-fx-background-color: #ADD8E6;");

        // Welcome label
        Label welcomeLabel = new Label("Welcome to Our Bookstore!");
        welcomeLabel.setFont(new Font("Arial", 20));
        welcomeLabel.setStyle("-fx-text-fill: #000080;");
        p.add(welcomeLabel, 0, 0, 2, 1);

        // Email label and field
        Label email = new Label("Email");
        email.setStyle("-fx-text-fill: #000080;");
        TextField emailF = new TextField();
        emailF.setId("emailField"); // Set fx:id
        p.add(email, 0, 1);
        p.add(emailF, 1, 1);

        // Password label and field
        Label passw = new Label("Password");
        passw.setStyle("-fx-text-fill: #000080;");
        PasswordField passwF = new PasswordField();
        passwF.setId("passwordField"); // Set fx:id
        p.add(passw, 0, 2);
        p.add(passwF, 1, 2);

        // Buttons
        Button login = new Button("Log in");
        login.setId("loginButton"); // Set fx:id
        Button signin = new Button("Sign up");
        signin.setId("signUpButton"); // Set fx:id

        login.setStyle("-fx-background-color: #87CEEB; -fx-text-fill: #000080;");
        signin.setStyle("-fx-background-color: #87CEEB; -fx-text-fill: #000080;");

        // Add buttons to layout
        HBox h = new HBox();
        h.getChildren().addAll(login, signin);
        h.setSpacing(10);
        p.add(h, 1, 4);

        // Login button action
        login.setOnAction(e -> {
            UserController uc = new UserController();
            User user = uc.login(emailF.getText(), passwF.getText());
            if (user != null) {
                if (user.getRole() == User.UserRole.ADMIN) {
                    AdminScene adminScene = new AdminScene(user, new Administrator());
                    stage.setScene(adminScene.showView(stage, new Administrator()));
                } else if (user.getRole() == User.UserRole.LIBRARIAN) {
                    LibrarianScene librarianScene = new LibrarianScene(user);
                    stage.setScene(librarianScene.showView(stage));
                } else if (user.getRole() == User.UserRole.MANAGER) {
                    ManagerScene managerScene = new ManagerScene(user);
                    stage.setScene(managerScene.showView(stage));
                }
            } else {
                Alert al = new Alert(Alert.AlertType.ERROR);
                al.setHeaderText("Login Failed");
                al.setContentText("Invalid email or password. Please try again.");
                al.showAndWait();
            }
        });

        // Sign-up button action
        signin.setOnAction(e -> {
            SignUpView sv = new SignUpView();
            stage.setScene(sv.showView(stage));
        });

        // Create and return scene
        Scene sc = new Scene(p, 400, 300);
        return sc;
    }
}