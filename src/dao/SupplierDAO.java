package dao;

import config.DatabaseConnection;
import model.Supplier;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO untuk tabel "supplier".
 */
public class SupplierDAO {

    public List<Supplier> getAll() {
        List<Supplier> list = new ArrayList<>();
        String sql = "SELECT * FROM supplier ORDER BY kode_supplier";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Supplier> search(String keyword) {
        List<Supplier> list = new ArrayList<>();
        String sql = "SELECT * FROM supplier "
                   + "WHERE kode_supplier LIKE ? OR nama_supplier LIKE ? "
                   + "ORDER BY kode_supplier";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String key = "%" + keyword + "%";
            ps.setString(1, key);
            ps.setString(2, key);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insert(Supplier s) {
        String sql = "INSERT INTO supplier (kode_supplier, nama_supplier, alamat, telepon) "
                   + "VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, s.getKodeSupplier());
            ps.setString(2, s.getNamaSupplier());
            ps.setString(3, s.getAlamat());
            ps.setString(4, s.getTelepon());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(Supplier s) {
        String sql = "UPDATE supplier SET kode_supplier = ?, nama_supplier = ?, "
                   + "alamat = ?, telepon = ? WHERE id_supplier = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, s.getKodeSupplier());
            ps.setString(2, s.getNamaSupplier());
            ps.setString(3, s.getAlamat());
            ps.setString(4, s.getTelepon());
            ps.setInt(5, s.getIdSupplier());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int idSupplier) {
        String sql = "DELETE FROM supplier WHERE id_supplier = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idSupplier);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Supplier mapRow(ResultSet rs) throws SQLException {
        Supplier s = new Supplier();
        s.setIdSupplier(rs.getInt("id_supplier"));
        s.setKodeSupplier(rs.getString("kode_supplier"));
        s.setNamaSupplier(rs.getString("nama_supplier"));
        s.setAlamat(rs.getString("alamat"));
        s.setTelepon(rs.getString("telepon"));
        return s;
    }
}
