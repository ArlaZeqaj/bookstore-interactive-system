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
import model.AccessLevel;
import model.Manager;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.time.LocalDate;
import java.util.List;

import static view.AdminView.librarians;
import static view.UserDashboardView.showAlert;

public class ManagersView {

    public static void showManagersTable(Stage primaryStage, List<Manager> managers) {
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

        Label titleLabel = new Label("Manager List");
        titleLabel.getStyleClass().add("text-header");

        TableView<Manager> tableView = new TableView<>();
        TableColumn<Manager, String> nameColumn = new TableColumn<>("Name");
        TableColumn<Manager, String> surnameColumn = new TableColumn<>("Surname");
        TableColumn<Manager, LocalDate> birthDateColumn = new TableColumn<>("Birth Date");
        TableColumn<Manager, String> phoneNumberColumn = new TableColumn<>("Phone Number");
        TableColumn<Manager, String> emailColumn = new TableColumn<>("Email");
        TableColumn<Manager, Double> salaryColumn = new TableColumn<>("Salary");
        TableColumn<Manager, AccessLevel> accessLevelColumn = new TableColumn<>("Access Level");

        tableView.setId("tableView");

        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        surnameColumn.setCellValueFactory(new PropertyValueFactory<>("surname"));
        birthDateColumn.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
        phoneNumberColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        salaryColumn.setCellValueFactory(new PropertyValueFactory<>("salary"));
        accessLevelColumn.setCellValueFactory(new PropertyValueFactory<>("accessLevel"));

        tableView.getColumns().addAll(nameColumn, surnameColumn, birthDateColumn, phoneNumberColumn, emailColumn, salaryColumn, accessLevelColumn);

        ObservableList<Manager> managerData = FXCollections.observableArrayList(managers);

        tableView.setItems(managerData);
        tableView.setEditable(true);

        TextField nameField = new TextField();
        TextField surnameField = new TextField();
        DatePicker birthDateField = new DatePicker();
        TextField phoneNumberField = new TextField();
        TextField salaryField = new TextField();
        ComboBox<AccessLevel> accessLevelComboBox = new ComboBox<>(FXCollections.observableArrayList(AccessLevel.values()));

        Button addEmployeeButton = new Button("Add Employee");
        addEmployeeButton.setOnAction(e -> {
            try {
                addEmployeeToTableView(tableView, managers, nameField, surnameField, birthDateField, phoneNumberField, salaryField, accessLevelComboBox);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            showAlert("Manager " + nameField.getText() + " added successfully!");
        });

        VBox addEmployeeForm = new VBox(10);
        addEmployeeForm.getChildren().addAll(
                new Label("Add New Manager"),
                new HBox(10, new Label("Name:"), nameField),
                new HBox(10, new Label("Surname:"), surnameField),
                new HBox(10, new Label("Birth Date:"), birthDateField),
                new HBox(10, new Label("Phone Number:"), phoneNumberField),
                new HBox(10, new Label("Salary:"), salaryField),
                new HBox(10, new Label("Access Level:"), accessLevelComboBox),
                addEmployeeButton
        );

        dashboardLayout.getChildren().clear();
        dashboardLayout.getChildren().addAll(hbox, titleLabel, tableView, addEmployeeForm);
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(dashboardLayout);

        Scene dashboardScene = new Scene(scrollPane, 1000, 600);
        dashboardScene.getStylesheets().add(UserDashboardView.class.getResource("styles.css").toExternalForm());
        primaryStage.setScene(dashboardScene);
    }

    private static void addEmployeeToTableView(TableView<Manager> tableView, List<Manager> managers, TextField nameField, TextField surnameField, DatePicker birthDateField, TextField phoneNumberField, TextField salaryField, ComboBox<AccessLevel> accessLevelComboBox) throws IOException {
        String name = nameField.getText();
        String surname = surnameField.getText();
        LocalDate birthDate = birthDateField.getValue();
        String phoneNumber = phoneNumberField.getText();
        double salary = Double.parseDouble(salaryField.getText());
        AccessLevel accessLevel = accessLevelComboBox.getValue();

        Manager newEmployee = new Manager(name, surname, birthDate, phoneNumber, salary, "password");

        managers.add(newEmployee);
        tableView.getItems().add(newEmployee);

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("data/binaryFiles/librarians.bin"))) {
            oos.writeObject(managers);
        } catch (IOException e) {
            e.printStackTrace();
        }

        nameField.clear();
        surnameField.clear();
        birthDateField.getEditor().clear();
        phoneNumberField.clear();
        salaryField.clear();
        accessLevelComboBox.getSelectionModel().clearSelection();
    }
}
