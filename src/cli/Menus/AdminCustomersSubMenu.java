package cli.Menus;

import java.util.List;

public class AdminCustomersSubMenu {

    private static AdminCustomersSubMenu instance;

    private AdminCustomersSubMenu() {

    }

    public static AdminCustomersSubMenu getInstance() {
    
        if (instance == null) {
    
            instance = new AdminCustomersSubMenu();
    
        }
    
        return instance;

    }

    public List<String> getOptions() {
        
        return List.of("1. Show Customers", "2. Show Customer Details");
    
    }

}
