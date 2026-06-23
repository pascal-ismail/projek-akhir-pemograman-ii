package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Kelas pusat koneksi database (JDBC) ke MySQL.
 *
 * Semua DAO mengambil koneksi melalui kelas ini, sehingga pengaturan
 * koneksi cukup ditulis di satu tempat. Sesuaikan USER / PASSWORD dengan
 * konfigurasi MySQL di komputer Anda.
 */
public class DatabaseConnection {

    // Ubah bagian ini sesuai konfigurasi MySQL Anda
    private static final String HOST = "localhost";
    private static final String PORT = "3306";
    private static final String DB_NAME = "db_logistik";
    private static final String USER = "root";
    private static final String PASSWORD = ""; // isi jika MySQL Anda memakai password

    private static final String URL =
            "jdbc:mysql://" + HOST + ":" + PORT + "/" + DB_NAME
            + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Jakarta";

    /**
     * Mengembalikan koneksi baru ke database.
     * Pemanggil bertanggung jawab menutup koneksi (gunakan try-with-resources).
     */
    public static Connection getConnection() throws SQLException {
        try {
            // Memuat driver MySQL (mysql-connector-j)
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException(
                "Driver MySQL tidak ditemukan. "
                + "Tambahkan library 'mysql-connector-j' ke project.", e);
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
