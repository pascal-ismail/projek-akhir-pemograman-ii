package view;

import controller.BarangKeluarController;
import model.Barang;
import model.BarangKeluar;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Form transaksi Barang Keluar.
 * Saat disimpan, stok barang otomatis berkurang (trigger database).
 * Jumlah keluar tidak boleh melebihi stok (divalidasi di controller).
 */
public class BarangKeluarForm extends JPanel {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final BarangKeluarController controller = new BarangKeluarController();

    private final JTextField txtNoTransaksi = new JTextField();
    private final JTextField txtTanggal = new JTextField(LocalDate.now().format(FMT));
    private final JComboBox<Barang> cmbBarang = new JComboBox<>();
    private final JTextField txtJumlah = new JTextField();
    private final JLabel lblStok = new JLabel("-");

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new String[]{"No. Transaksi", "Tanggal", "Barang", "Jumlah"}, 0) {
        @Override
        public boolean isCellEditable(int r, int c) {
            return false;
        }
    };
    private final JTable table = new JTable(tableModel);

    public BarangKeluarForm() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("Transaksi Barang Keluar");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
        add(title, BorderLayout.NORTH);
        add(buildForm(), BorderLayout.WEST);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Saat barang dipilih, tampilkan stok tersedia
        cmbBarang.addActionListener(e -> tampilkanStok());

        muatCombo();
        muatTabel();
        tampilkanStok();
    }

    private JComponent buildForm() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Input Barang Keluar"));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 5, 5, 5);
        g.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        addRow(panel, g, row++, "No. Transaksi", txtNoTransaksi);
        addRow(panel, g, row++, "Tanggal (yyyy-MM-dd)", txtTanggal);
        addRow(panel, g, row++, "Barang", cmbBarang);
        addRow(panel, g, row++, "Stok Tersedia", lblStok);
        addRow(panel, g, row++, "Jumlah", txtJumlah);

        JButton btnSimpan = new JButton("Simpan");
        JButton btnBersih = new JButton("Bersihkan");
        btnSimpan.addActionListener(e -> simpan());
        btnBersih.addActionListener(e -> bersihkan());

        JPanel buttons = new JPanel(new GridLayout(1, 2, 5, 5));
        buttons.add(btnSimpan);
        buttons.add(btnBersih);

        g.gridx = 0;
        g.gridy = row;
        g.gridwidth = 2;
        panel.add(buttons, g);
        return panel;
    }

    private void addRow(JPanel panel, GridBagConstraints g, int row, String label, JComponent field) {
        g.gridx = 0;
        g.gridy = row;
        g.gridwidth = 1;
        g.weightx = 0;
        panel.add(new JLabel(label), g);

        g.gridx = 1;
        g.weightx = 1;
        if (field instanceof JTextField) {
            ((JTextField) field).setColumns(16);
        }
        panel.add(field, g);
    }

    private void muatCombo() {
        cmbBarang.removeAllItems();
        for (Barang b : controller.getDaftarBarang()) {
            cmbBarang.addItem(b);
        }
    }

    private void muatTabel() {
        tableModel.setRowCount(0);
        List<BarangKeluar> list = controller.getRiwayatKeluar();
        for (BarangKeluar bk : list) {
            tableModel.addRow(new Object[]{
                    bk.getNoTransaksi(),
                    bk.getTanggal().format(FMT),
                    bk.getNamaBarang(),
                    bk.getJumlah()
            });
        }
    }

    private void tampilkanStok() {
        Barang b = (Barang) cmbBarang.getSelectedItem();
        lblStok.setText(b == null ? "-" : String.valueOf(b.getStok()));
    }

    private void simpan() {
        Barang barang = (Barang) cmbBarang.getSelectedItem();
        if (barang == null) {
            error("Barang harus dipilih. Pastikan data barang sudah ada.");
            return;
        }

        LocalDate tanggal;
        int jumlah;
        try {
            tanggal = LocalDate.parse(txtTanggal.getText().trim(), FMT);
        } catch (Exception ex) {
            error("Format tanggal harus yyyy-MM-dd, contoh: 2026-06-22.");
            return;
        }
        try {
            jumlah = Integer.parseInt(txtJumlah.getText().trim());
        } catch (NumberFormatException ex) {
            error("Jumlah harus berupa angka.");
            return;
        }

        BarangKeluar bk = new BarangKeluar();
        bk.setNoTransaksi(txtNoTransaksi.getText().trim());
        bk.setTanggal(tanggal);
        bk.setIdBarang(barang.getIdBarang());
        bk.setJumlah(jumlah);

        String err = controller.simpanBarangKeluar(bk);
        if (err == null) {
            info("Transaksi barang keluar tersimpan. Stok otomatis berkurang.");
            bersihkan();
            muatCombo();
            muatTabel();
        } else {
            error(err);
        }
    }

    private void bersihkan() {
        txtNoTransaksi.setText("");
        txtTanggal.setText(LocalDate.now().format(FMT));
        txtJumlah.setText("");
        if (cmbBarang.getItemCount() > 0) {
            cmbBarang.setSelectedIndex(0);
        }
        tampilkanStok();
    }

    private void info(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Informasi", JOptionPane.INFORMATION_MESSAGE);
    }

    private void error(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Peringatan", JOptionPane.WARNING_MESSAGE);
    }
}
