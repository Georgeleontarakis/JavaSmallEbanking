package entities.Users;

public abstract class Customer extends User{
   
    public String vat;
    
    public Customer(String vat, String legalName, String userName, String password, String userType){
        
        super(legalName, userName, password, userType);
        this.vat = vat;
    
    }

    public Customer() {

    }

    public String getVat(){

        return vat;

    } 

}
