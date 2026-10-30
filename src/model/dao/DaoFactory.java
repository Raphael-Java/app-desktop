package model.dao;

import db.DB;
import model.dao.impl.ClienteDaoJDBC;
import model.dao.impl.DepartmentDaoJDBC;
import model.dao.impl.ManagerDaoJDBC; // 👈 Certifica-te de importar o impl do Manager
import model.dao.impl.SalesDaoJDBC;
import model.dao.impl.SellerDaoJDBC;

public interface DaoFactory {

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
		return new ManagerDaoJDBC(DB.getConnection()); // 👈 AGORA RETORNA A INSTÂNCIA CERTA
	}

	public static ClienteDao createClienteDao() {
	    return new ClienteDaoJDBC(DB.getConnection());
	}
}