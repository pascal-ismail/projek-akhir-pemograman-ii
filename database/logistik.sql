-- =============================================================
--  Sistem Informasi Gudang Logistik
--  Database Schema — MySQL 8.x
--  Berdasarkan: PRODUCT.md & DATABASE.md
-- =============================================================

CREATE DATABASE IF NOT EXISTS db_logistik
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE db_logistik;

-- =============================================================
--  1. TABEL SUPPLIER
-- =============================================================
CREATE TABLE supplier (
    id_supplier   INT          NOT NULL AUTO_INCREMENT,
    kode_supplier VARCHAR(20)  NOT NULL,
    nama_supplier VARCHAR(100) NOT NULL,
    alamat        TEXT,
    telepon       VARCHAR(20),
    PRIMARY KEY (id_supplier),
    UNIQUE KEY uq_kode_supplier (kode_supplier)
) ENGINE=InnoDB;

-- =============================================================
--  2. TABEL BARANG
-- =============================================================
CREATE TABLE barang (
    id_barang   INT             NOT NULL AUTO_INCREMENT,
    kode_barang VARCHAR(20)     NOT NULL,
    nama_barang VARCHAR(100)    NOT NULL,
    kategori    VARCHAR(50)     NOT NULL,
    stok        INT             NOT NULL DEFAULT 0,
    harga       DECIMAL(15, 2)  NOT NULL DEFAULT 0.00,
    PRIMARY KEY (id_barang),
    UNIQUE KEY uq_kode_barang (kode_barang)
) ENGINE=InnoDB;

-- =============================================================
--  3. TABEL BARANG MASUK
-- =============================================================
CREATE TABLE barang_masuk (
    id_masuk     INT         NOT NULL AUTO_INCREMENT,
    no_transaksi VARCHAR(20) NOT NULL,
    tanggal      DATE        NOT NULL,
    id_barang    INT         NOT NULL,
    id_supplier  INT         NOT NULL,
    jumlah       INT         NOT NULL,
    PRIMARY KEY (id_masuk),
    UNIQUE KEY uq_no_transaksi_masuk (no_transaksi),
    CONSTRAINT fk_masuk_barang
        FOREIGN KEY (id_barang) REFERENCES barang (id_barang)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_masuk_supplier
        FOREIGN KEY (id_supplier) REFERENCES supplier (id_supplier)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT chk_masuk_jumlah CHECK (jumlah > 0)
) ENGINE=InnoDB;

-- =============================================================
--  4. TABEL BARANG KELUAR
-- =============================================================
CREATE TABLE barang_keluar (
    id_keluar    INT         NOT NULL AUTO_INCREMENT,
    no_transaksi VARCHAR(20) NOT NULL,
    tanggal      DATE        NOT NULL,
    id_barang    INT         NOT NULL,
    jumlah       INT         NOT NULL,
    PRIMARY KEY (id_keluar),
    UNIQUE KEY uq_no_transaksi_keluar (no_transaksi),
    CONSTRAINT fk_keluar_barang
        FOREIGN KEY (id_barang) REFERENCES barang (id_barang)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT chk_keluar_jumlah CHECK (jumlah > 0)
) ENGINE=InnoDB;

-- =============================================================
--  TRIGGER: Tambah stok otomatis saat barang masuk
-- =============================================================
DELIMITER $$

CREATE TRIGGER trg_stok_masuk
AFTER INSERT ON barang_masuk
FOR EACH ROW
BEGIN
    UPDATE barang
    SET stok = stok + NEW.jumlah
    WHERE id_barang = NEW.id_barang;
END$$

-- =============================================================
--  TRIGGER: Kurangi stok otomatis saat barang keluar
-- =============================================================
CREATE TRIGGER trg_stok_keluar
AFTER INSERT ON barang_keluar
FOR EACH ROW
BEGIN
    UPDATE barang
    SET stok = stok - NEW.jumlah
    WHERE id_barang = NEW.id_barang;
END$$

DELIMITER ;

-- =============================================================
--  VIEW: Laporan stok barang
-- =============================================================
CREATE VIEW v_laporan_stok AS
SELECT
    b.kode_barang,
    b.nama_barang,
    b.kategori,
    b.stok,
    b.harga,
    (b.stok * b.harga) AS nilai_stok
FROM barang b
ORDER BY b.kode_barang;

-- =============================================================
--  VIEW: Riwayat barang masuk
-- =============================================================
CREATE VIEW v_riwayat_masuk AS
SELECT
    bm.no_transaksi,
    bm.tanggal,
    b.kode_barang,
    b.nama_barang,
    s.kode_supplier,
    s.nama_supplier,
    bm.jumlah
FROM barang_masuk bm
JOIN barang   b ON bm.id_barang   = b.id_barang
JOIN supplier s ON bm.id_supplier = s.id_supplier
ORDER BY bm.tanggal DESC, bm.id_masuk DESC;

-- =============================================================
--  VIEW: Riwayat barang keluar
-- =============================================================
CREATE VIEW v_riwayat_keluar AS
SELECT
    bk.no_transaksi,
    bk.tanggal,
    b.kode_barang,
    b.nama_barang,
    bk.jumlah
FROM barang_keluar bk
JOIN barang b ON bk.id_barang = b.id_barang
ORDER BY bk.tanggal DESC, bk.id_keluar DESC;


-- =============================================================
--  SAMPLE DATA
-- =============================================================

-- Supplier
INSERT INTO supplier (kode_supplier, nama_supplier, alamat, telepon) VALUES
    ('SUP001', 'PT Maju Jaya',      'Jl. Sudirman No. 12, Jakarta',      '021-5550101'),
    ('SUP002', 'CV Berkah Abadi',   'Jl. Gatot Subroto No. 45, Bandung', '022-5550202'),
    ('SUP003', 'UD Sinar Terang',   'Jl. Ahmad Yani No. 8, Surabaya',    '031-5550303'),
    ('SUP004', 'PT Indo Perkasa',   'Jl. Diponegoro No. 33, Semarang',   '024-5550404'),
    ('SUP005', 'CV Karya Mandiri',  'Jl. Imam Bonjol No. 7, Medan',      '061-5550505');

-- Barang (stok awal 0 — akan bertambah otomatis via trigger saat INSERT barang_masuk)
INSERT INTO barang (kode_barang, nama_barang, kategori, stok, harga) VALUES
    ('BRG001', 'Laptop ASUS VivoBook',    'Elektronik',       0,  7500000.00),
    ('BRG002', 'Mouse Wireless Logitech', 'Elektronik',       0,   250000.00),
    ('BRG003', 'Keyboard Mechanical',     'Elektronik',       0,   450000.00),
    ('BRG004', 'Monitor LG 24 inch',      'Elektronik',       0,  2800000.00),
    ('BRG005', 'Printer Canon PIXMA',     'Elektronik',       0,  1200000.00),
    ('BRG006', 'Flash Drive 32GB',        'Elektronik',       0,    95000.00),
    ('BRG007', 'Meja Kerja Kayu',         'Furniture',        0,  1500000.00),
    ('BRG008', 'Kursi Ergonomis',         'Furniture',        0,  2200000.00),
    ('BRG009', 'Lemari Arsip Besi',       'Furniture',        0,  1800000.00),
    ('BRG010', 'Kertas HVS A4 (Rim)',     'Alat Tulis',       0,    55000.00),
    ('BRG011', 'Pulpen Pilot (Box)',       'Alat Tulis',       0,    35000.00),
    ('BRG012', 'Stapler Besar',           'Peralatan Kantor', 0,    85000.00),
    ('BRG013', 'Kalkulator Casio',        'Peralatan Kantor', 0,   150000.00),
    ('BRG014', 'Sabun Cuci Tangan',       'Kebersihan',       0,    25000.00),
    ('BRG015', 'Tisu Meja (Pack)',         'Kebersihan',       0,    18000.00);

-- Barang Masuk (trigger otomatis update stok barang)
INSERT INTO barang_masuk (no_transaksi, tanggal, id_barang, id_supplier, jumlah) VALUES
    ('BM-2026-001', '2026-01-05',  1, 1, 10),
    ('BM-2026-002', '2026-01-05',  2, 1, 50),
    ('BM-2026-003', '2026-01-06',  3, 1, 30),
    ('BM-2026-004', '2026-01-08',  4, 2,  8),
    ('BM-2026-005', '2026-01-10',  5, 2,  5),
    ('BM-2026-006', '2026-01-12',  6, 1, 40),
    ('BM-2026-007', '2026-01-12',  7, 3, 15),
    ('BM-2026-008', '2026-01-14',  8, 3, 20),
    ('BM-2026-009', '2026-01-15',  9, 4, 10),
    ('BM-2026-010', '2026-01-15', 10, 5,100),
    ('BM-2026-011', '2026-01-16', 11, 5,200),
    ('BM-2026-012', '2026-02-01', 12, 2, 25),
    ('BM-2026-013', '2026-02-03', 13, 2, 15),
    ('BM-2026-014', '2026-02-05', 14, 5, 60),
    ('BM-2026-015', '2026-02-05', 15, 5, 80);

-- Barang Keluar (trigger otomatis kurangi stok barang)
INSERT INTO barang_keluar (no_transaksi, tanggal, id_barang, jumlah) VALUES
    ('BK-2026-001', '2026-01-20',  1,  2),
    ('BK-2026-002', '2026-01-20',  2, 10),
    ('BK-2026-003', '2026-01-22',  3,  5),
    ('BK-2026-004', '2026-01-25', 10, 20),
    ('BK-2026-005', '2026-01-25', 11, 50),
    ('BK-2026-006', '2026-02-08',  7,  3),
    ('BK-2026-007', '2026-02-08',  8,  4),
    ('BK-2026-008', '2026-02-12', 14, 15),
    ('BK-2026-009', '2026-02-14', 15, 20),
    ('BK-2026-010', '2026-02-15',  6,  8);

-- =============================================================
--  Cek hasil akhir
-- =============================================================
-- SELECT * FROM v_laporan_stok;
-- SELECT * FROM v_riwayat_masuk;
-- SELECT * FROM v_riwayat_keluar;
