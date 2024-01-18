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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.time.LocalDate;
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
                AdminView.showUserDashboard(primaryStage);
            }
        }
        Button btnProfile = new Button("Profile");
        Button btnBooks = new Button();
        if(currentUser instanceof Librarian)
            btnBooks = new Button("Books");
        else if(currentUser instanceof Manager)
            btnBooks = new Button("Inventory");
        Button btnBill = new Button("Create bill");
        Button logoutButton = new Button("Logout");

        HBox hbox = new HBox(10); //spacing between buttons
        hbox.getChildren().addAll(btnProfile, btnBooks, btnBill, logoutButton);

        btnProfile.setOnAction(e -> {
            assert employee != null;
            showProfileView(primaryStage, employee);
        });
        if(currentUser instanceof Librarian)
            btnBooks.setOnAction(e -> BooksView.showBooksTable(primaryStage, books, employee));
        else if(currentUser instanceof Manager)
            btnBooks.setOnAction(e -> AddBooksView.showBooksTable(primaryStage, books, employee));
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