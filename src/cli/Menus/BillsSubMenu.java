package cli.Menus;

import java.util.List;

public class BillsSubMenu {

    private static BillsSubMenu instance;

    private BillsSubMenu() {

    }

    public static BillsSubMenu getInstance() {
    
        if (instance == null) {
    
            instance = new BillsSubMenu();
    
        }
    
        return instance;

    }

    public List<String> getOptions() {
        
        return List.of("1. Load Issued Bills", "2. Show Paid Bills");
    
    }

}
