package model;

import model.Utility.ValidationUtil;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class Category implements Serializable {
    @Serial
    private static final long serialVersionUID = 7762027686636636721L;
    private String name;
    public static String filename = "data/binaryFiles/categories.bin";
    private static Set<String> existingCategories = new HashSet<>();
    private CategoryList categoryList = new CategoryList();

    public Category(String name) {
        if (ValidationUtil.isValid(name, ValidationUtil.BOOK_TITLE_REGEX))
            this.name = name;
        if (!categoryList.hasDuplicate(this) && !existingCategories.contains(name)) {
            saveCategoryToFile();
            categoryList.addCategory(this);
            existingCategories.add(name);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (ValidationUtil.isValid(name, ValidationUtil.STRING_REGEX))
            this.name = name;
    }

    public CategoryList getCategoryList() {
        return categoryList;
    }

    public void setCategoryList(CategoryList categoryList) {
        this.categoryList = categoryList;
    }

    @Override
    public String toString() {
        return name;
    }

    private void saveCategoryToFile() {
        File file = new File(filename);
        boolean fileIsEmpty = !file.exists() || file.length() == 0;

        if (fileIsEmpty || (!categoryList.hasDuplicate(this) && !existingCategories.contains(name))) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true))) {
                writer.write(name);
                writer.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}