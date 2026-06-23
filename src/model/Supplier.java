package model;

/**
 * Model (POJO) untuk tabel "supplier".
 */
public class Supplier {

    private int idSupplier;
    private String kodeSupplier;
    private String namaSupplier;
    private String alamat;
    private String telepon;

    public Supplier() {
    }

    public Supplier(int idSupplier, String kodeSupplier, String namaSupplier,
                    String alamat, String telepon) {
        this.idSupplier = idSupplier;
        this.kodeSupplier = kodeSupplier;
        this.namaSupplier = namaSupplier;
        this.alamat = alamat;
        this.telepon = telepon;
    }

    public int getIdSupplier() {
        return idSupplier;
    }

    public void setIdSupplier(int idSupplier) {
        this.idSupplier = idSupplier;
    }

    public String getKodeSupplier() {
        return kodeSupplier;
    }

    public void setKodeSupplier(String kodeSupplier) {
        this.kodeSupplier = kodeSupplier;
    }

    public String getNamaSupplier() {
        return namaSupplier;
    }

    public void setNamaSupplier(String namaSupplier) {
        this.namaSupplier = namaSupplier;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getTelepon() {
        return telepon;
    }

    public void setTelepon(String telepon) {
        this.telepon = telepon;
    }

    @Override
    public String toString() {
        return kodeSupplier + " - " + namaSupplier;
    }
}
