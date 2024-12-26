package com.example.java_bookstore;
import javafx.application.Application;
import javafx.stage.Stage;
import View.LogInView;

import static Users.Librarian.loadBillsFromFile;
import static Users.Manager.loadBillsFromFileManager;
import static Users.Manager.loadBooksFromFileManager;

public class MainClass extends Application {

    @Override
    public void start(Stage s) throws Exception {
        loadBillsFromFile();
        loadBillsFromFileManager();
        loadBooksFromFileManager();
        LogInView lg = new LogInView();
        s.setTitle("Log In");
        s.setScene(lg.showView(s));
        s.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}