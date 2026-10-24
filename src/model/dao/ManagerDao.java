package model.dao;

import java.util.List;
import model.entities.Manager;

public interface ManagerDao {
    void insert(Manager obj);
    void update(Manager obj);
    void deleteById(Integer id);
    Manager findById(Integer id);
    List<Manager> findAll();
}