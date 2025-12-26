package entities.Transactions;

import entities.Accounts.BankAccount;
import entities.Users.User;

public class Transfer extends Transaction{

    private final BankAccount receiver;
    private final double amount;
    private final String senderDescription;
    private final String receiverDescription;


    public Transfer(User transactor, BankAccount receiver, double amount, String senderDescription, String receiverDescription) {
        super(transactor, "Transfer");
        this.receiver = receiver;
        this.amount = amount;
        this.senderDescription = senderDescription;
        this.receiverDescription = receiverDescription;

    }



    private BankAccount getReceiver() {
        return receiver;
    }


    private double getAmount() {
        return amount;
    }


    private String getSenderDescription() {
        return senderDescription;
    }


    private String getReceiverDescription() {
        return receiverDescription;
    }

}
