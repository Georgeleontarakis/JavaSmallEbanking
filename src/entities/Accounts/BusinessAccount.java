package entities.Accounts;

import entities.Users.Company;
import entities.Users.Individual;
import managers.UserManager;

import java.time.LocalDate;

public class BusinessAccount extends BankAccount {

    private String maintenanceFee;
    private static BusinessAccount bankAccount;
    private LocalDate dateCreated;

    public BusinessAccount(double balance, String iban, Company owner, double interestRate, String maintenanceFee, LocalDate dateCreated) {
        
        super(balance, iban, owner, interestRate, dateCreated);
        this.maintenanceFee = maintenanceFee;

    }

    public BusinessAccount(){}

    private BusinessAccount(String iban){
        
        super(1000000 ,iban, null, 0, null);
    }

    public static BusinessAccount getBankAccount(){
        if(bankAccount == null){
        bankAccount = new BusinessAccount("GR000000000000000001");
        }
        return bankAccount;
    }


    @Override
    public String marshal(){
        return "type:" + this.getClass().getSimpleName() +
                ",iban:" + iban +
                ",primaryOwner:" + owner.getVat() +
                ",dateCreated:" + dateCreated +
                ",rate:" + interestRate +
                ",balance:" + balance +
                ",maintenanceFee:" + maintenanceFee;

    }

    @Override
    public void unmarshal(String data) {
        String[] parts = data.split(",");

        for (String part : parts) {
            String[] keyValue = part.split(":");

            String key = keyValue[0];
            String value = keyValue[1];

            switch (key) {
                case "iban":
                    this.iban = value;
                    break;
                case "primaryOwner":
                    this.owner = (Company) UserManager.getInstance().getUserByVat(value);
                    break;
                case "dateCreated":
                    this.dateCreated = LocalDate.parse(value);
                    break;
                case "rate":
                    this.interestRate = Double.parseDouble(value);
                    break;
                case "balance":
                    this.balance = Double.parseDouble(value);
                    break;
                case "maintenanceFee":
                    this.maintenanceFee = value;
                    break;
            }
        }
    }


    public String getMaintenanceFee() {
        return maintenanceFee;
    }

    private void setMaintenanceFee(String maintenanceFee) {
        this.maintenanceFee = maintenanceFee;
    }

    

    @Override
    public String getAccountType(){
        
        return "BUSINESS";
    
    }

    public String getOwnerVAT(){
        return owner.getVat();
    }

}

