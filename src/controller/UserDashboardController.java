package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Book;
import view.LoginFormView;
import view.UserDashboardView;

import java.io.FileNotFoundException;

public class UserDashboardController {
    public void handleLogout(Stage primaryStage) throws FileNotFoundException {
        LoginFormController controller = new LoginFormController(primaryStage);
        LoginFormView loginView = new LoginFormView();
        Scene scene = loginView.getLoginScene(controller);
        scene.getStylesheets().add(UserDashboardView.class.getResource("styles.css").toExternalForm());
        primaryStage.setScene(scene);
    }
    public static ObservableList<Book> filterBooks(ObservableList<Book> originalList, String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            return originalList;
        }
        String lowerCaseFilter = searchText.toLowerCase();

        ObservableList<Book> filteredList = FXCollections.observableArrayList();

        for (Book book : originalList) {
            if (book.getTitle().toLowerCase().contains(lowerCaseFilter) ||
                    String.valueOf(book.getISBN()).contains(lowerCaseFilter) ||
                    book.getAuthor().toString().toLowerCase().contains(lowerCaseFilter) ||
                    book.getCategory().toLowerCase().contains(lowerCaseFilter)) {
                filteredList.add(book);
            }
        }
        return filteredList;
    }
}
