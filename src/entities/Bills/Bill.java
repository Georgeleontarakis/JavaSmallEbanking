package entities.Bills;

import java.time.LocalDate;
import entities.Users.Company;
import entities.Users.Customer;
import storage.Storable;
import managers.UserManager;

public class Bill implements Storable {

    private String rfCode;
    private String billCode;
    private double amount;
    private Company issuer;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private boolean paid;
    private Customer customer;

    public Bill(String rfCode, String billCode, double amount, Company issuer, LocalDate issueDate, LocalDate dueDate, Customer customer) {
        
        this.rfCode = rfCode;
        this.billCode = billCode;
        this.amount = amount;
        this.issuer = issuer;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.paid = false;
        this.customer = customer;
    
    }

    public Bill(){}


    public String marshal() {
        return "type:" + this.getClass().getSimpleName() +
                ",paymentCode:" + rfCode +
                ",billNumber:" + billCode +
                ",issuer:" + issuer.getVat() +
                ",customer" +customer.getVat()+// υποθέτουμε ότι υπάρχει getVat() στην Company
                ",amount:" + amount +
                ",issueDate:" + issueDate +
                ",dueDate:" + dueDate;
    }


    @Override
    public void unmarshal(String data) {
        String[] parts = data.split(",");

        for (String part : parts) {
            String[] keyValue = part.split(":");

            String key = keyValue[0];
            String value = keyValue[1];

            switch (key) {
                case "paymentCode":
                    this.rfCode = value;
                    break;
                case "billNumber":
                    this.billCode = value;
                    break;
                case "issuer":
                    this.issuer = (Company) UserManager.getInstance().getUserByVat(value);
                    break;
                case "customer":
                    this.customer = UserManager.getInstance().getUserByVat(value);
                    break;
                case "amount":
                    this.amount = Double.parseDouble(value);
                    break;
                case "issueDate":
                    this.issueDate = LocalDate.parse(value);
                    break;
                case "dueDate":
                    this.dueDate = LocalDate.parse(value);
                    break;
                // "type" is ignored
            }
        }

        this.paid = false; // default όπως στον constructor
    }



    public String getRfCode() {
        return rfCode;
    }

    public void setRfCode(String rfCode) {
        this.rfCode = rfCode;
    }

    public String getBillCode() {
        return billCode;
    }

    public void setBillCode(String billCode) {
        this.billCode = billCode;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Company getIssuer() {
        return issuer;
    }

    public void setIssuer(Company issuer) {
        this.issuer = issuer;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    @Override
    public String toString(){

        return "[BILL " + billCode + "] RF: " + rfCode + " | Amount: " + amount + " | Due: " + dueDate + " | Paid: " + (paid ? "YES" : "NO");

    }    

}
