package managers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static cli.Scanner.PublicScanner.SCANNER;

import entities.Users.Admin;
import entities.Users.Company;
import entities.Users.Customer;
import entities.Users.Individual;
import entities.Users.User;
import storage.*;

public class UserManager{

    private static UserManager instance;
    private StorableList<User> users = new StorableList<>();

    private UserManager(){
    }

    public static UserManager getInstance(){

        if(instance == null){
            
            instance = new UserManager();

        }    

        return instance;

    }

    public void storeUsers() {
        CsvStorageManager sm = CsvStorageManager.getInstance();

        try {
            sm.save(users, "src/data/users/users.csv", false);
            System.out.println("Users stored successfully!");
        } catch (IOException e) {
            System.out.println("Error saving users: " + e.getMessage());
        }
    }

    public void loadUsers() {
        CsvStorageManager sm = CsvStorageManager.getInstance();

        try {
            sm.load(users, "src/data/users/users.csv");
            System.out.println(users.size() + " users loaded!");
        } catch (IOException | UnMarshalingException e) {
            System.out.println("Error loading users: " + e.getMessage());
        }
    }

    public void addUser(User user) {
        users.add(user);
    }

    public Customer getUserByVat(String vat) {
        for (User u : users) {
            if (u instanceof Customer c && String.valueOf(c.getVat()).equals(vat)){

                return c;

            }
        }
        return null;
    }

    public boolean authenticate(String vat, String password) {
        for (User u : users) {
            if (u instanceof Customer c && String.valueOf(c.getVat()).equals(vat)) {
                return c.getPassword().equals(password);
            }
        }
        return false;
    }

    public StorableList<User> getAllUsers() {
        return users;
    }

    public User getUserByUsername(String username){

        for(User u : users){

            if(u.getUserName().equals(username)){

                return u;

            }

        }

        return null;

    }

    public void uptadeUserPassword(String vat, String newPassword){

        User u = getUserByVat(vat);
        
        if(u != null){

            u.setPassword(newPassword);

        }

    }

    public void removeUserByVat(String vat){

        users.removeIf(u -> u instanceof Customer c && String.valueOf(c.getVat()).equals(vat));

    }

    public Admin getBankUser() {
        
        for (User user : users) {
        
            if (user instanceof Admin && user.getUserName().equalsIgnoreCase("admin")) {
        
                return (Admin) user;
        
            }
        
        }
        
        throw new IllegalStateException("Bank Admin user not found (expected username: 'admin')");
    
    }

    public User login() {
  
        System.out.println("=== Login ===");

  
        System.out.print("Username: ");
        String username = SCANNER.nextLine().trim();

        System.out.print("Password: ");
        String password = SCANNER.nextLine().trim();

        for (User user : users) {
        
            if (user.getUserName().equals(username) && user.checkPassword(password)) {
        
                System.out.println("Successful login. Welcome " + user.getLegalName() + "!");
                
        
                return user;
        
            }
           
    
        }
    
        return null;        
    
    }
    



}
