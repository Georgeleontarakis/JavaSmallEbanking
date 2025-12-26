package entities.Transactions;

import java.time.LocalDateTime;
import java.util.UUID;

import entities.Users.User;

public abstract class Transaction {
    
    protected String transactionId;
    protected User transactor;
    protected String description;
    protected LocalDateTime timestamp;

    public Transaction(User transactor, String description){
        
        this.transactionId = generateTransactionId();
        this.transactor = transactor;
        this.description = description;
        this.timestamp = LocalDateTime.now();
   
    }

    private String generateTransactionId(){
        
        return "TX-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    
    }

    public String getTransactionId() {
        return transactionId;
    }

    public User getTransactor() {
        return transactor;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

}
