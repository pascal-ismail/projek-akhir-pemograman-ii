package controller;

import dao.SupplierDAO;
import model.Supplier;

import java.util.List;

/**
 * Controller untuk modul Data Supplier.
 */
public class SupplierController {

    private final SupplierDAO supplierDAO = new SupplierDAO();

    public List<Supplier> getAllSupplier() {
        return supplierDAO.getAll();
    }

    public List<Supplier> cariSupplier(String keyword) {
        return supplierDAO.search(keyword);
    }

    public boolean tambahSupplier(Supplier s) {
        if (!valid(s)) {
            return false;
        }
        return supplierDAO.insert(s);
    }

    public boolean ubahSupplier(Supplier s) {
        if (!valid(s) || s.getIdSupplier() <= 0) {
            return false;
        }
        return supplierDAO.update(s);
    }

    public boolean hapusSupplier(int idSupplier) {
        return supplierDAO.delete(idSupplier);
    }

    private boolean valid(Supplier s) {
        return s != null
            && s.getKodeSupplier() != null && !s.getKodeSupplier().trim().isEmpty()
            && s.getNamaSupplier() != null && !s.getNamaSupplier().trim().isEmpty();
    }
}
