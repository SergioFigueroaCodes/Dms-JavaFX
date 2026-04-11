import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDate;

/**
 * Manages donation records and database operations for the Donation Management System.
 * <p>
 * This class stores donation data in an observable list for use by the JavaFX user interface
 * and handles database tasks such as connecting to SQLite, creating the donations table,
 * loading records, adding new donations, updating existing donations, deleting donations,
 * and calculating donation totals.
 */
public class DonationManager {

    /**
     * The observable list of donation records displayed by the user interface.
     */
    private final ObservableList<Donation> donations = FXCollections.observableArrayList();

    /**
     * The active SQLite database connection.
     */
    private Connection connection;

    /**
     * Creates a donation manager for the specified database file.
     * <p>
     * The constructor connects to the database, creates the donations table
     * if necessary, and loads existing donation records into memory.
     *
     * @param dbPath the file path to the SQLite database
     */
    public DonationManager(String dbPath) {
        connect(dbPath);
        createTableIfNeeded();
        loadFromDatabase();
    }

    /**
     * Returns all donation records currently loaded in the system.
     *
     * @return an observable list of donation records
     */
    public ObservableList<Donation> getDonations() {
        return donations;
    }

    /**
     * Connects to the SQLite database at the specified file path.
     * <p>
     * Any existing connection is closed before a new connection is opened.
     *
     * @param dbPath the file path to the SQLite database
     */
    private void connect(String dbPath) {
        closeConnection();

        try {
            Class.forName("org.sqlite.JDBC");
            String url = "jdbc:sqlite:" + dbPath;
            connection = DriverManager.getConnection(url);
        } catch (Exception ex) {
            System.out.println("Could not connect to database: " + dbPath);
            ex.printStackTrace();
            connection = null;
        }
    }

    /**
     * Closes the current database connection if one exists.
     */
    private void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException ex) {
                System.out.println("Could not close previous database connection.");
                ex.printStackTrace();
            }
            connection = null;
        }
    }

    /**
     * Creates the donations table if it does not already exist.
     */
    private void createTableIfNeeded() {
        if (connection == null) {
            return;
        }

        String sql = """
                CREATE TABLE IF NOT EXISTS donations (
                    donationId TEXT PRIMARY KEY,
                    donorName TEXT NOT NULL,
                    donorEmail TEXT NOT NULL,
                    donationDate TEXT NOT NULL,
                    amount REAL NOT NULL,
                    fund TEXT NOT NULL,
                    paymentMethod TEXT NOT NULL
                )
                """;

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException ex) {
            System.out.println("Could not create donations table.");
            ex.printStackTrace();
        }
    }

    /**
     * Loads all donation records from the database into the observable list.
     * <p>
     * Existing items in the list are cleared before new records are loaded.
     */
    public void loadFromDatabase() {
        donations.clear();

        if (connection == null) {
            return;
        }

        String sql = "SELECT * FROM donations";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Donation donation = new Donation(
                        rs.getString("donationId"),
                        rs.getString("donorName"),
                        rs.getString("donorEmail"),
                        LocalDate.parse(rs.getString("donationDate")),
                        rs.getDouble("amount"),
                        normalizeFund(rs.getString("fund")),
                        normalizePayment(rs.getString("paymentMethod"))
                );

                donations.add(donation);
            }
        } catch (SQLException ex) {
            System.out.println("Could not load donations from database.");
            ex.printStackTrace();
        }
    }

    /**
     * Adds a donation record if it is valid and the ID is not already in use.
     *
     * @param donation the donation to add
     * @return true if the donation was added successfully; false otherwise
     */
    public boolean addDonation(Donation donation) {
        if (connection == null) {
            return false;
        }

        String error = DonationValidator.validateDonation(donation);

        if (error != null || findById(donation.getDonationId()) != null) {
            return false;
        }

        String sql = """
                INSERT INTO donations (donationId, donorName, donorEmail, donationDate, amount, fund, paymentMethod)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, donation.getDonationId());
            stmt.setString(2, donation.getDonorName());
            stmt.setString(3, donation.getDonorEmail());
            stmt.setString(4, donation.getDonationDate().toString());
            stmt.setDouble(5, donation.getAmount());
            stmt.setString(6, normalizeFund(donation.getFund()));
            stmt.setString(7, normalizePayment(donation.getPaymentMethod()));

            int rows = stmt.executeUpdate();

            if (rows > 0) {
                donations.add(donation);
                return true;
            }
        } catch (SQLException ex) {
            System.out.println("Could not add donation.");
            ex.printStackTrace();
        }

        return false;
    }

    /**
     * Finds a donation record by its donation ID.
     *
     * @param donationId the donation ID to search for
     * @return the matching donation if found; {@code null} otherwise
     */
    public Donation findById(String donationId) {
        for (Donation donation : donations) {
            if (donation.getDonationId().equals(donationId)) {
                return donation;
            }
        }
        return null;
    }

    /**
     * Deletes a donation record by its donation ID.
     *
     * @param donationId the donation ID of the record to delete
     * @return true if the donation was deleted successfully; false otherwise
     */
    public boolean deleteDonation(String donationId) {
        if (connection == null) {
            return false;
        }

        Donation donation = findById(donationId);

        if (donation == null) {
            return false;
        }

        String sql = "DELETE FROM donations WHERE donationId = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, donationId);

            int rows = stmt.executeUpdate();

            if (rows > 0) {
                donations.remove(donation);
                return true;
            }
        } catch (SQLException ex) {
            System.out.println("Could not delete donation.");
            ex.printStackTrace();
        }

        return false;
    }

    /**
     * Updates an existing donation record.
     *
     * @param donationId the original donation ID of the record to update
     * @param updatedDonation the replacement donation data
     * @return true if the donation was updated successfully; false otherwise
     */
    public boolean updateDonation(String donationId, Donation updatedDonation) {
        if (connection == null) {
            return false;
        }

        Donation existing = findById(donationId);

        if (existing == null) {
            return false;
        }

        String error = DonationValidator.validateDonation(updatedDonation);
        if (error != null) {
            return false;
        }

        if (!donationId.equals(updatedDonation.getDonationId())
                && findById(updatedDonation.getDonationId()) != null) {
            return false;
        }

        String sql = """
                UPDATE donations
                SET donationId = ?, donorName = ?, donorEmail = ?, donationDate = ?, amount = ?, fund = ?, paymentMethod = ?
                WHERE donationId = ?
                """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, updatedDonation.getDonationId());
            stmt.setString(2, updatedDonation.getDonorName());
            stmt.setString(3, updatedDonation.getDonorEmail());
            stmt.setString(4, updatedDonation.getDonationDate().toString());
            stmt.setDouble(5, updatedDonation.getAmount());
            stmt.setString(6, normalizeFund(updatedDonation.getFund()));
            stmt.setString(7, normalizePayment(updatedDonation.getPaymentMethod()));
            stmt.setString(8, donationId);

            int rows = stmt.executeUpdate();

            if (rows > 0) {
                existing.setDonationId(updatedDonation.getDonationId());
                existing.setDonorName(updatedDonation.getDonorName());
                existing.setDonorEmail(updatedDonation.getDonorEmail());
                existing.setDonationDate(updatedDonation.getDonationDate());
                existing.setAmount(updatedDonation.getAmount());
                existing.setFund(normalizeFund(updatedDonation.getFund()));
                existing.setPaymentMethod(normalizePayment(updatedDonation.getPaymentMethod()));
                return true;
            }
        } catch (SQLException ex) {
            System.out.println("Could not update donation.");
            ex.printStackTrace();
        }

        return false;
    }

    /**
     * Reloads donations from the specified database file.
     * <p>
     * This method reconnects to the database, ensures the table exists,
     * loads donation records, and returns the total number of records loaded.
     *
     * @param filePath the path to the database file
     * @return the number of donation records loaded
     */
    public int loadFromFile(String filePath) {
        connect(filePath);
        createTableIfNeeded();
        loadFromDatabase();
        return donations.size();
    }

    /**
     * Calculates the total donation amount for a specific fund.
     *
     * @param fund the fund name to total
     * @return the total donation amount for the specified fund
     */
    public double getTotalByFund(String fund) {
        if (connection == null) {
            return 0.0;
        }

        String sql = "SELECT SUM(amount) FROM donations WHERE LOWER(fund) = LOWER(?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, fund);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        } catch (SQLException ex) {
            System.out.println("Could not calculate total by fund.");
            ex.printStackTrace();
        }

        return 0.0;
    }

    /**
     * Calculates the total donation amount across all funds.
     *
     * @return the grand total of all donations
     */
    public double getGrandTotal() {
        if (connection == null) {
            return 0.0;
        }

        String sql = "SELECT SUM(amount) FROM donations";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException ex) {
            System.out.println("Could not calculate grand total.");
            ex.printStackTrace();
        }

        return 0.0;
    }

    /**
     * Normalizes fund names so they are stored consistently in the database.
     *
     * @param fund the fund name to normalize
     * @return the standardized fund name, or an empty string if the input is null
     */
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

        return trimmed;
    }

    /**
     * Normalizes payment method names so they are stored consistently in the database.
     *
     * @param payment the payment method to normalize
     * @return the standardized payment method, or an empty string if the input is null
     */
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

        return trimmed;
    }
}