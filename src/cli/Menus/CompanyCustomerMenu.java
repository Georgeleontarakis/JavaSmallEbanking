package cli.Menus;

import java.util.List;

public class CompanyCustomerMenu {

    private static CompanyCustomerMenu instance;

    private CompanyCustomerMenu() {

    }

    public static CompanyCustomerMenu getInstance() {
    
        if (instance == null) {
    
            instance = new CompanyCustomerMenu();
    
        }
    
        return instance;

    }

    public List<String> getOptions() {
        
        return List.of("1. Overview", "2. Bills", "3. Log out");
    
    }

}
