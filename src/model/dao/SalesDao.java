package model.dao;

import java.util.List;
import model.entities.Sales;

public interface SalesDao {
    void insert(Sales obj);
    void update(Sales obj);
    void deleteById(Integer id);
    Sales findById(Integer id);
    List<Sales> findAll();
}