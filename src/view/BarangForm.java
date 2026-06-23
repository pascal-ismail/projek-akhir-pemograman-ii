package view;

import controller.BarangController;
import model.Barang;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Form CRUD Data Barang.
 * Berisi field input, tombol Tambah/Ubah/Hapus/Bersihkan, pencarian, dan JTable.
 * Semua akses data dilakukan lewat BarangController (View tidak menyentuh DB).
 */
public class BarangForm extends JPanel {

    private final BarangController controller = new BarangController();

    private final JTextField txtKode = new JTextField();
    private final JTextField txtNama = new JTextField();
    private final JTextField txtKategori = new JTextField();
    private final JTextField txtStok = new JTextField();
    private final JTextField txtHarga = new JTextField();
    private final JTextField txtCari = new JTextField();

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new String[]{"ID", "Kode", "Nama", "Kategori", "Stok", "Harga"}, 0) {
        @Override
        public boolean isCellEditable(int r, int c) {
            return false;
        }
    };
    private final JTable table = new JTable(tableModel);

    private int selectedId = 0; // id barang yang sedang dipilih (0 = belum ada)

    public BarangForm() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                isiFormDariTabel();
            }
        });

        add(buildHeader(), BorderLayout.NORTH);
        add(buildForm(), BorderLayout.WEST);
        add(buildTable(), BorderLayout.CENTER);

        loadData(controller.getAllBarang());
    }

    private JComponent buildHeader() {
        JLabel title = new JLabel("Data Barang");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
        return title;
    }

    private JComponent buildForm() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Input Barang"));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 5, 5, 5);
        g.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        addField(panel, g, row++, "Kode Barang", txtKode);
        addField(panel, g, row++, "Nama Barang", txtNama);
        addField(panel, g, row++, "Kategori", txtKategori);
        addField(panel, g, row++, "Stok", txtStok);
        addField(panel, g, row++, "Harga", txtHarga);

        JButton btnTambah = new JButton("Tambah");
        JButton btnUbah = new JButton("Ubah");
        JButton btnHapus = new JButton("Hapus");
        JButton btnBersih = new JButton("Bersihkan");

        btnTambah.addActionListener(e -> tambah());
        btnUbah.addActionListener(e -> ubah());
        btnHapus.addActionListener(e -> hapus());
        btnBersih.addActionListener(e -> bersihkan());

        JPanel buttons = new JPanel(new GridLayout(2, 2, 5, 5));
        buttons.add(btnTambah);
        buttons.add(btnUbah);
        buttons.add(btnHapus);
        buttons.add(btnBersih);

        g.gridx = 0;
        g.gridy = row;
        g.gridwidth = 2;
        panel.add(buttons, g);

        return panel;
    }

    private void addField(JPanel panel, GridBagConstraints g, int row, String label, JTextField field) {
        g.gridx = 0;
        g.gridy = row;
        g.gridwidth = 1;
        g.weightx = 0;
        panel.add(new JLabel(label), g);

        g.gridx = 1;
        g.weightx = 1;
        field.setColumns(16);
        panel.add(field, g);
    }

    private JComponent buildTable() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));

        // Bar pencarian
        JPanel searchBar = new JPanel(new BorderLayout(5, 5));
        searchBar.add(new JLabel("Cari: "), BorderLayout.WEST);
        searchBar.add(txtCari, BorderLayout.CENTER);
        JButton btnCari = new JButton("Cari");
        JButton btnReset = new JButton("Tampilkan Semua");
        btnCari.addActionListener(e -> loadData(controller.cariBarang(txtCari.getText().trim())));
        btnReset.addActionListener(e -> {
            txtCari.setText("");
            loadData(controller.getAllBarang());
        });
        JPanel searchBtns = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        searchBtns.add(btnCari);
        searchBtns.add(btnReset);
        searchBar.add(searchBtns, BorderLayout.EAST);

        panel.add(searchBar, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    // ---------- aksi tombol ----------

    private void tambah() {
        Barang b = bacaForm();
        if (b == null) {
            return;
        }
        if (controller.tambahBarang(b)) {
            info("Barang berhasil ditambahkan.");
            bersihkan();
            loadData(controller.getAllBarang());
        } else {
            error("Gagal menambahkan barang. Periksa input / kode barang mungkin duplikat.");
        }
    }

    private void ubah() {
        if (selectedId == 0) {
            error("Pilih data pada tabel terlebih dahulu.");
            return;
        }
        Barang b = bacaForm();
        if (b == null) {
            return;
        }
        b.setIdBarang(selectedId);
        if (controller.ubahBarang(b)) {
            info("Barang berhasil diubah.");
            bersihkan();
            loadData(controller.getAllBarang());
        } else {
            error("Gagal mengubah barang.");
        }
    }

    private void hapus() {
        if (selectedId == 0) {
            error("Pilih data pada tabel terlebih dahulu.");
            return;
        }
        int konfirmasi = JOptionPane.showConfirmDialog(this,
                "Yakin menghapus barang ini?", "Konfirmasi",
                JOptionPane.YES_NO_OPTION);
        if (konfirmasi != JOptionPane.YES_OPTION) {
            return;
        }
        if (controller.hapusBarang(selectedId)) {
            info("Barang berhasil dihapus.");
            bersihkan();
            loadData(controller.getAllBarang());
        } else {
            error("Gagal menghapus. Barang mungkin masih dipakai di transaksi.");
        }
    }

    private void bersihkan() {
        selectedId = 0;
        txtKode.setText("");
        txtNama.setText("");
        txtKategori.setText("");
        txtStok.setText("");
        txtHarga.setText("");
        table.clearSelection();
    }

    // ---------- util form & tabel ----------

    /** Membaca isi form menjadi objek Barang, dengan validasi angka. */
    private Barang bacaForm() {
        String kode = txtKode.getText().trim();
        String nama = txtNama.getText().trim();
        String kategori = txtKategori.getText().trim();

        if (kode.isEmpty() || nama.isEmpty() || kategori.isEmpty()) {
            error("Kode, Nama, dan Kategori wajib diisi.");
            return null;
        }

        int stok;
        double harga;
        try {
            stok = Integer.parseInt(txtStok.getText().trim());
            harga = Double.parseDouble(txtHarga.getText().trim());
        } catch (NumberFormatException ex) {
            error("Stok harus berupa bilangan bulat dan Harga berupa angka.");
            return null;
        }
        if (stok < 0 || harga < 0) {
            error("Stok dan Harga tidak boleh negatif.");
            return null;
        }

        Barang b = new Barang();
        b.setKodeBarang(kode);
        b.setNamaBarang(nama);
        b.setKategori(kategori);
        b.setStok(stok);
        b.setHarga(harga);
        return b;
    }

    /** Mengisi form ketika sebuah baris tabel dipilih. */
    private void isiFormDariTabel() {
        int row = table.getSelectedRow();
        if (row < 0) {
            return;
        }
        selectedId = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
        txtKode.setText(tableModel.getValueAt(row, 1).toString());
        txtNama.setText(tableModel.getValueAt(row, 2).toString());
        txtKategori.setText(tableModel.getValueAt(row, 3).toString());
        txtStok.setText(tableModel.getValueAt(row, 4).toString());
        txtHarga.setText(tableModel.getValueAt(row, 5).toString());
    }

    /** Mengisi JTable dari daftar barang. */
    private void loadData(List<Barang> list) {
        tableModel.setRowCount(0);
        for (Barang b : list) {
            tableModel.addRow(new Object[]{
                    b.getIdBarang(),
                    b.getKodeBarang(),
                    b.getNamaBarang(),
                    b.getKategori(),
                    b.getStok(),
                    b.getHarga()
            });
        }
    }

    private void info(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Informasi", JOptionPane.INFORMATION_MESSAGE);
    }

    private void error(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Peringatan", JOptionPane.WARNING_MESSAGE);
    }
}
