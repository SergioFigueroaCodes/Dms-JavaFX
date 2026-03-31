
import java.time.LocalDate;

// Stores one donation record for the Donation Management System.
public class Donation {

    // Unique ID for the donation
    private String donationId;

    // Name of the donor
    private String donorName;

    // Email address of the donor
    private String donorEmail;

    // Date the donation was made
    private LocalDate donationDate;

    // Amount donated
    private double amount;

    // Fund category the donation belongs to
    private String fund;

    // Payment method used for the donation
    private String paymentMethod;

    // Constructor to create a new Donation object with all details
    public Donation(String donationId, String donorName, String donorEmail,
                    LocalDate donationDate, double amount, String fund, String paymentMethod) {
        this.donationId = donationId;       // Set donation ID
        this.donorName = donorName;         // Set donor name
        this.donorEmail = donorEmail;       // Set donor email
        this.donationDate = donationDate;   // Set donation date
        this.amount = amount;               // Set donation amount
        this.fund = fund;                   // Set donation fund
        this.paymentMethod = paymentMethod; // Set payment method
    }

    // Returns the donation ID
    public String getDonationId() {
        return donationId;
    }

    // Updates the donation ID
    public void setDonationId(String donationId) {
        this.donationId = donationId;
    }

    // Returns the donor's name
    public String getDonorName() {
        return donorName;
    }

    // Updates the donor's name
    public void setDonorName(String donorName) {
        this.donorName = donorName;
    }

    // Returns the donor's email
    public String getDonorEmail() {
        return donorEmail;
    }

    // Updates the donor's email
    public void setDonorEmail(String donorEmail) {
        this.donorEmail = donorEmail;
    }

    // Returns the date of the donation
    public LocalDate getDonationDate() {
        return donationDate;
    }

    // Updates the donation date
    public void setDonationDate(LocalDate donationDate) {
        this.donationDate = donationDate;
    }

    // Returns the donation amount
    public double getAmount() {
        return amount;
    }

    // Updates the donation amount
    public void setAmount(double amount) {
        this.amount = amount;
    }

    // Returns the fund category
    public String getFund() {
        return fund;
    }

    // Updates the fund category
    public void setFund(String fund) {
        this.fund = fund;
    }

    // Returns the payment method
    public String getPaymentMethod() {
        return paymentMethod;
    }

    // Updates the payment method
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    // Returns the donation record as a formatted string
    @Override
    public String toString() {
        return donationId + " | " + donorName + " | " + donorEmail + " | "
                + donationDate + " | $" + amount + " | " + fund + " | " + paymentMethod;
    }
}