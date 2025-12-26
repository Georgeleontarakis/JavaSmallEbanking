package cli.Menus;

import java.util.List;

public class AdminMenu {

    private static AdminMenu instance;

    private AdminMenu() {

    }

    public static AdminMenu getInstance() {
    
        if (instance == null) {
    
            instance = new AdminMenu();
    
        }
    
        return instance;

    }

    public List<String> getOptions() {
        
        return List.of("1. Customers", "2. Bank Accounts", "3. Company Bills", "4. List Standing Orders", "5. Pay Customer's Bill", "6. Simulate Time Passing", "7. Log out");
    
    }

}
