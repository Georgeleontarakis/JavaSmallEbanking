package managers;

import entities.Bills.Bill;
import entities.Users.Company;
import entities.Users.Customer;
import storage.CsvStorageManager;
import storage.Storable;
import storage.StorableList;
import storage.UnMarshalingException;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class BillManager {

    private static BillManager instance;
    private StorableList<Bill> bills = new StorableList<>();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private BillManager() {
    }

    public StorableList<Bill> getBills() {
        return bills;
    }

    ;

    public static BillManager getInstance() {
        if (instance == null) {
            instance = new BillManager();
        }
        return instance;
    }

    public void addBill(Bill bill) {
        bills.add(bill);
    }

    public Bill getBillByRfCode(String rfCode) {
        for (Bill b : bills) {
            if (b.getRfCode().equals(rfCode)) {
                return b;
            }
        }
        return null;
    }

    public StorableList<Bill> getAllBills() {
        return bills;
    }

    public StorableList<Bill> getUnpaidBillsByCustomer(Customer customer) {
        StorableList<Bill> unpaidBills = new StorableList<>();

        for (Bill b : bills) {
            if (b != null) {
                if (!b.isPaid() && b.getIssuer().equals(customer)) {
                    unpaidBills.add(b);
                }
            }
        }
        return unpaidBills;
    }

    public StorableList<Bill> findUnpaidBillsByRf(String rfCode) {
        StorableList<Bill> result = new StorableList<>();
        for (Bill bill : bills) {
            if (bill.getRfCode() != null && bill.getRfCode().equals(rfCode) && !bill.isPaid()) {
                result.add(bill);
            }
        }
        return result;
    }

    ///  Returns all bills for a Company
    public StorableList<Bill> getBillsByIssuer(Company issuer) {
        StorableList<Bill> result = new StorableList<>();
        for (Bill bill : bills) {
            if (bill.getIssuer().getVat().equals(issuer.getVat())) {
                result.add(bill);
            }
        }
        return result;
    }


    ///  Loads the bills (Storable List) from the given date
    public void loadBill(String fullPath) {
        CsvStorageManager sm = CsvStorageManager.getInstance();
        File file = new File(fullPath);

        if(!file.exists()){
            System.out.println("\nNo file found to load bills for: " + fullPath);
            return;
        }

        try {
            //bills.clear();
            sm.load(bills, fullPath);
            System.out.println(bills.size() + " bills loaded!");
        } catch (IOException | UnMarshalingException e) {
            System.out.println("Error loading bills: " + e.getMessage());
        }
    }

    public void store() {


    }




    ///  Returns all paid Bills inside the bills storable list
    public StorableList<Bill> getPaidBills() {
        StorableList<Bill> paidBills = new StorableList<>();
        for (Bill b : bills) {
            if (b != null && b.isPaid()) {
                paidBills.add(b);
            }
        }
        return paidBills;
    }

    ///  Returns the Company that has issued the specific bill with the given rf code
    public Company getCompanyByRfCode(String rfCode) {
        for (Bill bill : bills) {
            if (bill.getRfCode().equals(rfCode)) {
                return bill.getIssuer();
            }
        }
        return null;
    }

    public void storeIssuedBills() {
        CsvStorageManager sm = CsvStorageManager.getInstance();

        try {
            sm.save(bills, "src/data/bills/issued.csv", false);
            System.out.println("Issued Bills stored successfully!");
        } catch (IOException e) {
            System.out.println("Error saving Issued Bills: " + e.getMessage());
        }
    }

    public void storePaidBills() {
        CsvStorageManager sm = CsvStorageManager.getInstance();
        StorableList<Bill> paidBills = BillManager.getInstance().getPaidBills();

        try {
            sm.save(paidBills, "src/data/bills/paid.csv", false);
            System.out.println("Paid Bills stored successfully!");
        } catch (IOException e) {
            System.out.println("Error saving Paid Bills: " + e.getMessage());
        }
    }


}


