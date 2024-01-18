package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Employee;
import controller.UserDashboardController;
import model.Librarian;
import model.Manager;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class StatisticsView {

    public static void showStatisticsView(Stage primaryStage, Employee employee) {
        String illustrationPath;
        Button btnProfile = new Button("Profile");
        Button btnBooks = new Button();
        if (employee instanceof Librarian)
            btnBooks = new Button("Books");
        else if (employee instanceof Manager)
            btnBooks = new Button("Inventory");
        Button btnBill = new Button("Create bill");
        Button btnStatistics = new Button("Statistics");
        Button logoutButton = new Button("Logout");

        HBox hbox = new HBox(10); // spacing between buttons
        hbox.getChildren().addAll(btnProfile, btnBooks, btnBill, btnStatistics, logoutButton);

        if (employee instanceof Librarian)
            btnBooks.setOnAction(e -> BooksView.showBooksTable(primaryStage, UserDashboardView.books, employee));
        else if (employee instanceof Manager)
            btnBooks.setOnAction(e -> AddBooksView.showBooksTable(primaryStage, UserDashboardView.books, employee));

        VBox dashboardLayout = new VBox(20);
        dashboardLayout.setPadding(new Insets(10, 10, 10, 10));

        // Display statistics directly on the dashboard
        btnStatistics.setOnAction(e -> showStatisticsViewOnDashboard(primaryStage, employee));

        btnBill.setOnAction(e -> AddBillView.createBillTable(primaryStage, UserDashboardView.books, employee));

        // Assuming these are defined elsewhere in your code
        Text profileText = new Text("Statistics Text");
        GridPane mainGrid = new GridPane();

        dashboardLayout.getChildren().addAll(hbox, profileText, mainGrid);
        Scene dashboardScene = new Scene(dashboardLayout, 1000, 600);
        dashboardScene.getStylesheets().add(UserDashboardView.class.getResource("styles.css").toExternalForm());
        primaryStage.setScene(dashboardScene);
    }

    private static void showStatisticsViewOnDashboard(Stage primaryStage, Employee employee) {
        // Implement the logic to calculate and display statistics directly on the dashboard
        // For now, let's just show a placeholder message

        Label placeholderLabel = new Label("Actual statistics calculation and display logic goes here.");

        // Replace this label with your actual statistics data and formatting
        // For example, you can create additional labels to display different statistics
        // based on your application's data model.

        VBox statisticsBox = new VBox(10);
        statisticsBox.getChildren().addAll(placeholderLabel);

        // Replace the existing statistics label in the profileCard with the actual statistics data
        VBox profileCard = (VBox) primaryStage.getScene().getRoot().lookup("#profileCard");
        profileCard.getChildren().remove(1); // Remove existing statistics label
        profileCard.getChildren().add(statisticsBox);
    }
}
