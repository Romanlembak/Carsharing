package carsharing.db.dao;

import carsharing.db.entity.Car;
import carsharing.db.entity.Company;
import carsharing.db.entity.Entity;

import java.util.List;

public interface CompanyDao {
    List<Entity> findAll();
    void add(Company company);
    void addCarToCompany(Company company, Car car);
}
