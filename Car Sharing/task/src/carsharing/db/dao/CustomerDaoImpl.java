package carsharing.db.dao;

import carsharing.db.entity.Customer;
import carsharing.db.entity.Entity;

import java.util.List;

public class CustomerDaoImpl implements CustomerDao{
    private static final String CONNECTION_URL = "jdbc:h2:./src/carsharing/db/";
    private static final String CREATE_TABLE_CUSTOMER = "create table if not exists CUSTOMER (" +
            "ID int auto_increment primary key, " +
            "NAME varchar(20) not null unique, " +
            "RENTED_CAR_ID int, " +
            "constraint FK_CAR foreign key(RENTED_CAR_ID) " +
            "references CAR(ID))";
    private static final String INSERT_DATA_TO_CUSTOMER = "insert into CUSTOMER (NAME) values ('%s')";
    private static final String RENT_CAR = "update CUSTOMER set RENTED_CAR_ID = %d where ID = %d";
    private static final String RETURN_CAR = "update CUSTOMER set RENTED_CAR_ID = null where ID = %d";
    private static final String SELECT_ALL = "select * from CUSTOMER";
    private static final String ID_RESTART = "alter table CUSTOMER alter column ID restart with 1";
     private final DbClient dbClient;

    public CustomerDaoImpl(String dbName) {
        dbClient = DbClient.getInstance(CONNECTION_URL + dbName);
        dbClient.run(CREATE_TABLE_CUSTOMER);
        dbClient.run(ID_RESTART);
    }

    @Override
    public void add(Customer customer) {
        dbClient.run(String.format(INSERT_DATA_TO_CUSTOMER, customer.getName()));
    }

    @Override
    public void update(Customer customer) {
        if(customer.getCar() == null) {
            dbClient.run(String.format(RETURN_CAR,customer.getId()));
        } else {
            dbClient.run(String.format(RENT_CAR, customer.getCar().getId(), customer.getId()));
        }

    }

    @Override
    public List<Entity> findAll() {
        return dbClient.selectForList(SELECT_ALL);
    }
}
