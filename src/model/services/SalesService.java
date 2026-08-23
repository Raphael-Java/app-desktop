package model.services;

import java.util.List;
import model.dao.DaoFactory;
import model.dao.SalesDao;
import model.entities.Sales;

public class SalesService {

    private SalesDao dao = DaoFactory.createSalesDao();

    public List<Sales> findAll() {
        return dao.findAll();
    }

    public void saveOrUpdate(Sales obj) {
        if (obj.getId() == null) {
            dao.insert(obj);
        } else {
            dao.update(obj);
        }
    }

    public void remove(Sales obj) {
        dao.deleteById(obj.getId());
    }
}