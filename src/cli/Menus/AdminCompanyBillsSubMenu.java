package cli.Menus;

import java.util.List;

public class AdminCompanyBillsSubMenu {

    private static AdminCompanyBillsSubMenu instance;

    private AdminCompanyBillsSubMenu() {

    }

    public static AdminCompanyBillsSubMenu getInstance() {
    
        if (instance == null) {
    
            instance = new AdminCompanyBillsSubMenu();
    
        }
    
        return instance;

    }

    public List<String> getOptions() {
        
        return List.of("1. Show Issued Bills", "2. Show Paid Bills", "3. Load Company Bills");
    
    }

}
