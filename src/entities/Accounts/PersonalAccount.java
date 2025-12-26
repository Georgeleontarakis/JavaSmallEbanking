package entities.Accounts;

import java.time.LocalDate;
import java.util.List;
import entities.Users.Individual;
import managers.UserManager;
import storage.StorableList;

public class PersonalAccount extends BankAccount {

    private StorableList<Individual> secondaryOwners = new StorableList<>();
    LocalDate dateCreated;

    public PersonalAccount(double balance, String iban, Individual owner, double interestRate, StorableList<Individual> co_owners, LocalDate dateCreated) {

        super(balance, iban, owner, interestRate, dateCreated);
        this.secondaryOwners = co_owners;

    }

    public PersonalAccount() {
    }

    @Override
    public String marshal() {
        StringBuilder sb = new StringBuilder();

        sb.append("type:").append(this.getClass().getSimpleName()) // για PersonalAccount
                .append(",iban:").append(iban)
                .append(",primaryOwner:").append(((Individual) owner).getVat())
                .append(",dateCreated:").append(dateCreated)
                .append(",rate:").append(interestRate)
                .append(",balance:").append(balance);

        if (secondaryOwners != null && !secondaryOwners.isEmpty()) {
            for (Individual coOwner : secondaryOwners) {
                sb.append(",coOwner:").append(coOwner.getVat());
            }
        }

        return sb.toString();
    }

    @Override
    public void unmarshal(String data) {
        String[] parts = data.split(",");
        //this.secondaryOwners = new ArrayList<>();

        for (String part : parts) {
            String[] keyValue = part.split(":");


            String key = keyValue[0];
            String value = keyValue[1];

            switch (key) {
                case "iban":
                    this.iban = value;
                    break;
                case "primaryOwner":
                    this.owner = (Individual) UserManager.getInstance().getUserByVat(value);
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
                case "coOwner":
                    Individual coOwner = (Individual) UserManager.getInstance().getUserByVat(value);
                    this.secondaryOwners.add(coOwner);
                    break;
                // "type" is ignored
            }
        }
    }


    public void addSecondaryOwner(Individual person) {

        secondaryOwners.add(person);

    }

    public List<Individual> getSecondaryOwners() {

        return secondaryOwners;

    }

    @Override
    public String getAccountType() {

        return "PERSONAL";

    }


}