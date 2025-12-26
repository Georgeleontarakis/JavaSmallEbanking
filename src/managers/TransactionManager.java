package managers;

import java.util.ArrayList;
import java.util.List;

import entities.Accounts.BankAccount;
import entities.Accounts.BusinessAccount;
import entities.Bills.Bill;
import entities.Orders.PaymentOrder;
import entities.Orders.TransferOrder;
import entities.Transactions.Deposit;
import entities.Transactions.Payment;
import entities.Transactions.Transaction;
import entities.Transactions.Transfer;
import entities.Transactions.Withdraw;
import entities.Users.Admin;
import entities.Users.Company;
import entities.Users.User;
import storage.Storable;
import storage.StorableList;

public class TransactionManager{

    private static TransactionManager instance;
    private ArrayList<Transaction> transactionHistory = new ArrayList<>();

    private TransactionManager(){}

    public static TransactionManager getInstance(){
        if(instance == null){  
            instance = new TransactionManager();
        }    
        return instance;
    }

    public void credit(BankAccount acc, double amount) {
        acc.setBalance(acc.getBalance() + amount);
    }

    public void debit(BankAccount acc, double amount) {
        acc.setBalance(acc.getBalance() - amount);
    }

    public void executeDeposit(User transactor, BankAccount account, double amount, String description) {
        credit(account, amount);
        Deposit tx = new Deposit(transactor, account, amount, description);
        transactionHistory.add(tx);
        StatementManager.getInstance().createStatement(account, tx, description, amount, account.getBalance(), true);
    }

    public void executeWithdraw(User transactor, BankAccount account, double amount, String description) {
        if (account.getBalance() < amount) {
            throw new IllegalArgumentException("Insufficient funds");
        }
        debit(account, amount);
        Withdraw tx = new Withdraw(transactor, account, amount, description);
        transactionHistory.add(tx);
        StatementManager.getInstance().createStatement(account, tx, description, amount, account.getBalance(), false);
    }

    public void executeTransfer(User transactor, BankAccount from, BankAccount to, double amount, String senderDescription, String receiverDescription) {
        if (from.getBalance() < amount) {
            throw new IllegalArgumentException("Insufficient funds");
        }
        debit(from, amount);
        credit(to, amount);
        Transfer tx = new Transfer(transactor, to, amount, senderDescription, receiverDescription);
        transactionHistory.add(tx);
        StatementManager.getInstance().createStatement(from, tx, senderDescription, amount, from.getBalance(), false);
        StatementManager.getInstance().createStatement(to, tx, receiverDescription, amount, to.getBalance(), true);
    }

    public void executePayment(User transactor, BankAccount from, BankAccount to, Bill bill) {
        double amount = bill.getAmount();
        if (from.getBalance() < amount) {
            throw new IllegalArgumentException("Insufficient funds");
        }
        debit(from, amount);
        credit(to, amount);
        bill.setPaid(true);
        Payment tx = new Payment(transactor, from, to, bill);
        transactionHistory.add(tx);
        StatementManager.getInstance().createStatement(from, tx, tx.getDescription(), amount, from.getBalance(), false);
        StatementManager.getInstance().createStatement(to, tx, tx.getDescription(), amount, to.getBalance(), true);
    }

    /// Executes interest for one Account (targetAccount)
    public void executeInterest(BankAccount targetAccount) {
        Admin bankUser = UserManager.getInstance().getBankUser();
        BankAccount bankAccount = BusinessAccount.getBankAccount();
        
        // FIXED: Calculate interest based on target account balance, not bank balance
        double amount = targetAccount.getInterestRate() * targetAccount.getBalance();

        // Bank is debited first
        debit(bankAccount, amount);
        // Customer is credited
        credit(targetAccount, amount);
        // Record accrued interest
        targetAccount.setAccruedInterest(amount);

        Transfer tx = new Transfer(bankUser, targetAccount, amount, "Bank Interest Credit", "Interest Received");
        transactionHistory.add(tx);

        StatementManager.getInstance().createStatement(bankAccount, tx, "Interest Paid", amount, bankAccount.getBalance(), false);
        StatementManager.getInstance().createStatement(targetAccount, tx, "Interest Received", amount, targetAccount.getBalance(), true);
    }

    public void executeFee(BankAccount fromAccount, double fee) {
        Admin bankUser = UserManager.getInstance().getBankUser();
        BankAccount bankAccount = BusinessAccount.getBankAccount();

        // Customer is debited
        debit(fromAccount, fee);
        // Bank is credited
        credit(bankAccount, fee);

        Transfer tx = new Transfer(bankUser, bankAccount, fee, "Maintenance Fee", "Fee Received");
        transactionHistory.add(tx);

        StatementManager.getInstance().createStatement(fromAccount, tx, "Maintenance Fee", fee, fromAccount.getBalance(), false);
        StatementManager.getInstance().createStatement(bankAccount, tx, "Fee Received", fee, bankAccount.getBalance(), true);
    }

    public boolean processTransferOrder(TransferOrder order) {
        BankAccount from = AccountManager.getInstance().getAccountByIban(order.getChargeAccountIBAN());
        BankAccount to = AccountManager.getInstance().getAccountByIban(order.getCreditAccountIBAN());
        User bank = UserManager.getInstance().getBankUser();

        if (from == null || to == null || from.getBalance() < order.getAmount() + order.getFee()) {
            return false;
        }

        debit(from, order.getAmount());
        credit(to, order.getAmount());

        Transfer tx = new Transfer(bank, to, order.getAmount(), "Standing Order: " + order.getTitle(), order.getDescription());
        transactionHistory.add(tx);
        StatementManager.getInstance().createStatement(from, tx, "Transfer to " + to.getIban(), order.getAmount(), from.getBalance(), false);
        StatementManager.getInstance().createStatement(to, tx, "Transfer from " + from.getIban(), order.getAmount(), to.getBalance(), true);

        // FIXED: Use executeFee instead of creating Deposit transaction
        if (order.getFee() > 0 && from.getBalance() >= order.getFee()) {
            executeFee(from, order.getFee());
        }

        return true;
    }

    public boolean processPaymentOrder(PaymentOrder order) {
        BankAccount from = AccountManager.getInstance().getAccountByIban(order.getChargeAccountIBAN());
        Company company = BillManager.getInstance().getCompanyByRfCode(order.getPaymentCode());
        
        if (company == null) {
            return false;
        }
        
        BusinessAccount to = AccountManager.getInstance().getAccountByOwnerVat(company.getVat());
        User bank = UserManager.getInstance().getBankUser();

        if (from == null || to == null) {
            return false;
        }

        List<Bill> unpaidBills = BillManager.getInstance().findUnpaidBillsByRf(order.getPaymentCode()).stream()
            .filter(b -> b.getRfCode() != null && b.getRfCode().equals(order.getPaymentCode()))
            .toList();

        boolean executedAtLeastOne = false;

        for (Bill bill : unpaidBills) {
            if (bill.isPaid()) continue;
            if (bill.getAmount() > order.getMaxAmount()) continue;
            if (from.getBalance() < bill.getAmount()) continue;

            debit(from, bill.getAmount());
            credit(to, bill.getAmount());
            bill.setPaid(true);

            Payment tx = new Payment(bank, from, to, bill);
            transactionHistory.add(tx);

            StatementManager.getInstance().createStatement(from, tx, "Bill Payment", bill.getAmount(), from.getBalance(), false);
            StatementManager.getInstance().createStatement(to, tx, "Bill Received", bill.getAmount(), to.getBalance(), true);

            executedAtLeastOne = true;
        }

        // FIXED: Use executeFee instead of creating Deposit transaction
        if (executedAtLeastOne && order.getFee() > 0 && from.getBalance() >= order.getFee()) {
            executeFee(from, order.getFee());
        }

        return executedAtLeastOne;
    }
}