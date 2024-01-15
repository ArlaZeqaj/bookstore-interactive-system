package model.Utility;

import java.io.*;
import java.util.ArrayList;

public class FileReaderUtil {
    public static <T> ArrayList<T> readArrayListFromFile(String filePath) {
        ArrayList<T> readList = new ArrayList<>();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            Object obj = ois.readObject();

            if (obj instanceof ArrayList<?>) {
                readList = (ArrayList<T>) obj;
            } else {
                System.err.println("Error: Unexpected object type found in the file.");
            }
        } catch (EOFException eofException) {
            // Handle EOFException (end of file reached)
            if (isFileEmpty(filePath)) {
                // If the file is empty, return an empty list
                return readList;
            } else {
                System.err.println("Error: Unexpected end of file.");
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error reading the ArrayList from the file: " + e.getMessage());
            e.printStackTrace();
        }
        return readList;
    }

    public static boolean isFileEmpty(String filePath) {
        File file = new File(filePath);
        return file.length() == 0;
    }
}