import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import java.sql.*;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        try (Connection connection = getConnection()) {
            System.out.println("Connection to Store DB successful!");

            Statement statement = connection.createStatement();
            statement.executeUpdate("DROP TABLE IF EXISTS orders");
            statement.executeUpdate("DROP TABLE IF EXISTS client");

            statement.executeUpdate("CREATE TABLE client (Id INT PRIMARY KEY AUTO_INCREMENT, FirstName varchar(30), " +
                    "SecondName varchar(30), DayOfBirth date)");
            System.out.println("Table (client) created.");
            statement.executeUpdate("CREATE TABLE orders (Id INT PRIMARY KEY AUTO_INCREMENT, ClientId INT, OrderPrice varchar(30)," +
                    " FOREIGN KEY (ClientId) REFERENCES client(Id))");
            System.out.println("Table (orders) created.\n");

            statement.executeUpdate("INSERT INTO client (FirstName, SecondName, DayOfBirth) VALUES " +
                    "('John', 'Doe', '1990-01-01')," +
                    "('Alice', 'Smith', '1985-05-10')," +
                    "('Bob', 'Johnson', '1995-09-15')");
            System.out.println("Data inserted into (client) table.");

            statement.executeUpdate("INSERT INTO orders (ClientId, OrderPrice) VALUES " +
                    "(1, '100.00')," +
                    "(1, '50.00')," +
                    "(2, '200.00')," +
                    "(2, '75.00')," +
                    "(2, '150.00')," +
                    "(3, '300.00')," +
                    "(3, '125.00')," +
                    "(3, '250.00')");
            System.out.println("Data inserted into (orders) table.");
            System.out.println();

            ResultSet clientResultSet = statement.executeQuery("SELECT * FROM client");
            System.out.println("Data from (client) table:");
            while (clientResultSet.next()) {
                int clientId = clientResultSet.getInt("Id");
                String firstName = clientResultSet.getString("FirstName");
                String secondName = clientResultSet.getString("SecondName");
                Date dateOfBirth = clientResultSet.getDate("DayOfBirth");
                System.out.println("Client ID: " + clientId + ", First Name: " + firstName +
                        ", Last Name: " + secondName + ", Date of Birth: " + dateOfBirth);
            }
            ResultSet ordersResultSet = statement.executeQuery("SELECT * FROM orders");
            System.out.println("Data from (orders) table:");
            while (ordersResultSet.next()) {
                int orderId = ordersResultSet.getInt("Id");
                int clientId = ordersResultSet.getInt("ClientId");
                String orderPrice = ordersResultSet.getString("OrderPrice");
                System.out.println("Order ID: " + orderId + ", Client ID: " + clientId +
                        ", Order Price: " + orderPrice);
            }
            System.out.println();

            System.out.print("Enter client ID to get their orders: ");
            int clientId = scanner.nextInt();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM orders WHERE ClientId = " + clientId);
            System.out.println("Orders for client with ID " + clientId + ":");
            while (resultSet.next()) {
                int orderId = resultSet.getInt("Id");
                String orderPrice = resultSet.getString("OrderPrice");
                System.out.println("Order ID: " + orderId + ", Order Price: " + orderPrice);
            }
            System.out.println();

            ResultSet resultAgeSet = statement.executeQuery("SELECT Id, FirstName, SecondName, TIMESTAMPDIFF(YEAR, " +
                    "DayOfBirth, CURDATE()) AS Age FROM client");
            System.out.println("Age of clients:");
            while (resultAgeSet.next()) {
                clientId = resultAgeSet.getInt("Id");
                String firstName = resultAgeSet.getString("FirstName");
                String secondName = resultAgeSet.getString("SecondName");
                int age = resultAgeSet.getInt("Age");
                System.out.println("Client ID: " + clientId + ", Name: " + firstName + " " + secondName + ", Age: " + age);
            }

        } catch (Exception ex) {
            System.out.println("Connection failed...");
            System.out.println(ex);
        }

    }

    public static Connection getConnection() throws SQLException, IOException {

        Properties props = new Properties();
        try (InputStream in = Files.newInputStream(Paths.get("src/database.properties"))) {
            props.load(in);
        }
        String url = props.getProperty("url");
        String username = props.getProperty("username");
        String password = props.getProperty("password");

        return DriverManager.getConnection(url, username, password);

    }

}