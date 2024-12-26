package Scenes;

import Exceptions.ManagerException;
import Model.User;
import Users.Administrator;
import View.LogInView;
import com.example.java_bookstore.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import Users.Librarian;
import java.io.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static Scenes.LibrarianScene.getTotalBills;
import static Users.Librarian.*;
import static Users.Manager.getBookBoughtStatistics;
import static Users.Manager.getBookSoldStatistics;

public class ManagerScene {
    private User user;
    private BookController bookController;
    private ObservableList<Book> booksData = FXCollections.observableArrayList();
    private TableView<Book> bookTableView = new TableView<>(booksData);
    private static ArrayList<Author> authors;
    private static ArrayList<Category> categories;
    private static final String AUTHORS_FILE_PATH = "authors.txt";
    private static final String CATEGORIES_FILE_PATH = "categories.txt";
    private static boolean printBillButtonEnabled = false;
    Administrator administrator = new Administrator();
    private static Button printBillButton;
    private boolean implementOther;
    private Button implementOtherButton;
    private TextField searchField;


    public ManagerScene(User user) {
        this.user = user;
        this.bookController = new BookController();
        this.authors = loadAuthors();
        this.categories = loadCategories();
        this.printBillButton = new Button("Print Bill");
    }

    public static void enablePrintBillButton() {
        printBillButtonEnabled = true;
        if (printBillButton != null) {
            printBillButton.setDisable(!printBillButtonEnabled);
            System.out.println("Print Bill button enabled");
        }
    }

    public void disablePrintBillButton() {

        printBillButtonEnabled = false;
        if (printBillButton != null) {
            printBillButton.setDisable(!printBillButtonEnabled);
            System.out.println("Print Bill button disabled");
        }
    }

    public Scene showView(Stage stage) {
        GridPane managerPane = new GridPane();
        managerPane.setHgap(10);
        managerPane.setVgap(10);
        managerPane.setPadding(new Insets(10, 10, 10, 10));
        managerPane.setAlignment(Pos.CENTER);
        managerPane.setStyle("-fx-background-color: #ADD8E6;");

        initializeTableView();
        searchField = new TextField();
        searchField.setPromptText("Search for a book");
        searchField.setOnKeyReleased(this::handleSearch);

        VBox searchBarContainer = new VBox(searchField);
        searchBarContainer.setAlignment(Pos.CENTER);
        searchBarContainer.setSpacing(5);

        Button addBookButton = new Button("Add New Book");
        addBookButton.setPrefWidth(150);
        addBookButton.setStyle("-fx-background-color: #87CEEB; -fx-text-fill: #000080;");
        addBookButton.setOnAction(e -> showAddBookDialog(stage));

        Button addLibrarianPerformanceButton = new Button("See Librarian Performance");
        addLibrarianPerformanceButton.setStyle("-fx-background-color: #87CEEB; -fx-text-fill: #000080;");
        addLibrarianPerformanceButton.setOnAction(e -> showLibrarianPerformanceDialog(stage));

        Button getBookSoldStatisticsButton = new Button("Get Books Sold Statistics");
        getBookSoldStatisticsButton.setStyle("-fx-background-color: #87CEEB; -fx-text-fill: #000080;");
        getBookSoldStatisticsButton.setOnAction(e -> showBookSoldStatisticsDialog(stage));

        Button getBookBoughtStatisticsButton = new Button("Get Books Bought Statistics");
        getBookBoughtStatisticsButton.setStyle("-fx-background-color: #87CEEB; -fx-text-fill: #000080;");
        getBookBoughtStatisticsButton.setOnAction(e -> showBookBoughtStatisticsDialog(stage));

        Button backButton = new Button("Back to Login");
        backButton.setPrefWidth(150);
        backButton.setStyle("-fx-background-color: #87CEEB; -fx-text-fill: #000080;");
        backButton.setOnAction(e -> {
            LogInView loginView = new LogInView();
            stage.setScene(loginView.showView(stage));
        });

        managerPane.add(bookTableView, 1, 0);
        managerPane.add(addBookButton, 1, 2);
        managerPane.add(backButton, 2, 2);
        managerPane.add(addLibrarianPerformanceButton, 1, 3);
        managerPane.add(getBookSoldStatisticsButton, 2, 3);
        managerPane.add(getBookBoughtStatisticsButton, 2, 4);
        managerPane.add(searchBarContainer, 1, 1);


        printBillButton.setDisable(!printBillButtonEnabled);

        if (printBillButtonEnabled) {
            printBillButton.setStyle("-fx-background-color: #87CEEB; -fx-text-fill: #000080;");
            printBillButton.setPrefWidth(150);
            printBillButton.setOnAction(e -> {
                LibrarianScene librarianScene = new LibrarianScene(user);
                Scene librarianSceneView = librarianScene.showView(stage);

                stage.setScene(librarianSceneView);
            });
            managerPane.add(printBillButton, 1, 4);
        }

        if (implementOther) {
            implementOtherButton = new Button("Back to Admin View");
            implementOtherButton.setPrefWidth(150);
            implementOtherButton.setStyle("-fx-background-color: #87CEEB; -fx-text-fill: #000080;");
            implementOtherButton.setOnAction(e -> {
                AdminScene adminScene = new AdminScene(user, administrator);
                Scene adminSceneView = adminScene.showView(stage, administrator);

                stage.setScene(adminSceneView);
            });
            managerPane.add(implementOtherButton, 2, 2);
        }

        Scene managerScene = new Scene(managerPane, 900, 800);
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        stage.setX((bounds.getWidth() - managerScene.getWidth()) / 2);
        stage.setY((bounds.getHeight() - managerScene.getHeight()) / 2);

        checkLowStockBooks();

        return managerScene;
    }

    private void checkLowStockBooks() {
        for (Book book : booksData) {
            if (book.getStock() < 5) {
                showAlert("Low Stock", "Low stock for book: " + book.getTitle() + ". Current stock: " + book.getStock());
            }
        }
    }

    private void initializeTableView() {
        TableColumn<Book, String> titleColumn = new TableColumn<>("Title");
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));

        TableColumn<Book, Integer> stockColumn = new TableColumn<>("Stock");
        stockColumn.setCellValueFactory(new PropertyValueFactory<>("stock"));

        TableColumn<Book, Double> sellingPriceColumn = new TableColumn<>("Selling Price");
        sellingPriceColumn.setCellValueFactory(new PropertyValueFactory<>("sellingPrice"));

        TableColumn<Book, String> authorColumn = new TableColumn<>("Author");
        authorColumn.setCellValueFactory(cellData -> {
            Author author = cellData.getValue().getAuthor();
            return new SimpleStringProperty(author != null ? author.toString() : "");
        });

        bookTableView.getColumns().addAll(titleColumn, stockColumn, sellingPriceColumn, authorColumn);
        booksData.addAll(bookController.getListOfBooks());
        bookTableView.setItems(booksData);
        bookTableView.setPrefSize(700, 600);
    }

    public void showAddBookDialog(Stage primaryStage) {
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Add New Book");

        ComboBox<String> authorComboBox = new ComboBox<>();
        authors.forEach(author -> authorComboBox.getItems().add(author.getFirstName() + " " + author.getLastName()));
        authorComboBox.setPromptText("Select Author");

        Button createAuthorButton = new Button("Create New Author");
        createAuthorButton.setOnAction(e -> showCreateAuthorDialog(dialogStage, authorComboBox));

        ComboBox<String> categoryComboBox = new ComboBox<>();
        categories.forEach(category -> categoryComboBox.getItems().add(category.getCategoryName()));
        categoryComboBox.setPromptText("Select Category");

        Button createCategoryButton = new Button("Create New Category");
        createCategoryButton.setOnAction(e -> showCreateCategoryDialog(dialogStage, categoryComboBox));

        TextField isbnField = new TextField();
        TextField titleField = new TextField();

        TextField purchasedPriceField = new TextField();
        TextField originalPriceField = new TextField();
        TextField sellingPriceField = new TextField();
        TextField stockField = new TextField();

        DatePicker purchasedDatePicker = new DatePicker();

        Button addBookButton = new Button("Add Book");
        addBookButton.setOnAction(e -> {
            try {
                if (validateInput(
                        isbnField.getText(),
                        titleField.getText(),
                        purchasedPriceField.getText(),
                        originalPriceField.getText(),
                        sellingPriceField.getText(),
                        stockField.getText()
                )) {
                    Author author = searchAuthorByName(authorComboBox.getValue());
                    if (author != null) {
                        String categoryName = categoryComboBox.getValue();
                        Category category = new Category(categoryName);

                        LocalDate selectedDate = purchasedDatePicker.getValue();
                        Date purchasedDate = Date.from(selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

                        Book newBook = new Book(
                                isbnField.getText(),
                                titleField.getText(),
                                "Supplier",
                                purchasedDate,
                                Double.parseDouble(purchasedPriceField.getText()),
                                Double.parseDouble(originalPriceField.getText()),
                                Double.parseDouble(sellingPriceField.getText()),
                                Integer.parseInt(stockField.getText()),
                                author,
                                Integer.parseInt(stockField.getText())
                                );
                        newBook.addCategory(category);
                        bookController.addBook(newBook);
                        booksData.add(newBook);
                        System.out.println("New book added: " + newBook.getTitle());
                        dialogStage.close();
                    } else {
                        showAlert("Invalid Input", "Please select an author.");
                    }
                } else {
                    showAlert("Invalid Input", "Please enter valid values for all fields.");
                }
            } catch (NumberFormatException ex) {
                showAlert("Invalid Number Format", "Please enter valid numeric values for numeric fields.");
            }
        });

        VBox dialogLayout = new VBox();
        dialogLayout.getChildren().addAll(
                new Label("ISBN:"), isbnField,
                new Label("Title:"), titleField,
                new Label("Purchased Price:"), purchasedPriceField,
                new Label("Original Price:"), originalPriceField,
                new Label("Selling Price:"), sellingPriceField,
                new Label("Stock:"), stockField,
                new Label("Purchased Date:"), purchasedDatePicker,
                new Label("Author:"), authorComboBox,
                createAuthorButton,
                new Label("Category:"), categoryComboBox,
                createCategoryButton,
                addBookButton
        );
        dialogLayout.setAlignment(Pos.CENTER);
        dialogLayout.setSpacing(10);
        Scene dialogScene = new Scene(dialogLayout, 600, 600);
        dialogStage.setScene(dialogScene);
        dialogStage.showAndWait();
    }

    private void showCreateCategoryDialog(Stage primaryStage, ComboBox<String> categoryComboBox) {
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Create New Category");

        TextField categoryNameField = new TextField();

        Button createCategoryButton = new Button("Create Category");
        createCategoryButton.setOnAction(e -> {
            String categoryName = categoryNameField.getText();

            if (validateCategoryInput(categoryName)) {
                Category newCategory = new Category(categoryName);
                categories.add(newCategory);

                saveCategories(categories);

                categoryComboBox.getItems().add(newCategory.getCategoryName());
                categoryComboBox.setValue(newCategory.getCategoryName());

                dialogStage.close();
            } else {
                showAlert("Invalid Input", "Please enter valid values for category fields.");
            }
        });

        VBox dialogLayout = new VBox();
        dialogLayout.getChildren().addAll(
                new Label("Category Name:"), categoryNameField,
                createCategoryButton
        );
        dialogLayout.setAlignment(Pos.CENTER);
        dialogLayout.setSpacing(10);
        Scene dialogScene = new Scene(dialogLayout, 400, 300);
        dialogStage.setScene(dialogScene);
        dialogStage.showAndWait();
    }

    private void showLibrarianPerformanceDialog(Stage primaryStage) {
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Librarian Performance");

        DatePicker startDatePicker = new DatePicker();
        DatePicker endDatePicker = new DatePicker();

        RadioButton singleDateRadioButton = new RadioButton("Single Date");
        RadioButton periodRadioButton = new RadioButton("Period");

        ToggleGroup toggleGroup = new ToggleGroup();
        singleDateRadioButton.setToggleGroup(toggleGroup);
        periodRadioButton.setToggleGroup(toggleGroup);

        VBox radioButtonsLayout = new VBox(10, singleDateRadioButton, periodRadioButton);
        radioButtonsLayout.setAlignment(Pos.CENTER);

        HBox datePickersLayout = new HBox(10, startDatePicker, endDatePicker);
        datePickersLayout.setAlignment(Pos.CENTER);

        Button calculateButton = new Button("Calculate");
        calculateButton.setOnAction(e -> {
            if (toggleGroup.getSelectedToggle() == singleDateRadioButton) {
                LocalDate selectedDate = startDatePicker.getValue();
                if (selectedDate != null) {

                    LocalDateTime startDateTime = selectedDate.atStartOfDay();
                    String formattedStartDate = startDateTime.format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

                    VBox dialogLayout = new VBox();
                    dialogLayout.setAlignment(Pos.CENTER);
                    dialogLayout.setSpacing(10);

                    Label totalBillsLabel = new Label("Number of Total Bills: " + getBillsForDay(selectedDate).size());
                    ArrayList<String> bookTitlesForDay = getBookTitlesForDay(selectedDate);
                    StringBuilder booksSoldDetails = new StringBuilder("Data for Books Sold:\n");
                    for (String bookTitle : bookTitlesForDay) {
                        booksSoldDetails.append(bookTitle).append("\n");
                    }
                    Label booksSoldLabel = new Label(booksSoldDetails.toString());

                    Label totalRevenueLabel = new Label("Total Revenue for Day: " + calculateTotalRevenueForDay(selectedDate));

                    dialogLayout.getChildren().addAll(totalBillsLabel, booksSoldLabel, totalRevenueLabel);
                    Scene dialogScene = new Scene(dialogLayout, 400, 200);
                    dialogStage.setScene(dialogScene);
                } else {
                    showAlert("Invalid Input", "Please select a date.");
                }
            } else if (toggleGroup.getSelectedToggle() == periodRadioButton) {
                LocalDate startDate = startDatePicker.getValue();
                LocalDate endDate = endDatePicker.getValue();

                if (startDate != null && endDate != null) {
                    LocalDateTime startDateTime = startDate.atStartOfDay();
                    LocalDateTime endDateTime = endDate.atStartOfDay();
                    String formattedStartDate = startDateTime.format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
                    String formattedEndDate = endDateTime.format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

                    VBox dialogLayout = new VBox();
                    dialogLayout.setAlignment(Pos.CENTER);
                    dialogLayout.setSpacing(10);

                    Label totalBillsLabel = new Label("Number of Total Bills: " + getBillsForPeriod(formattedStartDate, formattedEndDate).size());
                    ArrayList<String> bookTitlesForPeriod = Librarian.getBookTitlesForPeriod(formattedStartDate, formattedEndDate);
                    StringBuilder booksSoldDetails = new StringBuilder("Data for Books Sold:\n");
                    for (String bookTitle : bookTitlesForPeriod) {
                        booksSoldDetails.append(bookTitle).append("\n");
                    }
                    Label booksSoldLabel = new Label(booksSoldDetails.toString());

                    Label totalRevenueLabel = new Label("Total Revenue for Period: " + calculateTotalRevenueForPeriod(formattedStartDate, formattedEndDate));

                    dialogLayout.getChildren().addAll(totalBillsLabel, booksSoldLabel, totalRevenueLabel);
                    Scene dialogScene = new Scene(dialogLayout, 400, 200);
                    dialogStage.setScene(dialogScene);
                } else {
                    showAlert("Invalid Input", "Please select both start and end dates.");
                }
            }
        });

        VBox dialogLayout = new VBox(10, radioButtonsLayout, datePickersLayout, calculateButton);
        dialogLayout.setAlignment(Pos.CENTER);
        Scene dialogScene = new Scene(dialogLayout, 400, 200);
        dialogStage.setScene(dialogScene);
        dialogStage.showAndWait();
    }

    private void saveAuthors(ArrayList<Author> authors) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(AUTHORS_FILE_PATH))) {
            oos.writeObject(authors);
            System.out.println("Authors saved successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveCategories(ArrayList<Category> categories) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(CATEGORIES_FILE_PATH))) {
            oos.writeObject(categories);
            System.out.println("Categories saved successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Author> loadAuthors() {
        ArrayList<Author> loadedAuthors = new ArrayList<>();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(AUTHORS_FILE_PATH))) {
            loadedAuthors = (ArrayList<Author>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return loadedAuthors;
    }

    private static ArrayList<Category> loadCategories() {
        ArrayList<Category> loadedCategories = new ArrayList<>();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(CATEGORIES_FILE_PATH))) {
            loadedCategories = (ArrayList<Category>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return loadedCategories;
    }

    private void showCreateAuthorDialog(Stage primaryStage, ComboBox<String> authorComboBox) {
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Create New Author");

        TextField firstNameField = new TextField();
        TextField lastNameField = new TextField();

        Button createAuthorButton = new Button("Create Author");
        createAuthorButton.setOnAction(e -> {
            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();

            if (validateAuthorInput(firstName, lastName)) {
                Author newAuthor = new Author(firstName, lastName, Gender.OTHER);
                authors.add(newAuthor);

                saveAuthors(authors);

                authorComboBox.getItems().add(newAuthor.getFirstName() + " " + newAuthor.getLastName());
                authorComboBox.setValue(newAuthor.getFirstName() + " " + newAuthor.getLastName());

                dialogStage.close();
            } else {
                showAlert("Invalid Input", "Please enter valid values for author fields.");
            }
        });

        VBox dialogLayout = new VBox();
        dialogLayout.getChildren().addAll(
                new Label("First Name:"), firstNameField,
                new Label("Last Name:"), lastNameField,
                createAuthorButton
        );
        dialogLayout.setAlignment(Pos.CENTER);
        dialogLayout.setSpacing(10);
        Scene dialogScene = new Scene(dialogLayout, 500, 400);
        dialogStage.setScene(dialogScene);
        dialogStage.showAndWait();
    }

    private boolean validateAuthorInput(String firstName, String lastName) {
        if (firstName.isEmpty() || lastName.isEmpty()) {
            throw new ManagerException("Invalid Input. Please enter valid values for author fields.", 1002);
        }
        if (isAuthor(firstName, lastName)) {
            showAlert("Invalid Input", "Author with the same name exists.");
            throw new ManagerException("Author with the same name already exists.", 1001);
        }
        return true;
    }

    private boolean validateCategoryInput(String categoryName) {
        return !categoryName.isEmpty();
    }

    public boolean isAuthor(String firstName, String lastName) {
        for (Author author : authors) {
            if (author.getFirstName().equalsIgnoreCase(firstName) && author.getLastName().equalsIgnoreCase(lastName)) {
                throw new ManagerException("Author with the same name already exists.", 1001);
            }
        }
        return false;
    }

    private Author searchAuthorByName(String fullName) {
        for (Author author : authors) {
            if ((author.toString()).equals(fullName)) {
                return author;
            }
        }
        return null;
    }

    private boolean validateInput(String isbn, String title, String purchasedPrice, String originalPrice, String sellingPrice, String stock) {
        return !isbn.isEmpty() && !title.isEmpty() && isDouble(purchasedPrice) &&
                isDouble(originalPrice) && isDouble(sellingPrice) && isInteger(stock);
    }

    private boolean isDouble(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void implementOtherManager() {
        implementOther = true;
    }

    private void showBookBoughtStatisticsDialog(Stage stage) {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.setTitle("Get Book Sold Statistics");

        ChoiceBox<String> filterChoiceBox = new ChoiceBox<>();
        filterChoiceBox.getItems().addAll("Daily", "Monthly", "Total");
        filterChoiceBox.setValue("Total");

        Button getStatisticsButton = new Button("Get Statistics");
        getStatisticsButton.setOnAction(e -> {
            String selectedFilter = filterChoiceBox.getValue();
            ArrayList<String> statistics = getBookBoughtStatistics(selectedFilter);

            if (statistics.isEmpty()) {
                showAlert("No Data", "No book statistics available for the selected filter.");
            } else {
                showStatisticsDialog(statistics);
            }
            dialogStage.close();
        });

        VBox dialogLayout = new VBox();
        dialogLayout.getChildren().addAll(
                new Label("Select Filter: "), filterChoiceBox,
                getStatisticsButton
        );
        dialogLayout.setSpacing(10);
        dialogLayout.setStyle("-fx-padding: 10;");
        Scene dialogScene = new Scene(dialogLayout, 300, 150);
        dialogStage.setScene(dialogScene);
        dialogStage.showAndWait();


    }

    private void showBookSoldStatisticsDialog(Stage primaryStage) {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle("Get Book Sold Statistics");

        ChoiceBox<String> filterChoiceBox = new ChoiceBox<>();
        filterChoiceBox.getItems().addAll("Daily", "Monthly", "Total");
        filterChoiceBox.setValue("Total");

        Button getStatisticsButton = new Button("Get Statistics");
        getStatisticsButton.setOnAction(e -> {
            String selectedFilter = filterChoiceBox.getValue();
            ArrayList<String> statistics = getBookSoldStatistics(selectedFilter);

            if (statistics.isEmpty()) {
                showAlert("No Data", "No book statistics available for the selected filter.");
            } else {
                showStatisticsDialog(statistics);
            }

            dialogStage.close();
        });
        VBox dialogLayout = new VBox();
        dialogLayout.getChildren().addAll(
                new Label("Select Filter: "), filterChoiceBox,
                getStatisticsButton
        );
        dialogLayout.setSpacing(10);
        dialogLayout.setStyle("-fx-padding: 10;");
        Scene dialogScene = new Scene(dialogLayout, 300, 150);
        dialogStage.setScene(dialogScene);
        dialogStage.showAndWait();
    }

    private void showStatisticsDialog(ArrayList<String> statistics) {
        Stage statisticsStage = new Stage();
        statisticsStage.initModality(Modality.APPLICATION_MODAL);
        statisticsStage.setTitle("Book Statistics");

        VBox statisticsLayout = new VBox();
        for (String stat : statistics) {
            Label statLabel = new Label(stat);
            statisticsLayout.getChildren().add(statLabel);
        }

        Scene statisticsScene = new Scene(statisticsLayout, 400, 300);
        statisticsStage.setScene(statisticsScene);
        statisticsStage.showAndWait();
    }

    private void handleSearch(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            String searchText = searchField.getText().toLowerCase();
            FilteredList<Book> filteredBooks = booksData.filtered(book ->
                    book.getTitle().toLowerCase().contains(searchText) ||
                            book.getAuthor().toString().toLowerCase().contains(searchText)
            );
            SortedList<Book> sortedBooks = new SortedList<>(filteredBooks);
            sortedBooks.comparatorProperty().bind(bookTableView.comparatorProperty());
            bookTableView.setItems(sortedBooks);
            bookTableView.refresh();
        }
    }

    public double calculateTotalCost(LocalDate startDate, LocalDate endDate) {
        double totalCost = 0.0;

        List<Book> booksBought = bookController.getBooksBoughtForPeriod(startDate, endDate);

        for (Book book : booksBought) {
            totalCost += book.getPurchasedPrice() * book.getEnteredStock();
        }

        return totalCost;
    }
}