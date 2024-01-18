package view;

import controller.UserDashboardController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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

public class AdminView {
    static List<Librarian> librarians = new LibrarianList().getReadLibrarians();
    static List<Manager> managers = new ManagerList().getReadManagers();
    public static void showUserDashboard(Stage primaryStage) {
        Button btnLibrarians = new Button("Librarians");
        Button btnManagers = new Button("Managers");
        Button btnManageRoles = new Button("User Roles");
        Button btnStatistics = new Button("Statistics");
        Button logoutButton = new Button("Logout");

        HBox hbox = new HBox(10);
        hbox.getChildren().addAll(btnLibrarians, btnManagers, btnManageRoles, btnStatistics, logoutButton);

        VBox dashboardLayout = new VBox(20);
        dashboardLayout.setAlignment(Pos.TOP_CENTER);
        dashboardLayout.setPadding(new Insets(10, 10, 10, 10));

        LocalDate currentDate = LocalDate.now();
        String weekday = currentDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String formattedDate = currentDate.format(formatter);

        Label welcomeLabel = new Label("Welcome admin");
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
        btnLibrarians.setOnAction(e -> LibrariansView.showLibrariansTable(primaryStage, librarians));
        btnManagers.setOnAction(e -> ManagersView.showManagersTable(primaryStage, managers));
        btnManageRoles.setOnAction(e -> ManageRolesView.showManageRoles(primaryStage, librarians, managers));
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
}
