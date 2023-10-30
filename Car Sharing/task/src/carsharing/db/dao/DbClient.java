package carsharing.db.dao;

import carsharing.db.entity.Car;
import carsharing.db.entity.Company;
import carsharing.db.entity.Customer;
import carsharing.db.entity.Entity;
import org.h2.jdbc.JdbcSQLSyntaxErrorException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DbClient {
    private static DbClient INSTANCE;
    private final String url;

    private DbClient(String url) {
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        this.url = url;
    }

    public static DbClient getInstance(String url) {
        if (INSTANCE == null) {
            INSTANCE = new DbClient(url);
        }
        return  INSTANCE;
    }

    public void run(String query) {
        try(Connection conn = DriverManager.getConnection(url);
            Statement statement = conn.createStatement()) {
            conn.setAutoCommit(true);
            statement.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Entity> selectForList(String query) {
        List<Entity> result = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(url);
             Statement statement = conn.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            conn.setAutoCommit(true);
            while (resultSet.next()) {
                int id = resultSet.getInt("ID");
                String name = resultSet.getString("NAME");
                try {
                    int rentedCarId = resultSet.getInt("RENTED_CAR_ID");
                    try (Statement carStatement = conn.createStatement();
                         ResultSet carSet = carStatement.executeQuery("select * from CAR where ID = " +rentedCarId )) {
                        carSet.next();
                        Customer customer = new Customer(id, name);
                        if (carSet.getRow() > 0) {
                            customer.setCar(new Car(carSet.getInt("ID"), carSet.getString("NAME")));
                        }
                        result.add(customer);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                } catch (JdbcSQLSyntaxErrorException e) {
                    Company company = new Company(id, name);
                    try (Statement carStatement = conn.createStatement();
                         ResultSet carSet = carStatement.executeQuery("select * from CAR where COMPANY_ID = " + id)) {
                        while (carSet.next()) {
                            Car car = new Car(carSet.getInt("ID"), carSet.getString("NAME"), company);
                            try(Statement customerStatement = conn.createStatement();
                                ResultSet customerSet = customerStatement.executeQuery("select * from CUSTOMER where RENTED_CAR_ID = " + car.getId())) {
                                customerSet.next();
                                if (customerSet.getRow() > 0) {
                                    car.setCustomer(new Customer(customerSet.getInt("ID"), customerSet.getString("NAME")));
                                }
                            } catch (SQLException ex) {
                                e.printStackTrace();
                            }
                            company.addCar(car);
                        }
                    } catch (SQLException exception) {
                        e.printStackTrace();
                    }
                    result.add(company);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
}
