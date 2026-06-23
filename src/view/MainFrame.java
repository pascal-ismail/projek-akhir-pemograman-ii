package view;

import controller.BarangController;
import controller.BarangKeluarController;
import controller.BarangMasukController;
import model.Barang;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Jendela utama aplikasi.
 * Memakai CardLayout: panel di sebelah kiri berisi menu navigasi, panel
 * tengah menampilkan modul yang dipilih (Dashboard / Barang / Supplier /
 * Barang Masuk / Barang Keluar).
 */
public class MainFrame extends JFrame {

    private final BarangController barangController = new BarangController();
    private final BarangMasukController masukController = new BarangMasukController();
    private final BarangKeluarController keluarController = new BarangKeluarController();

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel content = new JPanel(cardLayout);

    // Komponen dashboard yang perlu di-refresh
    private final JLabel lblTotalBarang = new JLabel("0");
    private final JLabel lblTotalStok = new JLabel("0");
    private final JLabel lblTotalMasuk = new JLabel("0");
    private final JLabel lblTotalKeluar = new JLabel("0");
    private final DefaultTableModel laporanModel = new DefaultTableModel(
            new String[]{"Kode", "Nama", "Kategori", "Stok", "Harga", "Nilai Stok"}, 0) {
        @Override
        public boolean isCellEditable(int r, int c) {
            return false;
        }
    };

    public MainFrame() {
        setTitle("Sistem Informasi Gudang Logistik");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(buildNav(), BorderLayout.WEST);
        add(content, BorderLayout.CENTER);

        // Daftarkan semua kartu (modul)
        content.add(buildDashboard(), "DASHBOARD");
        content.add(new BarangForm(), "BARANG");
        content.add(new SupplierForm(), "SUPPLIER");
        content.add(new BarangMasukForm(), "MASUK");
        content.add(new BarangKeluarForm(), "KELUAR");

        tampilkanDashboard();
    }

    private JComponent buildNav() {
        JPanel nav = new JPanel();
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));
        nav.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        nav.setBackground(new Color(33, 47, 61));

        JLabel header = new JLabel("MENU");
        header.setForeground(Color.WHITE);
        header.setFont(header.getFont().deriveFont(Font.BOLD, 16f));
        header.setAlignmentX(Component.CENTER_ALIGNMENT);
        nav.add(header);
        nav.add(Box.createVerticalStrut(15));

        nav.add(navButton("Dashboard", e -> tampilkanDashboard()));
        nav.add(Box.createVerticalStrut(8));
        nav.add(navButton("Data Barang", e -> cardLayout.show(content, "BARANG")));
        nav.add(Box.createVerticalStrut(8));
        nav.add(navButton("Data Supplier", e -> cardLayout.show(content, "SUPPLIER")));
        nav.add(Box.createVerticalStrut(8));
        nav.add(navButton("Barang Masuk", e -> cardLayout.show(content, "MASUK")));
        nav.add(Box.createVerticalStrut(8));
        nav.add(navButton("Barang Keluar", e -> cardLayout.show(content, "KELUAR")));
        nav.add(Box.createVerticalGlue());
        nav.add(navButton("Keluar", e -> keluarAplikasi()));

        return nav;
    }

    private JButton navButton(String text, java.awt.event.ActionListener action) {
        JButton b = new JButton(text);
        b.setAlignmentX(Component.CENTER_ALIGNMENT);
        b.setMaximumSize(new Dimension(170, 35));
        b.setFocusPainted(false);
        b.addActionListener(action);
        return b;
    }

    private JComponent buildDashboard() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("Dashboard");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 20f));
        panel.add(title, BorderLayout.NORTH);

        // Kartu ringkasan
        JPanel cards = new JPanel(new GridLayout(1, 4, 10, 10));
        cards.add(card("Total Jenis Barang", lblTotalBarang, new Color(52, 152, 219)));
        cards.add(card("Total Stok", lblTotalStok, new Color(46, 204, 113)));
        cards.add(card("Transaksi Masuk", lblTotalMasuk, new Color(155, 89, 182)));
        cards.add(card("Transaksi Keluar", lblTotalKeluar, new Color(231, 76, 60)));

        JPanel center = new JPanel(new BorderLayout(10, 10));
        center.add(cards, BorderLayout.NORTH);

        JLabel lap = new JLabel("Laporan Stok Barang");
        lap.setFont(lap.getFont().deriveFont(Font.BOLD, 14f));
        JPanel tableWrap = new JPanel(new BorderLayout(5, 5));
        tableWrap.add(lap, BorderLayout.NORTH);
        tableWrap.add(new JScrollPane(new JTable(laporanModel)), BorderLayout.CENTER);
        center.add(tableWrap, BorderLayout.CENTER);

        panel.add(center, BorderLayout.CENTER);
        return panel;
    }

    private JComponent card(String judul, JLabel valueLabel, Color warna) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(warna);
        p.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel l = new JLabel(judul);
        l.setForeground(Color.WHITE);
        valueLabel.setForeground(Color.WHITE);
        valueLabel.setFont(valueLabel.getFont().deriveFont(Font.BOLD, 28f));

        p.add(l, BorderLayout.NORTH);
        p.add(valueLabel, BorderLayout.CENTER);
        return p;
    }

    /** Memperbarui angka & tabel laporan, lalu menampilkan kartu Dashboard. */
    private void tampilkanDashboard() {
        lblTotalBarang.setText(String.valueOf(barangController.getTotalBarang()));
        lblTotalStok.setText(String.valueOf(barangController.getTotalStok()));
        lblTotalMasuk.setText(String.valueOf(masukController.getTotalTransaksiMasuk()));
        lblTotalKeluar.setText(String.valueOf(keluarController.getTotalTransaksiKeluar()));

        laporanModel.setRowCount(0);
        List<Barang> list = barangController.getAllBarang();
        for (Barang b : list) {
            laporanModel.addRow(new Object[]{
                    b.getKodeBarang(),
                    b.getNamaBarang(),
                    b.getKategori(),
                    b.getStok(),
                    b.getHarga(),
                    b.getStok() * b.getHarga()
            });
        }
        cardLayout.show(content, "DASHBOARD");
    }

    private void keluarAplikasi() {
        int pilih = JOptionPane.showConfirmDialog(this,
                "Yakin keluar dari aplikasi?", "Konfirmasi Keluar",
                JOptionPane.YES_NO_OPTION);
        if (pilih == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }
}
