package managers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import entities.Accounts.BankAccount;
import entities.Accounts.BusinessAccount;
import entities.Users.*;
import storage.CsvStorageManager;
import storage.StorableList;
import storage.UnMarshalingException;

public class AccountManager{

    private static AccountManager instance;
    private final StorableList<BankAccount> accounts = new StorableList<BankAccount>();

    private AccountManager(){



    }

    public static AccountManager getInstance(){

        if(instance == null){
            
            instance = new AccountManager();

        }    

        return instance;

    }

    public void addAccount(BankAccount acc) {
        accounts.add(acc);
    }

    public BankAccount getAccountByIban(String iban) {
        for (BankAccount acc : accounts) {
            if (acc.getIban().equals(iban)) return acc;
        }
        return null;
    }

    public BusinessAccount getAccountByOwnerVat(String vat) {
        for (BankAccount acc : accounts) {
            if (acc instanceof BusinessAccount businessAccount && vat.equals(businessAccount.getOwnerVAT())){
                return businessAccount;
            }
        }
        return null;
    }

    public List<BankAccount> getAccountsByOwnerVat(String vat) {
        return accounts.stream().filter(acc -> acc.getOwner() instanceof Customer customer && customer.getVat().equals(vat)).collect(Collectors.toList());
    }


    public List<BankAccount> getAllAccounts() {
        return accounts;
    }

    public List<BankAccount> getAccountsByUser(User user) {
        List<BankAccount> result = new ArrayList<>();
        for (BankAccount acc : accounts) {
            if (acc.getOwner().equals(user)) {
                result.add(acc);
            }
        }
        return result;
}


    public void calculateDailyInterest(){

      for(BankAccount acc : accounts){

        double annualRate = acc.getInterestRate();
        double dailyRate = annualRate / 365.0;
        double interest = acc.getBalance() * dailyRate;
        acc.addAccruedInterest(interest);


      }  

    }

    public void postMonthlyInterest() {

        for (BankAccount acc : accounts) {
            double toPost = acc.getAccruedInterest();
            if (toPost > 0) {
                // ενημερώνουμε το υπόλοιπο και καταγράφουμε transaction
                TransactionManager.getInstance().credit(acc, toPost);
                TransactionManager.getInstance().debit((BusinessAccount.getBankAccount()), toPost);

                acc.clearAccruedInterest();
            }
        }
    }


    public void chargeMaintenanceFees(){
        for(BankAccount acc : accounts){
            if(acc instanceof BusinessAccount b){
                String feeStr = b.getMaintenanceFee();
                
                // Add null check!
                if(feeStr != null && !feeStr.isEmpty() && !feeStr.equalsIgnoreCase("null")){
                    try {
                        double fee = Double.parseDouble(feeStr);
                        if(fee > 0){
                            TransactionManager.getInstance().executeFee(b, fee);
                        }
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid maintenance fee for account " + b.getIban() + ": " + feeStr);
                    }
                }
            }
        }
}

    public void printOverviewForUser(User user) {
        if (!(user instanceof Customer)) {
            System.out.println("User is not a customer.");
            return;
        }

        Customer customer = (Customer) user;
        String vat = customer.getVat();

        List<BankAccount> accounts = this.getAccountsByOwnerVat(vat);

        System.out.println("=== Overview for " + customer.getLegalName() + " (VAT: " + vat + ") ===");

        if (accounts.isEmpty()) {
            System.out.println("No accounts found.");
            return;
        }

        for (BankAccount acc : accounts) {
            System.out.println("- IBAN: " + acc.getIban());
            System.out.println("  Balance: " + acc.getBalance() + " €");
            System.out.println("  Interest Rate: " + acc.getInterestRate());
            System.out.println();
        }
    }


    public void storeAccounts() {
        CsvStorageManager sm = CsvStorageManager.getInstance();

        try {
            sm.save(accounts, "src/data/accounts/accounts.csv", false);
            System.out.println("Accounts stored successfully!");
        } catch (IOException e) {
            System.out.println("Error saving Accounts: " + e.getMessage());
        }
    }

    public void loadAccounts() {
        CsvStorageManager sm = CsvStorageManager.getInstance();

        try {
            sm.load(accounts, "src/data/accounts/accounts.csv");
            System.out.println(accounts.size() + " accounts loaded!");
        } catch (IOException | UnMarshalingException e) {
            System.out.println("Error loading accounts: " + e.getMessage());
        }
    }

}
