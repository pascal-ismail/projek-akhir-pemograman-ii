package view;

import controller.BarangMasukController;
import model.Barang;
import model.BarangMasuk;
import model.Supplier;
import controller.SupplierController;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Form transaksi Barang Masuk.
 * Saat disimpan, stok barang otomatis bertambah (trigger database).
 */
public class BarangMasukForm extends JPanel {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final BarangMasukController controller = new BarangMasukController();
    private final SupplierController supplierController = new SupplierController();

    private final JTextField txtNoTransaksi = new JTextField();
    private final JTextField txtTanggal = new JTextField(LocalDate.now().format(FMT));
    private final JComboBox<Barang> cmbBarang = new JComboBox<>();
    private final JComboBox<Supplier> cmbSupplier = new JComboBox<>();
    private final JTextField txtJumlah = new JTextField();

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new String[]{"No. Transaksi", "Tanggal", "Barang", "Supplier", "Jumlah"}, 0) {
        @Override
        public boolean isCellEditable(int r, int c) {
            return false;
        }
    };
    private final JTable table = new JTable(tableModel);

    public BarangMasukForm() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("Transaksi Barang Masuk");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
        add(title, BorderLayout.NORTH);
        add(buildForm(), BorderLayout.WEST);
        add(new JScrollPane(table), BorderLayout.CENTER);

        muatCombo();
        muatTabel();
    }

    private JComponent buildForm() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Input Barang Masuk"));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 5, 5, 5);
        g.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        addRow(panel, g, row++, "No. Transaksi", txtNoTransaksi);
        addRow(panel, g, row++, "Tanggal (yyyy-MM-dd)", txtTanggal);
        addRow(panel, g, row++, "Barang", cmbBarang);
        addRow(panel, g, row++, "Supplier", cmbSupplier);
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
        cmbSupplier.removeAllItems();
        for (Supplier s : supplierController.getAllSupplier()) {
            cmbSupplier.addItem(s);
        }
    }

    private void muatTabel() {
        tableModel.setRowCount(0);
        List<BarangMasuk> list = controller.getRiwayatMasuk();
        for (BarangMasuk bm : list) {
            tableModel.addRow(new Object[]{
                    bm.getNoTransaksi(),
                    bm.getTanggal().format(FMT),
                    bm.getNamaBarang(),
                    bm.getNamaSupplier(),
                    bm.getJumlah()
            });
        }
    }

    private void simpan() {
        String noTransaksi = txtNoTransaksi.getText().trim();
        Barang barang = (Barang) cmbBarang.getSelectedItem();
        Supplier supplier = (Supplier) cmbSupplier.getSelectedItem();

        if (barang == null || supplier == null) {
            error("Barang dan Supplier harus dipilih. Pastikan data master sudah ada.");
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

        BarangMasuk bm = new BarangMasuk();
        bm.setNoTransaksi(noTransaksi);
        bm.setTanggal(tanggal);
        bm.setIdBarang(barang.getIdBarang());
        bm.setIdSupplier(supplier.getIdSupplier());
        bm.setJumlah(jumlah);

        String err = controller.simpanBarangMasuk(bm);
        if (err == null) {
            info("Transaksi barang masuk tersimpan. Stok otomatis bertambah.");
            bersihkan();
            muatCombo();   // perbarui stok pada label combo bila perlu
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
        if (cmbSupplier.getItemCount() > 0) {
            cmbSupplier.setSelectedIndex(0);
        }
    }

    private void info(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Informasi", JOptionPane.INFORMATION_MESSAGE);
    }

    private void error(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Peringatan", JOptionPane.WARNING_MESSAGE);
    }
}
