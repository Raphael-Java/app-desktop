package model.services;

import java.util.List;

import model.dao.DaoFactory;
import model.dao.ManagerDao;
import model.entities.Manager;

public class ManagerService {

    private ManagerDao dao = DaoFactory.createManagerDao();

    public List<Manager> findAll() {
        return dao.findAll();
    }

    public void saveOrUpdate(Manager obj) {
        if (obj.getId() == null) {
            dao.insert(obj);
        } else {
            dao.update(obj);
        }
    }

    public void remove(Manager obj) {
        dao.deleteById(obj.getId());
    }
}