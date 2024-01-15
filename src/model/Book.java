package model;

import model.Utility.ValidationUtil;

import java.time.Year;
import java.time.LocalDate;
import java.io.Serializable;

public class Book implements Serializable{
    private BillUnit billUnit;
    private String ISBN;
    private String title;
    private Category category;
    private String supplierName;
    //added the book publication year
    private Year publishYear;
    private final LocalDate purchasedDate;
    private final double purchasedPrice;
    private final double originalPrice;
    private double sellingPrice;
    private Author author;
    private int stockNo;

    public Book(String ISBN, String title, Category category, String supplierName, Year publishYear, double purchasedPrice, double originalPrice, double sellingPrice, Author author, int stockNo) {
        if(ValidationUtil.isValid(ISBN, ValidationUtil.ISBN_13_REGEX))
            this.ISBN = ISBN;
        if(ValidationUtil.isValid(title, ValidationUtil.BOOK_TITLE_REGEX))
            this.title = title;
        if(ValidationUtil.isValid(category.getName(), ValidationUtil.STRING_REGEX))
            this.category = category;
        if(ValidationUtil.isValid(supplierName, ValidationUtil.BOOK_TITLE_REGEX))
            this.supplierName = supplierName;
        this.publishYear = publishYear;
        this.purchasedDate =  LocalDate.now();
        this.purchasedPrice = purchasedPrice;
        this.originalPrice = originalPrice;
        this.sellingPrice = sellingPrice;
        if(ValidationUtil.isValid(author.toString(), ValidationUtil.BOOK_TITLE_REGEX))
            this.author = author;
        this.stockNo = stockNo;
    }

    public String getISBN() {
        return ISBN;
    }

    public String getTitle() {
        return title;
    }

    public Category getCategory() {
        return category;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public Year getPublishYear() {
        return publishYear;
    }

    public  LocalDate getPurchasedDate() {
        return purchasedDate;
    }

    public double getPurchasedPrice() {
        return purchasedPrice;
    }

    public double getOriginalPrice() {
        return originalPrice;
    }

    public double getSellingPrice() {
        return sellingPrice;
    }

    public Author getAuthor() {
        return author;
    }

    public int getStockNo() {
        return stockNo;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public void setStockNo(int stockNo) {this.stockNo = stockNo;}

    public void setISBN(String ISBN) {
        if(ValidationUtil.isValid(ISBN, ValidationUtil.ISBN_13_REGEX))
            this.ISBN = ISBN;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public void setPublishYear(Year publishYear) {
        this.publishYear = publishYear;
    }
    public void setSellingPrice(double sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    @Override
    public String toString() {
        return "Book details: \n" +
                "ISBN: " + ISBN + '\n' +
                "title: " + title + '\n' +
                "category: " + category.toString() + '\n' +
                "supplierName: " + supplierName + '\n' +
                "publishYear: " + publishYear + '\n' +
                "purchasedDate: " + purchasedDate + '\n' +
                "purchasedPrice: " + purchasedPrice + '\n' +
                "originalPrice: " + originalPrice + '\n' +
                "sellingPrice: " + sellingPrice + '\n' +
                "author: " + author + '\n' +
                "stockNo: " + stockNo ;
    }
}