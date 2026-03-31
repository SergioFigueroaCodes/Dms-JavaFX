import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;

// Stores donations and handles CRUD operations, file loading, and totals.
public class DonationManager {

    // List that stores all donation records
    private final ObservableList<Donation> donations = FXCollections.observableArrayList();

    // Returns the full list of donations
    public ObservableList<Donation> getDonations() {
        return donations;
    }

    // Adds a donation if it passes validation and does not already exist
    public boolean addDonation(Donation donation) {
        String error = DonationValidator.validateDonation(donation);

        // Return false if donation is invalid or ID already exists
        if (error != null || findById(donation.getDonationId()) != null) {
            return false;
        }

        // Add valid donation to the list
        donations.add(donation);
        return true;
    }

    // Finds a donation by its ID
    public Donation findById(String donationId) {
        for (Donation donation : donations) {
            if (donation.getDonationId().equals(donationId)) {
                return donation; // Return matching donation
            }
        }
        return null; // Return null if not found
    }

    // Deletes a donation by its ID
    public boolean deleteDonation(String donationId) {
        Donation donation = findById(donationId);

        // Return false if donation does not exist
        if (donation == null) {
            return false;
        }

        // Remove donation from the list
        donations.remove(donation);
        return true;
    }

    // Updates an existing donation record
    public boolean updateDonation(String donationId, Donation updatedDonation) {
        Donation existing = findById(donationId);

        // Return false if original donation is not found
        if (existing == null) {
            return false;
        }

        // Validate updated donation data
        String error = DonationValidator.validateDonation(updatedDonation);
        if (error != null) {
            return false;
        }

        // Make sure the new ID is not already used by another donation
        if (!donationId.equals(updatedDonation.getDonationId())
                && findById(updatedDonation.getDonationId()) != null) {
            return false;
        }

        // Update all fields in the existing donation
        existing.setDonationId(updatedDonation.getDonationId());
        existing.setDonorName(updatedDonation.getDonorName());
        existing.setDonorEmail(updatedDonation.getDonorEmail());
        existing.setDonationDate(updatedDonation.getDonationDate());
        existing.setAmount(updatedDonation.getAmount());
        existing.setFund(normalizeFund(updatedDonation.getFund()));
        existing.setPaymentMethod(normalizePayment(updatedDonation.getPaymentMethod()));

        return true;
    }

    // Loads donation records from a text file
    public int loadFromFile(String filePath) {
        int loadedCount = 0; // Counter for successfully loaded donations

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;

            // Read the file line by line
            while ((line = reader.readLine()) != null) {

                // Skip empty lines
                if (line.trim().isEmpty()) {
                    continue;
                }

                // Split line into parts using commas
                String[] parts = line.split(",");

                // Each donation record must have exactly 7 fields
                if (parts.length != 7) {
                    System.out.println("Skipped line (wrong number of fields): " + line);
                    continue;
                }

                try {
                    // Extract and clean each field
                    String id = parts[0].trim();
                    String name = parts[1].trim();
                    String email = parts[2].trim();
                    LocalDate date = LocalDate.parse(parts[3].trim());
                    double amount = Double.parseDouble(parts[4].trim());
                    String fund = normalizeFund(parts[5].trim());
                    String payment = normalizePayment(parts[6].trim());

                    // Create a new donation object
                    Donation donation = new Donation(id, name, email, date, amount, fund, payment);

                    // Add donation if valid
                    if (addDonation(donation)) {
                        loadedCount++;
                    } else {
                        System.out.println("Failed validation or duplicate ID: " + line);
                    }
                } catch (Exception ex) {
                    // Skip badly formatted lines
                    System.out.println("Skipped invalid line: " + line);
                }
            }
        } catch (IOException ex) {
            // Handle file read errors
            System.out.println("Could not read file: " + filePath);
        }

        // Return number of successfully loaded donations
        return loadedCount;
    }

    // Calculates the total donation amount for a specific fund
    public double getTotalByFund(String fund) {
        double total = 0.0;

        for (Donation donation : donations) {
            if (donation.getFund().equalsIgnoreCase(fund)) {
                total += donation.getAmount();
            }
        }

        return total;
    }

    // Calculates the total donation amount across all funds
    public double getGrandTotal() {
        double total = 0.0;

        for (Donation donation : donations) {
            total += donation.getAmount();
        }

        return total;
    }

    // Standardizes fund names so they are stored consistently
    private String normalizeFund(String fund) {
        if (fund == null) {
            return "";
        }

        String trimmed = fund.trim();

        if (trimmed.equalsIgnoreCase("General")) {
            return "General";
        }
        if (trimmed.equalsIgnoreCase("Missions")) {
            return "Missions";
        }
        if (trimmed.equalsIgnoreCase("Building")) {
            return "Building";
        }
        if (trimmed.equalsIgnoreCase("Youth")) {
            return "Youth";
        }

        // Return original trimmed value if it doesn't match known funds
        return trimmed;
    }

    // Standardizes payment method names so they are stored consistently
    private String normalizePayment(String payment) {
        if (payment == null) {
            return "";
        }

        String trimmed = payment.trim();

        if (trimmed.equalsIgnoreCase("Cash")) {
            return "Cash";
        }
        if (trimmed.equalsIgnoreCase("Check")) {
            return "Check";
        }
        if (trimmed.equalsIgnoreCase("Card")) {
            return "Card";
        }

        // Return original trimmed value if it doesn't match known payment methods
        return trimmed;
    }
}