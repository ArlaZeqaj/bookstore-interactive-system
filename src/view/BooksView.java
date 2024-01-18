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
import model.*;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.Year;
import java.util.List;

public class BooksView {
    static List<String> roles;
    static void showBooksTable(Stage primaryStage, List<Book> books, Employee employee) {
        if (employee instanceof Librarian) {
            roles = Roles.getLibrarianRoles();
        } else if (employee instanceof Manager) {
            roles = Roles.getManagerRoles();
        }
        VBox dashboardLayout = new VBox(20);
        dashboardLayout.setAlignment(Pos.TOP_CENTER);
        dashboardLayout.setPadding(new Insets(10, 10, 10, 10));

        Button btnBooks = new Button("Books");
        Button btnProfile = new Button("Profile");
        Button btnBill = new Button("Create bill");
        Button btnInventory = new Button("Inventory");
        Button logoutButton = new Button("Logout");

        HBox hbox = new HBox(10);
        hbox.getChildren().addAll(btnProfile, btnBooks);
        if(roles.contains("Create Bill"))
            hbox.getChildren().add(btnBill);
        if(roles.contains("Add new books"))
            hbox.getChildren().add(btnInventory);
        hbox.getChildren().add(logoutButton);
        btnProfile.setOnAction(e -> {
            assert employee != null;
            ProfileView.showProfileView(primaryStage, employee);
        });
        btnBooks.setOnAction(e -> BooksView.showBooksTable(primaryStage, books, employee));
        btnInventory.setOnAction(e -> AddBooksView.showBooksTable(primaryStage, books, employee));
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
        TableColumn<Book, String> authorColumn = new TableColumn<>("Author");
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
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        ISBNColumn.setCellValueFactory(new PropertyValueFactory<>("ISBN"));
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("publishYear"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        costColumn.setCellValueFactory(new PropertyValueFactory<>("purchasedPrice"));
        sellingPriceColumn.setCellValueFactory(new PropertyValueFactory<>("sellingPrice"));
        stockNoColumn.setCellValueFactory(new PropertyValueFactory<>("stockNo"));
        supplierColumn.setCellValueFactory(new PropertyValueFactory<>("supplierName"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("purchasedDate"));
        initialPriceColumn.setCellValueFactory(new PropertyValueFactory<>("originalPrice"));

        tableView.getColumns().addAll(ISBNColumn, titleColumn, authorColumn, yearColumn, supplierColumn, categoryColumn, costColumn, initialPriceColumn, sellingPriceColumn, stockNoColumn, dateColumn);

        ObservableList<Book> bookData = FXCollections.observableArrayList(books);

        tableView.setItems(bookData);

        TextField searchField = new TextField();
        searchField.setPromptText("Search by title, ISBN, author or category...");
        searchField.setMaxWidth(350);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            tableView.setItems(UserDashboardController.filterBooks(bookData, newValue));
        });

        dashboardLayout.getChildren().clear();
        dashboardLayout.getChildren().addAll(hbox, titleLabel, tableView, searchField);

        Scene dashboardScene = new Scene(dashboardLayout, 1000, 600);
        dashboardScene.getStylesheets().add(UserDashboardView.class.getResource("styles.css").toExternalForm());
        primaryStage.setScene(dashboardScene);
    }
}
