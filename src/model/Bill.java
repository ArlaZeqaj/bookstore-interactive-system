package model;

import java.security.SecureRandom;
import java.util.Date;

public class Bill {
    private final String billNo;
    private BillUnit[] billUnits;
    private final Date purchaseDate;

    public Bill(BillUnit[] billUnits) {
        this.billNo = generateRandomBillNumber(12);
        this.billUnits = billUnits;
        this.purchaseDate = new Date();
    }

    public String getBillNo() {
        return billNo;
    }

    public BillUnit[] getBillUnits() {
        return billUnits;
    }

    public void setBillUnits(BillUnit[] billUnits) {
        this.billUnits = billUnits;
    }

    public Date getPurchaseDate() {
        return purchaseDate;
    }

    public void updateStockNumbers() {
        for (BillUnit billUnit : billUnits) {
            Book book = billUnit.getBook();
            int amount = billUnit.getAmount();
            int updatedStockNo = Math.max(0, book.getStockNo() - amount);
            book.setStockNo(updatedStockNo);
        }
    }

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