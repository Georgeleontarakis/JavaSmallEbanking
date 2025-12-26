package entities.Users;

import storage.Storable;

public abstract class User implements Storable {

    protected String legalName;
    protected String userName;
    protected String password;
    protected String userType; // "ADMIN", "INDIVIDUAL", "COMPANY"

    public User() {}


    public User(String legalName, String userName, String password, String userType){
        
        this.legalName = legalName;
        this.userName = userName;
        this.password = password;
        this.userType = userType;
    
    }

    public boolean checkPassword(String input) {
        return password.equals(input);
    }

    public String getUserName(){

        return userName;

    }

    public String getLegalName(){

        return legalName;
    
    }

    public String getUserType(){

        return userType;
    
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isAdmin() {
    
        return "Admin".equalsIgnoreCase(getUserType());
    
    }

    public boolean isIndividualCustomer() {
    
        return "Individual".equalsIgnoreCase(getUserType());
    
    }

    public boolean isCompanyCustomer() {

        return "Company".equalsIgnoreCase(getUserType());

    }

}