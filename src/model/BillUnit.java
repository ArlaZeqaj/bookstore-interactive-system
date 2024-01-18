package model;

import model.Utility.FileReaderUtil;

import java.io.Serializable;
import java.util.List;

public class BillUnit implements Serializable {
    private final Book book;
    private int amount;
    private double totalCost;

    public BillUnit(Book book, int amount) {
        this.book = book;
        this.amount = amount;
        updateTotalCost();
    }

    private void updateTotalCost() {
        this.totalCost = book.getSellingPrice() * amount;
    }

    private void updateStockNo(int amount) {
        if (amount > 0) {
            int currentStockNo = book.getStockNo();
            int updatedStockNo = Math.max(0, currentStockNo - amount);
            book.setStockNo(updatedStockNo);
        }
    }

    public Book getBook() {
        return book;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
        if (amount > 0) {
            updateTotalCost();
            updateStockNo(amount);
        }
    }

    public double getTotalCost() {
        return totalCost;
    }

    public int getStockNo() {
        return book.getStockNo();
    }

    @Override
    public String toString() {
        return "Item: \n" +
                "Book: " + book.getTitle() + "\n" +
                "Amount: " + getAmount() + "\n" +
                "Price per unit: " + book.getSellingPrice() + "\n" +
                "StockNo: " + getStockNo() + "\n" +
                "Total Cost: " + getTotalCost();
    }
}