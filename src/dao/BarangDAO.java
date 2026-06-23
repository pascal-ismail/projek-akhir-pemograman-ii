package dao;

import config.DatabaseConnection;
import model.Barang;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO (Data Access Object) untuk tabel "barang".
 * Seluruh akses database ke tabel barang dilakukan di sini, memakai
 * PreparedStatement agar aman dari SQL Injection.
 */
public class BarangDAO {

    /** Ambil semua barang. */
    public List<Barang> getAll() {
        List<Barang> list = new ArrayList<>();
        String sql = "SELECT * FROM barang ORDER BY kode_barang";

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

    /** Cari barang berdasarkan kode / nama / kategori. */
    public List<Barang> search(String keyword) {
        List<Barang> list = new ArrayList<>();
        String sql = "SELECT * FROM barang "
                   + "WHERE kode_barang LIKE ? OR nama_barang LIKE ? OR kategori LIKE ? "
                   + "ORDER BY kode_barang";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String key = "%" + keyword + "%";
            ps.setString(1, key);
            ps.setString(2, key);
            ps.setString(3, key);

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

    /** Ambil satu barang berdasarkan id. */
    public Barang getById(int idBarang) {
        String sql = "SELECT * FROM barang WHERE id_barang = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idBarang);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /** Tambah barang baru. */
    public boolean insert(Barang b) {
        String sql = "INSERT INTO barang (kode_barang, nama_barang, kategori, stok, harga) "
                   + "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, b.getKodeBarang());
            ps.setString(2, b.getNamaBarang());
            ps.setString(3, b.getKategori());
            ps.setInt(4, b.getStok());
            ps.setDouble(5, b.getHarga());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Ubah data barang. */
    public boolean update(Barang b) {
        String sql = "UPDATE barang SET kode_barang = ?, nama_barang = ?, "
                   + "kategori = ?, stok = ?, harga = ? WHERE id_barang = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, b.getKodeBarang());
            ps.setString(2, b.getNamaBarang());
            ps.setString(3, b.getKategori());
            ps.setInt(4, b.getStok());
            ps.setDouble(5, b.getHarga());
            ps.setInt(6, b.getIdBarang());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Hapus barang berdasarkan id. */
    public boolean delete(int idBarang) {
        String sql = "DELETE FROM barang WHERE id_barang = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idBarang);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Ambil stok terkini sebuah barang (dipakai untuk validasi barang keluar). */
    public int getStok(int idBarang) {
        String sql = "SELECT stok FROM barang WHERE id_barang = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idBarang);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("stok");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /** Jumlah seluruh jenis barang (untuk dashboard). */
    public int countBarang() {
        String sql = "SELECT COUNT(*) FROM barang";
        return singleInt(sql);
    }

    /** Total seluruh stok (untuk dashboard). */
    public int totalStok() {
        String sql = "SELECT COALESCE(SUM(stok), 0) FROM barang";
        return singleInt(sql);
    }

    // ---------- helper privat ----------

    private int singleInt(String sql) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /** Ubah satu baris ResultSet menjadi objek Barang. */
    private Barang mapRow(ResultSet rs) throws SQLException {
        Barang b = new Barang();
        b.setIdBarang(rs.getInt("id_barang"));
        b.setKodeBarang(rs.getString("kode_barang"));
        b.setNamaBarang(rs.getString("nama_barang"));
        b.setKategori(rs.getString("kategori"));
        b.setStok(rs.getInt("stok"));
        b.setHarga(rs.getDouble("harga"));
        return b;
    }
}
