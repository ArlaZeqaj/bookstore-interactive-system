package view;

import controller.LoginFormController;
import controller.UserDashboardController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import model.*;
import model.Utility.FileReaderUtil;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static view.ProfileView.showProfileView;

public class UserDashboardView {
    public UserDashboardView() {
    }
    private final LoginFormController loginFormController = new LoginFormController();
    User currentUser = loginFormController.getCurrentUser();
    static List<Book> books = new BookList().getReadBooks();
    static List<String> roles;
    private static boolean lowStockAlertShown = false;

    public static void showUserDashboard(Stage primaryStage, User currentUser) {
        Employee employee;
        if (currentUser instanceof Librarian) {
            employee = (Librarian) currentUser;
            roles = Roles.getLibrarianRoles();
        } else if (currentUser instanceof Manager) {
            employee = (Manager) currentUser;
            roles = Roles.getManagerRoles();
            // Call checkLowStockBooks only if the alert hasn't been shown before
            if (!lowStockAlertShown) {
                TextArea lowStockTextArea = new TextArea();
                checkLowStockBooks(primaryStage, employee, lowStockTextArea);
                // Set the flag to true after showing the alert
                lowStockAlertShown = true;
            }
        } else {
            employee = null;
            if (currentUser instanceof Admin) {
                AdminView.showUserDashboard(primaryStage);
            }
        }

        Button btnProfile = new Button("Profile");
        Button btnBooks = new Button("Books");
        Button btnBill = new Button("Create bill");
        Button btnInventory = new Button("Inventory");
        Button logoutButton = new Button("Logout");

        HBox hbox = new HBox(10); //spacing between buttons
        hbox.getChildren().addAll(btnProfile, btnBooks);
        if(roles.contains("Create Bill"))
            hbox.getChildren().add(btnBill);
        if(roles.contains("Add new books"))
            hbox.getChildren().add(btnInventory);
        hbox.getChildren().add(logoutButton);

        btnProfile.setOnAction(e -> {
            assert employee != null;
            showProfileView(primaryStage, employee);
        });
        btnBooks.setOnAction(e -> BooksView.showBooksTable(primaryStage, books, employee));
        btnInventory.setOnAction(e -> AddBooksView.showBooksTable(primaryStage, books, employee));
        btnBill.setOnAction(e -> AddBillView.createBillTable(primaryStage, books, employee));
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

    public static void checkLowStockBooks(Stage primaryStage, User currentUser, TextArea lowStockTextArea) {
        if (currentUser instanceof Employee) {
            Employee employee = (Employee) currentUser;
            List<Book> books = FileReaderUtil.readArrayListFromFile("data/binaryFiles/books.bin");

            StringBuilder lowStockBooksInfo = new StringBuilder();

            boolean showAlert = true; // Flag to track whether to show the alert

            for (Book book : books) {
                System.out.println("Checking book: " + book.getTitle()); // Log statement

                if (book.getStockNo() <= 5) {
                    // Append book information to the StringBuilder
                    lowStockBooksInfo.append("Book Title: ").append(book.getTitle()).append("\n")
                            .append("Author: ").append(book.getAuthor().toString()).append("\n")
                            .append("Stock No: ").append(book.getStockNo()).append("\n\n");
                }
            }

            // Show low stock books in the TextArea
            lowStockTextArea.setText(lowStockBooksInfo.toString());

            // Display an alert if there are low stock books and the showAlert flag is true
            if (lowStockBooksInfo.length() > 0 && showAlert) {
                showAlertForLowStockBooks(primaryStage, employee, lowStockBooksInfo.toString());

                // Set the showAlert flag to false after showing the alert
                showAlert = false;
            }
        }
    }

    private static void showAlertForLowStockBooks(Stage primaryStage, Employee employee, String booksInfo) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Low Stock Alert");
        alert.setHeaderText("Books with Low Stock");
        alert.setContentText(booksInfo);

        ButtonType OKButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        ButtonType closeButton = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(OKButton, closeButton);

        // Handle button click event
        Optional<ButtonType> result = alert.showAndWait();

        // Handle the result
        if (result.isPresent() && result.get() == OKButton) {
            System.out.println("OK Button Clicked");
        } else {
            System.out.println("Close Button Clicked");
        }
    }

    static void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("User Dashboard");
        alert.setHeaderText(null);
        alert.setContentText(message);
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setId("custom-alert");
        dialogPane.getStylesheets().add(UserDashboardView.class.getResource("styles.css").toExternalForm());
        alert.showAndWait();
    }
}
