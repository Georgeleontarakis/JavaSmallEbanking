package storage;

import entities.Users.Admin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StorableList <T extends Storable> extends ArrayList<T> implements Storable {

    // Map for simple name to fully qualified name
    private static final Map<String, String> SIMPLE_TO_FULL_CLASSNAME = new HashMap<>();
    static {
        SIMPLE_TO_FULL_CLASSNAME.put("Admin", "entities.Users.Admin");
        SIMPLE_TO_FULL_CLASSNAME.put("Individual", "entities.Users.Individual");
        SIMPLE_TO_FULL_CLASSNAME.put("Company", "entities.Users.Company");
        SIMPLE_TO_FULL_CLASSNAME.put("PersonalAccount", "entities.Accounts.PersonalAccount");
        SIMPLE_TO_FULL_CLASSNAME.put("BusinessAccount", "entities.Accounts.BusinessAccount");
        SIMPLE_TO_FULL_CLASSNAME.put("Bill", "entities.Bills.Bill");
        SIMPLE_TO_FULL_CLASSNAME.put("PaymentOrder", "entities.Orders.PaymentOrder");
        SIMPLE_TO_FULL_CLASSNAME.put("TransferOrder", "entities.Orders.TransferOrder");
        SIMPLE_TO_FULL_CLASSNAME.put("Statement", "entities.Statements.Statement");

        // Add more mappings as needed
    }


    @Override
    public String marshal() {
        StringBuilder sb = new StringBuilder();
        for (T item : this) {
            sb.append(item.marshal()).append("\n");
        }
        return sb.toString();
    }


    @Override
    public void unmarshal(String data) throws UnMarshalingException {
        try {
            String[] lines = data.split("\n");
            for (String line : lines) {
                String className = "";
                try {
                    String[] parts = line.split(",");
                    className = parts[0].split(":")[1].trim();
                    String fullClassName = SIMPLE_TO_FULL_CLASSNAME.get(className);
                    if (fullClassName == null) {
                        throw new ClassNotFoundException("Unknown class: " + className);
                    }
                    Class<?> typeClass = Class.forName(fullClassName);
                    if (typeClass != null) {
                        if (fullClassName.equals("entities.Users.Admin")) {
                            T item = (T) Admin.getInstance();
                            item.unmarshal(line);
                            add(item);
                        } else {
                            T item = (T) typeClass.getDeclaredConstructor().newInstance();
                            item.unmarshal(line);
                            add(item);
                        }
                    }
                    } catch (UnMarshalingException e) {
                    System.out.println("Error unmarshalling item: " + className + e.getMessage());
                } catch (Exception e) {
                    System.out.println("Error creating instance of class: " + e.getMessage());
                }

            }
        }catch (Exception e) {
            throw new UnMarshalingException(e.getMessage());
        }
    }
}
