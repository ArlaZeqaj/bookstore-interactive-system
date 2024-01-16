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
import model.Book;

import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.Year;
import java.util.List;

import static view.UserDashboardView.*;

public class AddBillView {
    static void createBillTable(Stage primaryStage, List<Book> books) {
        VBox dashboardLayout = new VBox(20);
        dashboardLayout.setAlignment(Pos.TOP_CENTER);
        dashboardLayout.setPadding(new Insets(10, 10, 10, 10));

        Button btnProfile = new Button("Profile");
        Button btnBooks = new Button("Books");
        Button btnBill = new Button("Create bill");
        Button logoutButton = new Button("Logout");

        HBox hbox = new HBox(10); //spacing between buttons
        hbox.getChildren().addAll(btnProfile, btnBooks,btnBill, logoutButton);
        btnBooks.setOnAction(e -> showBooksTable(primaryStage, books));
        btnBill.setOnAction(e -> showAlert("Settings button clicked"));
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
        TableColumn<Book, String> authorColumn = new TableColumn<>("Author");
        TableColumn<Book, Year> yearColumn = new TableColumn<>("Year");
        TableColumn<Book, String> ISBNColumn = new TableColumn<>("ISBN");
        TableColumn<Book, String> categoryColumn = new TableColumn<>("Category");
        TableColumn<Book, Double> costColumn = new TableColumn<>("Cost");
        TableColumn<Book, Double> sellingPriceColumn = new TableColumn<>("Price");
        TableColumn<Book, Integer> stockNoColumn = new TableColumn<>("Stock");
        TableColumn<Book, String> supplierColumn = new TableColumn<>("Supplier");
        TableColumn<Book, LocalDate> dateColumn = new TableColumn<>("Date");

        tableView.setId("tableView");

        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        ISBNColumn.setCellValueFactory(new PropertyValueFactory<>("ISBN"));
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("publishYear"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        costColumn.setCellValueFactory(new PropertyValueFactory<>("purchasedPrice"));
        sellingPriceColumn.setCellValueFactory(new PropertyValueFactory<>("sellingPrice"));
        stockNoColumn.setCellValueFactory(new PropertyValueFactory<>("stockNo"));
        supplierColumn.setCellValueFactory(new PropertyValueFactory<>("supplierName"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("purchasedDate"));

        tableView.getColumns().addAll(ISBNColumn, titleColumn, authorColumn, yearColumn, supplierColumn, categoryColumn, costColumn, sellingPriceColumn, stockNoColumn, dateColumn);

        ObservableList<Book> bookData = FXCollections.observableArrayList(books);

        tableView.setItems(bookData);

        TextField searchField = new TextField();
        searchField.setPromptText("Search by title, ISBN, author or category...");
        searchField.setMaxWidth(600);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> tableView.setItems(filterBooks(bookData, newValue)));

        Label quantityLabel = new Label("Quantity:");
        Spinner<Integer> quantitySpinner = new Spinner<>(1, 100, 1);
        quantitySpinner.setMaxWidth(75);
        Button btnAddToBill = new Button("Add to Bill");
        btnAddToBill.setOnAction(e -> addToBill(tableView.getSelectionModel().getSelectedItem(), quantitySpinner.getValue()));
        Button btnFinishBill = new Button("Finish Bill");
        HBox billOptions = new HBox(10);
        billOptions.getChildren().addAll(searchField, quantityLabel, quantitySpinner, btnAddToBill, btnFinishBill);
        dashboardLayout.getChildren().clear();
        dashboardLayout.getChildren().addAll(hbox, titleLabel, tableView, billOptions);

        Scene dashboardScene = new Scene(dashboardLayout, 1000, 600);
        dashboardScene.getStylesheets().add(UserDashboardView.class.getResource("styles.css").toExternalForm());
        primaryStage.setScene(dashboardScene);
    }
    private static void addToBill(Book selectedBook, int quantity){
        if (selectedBook != null) {
            try {
                showAlert("Book added to the bill: " + selectedBook.getTitle() + ", Quantity: " + quantity);
            } catch (NumberFormatException ex) {
                showAlert("Please enter a valid quantity.");
            }
        } else {
            showAlert("Please select a book to add to the bill.");
        }
    }
}