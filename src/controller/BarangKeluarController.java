package controller;

import dao.BarangDAO;
import dao.BarangKeluarDAO;
import model.Barang;
import model.BarangKeluar;

import java.util.List;

/**
 * Controller untuk modul Barang Keluar.
 * Stok berkurang otomatis lewat trigger database. Sebelum insert, jumlah
 * keluar divalidasi agar tidak melebihi stok yang tersedia.
 */
public class BarangKeluarController {

    private final BarangKeluarDAO barangKeluarDAO = new BarangKeluarDAO();
    private final BarangDAO barangDAO = new BarangDAO();
    private final BarangController barangController = new BarangController();

    public List<BarangKeluar> getRiwayatKeluar() {
        return barangKeluarDAO.getAll();
    }

    /** Jumlah transaksi barang keluar (untuk dashboard). */
    public int getTotalTransaksiKeluar() {
        return barangKeluarDAO.countKeluar();
    }

    /** Daftar barang untuk pilihan di JComboBox. */
    public List<Barang> getDaftarBarang() {
        return barangController.getAllBarang();
    }

    /**
     * Simpan transaksi barang keluar.
     * @return pesan error, atau null jika berhasil.
     */
    public String simpanBarangKeluar(BarangKeluar bk) {
        if (bk.getNoTransaksi() == null || bk.getNoTransaksi().trim().isEmpty()) {
            return "Nomor transaksi wajib diisi.";
        }
        if (bk.getTanggal() == null) {
            return "Tanggal tidak valid.";
        }
        if (bk.getIdBarang() <= 0) {
            return "Barang belum dipilih.";
        }
        if (bk.getJumlah() <= 0) {
            return "Jumlah harus lebih dari 0.";
        }

        // Validasi stok: jumlah keluar tidak boleh melebihi stok tersedia
        int stok = barangDAO.getStok(bk.getIdBarang());
        if (bk.getJumlah() > stok) {
            return "Jumlah keluar (" + bk.getJumlah() + ") melebihi stok tersedia (" + stok + ").";
        }

        boolean ok = barangKeluarDAO.insert(bk);
        return ok ? null : "Gagal menyimpan transaksi (cek nomor transaksi unik / koneksi DB).";
    }
}
