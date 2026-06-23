package model;

import java.time.LocalDate;

/**
 * Model (POJO) untuk tabel "barang_masuk".
 *
 * Field namaBarang & namaSupplier bukan kolom asli tabel, melainkan hasil
 * JOIN yang dipakai hanya untuk ditampilkan di JTable.
 */
public class BarangMasuk {

    private int idMasuk;
    private String noTransaksi;
    private LocalDate tanggal;
    private int idBarang;
    private int idSupplier;
    private int jumlah;

    // Field tambahan untuk tampilan (hasil JOIN)
    private String namaBarang;
    private String namaSupplier;

    public BarangMasuk() {
    }

    public int getIdMasuk() {
        return idMasuk;
    }

    public void setIdMasuk(int idMasuk) {
        this.idMasuk = idMasuk;
    }

    public String getNoTransaksi() {
        return noTransaksi;
    }

    public void setNoTransaksi(String noTransaksi) {
        this.noTransaksi = noTransaksi;
    }

    public LocalDate getTanggal() {
        return tanggal;
    }

    public void setTanggal(LocalDate tanggal) {
        this.tanggal = tanggal;
    }

    public int getIdBarang() {
        return idBarang;
    }

    public void setIdBarang(int idBarang) {
        this.idBarang = idBarang;
    }

    public int getIdSupplier() {
        return idSupplier;
    }

    public void setIdSupplier(int idSupplier) {
        this.idSupplier = idSupplier;
    }

    public int getJumlah() {
        return jumlah;
    }

    public void setJumlah(int jumlah) {
        this.jumlah = jumlah;
    }

    public String getNamaBarang() {
        return namaBarang;
    }

    public void setNamaBarang(String namaBarang) {
        this.namaBarang = namaBarang;
    }

    public String getNamaSupplier() {
        return namaSupplier;
    }

    public void setNamaSupplier(String namaSupplier) {
        this.namaSupplier = namaSupplier;
    }
}
