package com.example.test;

import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxAssert;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.control.LabeledMatchers;
import org.testfx.matcher.control.TextInputControlMatchers;

import View.SignUpView;

import static org.testfx.api.FxAssert.verifyThat;

public class SignUpViewTest extends ApplicationTest {

    private Stage stage;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        SignUpView signUpView = new SignUpView();
        stage.setScene(signUpView.showView(stage));
        stage.show();
    }

    @Test
    public void testSignUpWithValidData() {
        // Fill in the sign-up form
        clickOn("#firstNameField").write("John");
        clickOn("#lastNameField").write("Doe");
        clickOn("#emailField").write("john.doe@example.com");
        clickOn("#passwordField").write("password123");
        clickOn("#verifyPasswordField").write("password123");
        clickOn("#maleRadioButton");
        clickOn("#statusComboBox").clickOn("Administrator");
        clickOn("#rememberCheckBox");
        clickOn("#signUpButton");

        // Verify the alert dialog header text
        verifyThat(".dialog-pane .header-panel .label", LabeledMatchers.hasText("Registration Successful"));

    }

}
