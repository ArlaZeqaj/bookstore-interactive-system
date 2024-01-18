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

    public Author(String text) {

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
    //extract the author first/last/middlename from a string
    public void extractNamesFromText(String text) {
        String[] nameParts = text.split("\\s+");
        if (nameParts.length > 0) {
            setFirstName(nameParts[0]);
        }
        if (nameParts.length > 1) {
            setLastName(nameParts[nameParts.length - 1]);
        }
        if (nameParts.length > 2) {
            StringBuilder middleNameBuilder = new StringBuilder();
            for (int i = 1; i < nameParts.length - 1; i++) {
                middleNameBuilder.append(nameParts[i]).append(" ");
            }
            setMiddleName(middleNameBuilder.toString().trim());
        }
    }
}
