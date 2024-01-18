package model;

import java.io.Serializable;
import java.security.SecureRandom;
import java.util.Date;

public class Bill implements Serializable {
    private final String billNo;
    private BillUnit[] billUnits;
    private final Date purchaseDate;
    //private double finalTotalCost;

    public Bill(BillUnit[] billUnits) {
        this.billNo = generateRandomBillNumber(12);
        this.billUnits = billUnits;
        this.purchaseDate = new Date();
        //calculateFinalTotalCost();
    }

    public String getBillNo() {
        return billNo;
    }

    public BillUnit[] getBillUnits() {
        return billUnits;
    }

    public void setBillUnits(BillUnit[] billUnits) {
        this.billUnits = billUnits;
        //calculateFinalTotalCost();
    }

    public Date getPurchaseDate() {
        return purchaseDate;
    }

    /*
    public void setFinalTotalCost(double finalTotalCost) {
        this.finalTotalCost = finalTotalCost;
    }

    public double getFinalTotalCost() {
        return finalTotalCost;
    }

    public void calculateFinalTotalCost() {
        finalTotalCost = 0;
        for (BillUnit unit : billUnits) {
            finalTotalCost += unit.getTotalCost();
        }
    }

     */

    private static final String ALLOWED_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    public static String generateRandomBillNumber(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder stringBuilder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(ALLOWED_CHARACTERS.length());
            stringBuilder.append(ALLOWED_CHARACTERS.charAt(randomIndex));
        }
        return stringBuilder.toString();
    }
}
