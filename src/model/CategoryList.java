package model;

import model.Utility.FileReaderUtil;
import model.Utility.FileWriterUtil;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CategoryList implements Serializable {
    @Serial
    private static final long serialVersionUID = 2212749183394620980L;

    private List<Category> categories;
    private static Set<String> existingCategories;  // Use a set to track existing category names
    private static ArrayList<Category> readCategories;  // Keep it static if necessary
    public static String filePath = "data/binaryFiles/categories.bin";

    public CategoryList() {
        categories = new ArrayList<>();
        existingCategories = new HashSet<>();
        readCategories = FileReaderUtil.readArrayListFromFile(filePath);
        categories.addAll(readCategories);
        categories.forEach(category -> existingCategories.add(category.getName()));  // Populate the set initially
    }

    public void addCategory(Category category) {
        if (!hasDuplicate(category)) {
            categories.add(category);
            existingCategories.add(category.getName());
            readCategories.add(category);
            FileWriterUtil.writeArrayListToFile(readCategories, filePath);
            System.out.println(category.toString());
        } else {
            System.out.println("Category already exists.");
        }
    }

    public boolean hasDuplicate(Category newCategory) {
        return existingCategories.contains(newCategory.getName());
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public static Set<String> getExistingCategories() {
        return existingCategories;
    }

    public static void setExistingCategories(Set<String> existingCategories) {
        CategoryList.existingCategories = existingCategories;
    }

    public static ArrayList<Category> getReadCategories() {
        return readCategories;
    }

    public static void setReadCategories(ArrayList<Category> readCategories) {
        CategoryList.readCategories = readCategories;
    }

    public void printCategories() {
        for (Category category : categories) {
            System.out.println(category.toString());
        }
    }
}