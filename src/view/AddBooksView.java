package view;

import controller.UserDashboardController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Author;
import model.Book;
import model.Category;
import model.Employee;
import model.Utility.FileWriterUtil;

import java.io.*;
import java.time.LocalDate;
import java.time.Year;
import java.util.List;

import static view.UserDashboardView.showAlert;

public class AddBooksView {
    static void showBooksTable(Stage primaryStage, List<Book> books, Employee employee) {
        VBox dashboardLayout = new VBox(20);
        dashboardLayout.setAlignment(Pos.TOP_CENTER);
        dashboardLayout.setPadding(new Insets(10, 10, 10, 10));

        Button btnProfile = new Button("Profile");
        Button btnBooks = new Button("Inventory");
        Button btnBill = new Button("Create bill");
        Button logoutButton = new Button("Logout");

        HBox hbox = new HBox(10); //spacing between buttons
        hbox.getChildren().addAll(btnProfile, btnBooks,btnBill, logoutButton);
        btnProfile.setOnAction(e -> {
            assert employee != null;
            ProfileView.showProfileView(primaryStage, employee);
        });
        btnBill.setOnAction(e -> AddBillView.createBillTable(primaryStage, books, employee));
        dashboardLayout.setAlignment(Pos.TOP_CENTER);
        dashboardLayout.setPadding(new Insets(10, 10, 10, 10));

        UserDashboardController userController = new UserDashboardController();
        logoutButton.setOnAction(e -> {
            try {
                userController.handleLogout(primaryStage);
            } catch (FileNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        });
        Label titleLabel = new Label("Book List");
        titleLabel.getStyleClass().add("text-header");

        TableView<Book> tableView = new TableView<>();
        TableColumn<Book, String> titleColumn = new TableColumn<>("Title");
        TableColumn<Book, String> authorColumn = new TableColumn<>("Author name:");
        TableColumn<Book, Year> yearColumn = new TableColumn<>("Year");
        TableColumn<Book, String> ISBNColumn = new TableColumn<>("ISBN");
        TableColumn<Book, String> categoryColumn = new TableColumn<>("Category");
        TableColumn<Book, Double> costColumn = new TableColumn<>("Cost");
        TableColumn<Book, Double> initialPriceColumn = new TableColumn<>("Initial price");
        TableColumn<Book, Double> sellingPriceColumn = new TableColumn<>("Actual price");
        TableColumn<Book, Integer> stockNoColumn = new TableColumn<>("Stock");
        TableColumn<Book, String> supplierColumn = new TableColumn<>("Supplier");
        TableColumn<Book, LocalDate> dateColumn = new TableColumn<>("Date");

        tableView.setId("tableView");

        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        titleColumn.setOnEditCommit(e -> e.getTableView().getItems().get(e.getTablePosition().getRow()).setTitle(e.getNewValue()));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        authorColumn.setOnEditCommit(e -> e.getTableView().getItems().get(e.getTablePosition().getRow()).setAuthor(e.getNewValue()));
        ISBNColumn.setCellValueFactory(new PropertyValueFactory<>("ISBN"));
        ISBNColumn.setOnEditCommit(e -> e.getTableView().getItems().get(e.getTablePosition().getRow()).setISBN(e.getNewValue()));
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("publishYear"));
        yearColumn.setOnEditCommit(e -> e.getTableView().getItems().get(e.getTablePosition().getRow()).setPublishYear(e.getNewValue()));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        categoryColumn.setOnEditCommit(e -> e.getTableView().getItems().get(e.getTablePosition().getRow()).setCategory(e.getNewValue()));
        costColumn.setCellValueFactory(new PropertyValueFactory<>("purchasedPrice"));
        costColumn.setOnEditCommit(e -> e.getTableView().getItems().get(e.getTablePosition().getRow()).setSellingPrice(e.getNewValue()));
        sellingPriceColumn.setCellValueFactory(new PropertyValueFactory<>("sellingPrice"));
        sellingPriceColumn.setOnEditCommit(e -> e.getTableView().getItems().get(e.getTablePosition().getRow()).setSellingPrice(e.getNewValue()));
        stockNoColumn.setCellValueFactory(new PropertyValueFactory<>("stockNo"));
        stockNoColumn.setOnEditCommit(e -> e.getTableView().getItems().get(e.getTablePosition().getRow()).setStockNo(e.getNewValue()));
        supplierColumn.setCellValueFactory(new PropertyValueFactory<>("supplierName"));
        supplierColumn.setOnEditCommit(e -> e.getTableView().getItems().get(e.getTablePosition().getRow()).setSupplierName(e.getNewValue()));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("purchasedDate"));
        initialPriceColumn.setCellValueFactory(new PropertyValueFactory<>("originalPrice"));
        initialPriceColumn.setOnEditCommit(e -> e.getTableView().getItems().get(e.getTablePosition().getRow()).setOriginalPrice(e.getNewValue()));

        authorColumn.setOnEditCommit(e -> e.getTableView().getItems().get(e.getTablePosition().getRow()).setAuthor(e.getNewValue()));

        tableView.getColumns().addAll(ISBNColumn, titleColumn, authorColumn, yearColumn, supplierColumn, categoryColumn, costColumn, initialPriceColumn, sellingPriceColumn, stockNoColumn, dateColumn);

        ObservableList<Book> bookData = FXCollections.observableArrayList(books);

        tableView.setItems(bookData);
        tableView.setEditable(true);

        TextField isbnField = new TextField();
        TextField titleField = new TextField();
        TextField firstNameField = new TextField();
        TextField middleNameField = new TextField();
        TextField lastNameField = new TextField();
        TextField categoryField = new TextField();
        TextField supplierField = new TextField();
        TextField yearField = new TextField();
        TextField purchasedPriceField = new TextField();
        TextField originalPriceField = new TextField();
        TextField sellingPriceField = new TextField();
        TextField stockField = new TextField();

        Button addBookButton = new Button("Add Book");
        addBookButton.setOnAction(e -> {
            try {
                addBookToTableView(tableView, books, isbnField, titleField, firstNameField, middleNameField, lastNameField, categoryField, supplierField, yearField, purchasedPriceField, originalPriceField, sellingPriceField, stockField);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            showAlert("Book " + titleField.getText() + " added successfully!");
        });
        VBox addBookForm = new VBox(10);
        addBookForm.getChildren().addAll(
                new Label("Add New Book"),
                new HBox(10, new Label("ISBN:"), isbnField),
                new HBox(10, new Label("Title:"), titleField),
                new HBox(10, new Label("Author first name:"), firstNameField),
                new HBox(10, new Label("Author middle name:"), middleNameField),
                new HBox(10, new Label("Author last name:"), lastNameField),
                new HBox(10, new Label("Category:"), categoryField),
                new HBox(10, new Label("Supplier:"), supplierField),
                new HBox(10, new Label("Year:"), yearField),
                new HBox(10, new Label("Purchased price:"), purchasedPriceField),
                new HBox(10, new Label("Original price:"), originalPriceField),
                new HBox(10, new Label("Selling price:"), sellingPriceField),
                new HBox(10, new Label("Stock number:"), stockField),
                addBookButton
                );

        dashboardLayout.getChildren().clear();
        dashboardLayout.getChildren().addAll(hbox, titleLabel, tableView, addBookForm);
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(dashboardLayout);

        Scene dashboardScene = new Scene(scrollPane, 1000, 600);
        dashboardScene.getStylesheets().add(UserDashboardView.class.getResource("styles.css").toExternalForm());
        primaryStage.setScene(dashboardScene);
    }
    private static void addBookToTableView(TableView<Book> tableView, List<Book> books, TextField isbnField, TextField titleField, TextField firstNameField, TextField middleNameField, TextField lastNameField, TextField categoryField, TextField supplierField, TextField yearField, TextField purchasedPriceField, TextField originalPriceField, TextField sellingPriceField, TextField stockField) throws IOException {
        String isbn = isbnField.getText();
        String title = titleField.getText();
        Category category = new Category(categoryField.getText());
        String supplier = supplierField.getText();
        Year publishYear = Year.parse(yearField.getText());
        double purchasedPrice = Double.parseDouble(purchasedPriceField.getText());
        double originalPrice = Double.parseDouble(originalPriceField.getText());
        double sellingPrice = Double.parseDouble(sellingPriceField.getText());
        Author author = new Author(firstNameField.getText(), middleNameField.getText(), lastNameField.getText());
        int stock = Integer.parseInt(stockField.getText());
        Book newBook = new Book(isbn, title, category, supplier, publishYear, purchasedPrice, originalPrice, sellingPrice, author, stock);
        books.add(newBook);
        tableView.getItems().add(newBook);
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("data/binaryFiles/books.bin"))) {
            oos.writeObject(books);
        } catch (IOException e) {
            e.printStackTrace();
        }
        isbnField.clear();
        titleField.clear();
        firstNameField.clear();
        middleNameField.clear();
        lastNameField.clear();
        categoryField.clear();
        supplierField.clear();
        yearField.clear();
        purchasedPriceField.clear();
        originalPriceField.clear();
        sellingPriceField.clear();
        stockField.clear();
    }
}