package org.example;

import java.sql.*;
import java.util.Scanner;

public class Main {

    static final String URL = "jdbc:mysql://127.0.0.1:3306/hostel_db";
    static final String USER = "root";
    static final String PASSWORD = "your_password"; // Enter your MySQL password

    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {

        int choice;

        do {
            System.out.println("\n===== KIET HOSTEL MANAGEMENT SYSTEM =====");
            System.out.println("1. Insert Students");
            System.out.println("2. Display Students by Hostel with Fee > 50000");
            System.out.println("3. Update Student Fee");
            System.out.println("4. Search Student and Update Room Number");
            System.out.println("5. Display Total Fees and Delete Student");
            System.out.println("6. Exit");
            System.out.print("Enter your choice: ");

            choice = sc.nextInt();

            switch (choice) {
                case 1:
                    insertStudents();
                    break;

                case 2:
                    displayStudentsByHostel();
                    break;

                case 3:
                    updateFee();
                    break;

                case 4:
                    updateRoomNumber();
                    break;

                case 5:
                    displayTotalAndDelete();
                    break;

                case 6:
                    System.out.println("Exiting Hostel Management System...");
                    break;

                default:
                    System.out.println("Invalid choice!");
            }

        } while (choice != 6);

        sc.close();
    }

    // (a) Insert at least 3 student records using PreparedStatement

    public static void insertStudents() {

        String sql = "INSERT INTO HostelStudent VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = con.prepareStatement(sql)) {

            Object[][] students = {
                    {101, "Rahul Sharma", "24001", "Boys Hostel A", "101", 65000.0},
                    {102, "Aman Verma", "24002", "Boys Hostel B", "202", 55000.0},
                    {103, "Priya Singh", "24003", "Girls Hostel A", "105", 72000.0}
            };

            for (Object[] student : students) {

                ps.setInt(1, (int) student[0]);
                ps.setString(2, (String) student[1]);
                ps.setString(3, (String) student[2]);
                ps.setString(4, (String) student[3]);
                ps.setString(5, (String) student[4]);
                ps.setDouble(6, (double) student[5]);

                try {
                    ps.executeUpdate();
                    System.out.println(student[1] + " inserted successfully.");
                } catch (SQLIntegrityConstraintViolationException e) {
                    System.out.println("Duplicate Student ID: " + student[0]);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // (b) Display students of a given hostel having fee > 50000
    // Sort records by fee in descending order

    public static void displayStudentsByHostel() {

        System.out.print("Enter Hostel Name: ");
        sc.nextLine();
        String hostel = sc.nextLine();

        String sql = "SELECT * FROM HostelStudent " +
                "WHERE hostel_name = ? AND fee > 50000 " +
                "ORDER BY fee DESC";

        try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, hostel);

            try (ResultSet rs = ps.executeQuery()) {

                boolean found = false;

                while (rs.next()) {
                    found = true;

                    System.out.println("\nStudent ID: " + rs.getInt("student_id"));
                    System.out.println("Name: " + rs.getString("student_name"));
                    System.out.println("Roll Number: " + rs.getString("roll_number"));
                    System.out.println("Hostel: " + rs.getString("hostel_name"));
                    System.out.println("Room Number: " + rs.getString("room_number"));
                    System.out.println("Fee: " + rs.getDouble("fee"));
                }

                if (!found) {
                    System.out.println("No matching students found.");
                }
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // (c) Update fee using Student ID

    public static void updateFee() {

        System.out.print("Enter Student ID: ");
        int id = sc.nextInt();

        System.out.print("Enter New Fee: ");
        double fee = sc.nextDouble();

        String sql = "UPDATE HostelStudent SET fee = ? WHERE student_id = ?";

        try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDouble(1, fee);
            ps.setInt(2, id);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("Fee updated successfully!");
            } else {
                System.out.println("Student ID not found.");
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // (d) Search student by ID and update room number if found

    public static void updateRoomNumber() {

        System.out.print("Enter Student ID: ");
        int id = sc.nextInt();

        String searchSql = "SELECT * FROM HostelStudent WHERE student_id = ?";

        try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement searchPs = con.prepareStatement(searchSql)) {

            searchPs.setInt(1, id);

            try (ResultSet rs = searchPs.executeQuery()) {

                if (rs.next()) {

                    System.out.println("\nStudent Found!");
                    System.out.println("Name: " + rs.getString("student_name"));
                    System.out.println("Current Room: " + rs.getString("room_number"));

                    System.out.print("Enter New Room Number: ");
                    sc.nextLine();
                    String room = sc.nextLine();

                    String updateSql =
                            "UPDATE HostelStudent SET room_number = ? WHERE student_id = ?";

                    try (PreparedStatement updatePs = con.prepareStatement(updateSql)) {

                        updatePs.setString(1, room);
                        updatePs.setInt(2, id);

                        updatePs.executeUpdate();
                    }

                    System.out.println("\nRoom Number Updated Successfully!");
                    System.out.println("Updated Student Details:");

                    String displaySql =
                            "SELECT * FROM HostelStudent WHERE student_id = ?";

                    try (PreparedStatement displayPs = con.prepareStatement(displaySql)) {

                        displayPs.setInt(1, id);

                        try (ResultSet updatedRs = displayPs.executeQuery()) {
                            if (updatedRs.next()) {
                                System.out.println("Student ID: " + updatedRs.getInt("student_id"));
                                System.out.println("Name: " + updatedRs.getString("student_name"));
                                System.out.println("Room Number: " + updatedRs.getString("room_number"));
                                System.out.println("Hostel: " + updatedRs.getString("hostel_name"));
                                System.out.println("Fee: " + updatedRs.getDouble("fee"));
                            }
                        }
                    }

                } else {
                    System.out.println("Student ID not found. Room number cannot be updated.");
                }
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // (e) Display SUM(fee), then delete student only if fee > 0

    public static void displayTotalAndDelete() {

        String sumSql = "SELECT SUM(fee) AS total_fee FROM HostelStudent";

        try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement st = con.createStatement()) {

            try (ResultSet rs = st.executeQuery(sumSql)) {
                if (rs.next()) {
                    System.out.println("Total Hostel Fees: " + rs.getDouble("total_fee"));
                }
            }

            System.out.print("\nEnter Student ID to delete: ");
            int id = sc.nextInt();

            String deleteSql =
                    "DELETE FROM HostelStudent WHERE student_id = ? AND fee > 0";

            try (PreparedStatement ps = con.prepareStatement(deleteSql)) {

                ps.setInt(1, id);

                int rows = ps.executeUpdate();

                if (rows > 0) {
                    System.out.println("Student deleted successfully.");
                } else {
                    System.out.println("Student not deleted. ID not found or fee is not greater than 0.");
                }
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}