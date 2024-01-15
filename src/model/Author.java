package model;

import model.Utility.ValidationUtil;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class Author implements Serializable{
    @Serial
    private static final long serialVersionUID = 538258161104464548L;
    private String firstName;
    private String middleName; //author middle name should end with a period ex: J K. Rowling
    private String lastName;
    //ArrayList<Author> authors = new ArrayList<>();
    public String filename = "data/binaryFiles/authors.bin";
    private static Set<String> existingAuthors = new HashSet<>();
    private AuthorList authorList = new AuthorList();

    public Author(String firstName, String middleName, String lastName) {
        if(ValidationUtil.isValid(firstName, ValidationUtil.STRING_REGEX))
            this.firstName = firstName;
        this.middleName = middleName;
        if(ValidationUtil.isValid(lastName, ValidationUtil.STRING_REGEX))
            this.lastName = lastName;
        if (!authorList.hasDuplicate(this) && !existingAuthors.contains(firstName + " " + middleName + lastName)) {
            saveAuthorToFile();
            authorList.addAuthor(this);
            existingAuthors.add(firstName + " " + middleName + lastName);
        }
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        if(ValidationUtil.isValid(firstName, ValidationUtil.STRING_REGEX))
            this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        if(ValidationUtil.isValid(lastName, ValidationUtil.STRING_REGEX))
            this.lastName = lastName;
    }
    /*
        public ArrayList<Author> getAuthorList() {
            return authorList;
        }

        public void setAuthorList(ArrayList<Author> authorList) {
            this.authorList = authorList;
        }

        public void add(Author author) {
            authorList.add(author);
        }
     */
    @Override
    public String toString() {
        return firstName + " " + middleName  + lastName;
    }

    private void saveAuthorToFile() {
        File file = new File(filename);
        boolean fileIsEmpty = !file.exists() || file.length() == 0;

        if (fileIsEmpty || (!authorList.hasDuplicate(this) && !existingAuthors.contains(firstName + " " + middleName  + lastName))) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true))) {
                writer.write(firstName + " " + middleName + lastName);
                writer.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
