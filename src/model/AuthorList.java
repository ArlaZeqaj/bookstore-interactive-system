package model;

import model.Utility.FileReaderUtil;
import model.Utility.FileWriterUtil;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AuthorList implements Serializable {
    @Serial
    private static final long serialVersionUID = 538258161104464549L;
    private static List<Author> authors;
    private static Set<String> existingAuthors;
    private static ArrayList<Author> readAuthors;
    public static String filePath = "data/binaryFiles/authors.bin";
    public AuthorList(){
        authors = new ArrayList<>();
        existingAuthors = new HashSet<>();
        // initializeAuthors();
        readAuthors = FileReaderUtil.readArrayListFromFile(filePath);
        authors.addAll(readAuthors);
        authors.forEach(author -> existingAuthors.add(author.getFirstName()));
        //printAuthors(readAuthors);
    }
    private void initializeAuthors() {
        authors.add(new Author("Fyodor", "","Dostoyevski"));
        authors.add(new Author("Jane", "","Austin"));
        FileWriterUtil.writeArrayListToFile(authors,filePath);
        System.out.println("ArrayList of authors has been written to " + filePath);
    }
    public void addAuthor(Author author) {
        if (!hasDuplicate(author)) {
            authors.add(author);
            existingAuthors.add(author.getFirstName());
            readAuthors.add(author);
            FileWriterUtil.writeArrayListToFile(readAuthors, filePath);
            System.out.println(author.toString());
        } else {
            System.out.println("Category already exists.");
        }
    }

    public boolean hasDuplicate(Author newAuthor) {
        return existingAuthors.contains(newAuthor.getFirstName());
    }

    public static List<Author> getAuthors() {
        return authors;
    }

    public static void setAuthors(List<Author> authors) {
        AuthorList.authors = authors;
    }

    public static Set<String> getExistingAuthors() {
        return existingAuthors;
    }

    public static void setExistingAuthors(Set<String> existingAuthors) {
        AuthorList.existingAuthors = existingAuthors;
    }

    public static ArrayList<Author> getReadAuthors() {
        return readAuthors;
    }

    public static void setReadAuthors(ArrayList<Author> readAuthors) {
        AuthorList.readAuthors = readAuthors;
    }

    public static void printAuthors(){
        for (Author author : authors) {
            System.out.println(author.toString());
        }
    }
}