package carsharing.db.dao;

import carsharing.db.entity.Car;
import carsharing.db.entity.Company;
import carsharing.db.entity.Entity;

import java.util.List;

public class CompanyDaoImpl implements CompanyDao {
    private static final String CONNECTION_URL = "jdbc:h2:./src/carsharing/db/";
    private static final String CREATE_TABLE_COMPANY = "create table if not exists COMPANY (" +
            "ID int auto_increment primary key," +
            "NAME varchar(20) not null unique)";
    private static final String CREATE_TABLE_CAR = "create table if not exists CAR (" +
            "ID int auto_increment primary key, " +
            "NAME varchar(20) not null unique, " +
            "COMPANY_ID int not null, " +
            "constraint FK_COMPANY foreign key(COMPANY_ID) " +
            "references COMPANY(ID))";
    private static final String SELECT_ALL = "select * from COMPANY";
    private static final String INSERT_DATA_TO_COMPANY = "insert into COMPANY (NAME) values ('%s')";
    private static final String INSERT_DATA_TO_CAR = "insert into CAR (NAME, COMPANY_ID) values ('%s', %d)";
    private static final String ID_RESTART = "alter table COMPANY alter column ID restart with 1";
    private final DbClient dbClient;

    public CompanyDaoImpl(String dbName) {
        dbClient = DbClient.getInstance(CONNECTION_URL + dbName);
        dbClient.run(CREATE_TABLE_COMPANY);
        dbClient.run(ID_RESTART);
        dbClient.run(CREATE_TABLE_CAR);
    }

    @Override
    public List<Entity> findAll() {
        return dbClient.selectForList(SELECT_ALL);
    }

    @Override
    public void add(Company company) {
        dbClient.run(String.format(INSERT_DATA_TO_COMPANY, company.getName()));
    }

    @Override
    public void addCarToCompany(Company company, Car car) {
        company.addCar(car);
        dbClient.run(String.format(INSERT_DATA_TO_CAR, car.getName(), company.getId()));
    }
}
