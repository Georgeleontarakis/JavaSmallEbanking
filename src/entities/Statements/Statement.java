package entities.Statements;

import storage.Storable;

import java.io.Serializable;
import java.time.LocalDateTime;


public class Statement implements Storable {

    private String statementId;
    private String transactionId;
    private LocalDateTime timestamp;
    private String iban;
    private String description;
    private double amount;
    private double finalBalance;
    private boolean isCredit;

    public Statement(String statementId, String transactionId, LocalDateTime timestamp, String iban, String description, double amount, double finalBalance, boolean isCredit){

        this.statementId = statementId;
        this.transactionId = transactionId;
        this.timestamp = timestamp;
        this.iban = iban;
        this.description = description;
        this.amount = amount;
        this.finalBalance = finalBalance;
        this.isCredit = isCredit;

    }


    public String getStatementId() {
        return statementId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getIban() {
        return iban;
    }

    public String getDescription() {
        return description;
    }

    public double getAmount() {
        return amount;
    }

    public double getFinalBalance() {
        return finalBalance;
    }

    public boolean isCredit() {
        return isCredit;
    }

    @Override
    public String toString(){

        return "[" + timestamp + "] " + (isCredit ? "CREDIT " : "DEBIT  ") + String.format("%.2f", amount) + " EUR | Balance: " + String.format("%.2f", finalBalance) + " | " + description;

    }

    @Override
    public String marshal() {
        return "type:" + this.getClass().getName() +
                ",statementId:" + statementId +
                ",transactionId:" + transactionId +
                ",timestamp:" + timestamp +
                ",iban:" + iban +
                ",description:" + description +
                ",amount:" + amount +
                ",finalBalance:" + finalBalance +
                ",isCredit:" + isCredit;
    }


    @Override
    public void unmarshal(String data) {
        String[] parts = data.split(",");

        for (String part : parts) {
            String[] keyValue = part.split(":");

            String key = keyValue[0];
            String value = keyValue[1];

            switch (key) {
                case "statementId":
                    this.statementId = value;
                    break;
                case "transactionId":
                    this.transactionId = value;
                    break;
                case "timestamp":
                    this.timestamp = LocalDateTime.parse(value);
                    break;
                case "iban":
                    this.iban = value;
                    break;
                case "description":
                    this.description = value;
                    break;
                case "amount":
                    this.amount = Double.parseDouble(value);
                    break;
                case "finalBalance":
                    this.finalBalance = Double.parseDouble(value);
                    break;
                case "isCredit":
                    this.isCredit = Boolean.parseBoolean(value);
                    break;
                // "type" is ignored
            }
        }
    }

}
