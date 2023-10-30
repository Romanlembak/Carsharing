package carsharing.db.entity;

public class Customer extends Entity {
    private Car car;

    public Customer(int id, String name) {
        super(id, name);
    }

    public Customer(String nextLine) {
        super(nextLine);
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }
}
