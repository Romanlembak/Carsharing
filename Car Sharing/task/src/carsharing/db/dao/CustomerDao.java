package carsharing.db.dao;

import carsharing.db.entity.Customer;
import carsharing.db.entity.Entity;

import java.util.List;

public interface CustomerDao {
    void  add(Customer customer);
    void update(Customer customer);
    List<Entity> findAll();
}
