package cli.Menus;

import java.util.List;

public class AdminBankAccountsSubMenu {

    private static AdminBankAccountsSubMenu instance;

    private AdminBankAccountsSubMenu() {

    }

    public static AdminBankAccountsSubMenu getInstance() {
    
        if (instance == null) {
    
            instance = new AdminBankAccountsSubMenu();
    
        }
    
        return instance;

    }

    public List<String> getOptions() {
        
        return List.of("1. Show Bank Accounts", "2. Show Bank Account Info", "3. Show Bank Account Statements");
    
    }

}
