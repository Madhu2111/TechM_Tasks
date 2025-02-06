package com.jdbcexamples;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class JdbcMenuProgram {

    private Scanner scanner;

    public JdbcMenuProgram() {
        this.scanner = new Scanner(System.in);
    }

    public static void main(String[] args) throws SQLException, Exception {
        JdbcMenuProgram jdbcMenuProgram = new JdbcMenuProgram();
        jdbcMenuProgram.displayMenu();
    }

    private void displayMenu() throws SQLException, Exception {
        System.out.println("Choose the operation number to perform:");
        System.out.println("1. Insert");
        System.out.println("2. Update");
        System.out.println("3. Delete");
        System.out.println("4. Select");
        System.out.println("5. Exit");

        int choice = scanner.nextInt();
        switch (choice) {
            case 1:
                insertOption();
                displayMenu();
                break;
            case 2:
                updateOption();
                displayMenu();
                break;
            case 3:
                deleteOption();
                displayMenu();
                break;
            case 4:
                selectOption();
                displayMenu();
                break;
            case 5:
                System.out.println("Exiting...");
                break;
            default:
                System.out.println("Invalid choice. Please choose again.");
                displayMenu();
        }
    }

    private void insertOption() throws SQLException, Exception {
        Connection connection = getConnection();
        System.out.println("Enter emp id:");
        int empid = scanner.nextInt();
        System.out.println("Enter emp name:");
        String empname = scanner.next();
        System.out.println("Enter emp designation:");
        String empdesig = scanner.next();

        PreparedStatement preparedStatement = connection.prepareStatement("insert into employee values(?,?,?)");
        preparedStatement.setInt(1, empid);
        preparedStatement.setString(2, empname);
        preparedStatement.setString(3, empdesig);

        int value = preparedStatement.executeUpdate();
        System.out.println(value + " row inserted");

        closeResources(preparedStatement, connection);
    }

    private void updateOption() throws SQLException, Exception {
        Connection connection = getConnection();
        System.out.println("Enter emp id to update:");
        int empid = scanner.nextInt();
        System.out.println("Enter new emp name:");
        String empname = scanner.next();
        System.out.println("Enter new emp designation:");
        String empdesig = scanner.next();

        PreparedStatement preparedStatement = connection.prepareStatement("update employee set empid = ?, empname = ? where empdesig = ?");
        preparedStatement.setInt(1, empid);
        preparedStatement.setString(2, empname);
        preparedStatement.setString(3, empdesig);

        int value = preparedStatement.executeUpdate();
        System.out.println(value + " row updated");

        closeResources(preparedStatement, connection);
    }

    private void deleteOption() throws SQLException, Exception {
        Connection connection = getConnection();
        System.out.println("Enter emp id to delete:");
        int empid = scanner.nextInt();

        PreparedStatement preparedStatement = connection.prepareStatement("delete from employee where empid = ?");
        preparedStatement.setInt(1, empid);

        int value = preparedStatement.executeUpdate();
        System.out.println(value + " row deleted");

        closeResources(preparedStatement, connection);
    }

    private void selectOption() throws SQLException, Exception {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("select * from employee");
        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            System.out.println("Emp ID: " + resultSet.getInt("empid"));
            System.out.println("Emp Name: " + resultSet.getString("empname"));
            System.out.println("Emp Designation: " + resultSet.getString("empdesig"));
            System.out.println();
        }

        closeResources(preparedStatement, resultSet, connection);
    }

    private Connection getConnection() throws SQLException, Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/techm?autoReconnect=true&useSSL=false", "root", "madhu");
    }

    private void closeResources(PreparedStatement preparedStatement, Connection connection) throws SQLException {
        if (preparedStatement != null) {
            preparedStatement.close();
        }
        if (connection != null) {
            connection.close();
        }
    }

    private void closeResources(PreparedStatement preparedStatement, ResultSet resultSet, Connection connection) throws SQLException {
        if (preparedStatement != null) {
            preparedStatement.close();
        }
        if (resultSet != null) {
            resultSet.close();
        }
        if (connection != null) {
            connection.close();
        }
    }
}

