import java.time.LocalDate;

/**
 * Represents a single donation record in the Donation Management System.
 * <p>
 * This class stores the donor's identifying information, the donation date,
 * the donation amount, the selected fund, and the payment method for one
 * donation entered into the system.
 */
public class Donation {

    /**
     * The unique identifier assigned to the donation.
     */
    private String donationId;

    /**
     * The full name of the donor.
     */
    private String donorName;

    /**
     * The donor's email address.
     */
    private String donorEmail;

    /**
     * The date on which the donation was made.
     */
    private LocalDate donationDate;

    /**
     * The monetary amount donated.
     */
    private double amount;

    /**
     * The fund category that receives the donation.
     */
    private String fund;

    /**
     * The payment method used for the donation.
     */
    private String paymentMethod;

    /**
     * Creates a donation record with all required donation details.
     *
     * @param donationId the unique donation ID
     * @param donorName the donor's full name
     * @param donorEmail the donor's email address
     * @param donationDate the date of the donation
     * @param amount the amount donated
     * @param fund the fund category receiving the donation
     * @param paymentMethod the payment method used for the donation
     */
    public Donation(String donationId, String donorName, String donorEmail,
                    LocalDate donationDate, double amount, String fund, String paymentMethod) {
        this.donationId = donationId;
        this.donorName = donorName;
        this.donorEmail = donorEmail;
        this.donationDate = donationDate;
        this.amount = amount;
        this.fund = fund;
        this.paymentMethod = paymentMethod;
    }

    /**
     * Returns the donation ID.
     *
     * @return the donation ID
     */
    public String getDonationId() {
        return donationId;
    }

    /**
     * Updates the donation ID.
     *
     * @param donationId the new donation ID
     */
    public void setDonationId(String donationId) {
        this.donationId = donationId;
    }

    /**
     * Returns the donor's name.
     *
     * @return the donor's name
     */
    public String getDonorName() {
        return donorName;
    }

    /**
     * Updates the donor's name.
     *
     * @param donorName the new donor name
     */
    public void setDonorName(String donorName) {
        this.donorName = donorName;
    }

    /**
     * Returns the donor's email address.
     *
     * @return the donor's email address
     */
    public String getDonorEmail() {
        return donorEmail;
    }

    /**
     * Updates the donor's email address.
     *
     * @param donorEmail the new donor email address
     */
    public void setDonorEmail(String donorEmail) {
        this.donorEmail = donorEmail;
    }

    /**
     * Returns the donation date.
     *
     * @return the date of the donation
     */
    public LocalDate getDonationDate() {
        return donationDate;
    }

    /**
     * Updates the donation date.
     *
     * @param donationDate the new donation date
     */
    public void setDonationDate(LocalDate donationDate) {
        this.donationDate = donationDate;
    }

    /**
     * Returns the donation amount.
     *
     * @return the donation amount
     */
    public double getAmount() {
        return amount;
    }

    /**
     * Updates the donation amount.
     *
     * @param amount the new donation amount
     */
    public void setAmount(double amount) {
        this.amount = amount;
    }

    /**
     * Returns the fund category for the donation.
     *
     * @return the fund category
     */
    public String getFund() {
        return fund;
    }

    /**
     * Updates the fund category for the donation.
     *
     * @param fund the new fund category
     */
    public void setFund(String fund) {
        this.fund = fund;
    }

    /**
     * Returns the payment method used for the donation.
     *
     * @return the payment method
     */
    public String getPaymentMethod() {
        return paymentMethod;
    }

    /**
     * Updates the payment method used for the donation.
     *
     * @param paymentMethod the new payment method
     */
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    /**
     * Returns a formatted string representation of the donation record.
     *
     * @return a formatted donation record string
     */
    @Override
    public String toString() {
        return donationId + " | " + donorName + " | " + donorEmail + " | "
                + donationDate + " | $" + amount + " | " + fund + " | " + paymentMethod;
    }
}