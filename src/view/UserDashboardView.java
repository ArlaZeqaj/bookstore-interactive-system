package view;

import controller.LoginFormController;
import controller.UserDashboardController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import model.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

import static view.ProfileView.showProfileView;

public class UserDashboardView {
    public UserDashboardView() {
    }
    private final LoginFormController loginFormController = new LoginFormController();
    User currentUser = loginFormController.getCurrentUser();
    static List<Book> books = new BookList().getReadBooks();

    public static void showUserDashboard(Stage primaryStage, User currentUser) {
        Employee employee;
        if (currentUser instanceof Librarian) {
            employee = (Librarian) currentUser;
        } else if (currentUser instanceof Manager) {
            employee = (Manager) currentUser;
        } else {
            employee = null;
            if (currentUser instanceof Admin) {
                UserDashboardView.showUserDashboard(primaryStage, (Admin) currentUser);
            }
        }

        Button btnProfile = new Button("Profile");
        Button btnBooks = new Button("Books");
        Button btnBill = new Button("Create bill");
        Button logoutButton = new Button("Logout");

        HBox hbox = new HBox(10); //spacing between buttons
        hbox.getChildren().addAll(btnProfile, btnBooks,btnBill, logoutButton);

        btnProfile.setOnAction(e -> {
            assert employee != null;
            showProfileView(primaryStage, employee);
        });
        btnBooks.setOnAction(e -> showBooksTable(primaryStage, books));
        btnBill.setOnAction(e -> AddBillView.createBillTable(primaryStage, books));
        VBox dashboardLayout = new VBox(20);
        dashboardLayout.setAlignment(Pos.TOP_CENTER);
        dashboardLayout.setPadding(new Insets(10, 10, 10, 10));

        LocalDate currentDate = LocalDate.now();
        String weekday = currentDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String formattedDate = currentDate.format(formatter);

        assert employee != null;
        Label welcomeLabel = new Label("Welcome " + employee.getName() + " " + employee.getSurname());
        welcomeLabel.getStyleClass().add("text-header");
        Label dateLabel = new Label("Today is " + weekday + ", " + formattedDate);
        dateLabel.setFont(Font.font(40));

        FileInputStream input = null;
        try {
            input = new FileInputStream("images/welcome_illustration.png");
        }catch (java.io.FileNotFoundException e)  {
            System.out.println("No such pic in Images");
        }
        assert input != null;
        ImageView image = new ImageView(new Image(input));
        image.setFitHeight(450);
        image.setFitWidth(390);

        UserDashboardController userController = new UserDashboardController();
        logoutButton.setOnAction(e -> {
            try {
                userController.handleLogout(primaryStage);
            } catch (FileNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        });

        dashboardLayout.getChildren().addAll(hbox, welcomeLabel, dateLabel, image);
        Scene dashboardScene = new Scene(dashboardLayout, 1000, 600);
        dashboardScene.getStylesheets().add(UserDashboardView.class.getResource("styles.css").toExternalForm());
        primaryStage.setScene(dashboardScene);
    }
    static void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("User Dashboard");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    static void showBooksTable(Stage primaryStage, List<Book> books) {
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
        searchField.setMaxWidth(350);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            tableView.setItems(filterBooks(bookData, newValue));
        });

        dashboardLayout.getChildren().clear();
        dashboardLayout.getChildren().addAll(hbox, titleLabel, tableView, searchField);

        Scene dashboardScene = new Scene(dashboardLayout, 1000, 600);
        dashboardScene.getStylesheets().add(UserDashboardView.class.getResource("styles.css").toExternalForm());
        primaryStage.setScene(dashboardScene);
    }
    static ObservableList<Book> filterBooks(ObservableList<Book> originalList, String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            return originalList;
        }
        String lowerCaseFilter = searchText.toLowerCase();

        ObservableList<Book> filteredList = FXCollections.observableArrayList();

        for (Book book : originalList) {
            if (book.getTitle().toLowerCase().contains(lowerCaseFilter) ||
                    String.valueOf(book.getISBN()).contains(lowerCaseFilter) ||
                    book.getAuthor().toString().toLowerCase().contains(lowerCaseFilter) ||
                    book.getCategory().getName().toLowerCase().contains(lowerCaseFilter)) {
                filteredList.add(book);
            }
        }
        return filteredList;
    }
}
