package view;

import controller.SupplierController;
import model.Supplier;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Form CRUD Data Supplier.
 */
public class SupplierForm extends JPanel {

    private final SupplierController controller = new SupplierController();

    private final JTextField txtKode = new JTextField();
    private final JTextField txtNama = new JTextField();
    private final JTextField txtAlamat = new JTextField();
    private final JTextField txtTelepon = new JTextField();
    private final JTextField txtCari = new JTextField();

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new String[]{"ID", "Kode", "Nama", "Alamat", "Telepon"}, 0) {
        @Override
        public boolean isCellEditable(int r, int c) {
            return false;
        }
    };
    private final JTable table = new JTable(tableModel);

    private int selectedId = 0;

    public SupplierForm() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                isiFormDariTabel();
            }
        });

        JLabel title = new JLabel("Data Supplier");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
        add(title, BorderLayout.NORTH);
        add(buildForm(), BorderLayout.WEST);
        add(buildTable(), BorderLayout.CENTER);

        loadData(controller.getAllSupplier());
    }

    private JComponent buildForm() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Input Supplier"));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 5, 5, 5);
        g.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        addField(panel, g, row++, "Kode Supplier", txtKode);
        addField(panel, g, row++, "Nama Supplier", txtNama);
        addField(panel, g, row++, "Alamat", txtAlamat);
        addField(panel, g, row++, "Telepon", txtTelepon);

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

        JPanel searchBar = new JPanel(new BorderLayout(5, 5));
        searchBar.add(new JLabel("Cari: "), BorderLayout.WEST);
        searchBar.add(txtCari, BorderLayout.CENTER);
        JButton btnCari = new JButton("Cari");
        JButton btnReset = new JButton("Tampilkan Semua");
        btnCari.addActionListener(e -> loadData(controller.cariSupplier(txtCari.getText().trim())));
        btnReset.addActionListener(e -> {
            txtCari.setText("");
            loadData(controller.getAllSupplier());
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
        Supplier s = bacaForm();
        if (s == null) {
            return;
        }
        if (controller.tambahSupplier(s)) {
            info("Supplier berhasil ditambahkan.");
            bersihkan();
            loadData(controller.getAllSupplier());
        } else {
            error("Gagal menambahkan supplier. Kode supplier mungkin duplikat.");
        }
    }

    private void ubah() {
        if (selectedId == 0) {
            error("Pilih data pada tabel terlebih dahulu.");
            return;
        }
        Supplier s = bacaForm();
        if (s == null) {
            return;
        }
        s.setIdSupplier(selectedId);
        if (controller.ubahSupplier(s)) {
            info("Supplier berhasil diubah.");
            bersihkan();
            loadData(controller.getAllSupplier());
        } else {
            error("Gagal mengubah supplier.");
        }
    }

    private void hapus() {
        if (selectedId == 0) {
            error("Pilih data pada tabel terlebih dahulu.");
            return;
        }
        int konfirmasi = JOptionPane.showConfirmDialog(this,
                "Yakin menghapus supplier ini?", "Konfirmasi",
                JOptionPane.YES_NO_OPTION);
        if (konfirmasi != JOptionPane.YES_OPTION) {
            return;
        }
        if (controller.hapusSupplier(selectedId)) {
            info("Supplier berhasil dihapus.");
            bersihkan();
            loadData(controller.getAllSupplier());
        } else {
            error("Gagal menghapus. Supplier mungkin masih dipakai di transaksi.");
        }
    }

    private void bersihkan() {
        selectedId = 0;
        txtKode.setText("");
        txtNama.setText("");
        txtAlamat.setText("");
        txtTelepon.setText("");
        table.clearSelection();
    }

    // ---------- util ----------

    private Supplier bacaForm() {
        String kode = txtKode.getText().trim();
        String nama = txtNama.getText().trim();
        if (kode.isEmpty() || nama.isEmpty()) {
            error("Kode dan Nama supplier wajib diisi.");
            return null;
        }
        Supplier s = new Supplier();
        s.setKodeSupplier(kode);
        s.setNamaSupplier(nama);
        s.setAlamat(txtAlamat.getText().trim());
        s.setTelepon(txtTelepon.getText().trim());
        return s;
    }

    private void isiFormDariTabel() {
        int row = table.getSelectedRow();
        if (row < 0) {
            return;
        }
        selectedId = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
        txtKode.setText(tableModel.getValueAt(row, 1).toString());
        txtNama.setText(tableModel.getValueAt(row, 2).toString());
        txtAlamat.setText(value(row, 3));
        txtTelepon.setText(value(row, 4));
    }

    private String value(int row, int col) {
        Object v = tableModel.getValueAt(row, col);
        return v == null ? "" : v.toString();
    }

    private void loadData(List<Supplier> list) {
        tableModel.setRowCount(0);
        for (Supplier s : list) {
            tableModel.addRow(new Object[]{
                    s.getIdSupplier(),
                    s.getKodeSupplier(),
                    s.getNamaSupplier(),
                    s.getAlamat(),
                    s.getTelepon()
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
