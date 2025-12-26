package entities.Accounts;

import entities.Users.Customer;
import entities.Users.User;
import storage.Storable;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

public abstract class BankAccount implements Storable {
    
    protected String iban;
    protected Customer owner;
    protected double interestRate;
    protected double balance;
    protected LocalDate dateCreated;
    private double accruedInterest = 0.0;

    public void setAccruedInterest(double accruedInterest) {
        this.accruedInterest = accruedInterest;
    }

    public BankAccount(double balance, String iban, Customer owner, double interestRate, LocalDate dateCreated) {
        this.balance = balance;
        this.iban = iban;
        this.owner = owner;
        this.interestRate = interestRate;
        this.dateCreated = dateCreated;
    }

    public BankAccount(){}

    public String getIban() {
        return iban;
    }

    private void setIban(String iban) {
        this.iban = iban;
    }

    public User getOwner() {
        return owner;
    }

    private void setOwner(Customer owner) {
        this.owner = owner;
    }

    public double getInterestRate() {
        return interestRate;
    }

    private void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public abstract String getAccountType();

    public void addAccruedInterest(double amount) {
        this.accruedInterest += amount;
    }

    public double getAccruedInterest() {
        return accruedInterest;
    }

    public void clearAccruedInterest() {
        this.accruedInterest = 0.0;
    }


}
