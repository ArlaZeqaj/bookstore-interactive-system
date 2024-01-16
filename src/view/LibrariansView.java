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
import model.Librarian;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.time.LocalDate;
import java.util.List;

import static view.AdminView.managers;
import static view.UserDashboardView.showAlert;

public class LibrariansView {

    public static void showLibrariansTable(Stage primaryStage, List<Librarian> librarians) {
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
        btnManagers.setOnAction(e -> ManagersView.showManagersTable(primaryStage, managers));

        Label titleLabel = new Label("Librarian List");
        titleLabel.getStyleClass().add("text-header");

        TableView<Librarian> tableView = new TableView<>();
        TableColumn<Librarian, String> nameColumn = new TableColumn<>("Name");
        TableColumn<Librarian, String> surnameColumn = new TableColumn<>("Surname");
        TableColumn<Librarian, LocalDate> birthDateColumn = new TableColumn<>("Birth Date");
        TableColumn<Librarian, String> phoneNumberColumn = new TableColumn<>("Phone Number");
        TableColumn<Librarian, String> emailColumn = new TableColumn<>("Email");
        TableColumn<Librarian, Double> salaryColumn = new TableColumn<>("Salary");
        TableColumn<Librarian, AccessLevel> accessLevelColumn = new TableColumn<>("Access Level");

        tableView.setId("tableView");

        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        surnameColumn.setCellValueFactory(new PropertyValueFactory<>("surname"));
        birthDateColumn.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
        phoneNumberColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        salaryColumn.setCellValueFactory(new PropertyValueFactory<>("salary"));
        accessLevelColumn.setCellValueFactory(new PropertyValueFactory<>("accessLevel"));

        tableView.getColumns().addAll(nameColumn, surnameColumn, birthDateColumn, phoneNumberColumn, emailColumn, salaryColumn, accessLevelColumn);

        ObservableList<Librarian> librarianData = FXCollections.observableArrayList(librarians);

        tableView.setItems(librarianData);
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
                addEmployeeToTableView(tableView, librarians, nameField, surnameField, birthDateField, phoneNumberField, salaryField, accessLevelComboBox);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            showAlert("Librarian " + nameField.getText() + " added successfully!");
        });

        VBox addEmployeeForm = new VBox(10);
        addEmployeeForm.getChildren().addAll(
                new Label("Add New Librarian"),
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

    private static void addEmployeeToTableView(TableView<Librarian> tableView, List<Librarian> librarians, TextField nameField, TextField surnameField, DatePicker birthDateField, TextField phoneNumberField, TextField salaryField, ComboBox<AccessLevel> accessLevelComboBox) throws IOException {
        String name = nameField.getText();
        String surname = surnameField.getText();
        LocalDate birthDate = birthDateField.getValue();
        String phoneNumber = phoneNumberField.getText();
        double salary = Double.parseDouble(salaryField.getText());
        AccessLevel accessLevel = accessLevelComboBox.getValue();

        Librarian newEmployee = new Librarian(name, surname, birthDate, phoneNumber, salary, "password");

        librarians.add(newEmployee);
        tableView.getItems().add(newEmployee);

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("data/binaryFiles/librarians.bin"))) {
            oos.writeObject(librarians);
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
