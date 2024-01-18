package model;

import model.Utility.FilePrinterUtil;
import model.Utility.FileReaderUtil;
import model.Utility.FileWriterUtil;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;

public class BookList {
    private final List<Book> books;
    private ArrayList<Book> readBooks;
    public static String filePath = "data/binaryFiles/books.bin";

    public BookList() {
        books = new ArrayList<>();
        //initializeBooks();
        readBooks = FileReaderUtil.readArrayListFromFile(filePath);
        printBooksFromFile(readBooks);
    }

    private void initializeBooks() {
        books.add(new Book("978-0-73829-2849720-5-9", "The Secret History", new Category("Mystery"), "Penguin Books", Year.of(2018), 12.5, 18.0, 16.5, new Author("Donna", "", "Tartt"), 27));
        books.add(new Book("978-0-73829-2347321-6-1", "The Shadow of the Wind", new Category("Mystery"), "Dituria", Year.of(2000), 9.0, 12.0, 12.0, new Author("Carlos", "Ruiz. ", "Zafon"), 20));
        books.add(new Book("978-0-46721-5689001-2-9", "Ballad of the Songbirds and Snakes", new Category("Fantasy"), "Macmilan", Year.of(2015), 15.0, 25.0, 18.0, new Author("Suzanne", "", "Collins"), 22));
        books.add(new Book("978-0-12345-6789125-3-3", "The Great Gatsby", new Category("Classic"), "Random House", Year.of(1925), 8.5, 12.0, 10.0, new Author("F.", "Scott ", "Fitzgerald"), 5));
        books.add(new Book("978-0-98765-4321095-8-6", "To Kill a Mockingbird", new Category("Fiction"), "HarperCollins", Year.of(1960), 10.0, 15.0, 12.5, new Author("Harper", "", "Lee"), 25));
        books.add(new Book("978-0-13131-3131317-1-2", "Crime and Punishment", new Category("Classic"), "Vintage Books", Year.of(1866), 7.0, 11.0, 9.5, new Author("Fyodor", "", "Dostoevsky"), 4));
        books.add(new Book("978-0-86420-5793144-5-3", "Pride and Prejudice", new Category("Romance"), "Wordsworth Editions", Year.of(1813), 6.5, 10.0, 8.0, new Author("Jane", "", "Austen"), 35));

        FileWriterUtil.writeArrayListToFile(books, filePath);
    }

    public List<Book> getBooks() {
        return books;
    }

    public ArrayList<Book> getReadBooks() {
        return readBooks;
    }

    public void setReadBooks(ArrayList<Book> readBooks) {
        this.readBooks = readBooks;
    }

    public static void printBooksFromFile(ArrayList<Book> readBooks) {
        System.out.println("\n\nArrayList of books from file: ");
        for (Book book : readBooks) {
            System.out.println("Title: " + book.getTitle());
            System.out.println("Author: " + book.getAuthor().toString());
            System.out.println("StockNo: " + book.getStockNo());
            System.out.println();
        }
    }
}