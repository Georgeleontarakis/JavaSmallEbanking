package cli.Menus;

import java.util.List;

public class IndividualCustomerMenu{

    private static IndividualCustomerMenu instance;

    private IndividualCustomerMenu() {

    }

    public static IndividualCustomerMenu getInstance() {
    
        if (instance == null) {
    
            instance = new IndividualCustomerMenu();
    
        }
    
        return instance;

    }
    
    public List<String> getOptions() {
        
        return List.of("1. Overview", "2. Transactions", "3. Log out");
    
    }

}
