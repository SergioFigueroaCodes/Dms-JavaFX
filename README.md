# Donation Management System (JavaFX)

## Description
This project is a JavaFX based donation management system that connects to a SQLite database. It allows users to add, update, delete, and view donation records through a simple graphical user interface.

## Reflection
This project helped me understand how to connect a Java application to a database and manage data using a GUI. I also got more comfortable working with JavaFX and handling user input.

---

## Features
- Add new donations
- Update existing donation records
- Delete donations
- View all donations in a table
- Load a database file at runtime
- Calculate totals by fund
- Basic input validation

---

## Technologies Used
- Java
- JavaFX
- SQLite
- JDBC

---

## How to Run
1. Open the project in IntelliJ
2. Run the `Main` class
3. When prompted, select the database file:
   church_donations.db
4. The application will load and display the data

---

## Database Info
- Database type: SQLite
- File: church_donations.db
- Table used: donations

---

## Sample Data
The database includes 20+ donation records with:
- Donation ID
- Name
- Email
- Date
- Amount
- Fund
- Payment Method

---

## Notes
- The program requires selecting the database file when it starts
- Make sure the `.db` file is included or the app won’t run properly
