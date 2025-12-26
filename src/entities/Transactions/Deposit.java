package entities.Transactions;

import entities.Accounts.BankAccount;
import entities.Users.User;
import managers.TransactionManager;

public class Deposit extends Transaction {
    
    private final BankAccount target;
    public final double amount;

    public Deposit(User transactor, BankAccount target, double amount, String description){
        
        super(transactor, description);
        this.target = target;
        this.amount = amount;
    
    }

    public BankAccount getTarget() {
        return target;
    }

    public double getAmount() {
        return amount;
    }

}
