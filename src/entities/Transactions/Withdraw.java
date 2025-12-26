package entities.Transactions;

import entities.Accounts.BankAccount;
import entities.Users.User;
import managers.TransactionManager;

public class Withdraw extends Transaction{

    private final BankAccount source;
    private final double amount;

    public Withdraw(User transactor, BankAccount source, double amount, String description){
        
        super(transactor, description);
        this.source = source;
        this.amount = amount;
    
    }

    public BankAccount getSource() {
        return source;
    }

    public double getAmount() {
        return amount;
    }
    
}
