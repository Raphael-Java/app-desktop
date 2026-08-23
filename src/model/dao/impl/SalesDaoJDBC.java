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
import model.dao.SalesDao;
import model.entities.Sales;
import model.entities.Seller;

public class SalesDaoJDBC implements SalesDao {

    private Connection conn;

    public SalesDaoJDBC(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void insert(Sales obj) {
        PreparedStatement st = null;
        try {
            st = conn.prepareStatement(
                "INSERT INTO sales (date, amount, status, seller_id) VALUES (?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS);

            st.setTimestamp(1, new java.sql.Timestamp(obj.getDate().getTime()));
            st.setDouble(2, obj.getAmount());
            st.setString(3, obj.getStatus());
            st.setInt(4, obj.getSeller().getId());

            int rowsAffected = st.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet rs = st.getGeneratedKeys();
                if (rs.next()) {
                    int id = rs.getInt(1);
                    obj.setId(id);
                }
                DB.closeResultSet(rs);
            } else {
                throw new DbException("Unexpected error! No rows affected!");
            }
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(st);
        }
    }

    @Override
    public void update(Sales obj) {
        PreparedStatement st = null;
        try {
            st = conn.prepareStatement(
                "UPDATE sales SET date = ?, amount = ?, status = ?, seller_id = ? WHERE id = ?");

            st.setTimestamp(1, new java.sql.Timestamp(obj.getDate().getTime()));
            st.setDouble(2, obj.getAmount());
            st.setString(3, obj.getStatus());
            st.setInt(4, obj.getSeller().getId());
            st.setInt(5, obj.getId());

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
            st = conn.prepareStatement("DELETE FROM sales WHERE id = ?");
            st.setInt(1, id);
            st.executeUpdate();
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(st);
        }
    }

    @Override
    public Sales findById(Integer id) {
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = conn.prepareStatement(
                "SELECT sales.*, seller.Name as SellerName "
                + "FROM sales INNER JOIN seller "
                + "ON sales.seller_id = seller.Id "
                + "WHERE sales.id = ?");

            st.setInt(1, id);
            rs = st.executeQuery();

            if (rs.next()) {
                Seller seller = instantiateSeller(rs);
                return instantiateSales(rs, seller);
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
    public List<Sales> findAll() {
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = conn.prepareStatement(
                "SELECT sales.*, seller.Name as SellerName "
                + "FROM sales INNER JOIN seller "
                + "ON sales.seller_id = seller.Id "
                + "ORDER BY date DESC");

            rs = st.executeQuery();

            List<Sales> list = new ArrayList<>();
            Map<Integer, Seller> map = new HashMap<>();

            while (rs.next()) {
                Seller seller = map.get(rs.getInt("seller_id"));

                if (seller == null) {
                    seller = instantiateSeller(rs);
                    map.put(rs.getInt("seller_id"), seller);
                }

                Sales obj = instantiateSales(rs, seller);
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

    private Sales instantiateSales(ResultSet rs, Seller seller) throws SQLException {
        Sales obj = new Sales();
        obj.setId(rs.getInt("id"));
        obj.setDate(new java.util.Date(rs.getTimestamp("date").getTime()));
        obj.setAmount(rs.getDouble("amount"));
        obj.setStatus(rs.getString("status"));
        obj.setSeller(seller);
        return obj;
    }

    private Seller instantiateSeller(ResultSet rs) throws SQLException {
        Seller seller = new Seller();
        seller.setId(rs.getInt("seller_id"));
        seller.setName(rs.getString("SellerName"));
        return seller;
    }
}