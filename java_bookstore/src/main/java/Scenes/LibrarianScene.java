package Scenes;
import Exceptions.LibrarianException;
import Users.Administrator;
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
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;
import Model.User;
import Users.Librarian;
import java.util.ArrayList;
import View.LogInView;

public class LibrarianScene {
    private User user;
    private BookController bookController;
    private ObservableList<Book> booksData = FXCollections.observableArrayList();
    private TableView<Book> bookTableView = new TableView<>(booksData);
    private static ArrayList<Author> authors;
    private static ArrayList<Category> categories = new ArrayList<>();
    private static int billsNumber = 0;
    Librarian librarian = new Librarian();
    public static Button addButton;
    private static boolean addBookButtonEnabled;
    private boolean implementOther;
    private TextField searchField;
    private Button implementOtherButton;
    Administrator administrator=new Administrator();


    public LibrarianScene(User user) {
        this.user = user;
        this.bookController = new BookController();
        this.authors = new ArrayList<>();
        this.addButton = new Button("Add New Book");
    }

    public static void enableAddBookButton() {
        addBookButtonEnabled = true;
        if (addButton != null) {
            addButton.setDisable(!addBookButtonEnabled);
            System.out.println("Add Book Button enabled");
        }
    }

    public static void disableAddBookButton() {
        addBookButtonEnabled = false;
        if (addButton != null) {
            addButton.setDisable(!addBookButtonEnabled);
            System.out.println("Add Book Button disabled");
        }
    }

    public static int getTotalBills() {
        return billsNumber;
    }

    public Scene showView(Stage stage) {
        User manager = new User();
        ManagerScene managerScene = new ManagerScene(manager);

        GridPane librarianpane = new GridPane();
        librarianpane.setHgap(10);
        librarianpane.setVgap(10);
        librarianpane.setPadding(new Insets(10, 10, 10, 10));
        librarianpane.setAlignment(Pos.CENTER);

        librarianpane.setStyle("-fx-background-color: #ADD8E6;");

        initializeTableView();
        searchField = new TextField();
        searchField.setPromptText("Search for a book");
        searchField.setOnKeyReleased(this::handleSearch);

        VBox searchBarContainer = new VBox(searchField);
        searchBarContainer.setAlignment(Pos.CENTER);
        searchBarContainer.setSpacing(5);

        Button printBillButton = new Button("Print Bill");
        printBillButton.setPrefWidth(150);
        printBillButton.setStyle("-fx-background-color: #87CEEB; -fx-text-fill: #000080;");
        printBillButton.setOnAction(e -> {
            librarian.printBill();
            bookTableView.refresh();

            billsNumber++;
            System.out.println("Total number of bills: " + billsNumber);
        });

        Button addButton = new Button("Add New Book");
        addButton.setPrefWidth(150);
        addButton.setDisable(!addBookButtonEnabled);
        addButton.setStyle("-fx-background-color: #87CEEB; -fx-text-fill: #000080;");
        if (addBookButtonEnabled) {
            addButton.setOnAction(e -> {
                managerScene.showAddBookDialog(stage);
            });
        }

        Button backButton = new Button("Back to Login");
        backButton.setPrefWidth(150);
        backButton.setStyle("-fx-background-color: #87CEEB; -fx-text-fill: #000080;");
        backButton.setOnAction(e -> {
            LogInView loginView = new LogInView();
            stage.setScene(loginView.showView(stage));
        });

        librarianpane.add(bookTableView, 1, 0);
        librarianpane.add(printBillButton, 0, 1);
        librarianpane.add(addButton, 0, 2);
        librarianpane.add(backButton, 2, 2);
        librarianpane.add(searchBarContainer, 1, 1);

        if (implementOther) {
            implementOtherButton = new Button("Back to Admin View");
            implementOtherButton.setPrefWidth(150);
            implementOtherButton.setStyle("-fx-background-color: #87CEEB; -fx-text-fill: #000080;");
            implementOtherButton.setOnAction(e -> {
                AdminScene adminScene = new AdminScene(user, administrator);
                Scene adminSceneView = adminScene.showView(stage, administrator);
                stage.setScene(adminSceneView);
            });
            librarianpane.add(implementOtherButton, 2, 3);
        }

        Scene librariansc = new Scene(librarianpane, 970, 800);
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        stage.setX((bounds.getWidth() - librariansc.getWidth()) / 2);
        stage.setY((bounds.getHeight() - librariansc.getHeight()) / 2);
        return librariansc;
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

        TableColumn<Book, Void> quantityColumn = new TableColumn<>("Quantity");
        quantityColumn.setCellFactory(param -> new TableCell<>() {
            private final TextField quantityField = new TextField();
            private final Button selectButton = new Button("Select");

            {
                selectButton.setOnAction(event -> {
                    int quantity = Integer.parseInt(quantityField.getText());
                    Book book = getTableView().getItems().get(getIndex());

                    if (quantity > 0 && quantity <= book.getStock()) {
                        book.setSelected(quantity);
                        librarian.addToSelectedBooks(book);
                    } else {
                        showAlert("Stock Error", "Stock for this book is 0.");
                        throw new LibrarianException("Error printing bill. Book stock is 0.");
                    }
                });
            }

            private void showAlert(String title, String content) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle(title);
                alert.setHeaderText(null);
                alert.setContentText(content);
                alert.showAndWait();
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(new HBox());
                } else {
                    HBox hbox = new HBox(5, quantityField, selectButton);
                    setGraphic(hbox);
                }
            }
        });
        bookTableView.getColumns().addAll(titleColumn, stockColumn, sellingPriceColumn, authorColumn, quantityColumn);
        booksData.addAll(bookController.getListOfBooks());
        bookTableView.setItems(booksData);
        bookTableView.setPrefSize(700, 600);
    }

    public void implementOtherLibrarian() {
        implementOther=true;
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
}