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
import model.Bill;
import model.BillUnit;
import model.Book;
import model.Employee;
import model.Librarian;
import model.Manager;
import model.Utility.FileReaderUtil;
import model.Utility.FileWriterUtil;
import java.util.Optional;
import java.util.stream.Collectors;


import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.Optional;

import static view.UserDashboardView.*;

public class AddBillView {
    static void createBillTable(Stage primaryStage, List<Book> books, Employee employee) {
        VBox dashboardLayout = new VBox(20);
        dashboardLayout.setAlignment(Pos.TOP_CENTER);
        dashboardLayout.setPadding(new Insets(10, 10, 10, 10));

        Button btnProfile = new Button("Profile");
        Button btnBooks = new Button();
        if(employee instanceof Librarian)
            btnBooks = new Button("Books");
        else if(employee instanceof Manager)
            btnBooks = new Button("Inventory");
        Button btnBill = new Button("Create bill");
        Button logoutButton = new Button("Logout");

        HBox hbox = new HBox(10); //spacing between buttons
        hbox.getChildren().addAll(btnProfile, btnBooks,btnBill, logoutButton);
        btnProfile.setOnAction(e -> {
            assert employee != null;
            ProfileView.showProfileView(primaryStage, employee);
        });
        if(employee instanceof Librarian)
            btnBooks.setOnAction(e -> BooksView.showBooksTable(primaryStage, books, employee));
        else if(employee instanceof Manager)
            btnBooks.setOnAction(e -> AddBooksView.showBooksTable(primaryStage, books, employee));
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
        searchField.setMaxWidth(600);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> tableView.setItems(UserDashboardController.filterBooks(bookData, newValue)));

        Label quantityLabel = new Label("Quantity:");
        Spinner<Integer> quantitySpinner = new Spinner<>(1, 100, 1);
        quantitySpinner.setMaxWidth(75);
        Button btnAddToBill = new Button("Add to Bill");
        btnAddToBill.setOnAction(e -> addToBill(tableView.getSelectionModel().getSelectedItem(), quantitySpinner.getValue(), employee));
        Button btnFinishBill = new Button("Finish Bill");
        HBox billOptions = new HBox(10);
        billOptions.getChildren().addAll(searchField, quantityLabel, quantitySpinner, btnAddToBill, btnFinishBill);
        dashboardLayout.getChildren().clear();
        dashboardLayout.getChildren().addAll(hbox, titleLabel, tableView, billOptions);

        Scene dashboardScene = new Scene(dashboardLayout, 1000, 600);
        dashboardScene.getStylesheets().add(UserDashboardView.class.getResource("styles.css").toExternalForm());
        primaryStage.setScene(dashboardScene);
    }

    private static void addToBill(Book selectedBook, int quantity, Employee employee) {
        if (selectedBook != null && quantity > 0) {
            try {
                BillUnit billUnit = new BillUnit(selectedBook, quantity);
                Bill bill = new Bill(new BillUnit[]{billUnit});

                billUnit.setAmount(quantity);

                Dialog<ButtonType> dialog = new Dialog<>();
                dialog.setTitle("Bill Details");

                TextArea textArea = new TextArea();
                textArea.setEditable(false);
                textArea.setWrapText(true);
                textArea.setText("Bill No: " + bill.getBillNo() + "\n" +
                        "Retailer name: " + employee.getName() + " " + employee.getSurname() + "\n" +
                        "Purchase Date: " + bill.getPurchaseDate() + "\n"
                );
                textArea.appendText("------------------------------------------------------------------------------\n");
                for (BillUnit unit : bill.getBillUnits()) {
                    textArea.appendText(
                            String.format("%-30s\n %-4dx $%-40.2f $%-40.2f\n",
                                    unit.getBook().getTitle(),
                                    unit.getAmount(),
                                    unit.getBook().getSellingPrice(),
                                    unit.getTotalCost())
                    );
                }

                dialog.getDialogPane().setContent(textArea);
                ButtonType addBillButtonType = new ButtonType("Print", ButtonBar.ButtonData.OK_DONE);
                ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
                dialog.getDialogPane().getButtonTypes().addAll(addBillButtonType, cancelButtonType);

                //show dialog and wait for an action
                Optional<ButtonType> result = dialog.showAndWait();

                if (result.isPresent() && result.get() == addBillButtonType) {
                    //save the bill to file
                    String fileName = "data/bills/" + bill.getBillNo() + ".txt";
                    try (FileOutputStream fos = new FileOutputStream(fileName);
                         ObjectOutputStream outputStream = new ObjectOutputStream(fos)) {
                        outputStream.writeObject(bill);
                    } catch (IOException e) {
                        System.out.println("IOException: " + e.getMessage());
                        e.printStackTrace();
                    }

                    //update stock numbers in the binary file
                    updateStockNumbersInBinaryFile(bill);
                } else {
                    System.out.println("Bill addition canceled.");
                }

            } catch (NumberFormatException ex) {
                showAlert("Please enter a valid quantity.");
            }
        } else if (selectedBook == null) {
            showAlert("Please select a book to add to the bill.");
        } else {
            showAlert("Please enter a valid quantity greater than 0.");
        }
    }

    private static void updateStockNumbersInBinaryFile(Bill bill) {
        List<Book> books = FileReaderUtil.readArrayListFromFile("data/binaryFiles/books.bin");

        for (BillUnit billUnit : bill.getBillUnits()) {
            Book billBook = billUnit.getBook();
            int amount = billUnit.getAmount();

            for (Book existingBook : books) {
                if (existingBook.getISBN().equals(billBook.getISBN())) {
                    //update the stock number in memory
                    int updatedStockNo = Math.max(0, existingBook.getStockNo() - amount);
                    existingBook.setStockNo(updatedStockNo);
                    break;
                }
            }
        }

        FileWriterUtil.writeArrayListToFile(books, "data/binaryFiles/books.bin");
    }
}