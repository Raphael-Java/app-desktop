package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import db.DB;
import db.DbException;
import model.dao.ClienteDao;
import model.entities.Cliente;

public class ClienteDaoJDBC implements ClienteDao {

    private Connection conn;

    public ClienteDaoJDBC(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void insert(Cliente obj) {
        PreparedStatement st = null;
        try {
            st = conn.prepareStatement(
                "INSERT INTO cliente " +
                "(Nome, Documento, Email, Telefone, VeiculoInteresse, VeiculoTroca, EstagioVenda, Status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
            );

            st.setString(1, obj.getNome());
            st.setString(2, obj.getDocumento());
            st.setString(3, obj.getEmail());
            st.setString(4, obj.getTelefone());
            st.setString(5, obj.getVeiculoInteresse());
            st.setString(6, obj.getVeiculoTroca());
            st.setString(7, obj.getEstagioVenda());
            st.setString(8, obj.getStatus());

            int rowsAffected = st.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet rs = st.getGeneratedKeys();
                if (rs.next()) {
                    int id = rs.getInt(1);
                    obj.setId(id);
                }
                DB.closeResultSet(rs);
            } else {
                throw new DbException("Erro inesperado! Nenhuma linha afetada.");
            }
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(st);
        }
    }

    @Override
    public void update(Cliente obj) {
        // Implementação de update se necessário
    }

    @Override
    public void deleteById(Integer id) {
        // Implementação de delete se necessário
    }

    @Override
    public Cliente findById(Integer id) {
        return null;
    }

    @Override
    public List<Cliente> findAll() {
        return new ArrayList<>();
    }
}