package model;

//secureRandom generates secure random numbers, with a lower chance of repeating compared to math random
import java.security.SecureRandom;
import java.util.Date;

public class Bill {
    private final String billNo;
    private BillUnit[] billUnits;
    private final Date purchaseDate;
    private final double totalCost;


    public Bill(BillUnit[] billUnits) {
        this.billNo = generateRandomBillNumber(12); //generates a 9-character bill number
        this.billUnits = billUnits;
        this.purchaseDate = new Date();
        this.totalCost = calculateTotalCost(); //calculate by adding cost all bill units
    }

    public String getBillNo() {
        return billNo;
    }

    public BillUnit[] getBillUnits() {
        return billUnits;
    }

    public void setBooks(BillUnit[] billUnits) {
        this.billUnits = billUnits;
    }

    public Date getPurchaseDate() {
        return purchaseDate;
    }

    private double calculateTotalCost() {
        double total = 0.0;
        for (BillUnit unit : billUnits) {
            total += unit.getUnitPrice();
        }
        return total;
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

    public double getTotalCost() {
        return totalCost;
    }


}
