package controller;

import dao.BarangMasukDAO;
import model.Barang;
import model.BarangMasuk;

import java.util.List;

/**
 * Controller untuk modul Barang Masuk.
 * Stok bertambah otomatis lewat trigger database saat insert berhasil.
 */
public class BarangMasukController {

    private final BarangMasukDAO barangMasukDAO = new BarangMasukDAO();
    private final BarangController barangController = new BarangController();

    public List<BarangMasuk> getRiwayatMasuk() {
        return barangMasukDAO.getAll();
    }

    /** Jumlah transaksi barang masuk (untuk dashboard). */
    public int getTotalTransaksiMasuk() {
        return barangMasukDAO.countMasuk();
    }

    /** Daftar barang untuk pilihan di JComboBox. */
    public List<Barang> getDaftarBarang() {
        return barangController.getAllBarang();
    }

    /**
     * Simpan transaksi barang masuk.
     * @return pesan error, atau null jika berhasil.
     */
    public String simpanBarangMasuk(BarangMasuk bm) {
        if (bm.getNoTransaksi() == null || bm.getNoTransaksi().trim().isEmpty()) {
            return "Nomor transaksi wajib diisi.";
        }
        if (bm.getTanggal() == null) {
            return "Tanggal tidak valid.";
        }
        if (bm.getIdBarang() <= 0) {
            return "Barang belum dipilih.";
        }
        if (bm.getIdSupplier() <= 0) {
            return "Supplier belum dipilih.";
        }
        if (bm.getJumlah() <= 0) {
            return "Jumlah harus lebih dari 0.";
        }
        boolean ok = barangMasukDAO.insert(bm);
        return ok ? null : "Gagal menyimpan transaksi (cek nomor transaksi unik / koneksi DB).";
    }
}
