package carsharing;

import carsharing.db.dao.CompanyDao;
import carsharing.db.dao.CompanyDaoImpl;
import carsharing.db.dao.CustomerDao;
import carsharing.db.dao.CustomerDaoImpl;
import carsharing.db.entity.Car;
import carsharing.db.entity.Company;
import carsharing.db.entity.Customer;
import carsharing.db.entity.Entity;

import java.util.List;
import java.util.Scanner;

public class Main {
    private static final Scanner SCANNER = new Scanner(System.in);

    public static void main(String[] args) {
        String dbName = (args.length > 0) ? args[1] : "carsharing";
        CompanyDao companyDao = new CompanyDaoImpl(dbName);
        CustomerDao customerDao = new CustomerDaoImpl(dbName);
        displayMainMenu(companyDao, customerDao);
    }

    private static void displayMainMenu(CompanyDao companyDao, CustomerDao customerDao) {
        System.out.println("""
                1. Log in as a manager
                2. Log in as a customer
                3. Create a customer
                0. Exit""");
        switch (SCANNER.nextInt()) {
            case 0:
                return;
            case 1:
                displayManagerMenu(companyDao, customerDao);
                break;
            case 2:
                displayCustomerList(companyDao, customerDao);
                break;
            case 3:
                createCustomer(companyDao, customerDao);
        }
    }

    private static void createCustomer(CompanyDao companyDao, CustomerDao customerDao) {
        System.out.println("Enter the customer name:");
        SCANNER.nextLine();
        customerDao.add(new Customer(SCANNER.nextLine()));
        System.out.println("The customer was added!");
        displayMainMenu(companyDao, customerDao);
    }

    private static void displayCustomerList(CompanyDao companyDao, CustomerDao customerDao) {
        List<Entity> customerList = customerDao.findAll();
        if (customerList.isEmpty()) {
            System.out.println("The customer list is empty!");
            displayMainMenu(companyDao, customerDao);
        } else {
            System.out.println("Customer list:");
            customerList.forEach(System.out::println);
            System.out.println("0. Back");
            int id = SCANNER.nextInt();
            if (id == 0) {
                displayMainMenu(companyDao, customerDao);
            } else {
                displayCustomerMenu(companyDao, customerDao, customerList, id);
            }
        }
    }

    private static void displayCustomerMenu(CompanyDao companyDao, CustomerDao customerDao, List<Entity> customerList, int id) {
        Customer customer = (Customer) customerList.get(id - 1);
        System.out.println("""
                        1. Rent a car
                        2. Return a rented car
                        3. My rented car
                        0. Back""");
        switch (SCANNER.nextInt()) {
            case 1 -> rentCar(companyDao, customerDao, customer, customerList, id);
            case 2 -> returnRentedCar(companyDao, customerDao, customerList, id, customer);
            case 3 -> viewRentedCar(companyDao, customerDao, customerList, id, customer);
            case 0 -> displayMainMenu(companyDao, customerDao);
        }
    }

    private static void viewRentedCar(CompanyDao companyDao, CustomerDao customerDao, List<Entity> customerList, int id, Customer customer) {
        if (customer.getCar() == null) {
            System.out.println("You didn't rent a car!");
            displayCustomerMenu(companyDao, customerDao, customerList, id);
        } else {
            System.out.println("Your rented car:");
            System.out.println(customer.getCar().getName());
            System.out.println("Company:");
            System.out.println(customer.getCar().getCompany().getName());
            displayCustomerMenu(companyDao, customerDao, customerList, id);
        }
    }

    private static void returnRentedCar(CompanyDao companyDao, CustomerDao customerDao, List<Entity> customerList, int id, Customer customer) {
        if (customer.getCar() == null) {
            System.out.println("You didn't rent a car!");
            displayCustomerMenu(companyDao, customerDao, customerList, id);
        } else {
            customer.setCar(null);
            customerDao.update(customer);
            System.out.println("You've returned a rented car!");
            displayCustomerMenu(companyDao, customerDao, customerList, id);
        }
    }

    private static void rentCar(CompanyDao companyDao, CustomerDao customerDao, Customer customer, List<Entity> customerList, int id) {
        List<Entity> companies = companyDao.findAll();
        if(customer.getCar() != null) {
            System.out.println("You've already rented a car!");
            displayCustomerMenu(companyDao, customerDao, customerList, id);
        } else {
            if (companies.isEmpty()) {
                System.out.println("The company list is empty!");
                displayCustomerList(companyDao, customerDao);
            } else {
                System.out.println("Choose a company:");
                companies.forEach(System.out::println);
                System.out.println("0. Back");
                int index = SCANNER.nextInt();
                if(index == 0) {
                    displayMainMenu(companyDao, customerDao);
                } else {
                    Company company = ((Company) companies.get(index - 1));
                    List<Car> cars = company.getCars().stream()
                            .filter(car -> car.getCustomer() == null)
                            .toList();
                    if (cars.isEmpty()) {
                        System.out.printf("No available cars in the %s company%n", company.getName());
                        displayCustomerList(companyDao, customerDao);
                    } else {
                        System.out.println("Choose a car:");
                        for (int i = 1; i <= cars.size() ; i++) {
                            System.out.printf("%d. %s%n", i, cars.get(i - 1).getName());
                        }
                        System.out.println("0. Back");
                        int idOfCar = SCANNER.nextInt();
                        if (idOfCar == 0) {
                            displayMainMenu(companyDao, customerDao);
                        } else {
                            if (customer.getCar() != null) {
                                System.out.println("You've already rented a car!");
                                displayCustomerMenu(companyDao, customerDao, customerList, id);
                            } else {
                                Car car = cars.get(idOfCar - 1);
                                customer.setCar(car);
                                customerDao.update(customer);
                                System.out.printf("You rented '%s'%n", car.getName());
                                displayCustomerMenu(companyDao, customerDao, customerList, id);
                            }
                        }
                    }
                }
            }
        }

    }

    private static void displayManagerMenu(CompanyDao companyDao, CustomerDao customerDao) {
        System.out.println("""
                1. Company list
                2. Create a company
                0. Back""");
        switch (SCANNER.nextInt()) {
            case 1 -> displayCompanyList(companyDao, customerDao);
            case 2 -> createCompany(companyDao, customerDao);
            case 0 -> displayMainMenu(companyDao, customerDao);
        }
    }

    private static void createCompany(CompanyDao companyDao, CustomerDao customerDao) {
        System.out.println("Enter the company name:");
        SCANNER.nextLine();
        companyDao.add(new Company(SCANNER.nextLine()));
        System.out.println("The company was created!");
        displayManagerMenu(companyDao, customerDao);
    }

    private static void displayCompanyList(CompanyDao companyDao, CustomerDao customerDao) {
        List<Entity> companyList = companyDao.findAll();
        if (companyList.isEmpty()) {
            System.out.println("The company list is empty!");
            displayManagerMenu(companyDao, customerDao);
        } else {
            System.out.println("Choose a company:");
            companyList.forEach(System.out::println);
            System.out.println("0. Back");
            int id = SCANNER.nextInt();
            if(id == 0) {
                displayManagerMenu(companyDao, customerDao);
            } else {
                Company company = (Company) companyList.get(id - 1);
                System.out.printf(" '%s' company:%n", company.getName());
                displayCompanyMenu(company, companyDao, customerDao);
            }
        }
        displayManagerMenu(companyDao, customerDao);
    }

    private static void displayCompanyMenu(Company company, CompanyDao companyDao, CustomerDao customerDao) {
        System.out.println("""
                1. Car list
                2. Create a car
                0. Back""");
        switch (SCANNER.nextInt()) {
            case 1 -> displayCarList(company, companyDao, customerDao);
            case 2 -> createCar(company, companyDao, customerDao);
            case 0 -> displayManagerMenu(companyDao, customerDao);
        }
    }

    private static void createCar(Company company, CompanyDao companyDao, CustomerDao customerDao) {
        System.out.println("Enter the car name:");
        SCANNER.nextLine();
        companyDao.addCarToCompany(company, new Car(SCANNER.nextLine()));
        System.out.println("The car was added!");
        displayCompanyMenu(company, companyDao, customerDao);
    }

    private static void displayCarList(Company company, CompanyDao companyDao, CustomerDao customerDao) {
        List<Car> cars = company.getCars();
        if (cars.isEmpty()) {
            System.out.println("The car list is empty!");
            displayCompanyMenu(company, companyDao, customerDao);
        } else {
            for (int i = 1; i <= cars.size() ; i++) {
                System.out.printf("%d. %s", i, cars.get(i - 1).getName());
                System.out.println();
            }
            displayCompanyMenu(company, companyDao, customerDao);
        }
    }
}