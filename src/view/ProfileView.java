package view;

import controller.UserDashboardController;
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
import model.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import static view.UserDashboardView.*;

public class ProfileView {
    private Stage primaryStage;
    static List<String> roles;
    public static void showProfileView(Stage primaryStage, Employee employee) {
        String illustrationPath;
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

        HBox hbox = new HBox(10); //spacing between buttons
        hbox.getChildren().addAll(btnProfile, btnBooks);
        if(roles.contains("Create Bill"))
            hbox.getChildren().add(btnBill);
        if(roles.contains("Add new books"))
            hbox.getChildren().add(btnInventory);
        hbox.getChildren().add(logoutButton);

        btnBooks.setOnAction(e -> BooksView.showBooksTable(primaryStage, books, employee));
        btnInventory.setOnAction(e -> AddBooksView.showBooksTable(primaryStage, books, employee));
        btnBill.setOnAction(e -> AddBillView.createBillTable(primaryStage, books, employee));

        dashboardLayout.setAlignment(Pos.TOP_CENTER);
        dashboardLayout.setPadding(new Insets(10, 10, 10, 10));

        VBox profileCard = new VBox(10);
        profileCard.setPadding(new Insets(20));
        profileCard.setAlignment(Pos.TOP_LEFT);
        profileCard.getStyleClass().add("profile-card");

        Text profileText;
        if (employee instanceof Librarian) {
            profileText = new Text("Librarian Profile");
            illustrationPath = "images/librarian_illustration.png";
        } else if (employee instanceof Manager) {
            profileText = new Text("Manager profile details");
            illustrationPath = "images/manager_illustration.png";
        } else {
            profileText = new Text("Unknown profile details");
            illustrationPath = "images/admin_illustration.png";
        }
        profileText.getStyleClass().add("text-header");
        Label nameLabel = new Label("Full name:  " + employee.getName() + " " + employee.getSurname());
        Label usernameLabel = new Label("Username: @" + employee.getUsername());
        Label emailLabel = new Label("Email: " + employee.getEmail());
        Label phoneLabel = new Label("Phone number: " + employee.getPhoneNumber());
        Label birthDateLabel = new Label("Date of birth: " + employee.getBirthDate());
        Label accessLabel = new Label("Access level: " + employee.getAccessLevel());
        Label contactLabel = new Label("Contact details");
        Label profileLabel = new Label("Profile details");
        profileCard.getChildren().addAll(profileLabel, nameLabel, usernameLabel, birthDateLabel, contactLabel, emailLabel, phoneLabel, accessLabel);

        FileInputStream input = null;
        try {
            input = new FileInputStream(illustrationPath);
        }catch (java.io.FileNotFoundException e)  {
            System.out.println("No such pic in Images");
        }
        assert input != null;
        ImageView image = new ImageView(new Image(input));
        image.setFitHeight(420);
        image.setFitWidth(560);

        GridPane mainGrid = new GridPane();
        mainGrid.getStyleClass().add("profile-container");
        mainGrid.setAlignment(javafx.geometry.Pos.CENTER);
        mainGrid.add(profileCard, 0, 0);
        mainGrid.add(image, 1, 0);

        UserDashboardController userController = new UserDashboardController();
        logoutButton.setOnAction(e -> {
            try {
                userController.handleLogout(primaryStage);
            } catch (FileNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        });

        dashboardLayout.getChildren().addAll(hbox, profileText, mainGrid);
        Scene dashboardScene = new Scene(dashboardLayout, 1000, 600);
        dashboardScene.getStylesheets().add(UserDashboardView.class.getResource("styles.css").toExternalForm());
        primaryStage.setScene(dashboardScene);
    }
}
