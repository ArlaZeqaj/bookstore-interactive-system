package model;

import model.Utility.FilePrinterUtil;
import model.Utility.FileReaderUtil;
import model.Utility.FileWriterUtil;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LibrarianList {
    private final List<Librarian> librarians;
    private ArrayList<Librarian> readLibrarians;
    public static String filePath = "data/binaryFiles/librarians.bin";
    public LibrarianList(){
        librarians = new ArrayList<>();
        //initializeLibrarians();
        readLibrarians = FileReaderUtil.readArrayListFromFile(filePath);
        //printLibrariansFromFile(readLibrarians);
        FilePrinterUtil.printArrayList(readLibrarians);
    }
    private void initializeLibrarians(){
        librarians.add(new Librarian("Sara", "Molla", LocalDate.of(1998, 5, 11), "+355697900872", 65000.0,"1234"));
        librarians.add(new Librarian("Lisa", "Bardhi", LocalDate.of(1996, 11, 23), "+355682950125", 73000.0,"0000"));
        librarians.add(new Librarian("Riku", "Dedaj", LocalDate.of(1999, 1, 17), "+355691045581", 62500.0, "1111"));
        FileWriterUtil.writeArrayListToFile(librarians, filePath);
        System.out.println("ArrayList of librarians has been written to " + filePath);
    }

    public List<Librarian> getLibrarians() {
        return librarians;
    }

    public ArrayList<Librarian> getReadLibrarians() {

        return FileReaderUtil.readArrayListFromFile(filePath);
    }

    public void setReadLibrarians(ArrayList<Librarian> readLibrarians) {

        this.readLibrarians = readLibrarians;
    }

    public static void printLibrariansFromFile(ArrayList<Librarian> readLibrarians){
        System.out.println("ArrayList of librarians from file: " + filePath);
        for (Librarian librarian : readLibrarians) {
            System.out.println(librarian.getName());
            System.out.println(librarian.getSurname());
            System.out.println(librarian.getSalary());
            System.out.println();
        }
    }
}