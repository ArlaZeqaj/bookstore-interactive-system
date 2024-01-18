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
import model.*;
import model.Utility.FileReaderUtil;
import model.Utility.FileWriterUtil;

import java.io.*;
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
    static List<String> roles;
    private static Bill bill;  //declare as instance variable
    private static Employee employee;  //declare as instance variable
    static void createBillTable(Stage primaryStage, List<Book> books, Employee employee) {
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
        btnProfile.setOnAction(e -> {
            assert employee != null;
            ProfileView.showProfileView(primaryStage, employee);
        });
        btnBooks.setOnAction(e -> BooksView.showBooksTable(primaryStage, books, employee));
        btnInventory.setOnAction(e -> AddBooksView.showBooksTable(primaryStage, books, employee));
        btnBill.setOnAction(e -> AddBillView.createBillTable(primaryStage, books, employee));
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
        btnFinishBill.setOnAction(e -> finishBill());
        HBox billOptions = new HBox(10);
        billOptions.getChildren().addAll(searchField, quantityLabel, quantitySpinner, btnAddToBill, btnFinishBill);
        dashboardLayout.getChildren().clear();
        dashboardLayout.getChildren().addAll(hbox, titleLabel, tableView, billOptions);

        Scene dashboardScene = new Scene(dashboardLayout, 1000, 600);
        dashboardScene.getStylesheets().add(UserDashboardView.class.getResource("styles.css").toExternalForm());
        primaryStage.setScene(dashboardScene);
    }

    private static final String TEMPORARY_BILLS_DIR = "data/temporarybills/";
    private static final String BILLS_DIR = "data/bills/";

    private static void addToBill(Book selectedBook, int quantity, Employee employee) {
        if (selectedBook != null && quantity > 0) {
            try {
                BillUnit billUnit = new BillUnit(selectedBook, quantity);
                bill = new Bill(new BillUnit[]{billUnit});  // Set instance variable 'bill'
                AddBillView.employee = employee;  // Set instance variable 'employee'

                billUnit.setAmount(quantity);

                Dialog<ButtonType> dialog = new Dialog<>();
                dialog.setTitle("Bill Details");

                TextArea textArea = new TextArea();
                textArea.setEditable(false);
                textArea.setWrapText(true);
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
                textArea.appendText("------------------------------------------------------------------------------\n");

                dialog.getDialogPane().setContent(textArea);
                ButtonType addBillButtonType = new ButtonType("Continue", ButtonBar.ButtonData.OK_DONE);
                ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
                dialog.getDialogPane().getButtonTypes().addAll(addBillButtonType, cancelButtonType);

                // Show dialog and wait for an action
                Optional<ButtonType> result = dialog.showAndWait();

                if (result.isPresent() && result.get() == addBillButtonType) {
                    // Save the bill to a temporary text file
                    createTemporaryBillsDirIfNotExists();
                    String fileName = TEMPORARY_BILLS_DIR + "/" + bill.getBillNo() + ".txt";
                    saveBillToFile(bill, fileName, employee);

                    // Update stock numbers in the binary file
                    updateStockNumbers(bill);
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

    private static void createTemporaryBillsDirIfNotExists() {
        File directory = new File(TEMPORARY_BILLS_DIR);
        if (!directory.exists()) {
            directory.mkdir();
        }
    }

    private static void saveBillToFile(Bill bill, String fileName, Employee employee) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (BillUnit unit : bill.getBillUnits()) {
                writer.write(
                        String.format("%-30s\n %-4dx $%-40.2f $%-40.2f\n",
                                unit.getBook().getTitle(),
                                unit.getAmount(),
                                unit.getBook().getSellingPrice(),
                                unit.getTotalCost())
                );
            }
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static String displayTemporaryBillsContent(File[] temporaryBills, Bill bill, Employee employee) {
        StringBuilder content = new StringBuilder();

        // Append the bill information once at the top
        content.append("Bill No: ").append(bill.getBillNo()).append("\n")
                .append("Retailer name: ").append(employee.getName()).append(" ").append(employee.getSurname()).append("\n")
                .append("Purchase Date: ").append(bill.getPurchaseDate()).append("\n")
                .append("------------------------------------------------------------------------------\n");

        double totalCost = 0;

        for (File temporaryBill : temporaryBills) {
            // Read content from each temporary bill and append it to the consolidatedContent
            try (BufferedReader reader = new BufferedReader(new FileReader(temporaryBill))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");

                    // Calculate the total cost by checking for the line that contains the individual unit's total cost
                    if (line.trim().matches(".*\\$\\d+\\.\\d{2}.*")) {
                        totalCost += Double.parseDouble(line.substring(line.lastIndexOf('$') + 1).trim());
                    }
                }
                content.append("------------------------------------------------------------------------------\n");
            } catch (IOException e) {
                System.out.println("IOException: " + e.getMessage());
                e.printStackTrace();
            }
        }

        // Display the total cost
        content.append("Total Cost: $").append(String.format("%.2f", totalCost)).append("\n\n");
        content.append("Thanks for choosing our bookstore. Happy reading!").append("\n");
        content.append("Returns accepted within 14 days with receipt. See our return policy on our website.").append("\n");

        return content.toString();
    }

    private static void finishBill() {
        File temporaryBillsDir = new File(TEMPORARY_BILLS_DIR);

        if (temporaryBillsDir.exists() && temporaryBillsDir.isDirectory()) {
            File[] temporaryBills = temporaryBillsDir.listFiles();
            if (temporaryBills != null && temporaryBills.length > 0) {
                // Save the content to a text file
                String content = displayTemporaryBillsContent(temporaryBills, bill, employee);
                String finishBillFileName = BILLS_DIR + bill.getBillNo() + ".txt";
                saveContentToFile(content, finishBillFileName);

                // Display the content in a dialog
                displayContentDialog(content);

                //printTotalCostForBill(bill.getBillNo());

                // Delete the temporary bill files
                deleteTemporaryBillFiles(temporaryBills);
            } else {
                System.out.println("No temporary bills to display.");
            }
        } else {
            System.out.println("Temporary bills directory does not exist or is not a directory.");
        }
    }

    private static void saveContentToFile(String content, String fileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write(content);
            System.out.println("Content saved to file: " + fileName);
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void displayContentDialog(String content) {
        // Create a dialog to display the content
        Dialog<Void> displayDialog = new Dialog<>();
        displayDialog.setTitle("Print Bill");

        // Create a TextArea to show the content
        TextArea textArea = new TextArea(content);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        // Set the content of the dialog
        displayDialog.getDialogPane().setContent(textArea);

        // Add "Print" and "Close" buttons to the dialog
        ButtonType printButtonType = new ButtonType("Print", ButtonBar.ButtonData.OK_DONE);
        ButtonType closeButtonType = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
        displayDialog.getDialogPane().getButtonTypes().addAll(printButtonType, closeButtonType);

        // Show the dialog and wait for an action
        Optional<Void> result = displayDialog.showAndWait();

        // Handle the selected button
        if (result.isPresent()) {
            System.out.println("Close button clicked");
        } else {
            // Save content to a file when "Print" button is clicked
            saveContentToFile(content, "printed_bill.txt");
            System.out.println("Content saved to file: printed_bill.txt");
        }
    }

    private static void deleteTemporaryBillFiles(File[] temporaryBills) {
        for (File temporaryBill : temporaryBills) {
            // Delete the temporary bill file after consolidation
            if (!temporaryBill.delete()) {
                System.out.println("Failed to delete temporary bill file: " + temporaryBill.getAbsolutePath());
            }
        }
    }


    private static void updateStockNumbers(Bill bill) {
        List<Book> books = FileReaderUtil.readArrayListFromFile("data/binaryFiles/books.bin");

        for (BillUnit billUnit : bill.getBillUnits()) {
            Book billBook = billUnit.getBook();
            int amount = billUnit.getAmount();

            for (Book existingBook : books) {
                if (existingBook.getISBN().equals(billBook.getISBN())) {
                    //update the stock number in memory
                    int updatedStockNo = Math.max(0, existingBook.getStockNo() - amount); //cannot go below 0
                    existingBook.setStockNo(updatedStockNo);
                    break;
                }
            }
        }
        FileWriterUtil.writeArrayListToFile(books, "data/binaryFiles/books.bin");
    }
}

