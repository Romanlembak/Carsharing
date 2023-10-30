package carsharing.db.entity;

public class Car extends Entity {
    private Company company;
    private Customer customer;

    public Car(int id, String name, Company company) {
        super(id, name);
        this.company = company;
    }

    public Car(String name) {
        super(name);
    }

    public Car(int id, String name) {
        super(id, name);
    }

    public Company getCompany() {
        return company;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}
