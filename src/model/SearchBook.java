package model;

import model.Utility.FileReaderUtil;
import model.Utility.FileWriterUtil;
import model.Utility.ValidationUtil;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class SearchBook {
    private final Scanner scanner;

    public SearchBook(Scanner scanner) {
        this.scanner = scanner;
    }

    public void displaySearchOptions() {
        System.out.println("Choose the method to search for a book:");
        System.out.println("1. Search by title");
        System.out.println("2. Search by ISBN");
        System.out.println("3. Search by author");
        System.out.println("4. Search by category");
    }

    public List<Book> performSearch(int searchChoice, List<Book> books) {
        List<Book> selectedBooks = new ArrayList<>();

        switch (searchChoice) {
            case 1:
                System.out.println("Enter part of the book title you want to search:");
                String searchTitle = scanner.nextLine().toLowerCase();
                selectedBooks = findBookByTitle(searchTitle, books);
                break;
            case 2:
                System.out.println("Enter the ISBN of the book:");
                String searchISBN = scanner.nextLine();
                selectedBooks = findBookByISBN(searchISBN, books);
                break;
            case 3:
                System.out.println("Enter the name of the author:");
                String searchAuthor = scanner.nextLine().toLowerCase();
                selectedBooks = findBookByAuthor(searchAuthor, books);
                break;
            case 4:
                System.out.println("Enter the name of the category:");
                String searchCategory = scanner.nextLine().toLowerCase();
                selectedBooks = findBookByCategory(searchCategory, books);
                break;
            default:
                System.out.println("Invalid choice.");
                break;
        }
        return selectedBooks;
    }

    public List<Book> findBookByTitle(String searchTitle, List<Book> books) {
        List<Book> matchingBooks = new ArrayList<>();
        String searchTitleLower = searchTitle.toLowerCase();
        for (Book book : books) {
            String bookTitleLower = book.getTitle().toLowerCase();
            if (bookTitleLower.contains(searchTitleLower)) {
                matchingBooks.add(book);
            }
        }
        return matchingBooks;
    }

    public List<Book> findBookByISBN(String searchISBN, List<Book> books) {
        List<Book> matchingBooks = new ArrayList<>();
        if (ValidationUtil.isValid(searchISBN, ValidationUtil.ISBN_13_REGEX)) {
            for (Book book : books) {
                if (book.getISBN().equals(searchISBN)) {
                    matchingBooks.add(book);
                }
            }
        } else {
            System.out.println("Invalid ISBN format. Please enter a valid ISBN.");
        }
        return matchingBooks;
    }

    public List<Book> findBookByAuthor(String searchAuthor, List<Book> books) {
        List<Book> matchingBooks = new ArrayList<>();
        if (ValidationUtil.isValid(searchAuthor, ValidationUtil.STRING_REGEX)) {
            String searchAuthorLower = searchAuthor.toLowerCase();
            for (Book book : books) {
                String authorName = book.getAuthor().toString().toLowerCase();
                if (authorName.contains(searchAuthorLower)) {
                    matchingBooks.add(book);
                }
            }
        } else {
            System.out.println("Invalid author name format. Please enter a valid author name.");
        }
        return matchingBooks;
    }

    public List<Book> findBookByCategory(String searchCategory, List<Book> books) {
        List<Book> matchingBooks = new ArrayList<>();
        if (ValidationUtil.isValid(searchCategory, ValidationUtil.STRING_REGEX)) {
            String searchCategoryLower = searchCategory.toLowerCase();
            for (Book book : books) {
                String category = book.getCategory().toLowerCase();
                if (category.equals(searchCategoryLower)) {
                    matchingBooks.add(book);
                }
            }

            if (!matchingBooks.isEmpty()) {
                System.out.println("Books under the category '" + searchCategory + "':");
                for (Book book : matchingBooks) {
                    System.out.println(book.getTitle());
                }

                System.out.println("Enter the title of the book you want to create a bill for:");
                String partialTitle = scanner.nextLine().toLowerCase();

                List<Book> selectedBooks = matchingBooks.stream()
                        .filter(book -> book.getTitle().toLowerCase().contains(partialTitle))
                        .collect(Collectors.toList());

                if (!selectedBooks.isEmpty()) {
                    return selectedBooks;
                } else {
                    System.out.println("No books with title containing '" + partialTitle + "' found. No bill created.");
                }
            } else {
                System.out.println("No books found under the category '" + searchCategory + "'.");
            }
        } else {
            System.out.println("Invalid category format. Please enter a valid category.");
        }
        return Collections.emptyList();
    }

    public void saveBillToFile(Bill bill) {
        try (PrintWriter writer = new PrintWriter(new FileWriter("data/bills/" + bill.getBillNo() + ".txt"))) {
            writer.println("Bill details:");
            writer.println("Bill No.: " + bill.getBillNo());
            writer.println("Retailer name: ");
            writer.println("Purchase date: " + bill.getPurchaseDate());

            for (BillUnit billUnit : bill.getBillUnits()) {
                System.out.println(billUnit.getBook());
                System.out.print("Enter the amount of books to purchase: ");
                int amount = scanner.nextInt();
                scanner.nextLine();

                if (amount > 0) {
                    billUnit.setAmount(amount);
                    System.out.println(billUnit.getBook());

                    writer.println("Book: " + billUnit.getBook().getTitle());
                    writer.println("Amount: " + billUnit.getAmount());
                    writer.println("Stock No: " + billUnit.getBook().getStockNo());
                    writer.println("Price: " + billUnit.getBook().getSellingPrice());
                    writer.println("Total Cost: " + billUnit.getTotalCost());
                    writer.println();
                } else {
                    System.out.println("Skipped adding this book to the bill.");
                }
            }

            //update stock numbers in the binary file after processing all BillUnits
            updateStockNumbersInBinaryFile(bill);

            System.out.println("Bill details have been saved to bills/" + bill.getBillNo() + ".txt");
        } catch (IOException e) {
            System.err.println("Error writing the bill details to the file: " + e.getMessage());
        }
    }

    private void updateStockNumbersInBinaryFile(Bill bill) {
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