package cli.Menus;

import java.util.List;

public class TransactionsSubMenu {

    private static TransactionsSubMenu instance;

    private TransactionsSubMenu() {

    }

    public static TransactionsSubMenu getInstance() {
    
        if (instance == null) {
    
            instance = new TransactionsSubMenu();
    
        }
    
        return instance;

    }

    public List<String> getOptions() {
        
        return List.of("1. Withdraw", "2. Deposit", "3. Transfer", "4. Pay Bill");
    
    }

}
