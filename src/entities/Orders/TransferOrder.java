package entities.Orders;

import entities.Accounts.BankAccount;
import entities.Transactions.Transfer;
import entities.Users.Customer;
import entities.Users.User;
import managers.TransactionManager;
import storage.Storable;

//import java.lang.classfile.CustomAttribute;
import java.time.LocalDate;

public class TransferOrder extends StandingOrder{


    private  String creditAccountIBAN;
    private  String chargeAccountIBAN;
    private  float amount;
    private  String title;
    private  String description;
    private  String receiverNote;
    private  long customerVAT;
    private float fee;
    private int frequencyInMonths;
    private long dayOfMonth;

    private String defaultDescription = "";


    public TransferOrder(String orderId, String creditAccountIBAN, String chargeAccountIBAN, float amount, String title, String description, long customerVAT, LocalDate startDate, LocalDate endDate, float fee, int frequencyInMonths, long dayOfMonth){

        super(orderId, startDate, endDate);
        this.creditAccountIBAN = creditAccountIBAN;
        this.chargeAccountIBAN = chargeAccountIBAN;
        this.amount = amount;
        this.title = title;
        this.description = description;
        this.receiverNote = null;
        this.customerVAT = customerVAT;
        this.fee = fee;
        this.startDate = startDate;
        this.frequencyInMonths = frequencyInMonths;
        this.dayOfMonth = dayOfMonth;

    }

    public TransferOrder(){}


    public String marshal(){
       return "type:" + this.getClass().getSimpleName() +
               ",orderId:" + orderId +
               ",title:" + title +
               ",description:" + description +
               ",customer:" + customerVAT +
               ",amount:" + amount +
               ",startDate:" + startDate +
               ",endDate:" + endDate +
               ",fee:" + fee +
               ",chargeAccount:" + chargeAccountIBAN +
               ",creditAccount:" + creditAccountIBAN +
               ",frequencyInMonths:" + frequencyInMonths +
               ",dayOfMonth:" + dayOfMonth;

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
                case "title":
                    this.title = value;
                    break;
                case "description":
                    this.description = value;
                    break;
                case "customer":
                    this.customerVAT = Long.parseLong(value);
                    break;
                case "amount":
                    this.amount = Float.parseFloat(value);
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
                case "creditAccount":
                    this.creditAccountIBAN = value;
                    break;
                case "frequencyInMonths":
                    this.frequencyInMonths = Integer.parseInt(value);
                    break;
                case "dayOfMonth":
                    this.dayOfMonth = Long.parseLong(value);
                    break;
                // "type" is ignored
            }
        }
    }


    public Object getSourceIban() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getSourceIban'");
    }




	public Object getTargetIban() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'getTargetIban'");
	}

    

	public double getAmount() {
        return amount;
    }

    public String getCreditAccountIBAN() {
        return creditAccountIBAN;
    }

    public String getChargeAccountIBAN() {
        return chargeAccountIBAN;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getReceiverNote() {
        return receiverNote;
    }

    public long getCustomerVAT() {
        return customerVAT;
    }

    public float getFee() {
        return fee;
    }

    public int getFrequencyInMonths() {
        return frequencyInMonths;
    }

    public long getDayOfMonth() {
        return dayOfMonth;
    }

}
