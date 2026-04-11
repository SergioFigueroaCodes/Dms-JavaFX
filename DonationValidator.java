import java.time.LocalDate;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Validates user input for donation records in the Donation Management System.
 * <p>
 * This class provides reusable validation methods for donation IDs, donor names,
 * email addresses, dates, donation amounts, fund names, and payment methods.
 * It also validates an entire {@link Donation} object and returns a user-friendly
 * error message when validation fails.
 */
public class DonationValidator {

    /**
     * Regular expression pattern used to validate email addresses.
     */
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    /**
     * The set of valid fund names accepted by the system.
     */
    private static final Set<String> VALID_FUNDS =
            Set.of("General", "Missions", "Building", "Youth");

    /**
     * The set of valid payment methods accepted by the system.
     */
    private static final Set<String> VALID_PAYMENT_METHODS =
            Set.of("Cash", "Check", "Card");

    /**
     * Determines whether a donation ID is valid.
     * <p>
     * A valid donation ID must be exactly six digits.
     *
     * @param donationId the donation ID to validate
     * @return true if the donation ID is exactly six digits; false otherwise
     */
    public static boolean isValidDonationId(String donationId) {
        return donationId != null && donationId.matches("\\d{6}");
    }

    /**
     * Determines whether a donor name is valid.
     * <p>
     * A valid donor name must not be null, empty, or blank.
     *
     * @param donorName the donor name to validate
     * @return true if the donor name contains non-blank text; false otherwise
     */
    public static boolean isValidDonorName(String donorName) {
        return donorName != null && !donorName.trim().isEmpty();
    }

    /**
     * Determines whether an email address is valid.
     *
     * @param email the email address to validate
     * @return true if the email matches the expected format; false otherwise
     */
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /**
     * Determines whether a donation date is valid.
     * <p>
     * A valid date must not be null and must fall between January 1, 1930
     * and the current date.
     *
     * @param date the donation date to validate
     * @return true if the date is within the allowed range; false otherwise
     */
    public static boolean isValidDate(LocalDate date) {
        if (date == null) {
            return false;
        }

        LocalDate minDate = LocalDate.of(1930, 1, 1);
        LocalDate today = LocalDate.now();

        return !date.isBefore(minDate) && !date.isAfter(today);
    }

    /**
     * Determines whether a donation amount is valid.
     * <p>
     * A valid amount must be between 0.00 and 100000.00 inclusive.
     *
     * @param amount the donation amount to validate
     * @return true if the amount is within the allowed range; false otherwise
     */
    public static boolean isValidAmount(double amount) {
        return amount >= 0.00 && amount <= 100000.00;
    }

    /**
     * Determines whether a fund name is valid.
     * <p>
     * Fund names are compared case-insensitively against the allowed values.
     *
     * @param fund the fund name to validate
     * @return true if the fund matches an allowed value; false otherwise
     */
    public static boolean isValidFund(String fund) {
        if (fund == null) {
            return false;
        }

        String trimmed = fund.trim();
        return VALID_FUNDS.stream().anyMatch(valid -> valid.equalsIgnoreCase(trimmed));
    }

    /**
     * Determines whether a payment method is valid.
     * <p>
     * Payment methods are compared case-insensitively against the allowed values.
     *
     * @param paymentMethod the payment method to validate
     * @return true if the payment method matches an allowed value; false otherwise
     */
    public static boolean isValidPaymentMethod(String paymentMethod) {
        if (paymentMethod == null) {
            return false;
        }

        String trimmed = paymentMethod.trim();
        return VALID_PAYMENT_METHODS.stream().anyMatch(valid -> valid.equalsIgnoreCase(trimmed));
    }

    /**
     * Validates a complete donation object.
     * <p>
     * This method checks all required fields and returns the first validation
     * error message encountered. If all fields are valid, it returns {@code null}.
     *
     * @param donation the donation object to validate
     * @return an error message if validation fails; {@code null} if the donation is valid
     */
    public static String validateDonation(Donation donation) {
        if (donation == null) {
            return "Donation cannot be null.";
        }

        if (!isValidDonationId(donation.getDonationId())) {
            return "Donation ID must be exactly 6 digits.";
        }

        if (!isValidDonorName(donation.getDonorName())) {
            return "Donor name cannot be empty.";
        }

        if (!isValidEmail(donation.getDonorEmail())) {
            return "Email format is invalid.";
        }

        if (!isValidDate(donation.getDonationDate())) {
            return "Date must be between 1930 and today.";
        }

        if (!isValidAmount(donation.getAmount())) {
            return "Amount must be between 0.00 and 100000.00.";
        }

        if (!isValidFund(donation.getFund())) {
            return "Fund must be General, Missions, Building, or Youth.";
        }

        if (!isValidPaymentMethod(donation.getPaymentMethod())) {
            return "Payment method must be Cash, Check, or Card.";
        }

        return null;
    }
}