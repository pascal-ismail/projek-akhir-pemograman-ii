package controller;

import dao.BarangDAO;
import model.Barang;

import java.util.List;

/**
 * Controller untuk modul Data Barang.
 * Menjembatani View (BarangForm) dengan DAO, sekaligus tempat validasi
 * sederhana agar View tidak mengakses database secara langsung.
 */
public class BarangController {

    private final BarangDAO barangDAO = new BarangDAO();

    public List<Barang> getAllBarang() {
        return barangDAO.getAll();
    }

    /** Jumlah jenis barang (untuk dashboard). */
    public int getTotalBarang() {
        return barangDAO.countBarang();
    }

    /** Total seluruh stok (untuk dashboard). */
    public int getTotalStok() {
        return barangDAO.totalStok();
    }

    public List<Barang> cariBarang(String keyword) {
        return barangDAO.search(keyword);
    }

    public boolean tambahBarang(Barang b) {
        if (!valid(b)) {
            return false;
        }
        return barangDAO.insert(b);
    }

    public boolean ubahBarang(Barang b) {
        if (!valid(b) || b.getIdBarang() <= 0) {
            return false;
        }
        return barangDAO.update(b);
    }

    public boolean hapusBarang(int idBarang) {
        return barangDAO.delete(idBarang);
    }

    /** Validasi sederhana data barang. */
    private boolean valid(Barang b) {
        return b != null
            && b.getKodeBarang() != null && !b.getKodeBarang().trim().isEmpty()
            && b.getNamaBarang() != null && !b.getNamaBarang().trim().isEmpty()
            && b.getStok() >= 0
            && b.getHarga() >= 0;
    }
}
