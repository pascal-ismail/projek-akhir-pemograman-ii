package dao;

import config.DatabaseConnection;
import model.BarangMasuk;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO untuk tabel "barang_masuk".
 *
 * Catatan: penambahan stok pada tabel barang dilakukan OTOMATIS oleh
 * trigger 'trg_stok_masuk' di database, jadi tidak perlu UPDATE stok dari Java.
 */
public class BarangMasukDAO {

    /** Riwayat barang masuk (JOIN untuk menampilkan nama barang & supplier). */
    public List<BarangMasuk> getAll() {
        List<BarangMasuk> list = new ArrayList<>();
        String sql =
            "SELECT bm.id_masuk, bm.no_transaksi, bm.tanggal, bm.jumlah, "
          + "       bm.id_barang, bm.id_supplier, "
          + "       b.nama_barang, s.nama_supplier "
          + "FROM barang_masuk bm "
          + "JOIN barang   b ON bm.id_barang   = b.id_barang "
          + "JOIN supplier s ON bm.id_supplier = s.id_supplier "
          + "ORDER BY bm.tanggal DESC, bm.id_masuk DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                BarangMasuk bm = new BarangMasuk();
                bm.setIdMasuk(rs.getInt("id_masuk"));
                bm.setNoTransaksi(rs.getString("no_transaksi"));
                bm.setTanggal(rs.getDate("tanggal").toLocalDate());
                bm.setIdBarang(rs.getInt("id_barang"));
                bm.setIdSupplier(rs.getInt("id_supplier"));
                bm.setJumlah(rs.getInt("jumlah"));
                bm.setNamaBarang(rs.getString("nama_barang"));
                bm.setNamaSupplier(rs.getString("nama_supplier"));
                list.add(bm);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Simpan transaksi barang masuk.
     * Stok barang otomatis bertambah lewat trigger di database.
     */
    public boolean insert(BarangMasuk bm) {
        String sql = "INSERT INTO barang_masuk "
                   + "(no_transaksi, tanggal, id_barang, id_supplier, jumlah) "
                   + "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, bm.getNoTransaksi());
            ps.setDate(2, Date.valueOf(bm.getTanggal()));
            ps.setInt(3, bm.getIdBarang());
            ps.setInt(4, bm.getIdSupplier());
            ps.setInt(5, bm.getJumlah());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Jumlah transaksi barang masuk (untuk dashboard). */
    public int countMasuk() {
        String sql = "SELECT COUNT(*) FROM barang_masuk";
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
}
