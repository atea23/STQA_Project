package com.example.test;

import View.LogInView;
import Controller.UserController;
import Model.User;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.control.LabeledMatchers;
import org.testfx.matcher.control.TextInputControlMatchers;

import static org.mockito.Mockito.*;
import static org.testfx.api.FxAssert.verifyThat;

public class LogInViewTest extends ApplicationTest {

    private Stage stage;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        LogInView logInView = new LogInView();
        stage.setScene(logInView.showView(stage));
        stage.show();
    }

    @Test
    public void testLoginWithValidCredentials() {
        // Simulate valid credentials
        clickOn("#emailField").write("a@gmail.com");
        clickOn("#passwordField").write("admin");
        clickOn("#loginButton");

        // Verify navigation to AdminScene
        verifyThat("#adminWelcomeLabel", LabeledMatchers.hasText("Welcome to the Admin scene!"));
    }

    @Test
    public void testLoginWithInvalidCredentials() {
        // Simulate invalid credentials
        clickOn("#emailField").write("wrong@example.com");
        clickOn("#passwordField").write("wrongpassword");
        clickOn("#loginButton");

        // Verify the alert dialog appears with the expected content
        verifyThat(".dialog-pane .header-panel .label", LabeledMatchers.hasText("Login Failed"));
    }


    @Test
    public void testSignUpButtonNavigation() {
        // Click the "Sign Up" button
        clickOn("#signUpButton");

        // Verify navigation to SignUpView
        verifyThat("#firstNameLabel", LabeledMatchers.hasText("First Name"));
    }
}