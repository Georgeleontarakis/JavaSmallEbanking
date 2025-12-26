package entities.Transactions;

import entities.Accounts.BankAccount;
import entities.Accounts.BusinessAccount;
import entities.Bills.Bill;
import entities.Users.User;
import managers.TransactionManager;

public class Payment extends Transaction{

    private final BankAccount payer;
    private final BankAccount receiver;
    private final Bill bill;

    public Payment(User transactor, BankAccount payer, BankAccount receiver, Bill bill){
        
        super(transactor, "Bill Payment - RF: " + bill.getRfCode());
        this.payer = payer;
        this.receiver = receiver;
        this.bill = bill;

    }
    
}
