package carsharing.db.entity;

import java.util.ArrayList;
import java.util.List;

public class Company extends  Entity {
    private final List<Car> cars;

    public Company(int id, String name) {
        super(id, name);
        cars = new ArrayList<>();
    }

    public Company(String name) {
        super(name);
        cars = new ArrayList<>();
    }

    public List<Car> getCars() {
        return cars;
    }

    public void addCar(Car car) {
        cars.add(car);
    }
}
