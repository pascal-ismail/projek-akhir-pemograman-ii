package model;

/**
 * Model (POJO) untuk tabel "barang".
 * Hanya menyimpan data, tanpa logika database.
 */
public class Barang {

    private int idBarang;
    private String kodeBarang;
    private String namaBarang;
    private String kategori;
    private int stok;
    private double harga;

    public Barang() {
    }

    public Barang(int idBarang, String kodeBarang, String namaBarang,
                  String kategori, int stok, double harga) {
        this.idBarang = idBarang;
        this.kodeBarang = kodeBarang;
        this.namaBarang = namaBarang;
        this.kategori = kategori;
        this.stok = stok;
        this.harga = harga;
    }

    public int getIdBarang() {
        return idBarang;
    }

    public void setIdBarang(int idBarang) {
        this.idBarang = idBarang;
    }

    public String getKodeBarang() {
        return kodeBarang;
    }

    public void setKodeBarang(String kodeBarang) {
        this.kodeBarang = kodeBarang;
    }

    public String getNamaBarang() {
        return namaBarang;
    }

    public void setNamaBarang(String namaBarang) {
        this.namaBarang = namaBarang;
    }

    public String getKategori() {
        return kategori;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }

    public int getStok() {
        return stok;
    }

    public void setStok(int stok) {
        this.stok = stok;
    }

    public double getHarga() {
        return harga;
    }

    public void setHarga(double harga) {
        this.harga = harga;
    }

    /**
     * Ditampilkan saat objek dipakai di JComboBox (mis. form transaksi).
     */
    @Override
    public String toString() {
        return kodeBarang + " - " + namaBarang;
    }
}
