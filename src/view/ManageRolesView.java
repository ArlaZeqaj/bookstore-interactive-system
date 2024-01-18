package view;

import controller.UserDashboardController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Librarian;
import model.Manager;
import model.Roles;
import model.User;

import java.io.IOException;
import java.util.List;

import static view.UserDashboardView.showAlert;

public class ManageRolesView {
   public static void showManageRoles(Stage primaryStage, List<Librarian> librarians, List<Manager> managers){
       VBox dashboardLayout = new VBox(20);
       dashboardLayout.setAlignment(Pos.TOP_CENTER);
       dashboardLayout.setPadding(new Insets(10, 10, 10, 10));

       Button btnLibrarians = new Button("Librarians");
       Button btnManagers = new Button("Managers");
       Button btnManageRoles = new Button("User Roles");
       Button btnStatistics = new Button("Statistics");
       Button logoutButton = new Button("Logout");

       HBox hbox = new HBox(10);
       hbox.getChildren().addAll(btnLibrarians, btnManagers, btnManageRoles, btnStatistics, logoutButton);

       UserDashboardController userController = new UserDashboardController();
       logoutButton.setOnAction(e -> {
           try {
               userController.handleLogout(primaryStage);
           } catch (IOException ex) {
               throw new RuntimeException(ex);
           }
       });

       btnLibrarians.setOnAction(e -> LibrariansView.showLibrariansTable(primaryStage, librarians));
       btnManagers.setOnAction(e -> ManagersView.showManagersTable(primaryStage, managers));

       Label titleLabel = new Label("Admin Roles Management");
       titleLabel.getStyleClass().add("text-header");

       VBox layout = new VBox(20);
       layout.setPadding(new Insets(10));

       ComboBox<String> userComboBox = new ComboBox<>();
       userComboBox.getItems().addAll("Librarian", "Manager");
       userComboBox.setPromptText("Select User");

       ComboBox<String> rolesComboBox = new ComboBox<>();
       rolesComboBox.getItems().addAll("Create Bill", "Add new books", "Edit books", "Update books", "View statistics");
       rolesComboBox.setPromptText("Select Role");

       Button addButton = new Button("Add Role");
       Button removeButton = new Button("Remove Role");

       HBox buttonsBox = new HBox(10);
       buttonsBox.getChildren().addAll(addButton, removeButton);

       layout.getChildren().addAll(titleLabel, userComboBox, rolesComboBox, buttonsBox);

       addButton.setOnAction(e -> {
           String selectedUser = userComboBox.getValue();
           String selectedRole = rolesComboBox.getValue();
           if (selectedUser != null && selectedRole != null) {
              if(!(Roles.roleExists(Roles.getLibrarianRoles(), selectedRole)) || !(Roles.roleExists(Roles.getManagerRoles(), selectedRole))){
                   if(selectedUser.equals("Librarian"))
                       Roles.getLibrarianRoles().add(selectedRole);
                   else if(selectedUser.equals("Manager"))
                       Roles.getManagerRoles().add(selectedRole);
                   showAlert("Role added successfully!");
              }
              else showAlert("This role already exists!");
           } else {
               showAlert("Please select a user and a role.");
           }
       });

       removeButton.setOnAction(e -> {
           String selectedUser = userComboBox.getValue();
           String selectedRole = rolesComboBox.getValue();
           if (selectedUser != null && selectedRole != null) {
               if(Roles.roleExists(Roles.getLibrarianRoles(), selectedRole) || Roles.roleExists(Roles.getManagerRoles(), selectedRole)){
                   if(selectedUser.equals("Librarian"))
                       Roles.getLibrarianRoles().remove(selectedRole);
                   else if(selectedUser.equals("Manager"))
                       Roles.getManagerRoles().remove(selectedRole);
                   showAlert("Role removed successfully!");
               }
               else showAlert("This role doesnt exist on this user!");
           } else {
               showAlert("Please select a user and a role.");
           }
       });

       dashboardLayout.getChildren().clear();
       dashboardLayout.getChildren().addAll(hbox, titleLabel, layout);
       ScrollPane scrollPane = new ScrollPane();
       scrollPane.setContent(dashboardLayout);

       Scene dashboardScene = new Scene(scrollPane, 1000, 600);
       dashboardScene.getStylesheets().add(UserDashboardView.class.getResource("styles.css").toExternalForm());
       primaryStage.setScene(dashboardScene);
   }
}
