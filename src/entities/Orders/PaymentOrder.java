package entities.Orders;

import java.time.LocalDate;

import entities.Accounts.BankAccount;
import entities.Bills.Bill;
import entities.Transactions.Payment;
import entities.Users.Customer;
import entities.Users.User;
import managers.TransactionManager;
import storage.Storable;

public class PaymentOrder extends StandingOrder implements Storable {

    private  String chargeAccountIBAN;
    private  long customerVAT;
    private String title;
    private String description;
    private float maxAmount;
    private float fee;
    private String paymentCode;

    public PaymentOrder(String orderId, String chargeAccountIBAN, long customerVAT, LocalDate startDate, LocalDate endDate, String title, String description, float maxAmount, float fee, String paymentCode){

        super(orderId, startDate, endDate);
        this.chargeAccountIBAN = chargeAccountIBAN;
        this.customerVAT = customerVAT;
        this.endDate = endDate;
        this.title = title; 
        this.description = description;
        this.maxAmount = maxAmount;
        this.fee = fee;
        this.paymentCode = paymentCode;


    }

    public PaymentOrder(){}

    public String marshal() {
        return "type:" + this.getClass().getSimpleName() +
                ",orderId:" + orderId +
                ",paymentCode:" + paymentCode +
                ",title:" + title +
                ",description:" + description +
                ",customer:" + customerVAT +
                ",maxAmount:" + maxAmount +
                ",startDate:" + startDate +
                ",endDate:" + endDate +
                ",fee:" + fee +
                ",chargeAccount:" + chargeAccountIBAN;
    }

    @Override
    public void unmarshal(String data) {
        String[] parts = data.split(",");

        for (String part : parts) {
            String[] keyValue = part.split(":");

            String key = keyValue[0];
            String value = keyValue[1];

            switch (key) {
                case "orderId":
                    this.orderId = value;
                    break;
                case "paymentCode":
                    this.paymentCode = value;
                    break;
                case "title":
                    this.title = value;
                    break;
                case "description":
                    this.description = value;
                    break;
                case "customer":
                    this.customerVAT = Long.parseLong(value);
                    break;
                case "maxAmount":
                    this.maxAmount = Float.parseFloat(value);
                    break;
                case "startDate":
                    this.startDate = LocalDate.parse(value);
                    break;
                case "endDate":
                    this.endDate = LocalDate.parse(value);
                    break;
                case "fee":
                    this.fee = Float.parseFloat(value);
                    break;
                case "chargeAccount":
                    this.chargeAccountIBAN = value;
                    break;
                // "creditAccount" is not included in marshal(), so not handled
                // "type" is ignored
            }
        }
    }



    public Object getSourceIban() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getSourceIban'");
    }

    public Object getRfCode() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getRfCode'");
    }

    public Object getAmount() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAmount'");
    }


    public String getChargeAccountIBAN() {
        return chargeAccountIBAN;
    }




    public long getCustomerVAT() {
        return customerVAT;
    }

    public String getTitle() {
        return title;
    }


    public String getDescription() {
        return description;
    }


    public float getMaxAmount() {
        return maxAmount;
    }


    public float getFee() {
        return fee;
    }


    public String getPaymentCode() {
        return paymentCode;
    }



}
