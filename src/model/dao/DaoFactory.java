package model.dao;

import db.DB;
import model.dao.impl.DepartmentDaoJDBC;
import model.dao.impl.SalesDaoJDBC;
import model.dao.impl.SellerDaoJDBC;

public class DaoFactory {

	public static SellerDao createSellerDao() {
		return new SellerDaoJDBC(DB.getConnection());
	}
	
	public static DepartmentDao createDepartmentDao() {
		return new DepartmentDaoJDBC(DB.getConnection());
	}
	
	public static SalesDao createSalesDao() {
	    return new SalesDaoJDBC(DB.getConnection());
	}

	public static ManagerDao createManagerDao() {
		// TODO Auto-generated method stub
		return null;
	}
}
