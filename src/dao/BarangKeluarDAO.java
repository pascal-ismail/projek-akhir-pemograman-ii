package dao;

import config.DatabaseConnection;
import model.BarangKeluar;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO untuk tabel "barang_keluar".
 *
 * Catatan: pengurangan stok pada tabel barang dilakukan OTOMATIS oleh
 * trigger 'trg_stok_keluar' di database. Validasi "jumlah tidak melebihi stok"
 * dilakukan di controller sebelum insert.
 */
public class BarangKeluarDAO {

    /** Riwayat barang keluar (JOIN untuk menampilkan nama barang). */
    public List<BarangKeluar> getAll() {
        List<BarangKeluar> list = new ArrayList<>();
        String sql =
            "SELECT bk.id_keluar, bk.no_transaksi, bk.tanggal, bk.jumlah, "
          + "       bk.id_barang, b.nama_barang "
          + "FROM barang_keluar bk "
          + "JOIN barang b ON bk.id_barang = b.id_barang "
          + "ORDER BY bk.tanggal DESC, bk.id_keluar DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                BarangKeluar bk = new BarangKeluar();
                bk.setIdKeluar(rs.getInt("id_keluar"));
                bk.setNoTransaksi(rs.getString("no_transaksi"));
                bk.setTanggal(rs.getDate("tanggal").toLocalDate());
                bk.setIdBarang(rs.getInt("id_barang"));
                bk.setJumlah(rs.getInt("jumlah"));
                bk.setNamaBarang(rs.getString("nama_barang"));
                list.add(bk);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Simpan transaksi barang keluar.
     * Stok barang otomatis berkurang lewat trigger di database.
     */
    public boolean insert(BarangKeluar bk) {
        String sql = "INSERT INTO barang_keluar "
                   + "(no_transaksi, tanggal, id_barang, jumlah) "
                   + "VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, bk.getNoTransaksi());
            ps.setDate(2, Date.valueOf(bk.getTanggal()));
            ps.setInt(3, bk.getIdBarang());
            ps.setInt(4, bk.getJumlah());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Jumlah transaksi barang keluar (untuk dashboard). */
    public int countKeluar() {
        String sql = "SELECT COUNT(*) FROM barang_keluar";
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
