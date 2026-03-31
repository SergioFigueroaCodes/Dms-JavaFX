import java.time.LocalDate;
import java.util.Set;
import java.util.regex.Pattern;

// Validates all user input for donation records.
public class DonationValidator {

    // Regular expression pattern used to check if an email is in a valid format
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    // List of allowed fund names
    private static final Set<String> VALID_FUNDS =
            Set.of("General", "Missions", "Building", "Youth");

    // List of allowed payment methods
    private static final Set<String> VALID_PAYMENT_METHODS =
            Set.of("Cash", "Check", "Card");

    // Checks if the donation ID is exactly 6 digits
    public static boolean isValidDonationId(String donationId) {
        return donationId != null && donationId.matches("\\d{6}");
    }

    // Checks if the donor name is not null or blank
    public static boolean isValidDonorName(String donorName) {
        return donorName != null && !donorName.trim().isEmpty();
    }

    // Checks if the email is not null and matches the email pattern
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    // Checks if the donation date is valid
    public static boolean isValidDate(LocalDate date) {
        if (date == null) {
            return false; // Date cannot be null
        }

        // Minimum allowed donation date
        LocalDate minDate = LocalDate.of(1930, 1, 1);

        // Current date
        LocalDate today = LocalDate.now();

        // Date must be between 1930 and today
        return !date.isBefore(minDate) && !date.isAfter(today);
    }

    // Checks if the donation amount is within the allowed range
    public static boolean isValidAmount(double amount) {
        return amount >= 0.00 && amount <= 100000.00;
    }

    // Checks if the fund entered matches one of the valid fund names
    public static boolean isValidFund(String fund) {
        if (fund == null) {
            return false; // Fund cannot be null
        }

        String trimmed = fund.trim();

        // Compare ignoring uppercase/lowercase differences
        return VALID_FUNDS.stream().anyMatch(valid -> valid.equalsIgnoreCase(trimmed));
    }

    // Checks if the payment method entered matches one of the allowed values
    public static boolean isValidPaymentMethod(String paymentMethod) {
        if (paymentMethod == null) {
            return false; // Payment method cannot be null
        }

        String trimmed = paymentMethod.trim();

        // Compare ignoring uppercase/lowercase differences
        return VALID_PAYMENT_METHODS.stream().anyMatch(valid -> valid.equalsIgnoreCase(trimmed));
    }

    // Validates the full donation object and returns an error message if invalid
    public static String validateDonation(Donation donation) {
        if (donation == null) {
            return "Donation cannot be null.";
        }

        // Check donation ID
        if (!isValidDonationId(donation.getDonationId())) {
            return "Donation ID must be exactly 6 digits.";
        }

        // Check donor name
        if (!isValidDonorName(donation.getDonorName())) {
            return "Donor name cannot be empty.";
        }

        // Check donor email
        if (!isValidEmail(donation.getDonorEmail())) {
            return "Email format is invalid.";
        }

        // Check donation date
        if (!isValidDate(donation.getDonationDate())) {
            return "Date must be between 1930 and today.";
        }

        // Check donation amount
        if (!isValidAmount(donation.getAmount())) {
            return "Amount must be between 0.00 and 100000.00.";
        }

        // Check fund type
        if (!isValidFund(donation.getFund())) {
            return "Fund must be General, Missions, Building, or Youth.";
        }

        // Check payment method
        if (!isValidPaymentMethod(donation.getPaymentMethod())) {
            return "Payment method must be Cash, Check, or Card.";
        }

        // Return null if all validations pass
        return null;
    }
}