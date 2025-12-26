package managers;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import entities.Accounts.BankAccount;
import entities.Statements.Statement;
import entities.Transactions.Transaction;
import storage.CsvStorageManager;
import storage.Storable;
import storage.StorableList;

public class StatementManager{

    private static StatementManager instance;
    
    private StorableList<Statement> statements = new StorableList<>();
    private final Map<String, List<Statement>> statementsPerAccount = new HashMap<>();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("dd");



    private StatementManager(){}

    public static StatementManager getInstance(){

        if(instance == null){
            
            instance = new StatementManager();

        }    

        return instance;

    }

    


    public void createStatement(BankAccount account, Transaction transaction, String description, double amount, double balanceAfter, boolean credit) {
        
        String statementId = UUID.randomUUID().toString();

        Statement statement = new Statement(statementId, transaction.getTransactionId(), transaction.getTimestamp(), account.getIban(), description, amount, balanceAfter, credit);

        statementsPerAccount.computeIfAbsent(account.getIban(), k -> new LinkedList<>()).add(0, statement); // add first for reverse chronological order
    
    }

    public List<Statement> getStatementsForAccount(String iban) {
    
        return statementsPerAccount.getOrDefault(iban, new ArrayList<>());
    
    }

    public void clearStatementsForAccount(String iban) {
    
        statementsPerAccount.remove(iban);
    
    }

    public void clearAll() {
    
        statementsPerAccount.clear();
    
    }

    public Map<String, List<Statement>> getAllStatements() {
    
        return statementsPerAccount;
    
    }

    public void saveAllStatements() throws IOException {
    CsvStorageManager storage = CsvStorageManager.getInstance();

        for (Map.Entry<String, List<Statement>> entry : statementsPerAccount.entrySet()) {
            String iban = entry.getKey();
            List<Statement> statementList = entry.getValue();
            String filePath = "data/statements/" + iban + ".csv";

            boolean first = true;
            for (Statement s : statementList) {
                storage.save(((Storable) s), filePath, !first); //casting as Storable
                first = false;
            }
        }
    }




    private void addStatement(Statement st) {
        statements.add(st);
    }

}
