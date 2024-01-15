package main;

import javafx.application.Application;
import javafx.stage.Stage;
import model.*;
import model.Utility.FileReaderUtil;
import view.LoginFormView;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;



public class Main extends Application {
    public static void main(String[] args) {
        Application.launch(LoginFormView.class, args);

        ArrayList<Admin> admins = FileReaderUtil.readArrayListFromFile("data/binaryFiles/admins.bin");
        printAdmins(admins);

        //print categories
        CategoryList categoryList = new CategoryList();
        categoryList.printCategories();
        //print authors
        AuthorList authorList = new AuthorList();
        authorList.printAuthors();



        //initialize scanner for user input
        Scanner scanner = new Scanner(System.in);
        SearchBook searchBook = new SearchBook(scanner);
        //print books
        ArrayList<Book> books = new BookList().getReadBooks();
        //display search options
        searchBook.displaySearchOptions();
        //get user's search choice
        int searchChoice = scanner.nextInt();
        scanner.nextLine(); //consume the newline character
        //perform search based on user's choice
        List<Book> selectedBooks = searchBook.performSearch(searchChoice, books);
        if (!selectedBooks.isEmpty()) {
            for (Book selectedBook : selectedBooks) {
                BillUnit billUnit = new BillUnit(selectedBook, 1);
                Bill bill = new Bill(new BillUnit[]{billUnit});
                searchBook.saveBillToFile(bill);
            }
        } else {
            System.out.println("No books found matching the search criteria.");
        }
    }

    private static void printAdmins(List<Admin> admins) {
        System.out.println("ArrayList of admins from file: ");
        for (Admin admin : admins) {
            System.out.println("Username: " + admin.getUsername());
            System.out.println("Password: " + admin.getPassword());
            System.out.println();
        }
    }

    @Override
    public void start(Stage primaryStage) {
    }
}