package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import db.DB;
import db.DbException;
import model.dao.ManagerDao;
import model.entities.Department;
import model.entities.Manager;

public class ManagerDaoJDBC implements ManagerDao {

    private Connection conn;

    public ManagerDaoJDBC(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void insert(Manager obj) {
        PreparedStatement st = null;
        try {
            st = conn.prepareStatement(
                "INSERT INTO manager (name, email, department_id) VALUES (?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
            );
            st.setString(1, obj.getName());
            st.setString(2, obj.getEmail());
            st.setInt(3, obj.getDepartment().getId());

            int rowsAffected = st.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet rs = st.getGeneratedKeys();
                if (rs.next()) {
                    obj.setId(rs.getInt(1));
                }
                DB.closeResultSet(rs);
            } else {
                throw new DbException("Erro inesperado! Nenhuma linha foi alterada.");
            }
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(st);
        }
    }

    @Override
    public void update(Manager obj) {
        PreparedStatement st = null;
        try {
            st = conn.prepareStatement(
                "UPDATE manager SET name = ?, email = ?, department_id = ? WHERE id = ?"
            );
            st.setString(1, obj.getName());
            st.setString(2, obj.getEmail());
            st.setInt(3, obj.getDepartment().getId());
            st.setInt(4, obj.getId());

            st.executeUpdate();
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(st);
        }
    }

    @Override
    public void deleteById(Integer id) {
        PreparedStatement st = null;
        try {
            st = conn.prepareStatement("DELETE FROM manager WHERE id = ?");
            st.setInt(1, id);
            st.executeUpdate();
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(st);
        }
    }

    @Override
    public Manager findById(Integer id) {
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = conn.prepareStatement(
                "SELECT manager.*, department.name as depName " +
                "FROM manager INNER JOIN department " +
                "ON manager.department_id = department.id " +
                "WHERE manager.id = ?"
            );
            st.setInt(1, id);
            rs = st.executeQuery();

            if (rs.next()) {
                Department dep = instantiateDepartment(rs);
                return instantiateManager(rs, dep);
            }
            return null;
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(st);
            DB.closeResultSet(rs);
        }
    }

    @Override
    public List<Manager> findAll() {
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = conn.prepareStatement(
                "SELECT manager.*, department.name as depName " +
                "FROM manager INNER JOIN department " +
                "ON manager.department_id = department.id " +
                "ORDER BY name"
            );
            rs = st.executeQuery();

            List<Manager> list = new ArrayList<>();
            Map<Integer, Department> map = new HashMap<>();

            while (rs.next()) {
                Department dep = map.get(rs.getInt("department_id"));
                if (dep == null) {
                    dep = instantiateDepartment(rs);
                    map.put(rs.getInt("department_id"), dep);
                }
                Manager obj = instantiateManager(rs, dep);
                list.add(obj);
            }
            return list;
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(st);
            DB.closeResultSet(rs);
        }
    }

    private Manager instantiateManager(ResultSet rs, Department dep) throws SQLException {
        Manager obj = new Manager();
        obj.setId(rs.getInt("id"));
        obj.setName(rs.getString("name"));
        obj.setEmail(rs.getString("email"));
        obj.setDepartment(dep);
        return obj;
    }

    private Department instantiateDepartment(ResultSet rs) throws SQLException {
        Department dep = new Department();
        dep.setId(rs.getInt("department_id"));
        dep.setName(rs.getString("depName"));
        return dep;
    }
}