package cli.App;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import cli.Menus.*;
import cli.Scanner.CustomMenuScanner;
import cli.Scanner.PublicScanner;
import cli.Simulation.SimulateTimePassing;
import entities.Accounts.BankAccount;
import entities.Accounts.BusinessAccount;
import entities.Accounts.PersonalAccount;
import entities.Bills.Bill;
import entities.Orders.StandingOrder;
import entities.Users.Company;
import entities.Users.Customer;
import entities.Users.User;
import managers.AccountManager;
import managers.BillManager;
import managers.UserManager;
import managers.StandingOrderManager;
import managers.TransactionManager;
import storage.CsvStorageManager;
import storage.Storable;

public class Main {

    public static void main(String[] args) throws IOException {
        List<LocalDate> dates = new ArrayList<>();

        dates.addAll(List.of(
                LocalDate.of(2025, 5, 1),
                LocalDate.of(2025, 5, 5),
                LocalDate.of(2025, 5, 20),
                LocalDate.of(2025, 6, 1)
        ));
        LocalDate currentDate = LocalDate.now();
        for (LocalDate date : dates) {
            if(date == currentDate){
                BillManager.getInstance().loadBill("src/data/bills/" + date.toString() + ".csv");
            }
        }

        //load users
        UserManager.getInstance().loadUsers();

        //load accounts
        AccountManager.getInstance().loadAccounts();

        //load bills for program to start
        for (LocalDate date : dates) {
            String fullPath = "src/data/bills/"+ date.toString() + ".csv";
            BillManager.getInstance().loadBill(fullPath);
        }

        //load standing orders
        StandingOrderManager.getInstance().loadOrders();


        User currentUser = null;

        while (currentUser == null) {
        
            currentUser = UserManager.getInstance().login();

            if (currentUser == null) {
                System.out.println("Invalid credentials. Please try again.\n");
            }
        }

        boolean flag1 = false;

        while (!flag1) {

            if (currentUser.isAdmin()) {
                flag1 = runAdminMenu(flag1);
            } else if (currentUser.isIndividualCustomer()) {
                flag1 = runIndividualMenu(flag1, currentUser);
            } else if (currentUser.isCompanyCustomer()) {
                flag1 = runCompanyMenu(flag1, currentUser);
            } else {
                System.out.println("Invalid user type.");
                flag1 = true;
            }
        
        }

        System.out.println("Saving data...");

        System.out.println("Saving Users...");
        UserManager.getInstance().storeUsers();

        System.out.println("Saving Accounts...");
        AccountManager.getInstance().storeAccounts();

        System.out.println("Saving orders...");
        StandingOrderManager.getInstance().saveOrders();
        StandingOrderManager.getInstance().saveExpiredOrders();
        StandingOrderManager.getInstance().saveFailedOrders();

        // 5. Save statements per IBAN
        //StatementManager.getInstance().saveAllStatements();
        System.out.println("Saving Bills...");
        BillManager.getInstance().storePaidBills();
        BillManager.getInstance().storeIssuedBills();

        System.out.println("✅ All data saved successfully.");
        System.out.println("Thank you for using Bank of TUC E-Banking. Goodbye!");
        PublicScanner.SCANNER.close();

    }

    private static boolean runAdminMenu(boolean flag1) {
        System.out.println(AdminMenu.getInstance().getOptions());
        int choice1 = CustomMenuScanner.getMenuChoice(7);

        if (choice1 == 1) {
            System.out.println(AdminCustomersSubMenu.getInstance().getOptions());
            int choice2 = CustomMenuScanner.getMenuChoice(2);

            if (choice2 == 1) {
                //Show Customers
            
                List<User> users = UserManager.getInstance().getAllUsers();
                System.out.println("=== Registered Customers ===");
                            
                int count = 0;
                for (User user : users) {
                    if (user instanceof Customer customer) {
                        System.out.println("- " + customer.getLegalName() + " (VAT: " + customer.getVat() + ", Username: " + customer.getUserName() + ", Type: " + customer.getUserType() + ")");
                        count++;
                    }
                }
            
                if (count == 0) {
                    System.out.println("No customers found.");
                }

            } else if (choice2 == 2) {
                //Show Customer Details
                List<User> users = UserManager.getInstance().getAllUsers();
                List<Customer> customers = new ArrayList<>();

                System.out.println("=== Select a Customer ===");
                for (User user : users) {
                    if (user instanceof Customer customer) {
                        customers.add(customer);
                    }
                }
            
                if (customers.isEmpty()) {
                    System.out.println("No customers found.");
                    return flag1;
                }
            
                for (int i = 0; i < customers.size(); i++) {
                    Customer customer = customers.get(i);
                    System.out.println((i + 1) + ". " + customer.getLegalName() + " (VAT: " + customer.getVat() + ")");
                }
            
                int selected = CustomMenuScanner.getMenuChoice(customers.size());
                Customer chosen = customers.get(selected - 1);
            
                System.out.println("\n=== Customer Details ===");
                System.out.println("Name: " + chosen.getLegalName());
                System.out.println("Username: " + chosen.getUserName());
                System.out.println("VAT: " + chosen.getVat());
                System.out.println("Type: " + chosen.getUserType());
            
                List<BankAccount> accounts = AccountManager.getInstance().getAccountsByUser(chosen);
                if (accounts.isEmpty()) {
                    System.out.println("No linked bank accounts.");
                } else {
                    System.out.println("\nAccounts:");
                    for (BankAccount acc : accounts) {
                        System.out.println("- IBAN: " + acc.getIban() + " | Balance: " + acc.getBalance() + " $ | Interest: " + acc.getInterestRate() + "%");
                    }
                }
            }

        } else if (choice1 == 2) {
            System.out.println(AdminBankAccountsSubMenu.getInstance().getOptions());
            int choice3 = CustomMenuScanner.getMenuChoice(3);

            if (choice3 == 1) {
                //Show Bank Accounts
                List<BankAccount> accounts = AccountManager.getInstance().getAllAccounts();
                if (accounts.isEmpty()) {
                    System.out.println("No bank accounts found.");
                } else {
                    System.out.println("=== All Bank Accounts ===");
                    for (BankAccount acc : accounts) {
                        if (acc != null){
                            String ownerName = acc.getOwner().getLegalName();
                            System.out.println("- IBAN: " + acc.getIban());
                            System.out.println("  Owner: " + ownerName);
                            System.out.println("  Balance: " + acc.getBalance() + " $");
                            System.out.println("  Interest Rate: " + acc.getInterestRate() + "%");
                            System.out.println("  Account Type: " + acc.getAccountType());
                            System.out.println();
                        }
                    }
                }
            } else if (choice3 == 2) {
                //Show Bank Account Info
                System.out.print("Enter IBAN: ");
                String iban = PublicScanner.SCANNER.nextLine();
                       
                BankAccount account = AccountManager.getInstance().getAccountByIban(iban);
                       
                if (account == null) {
                    System.out.println("Account not found.");
                } else {
                    System.out.println("=== Bank Account Info ===");
                    System.out.println("IBAN: " + account.getIban());
                    System.out.println("Owner: " + account.getOwner().getLegalName());
                    
                    if (account.getOwner() instanceof Customer customer) {
                        System.out.println("VAT: " + customer.getVat());
                    } else {
                        System.out.println("VAT: N/A");
                   }
               
                    System.out.println("Balance: " + account.getBalance() + " €");
                    System.out.println("Interest Rate: " + account.getInterestRate() + "%");
                   System.out.println("Type: " + account.getAccountType());
               
                    if (account instanceof BusinessAccount b) {
                        System.out.println("Maintenance Fee: " + b.getMaintenanceFee() + " €");
                   }
               
                    // Αν είναι PersonalAccount, δείξε τους secondary owners
                    if (account instanceof PersonalAccount p) {
                       List<entities.Users.Individual> secondaryOwners = p.getSecondaryOwners();
                   
                        if (secondaryOwners != null && !secondaryOwners.isEmpty()) {
                            System.out.println("Secondary Owners:");
                            for (entities.Users.Individual sec : secondaryOwners) {
                                System.out.println(" - " + sec.getLegalName() + " (VAT: " + sec.getVat() + ")");
                            }
                        } else {
                            System.out.println("No secondary owners.");
                        }
                    }
                }
            } else if (choice3 == 3) {
                //Show Bank Account Statements
                System.out.print("Enter IBAN to show statements: ");
                String iban = PublicScanner.SCANNER.nextLine();

                BankAccount account = AccountManager.getInstance().getAccountByIban(iban);

                if (account == null) {
                    System.out.println("Account not found.");
                } else {
                    List<entities.Statements.Statement> statements =
                        managers.StatementManager.getInstance().getStatementsForAccount(iban);
                
                    if (statements.isEmpty()) {
                        System.out.println("No statements found for this account.");
                    } else {
                        System.out.println("=== Statements for account: " + iban + " ===");
                        for (entities.Statements.Statement s : statements) {
                            System.out.println(s);
                        }
                    }
                }

            }

        } else if (choice1 == 3) {
            System.out.println(AdminCompanyBillsSubMenu.getInstance().getOptions());
            int choice4 = CustomMenuScanner.getMenuChoice(3);

            if (choice4 == 1) {
                //Show Issued Bills
                List<entities.Bills.Bill> allBills = managers.BillManager.getInstance().getAllBills();

                if (allBills.isEmpty()) {
                    System.out.println("No bills issued.");
                } else {
                    System.out.println("=== All Issued Bills ===");
                    for (entities.Bills.Bill bill : allBills) {
                        System.out.println(bill);
                    }
                }

            } else if (choice4 == 2) {
                //Show Paid Bills
                List<entities.Bills.Bill> allBills = managers.BillManager.getInstance().getAllBills();

                List<entities.Bills.Bill> paidBills = new ArrayList<>();
                for (entities.Bills.Bill bill : allBills) {
                    if (bill.isPaid()) {
                        paidBills.add(bill);
                    }
                }

                if (paidBills.isEmpty()) {
                    System.out.println("No paid bills found.");
                } else {
                    System.out.println("=== Paid Bills ===");
                    for (entities.Bills.Bill bill : paidBills) {
                        System.out.println(bill);
                    }
                }

            } else if (choice4 == 3) {
                //Load Company Bills
                System.out.print("Enter the filename of the bills CSV (e.g., 2025-06-01.csv): ");
                String filename = PublicScanner.SCANNER.nextLine();

                // Εντοπισμός πλήρους διαδρομής (προσαρμόζεται αν έχεις άλλο path)
                String fullPath = "src/data/bills/" + filename;

                try {
                    BillManager.getInstance().loadBill(fullPath);
                    System.out.println("Bills loaded successfully from " + filename);
                } catch (Exception e) {
                    System.out.println("Failed to load bills: " + e.getMessage());
                }

            }

        } else if (choice1 == 4) {
            //List Standing Orders
            List<StandingOrder> orders = StandingOrderManager.getInstance().getAllOrders();

            if (orders.isEmpty()) {
                System.out.println("No standing orders found.");
            } else {
                System.out.println("=== Standing Orders ===");
                for (StandingOrder order : orders) {
                    System.out.println("ID: " + order.getOrderId());
                    System.out.println("Type: " + order.getClass().getSimpleName());
                    System.out.println("Start Date: " + order.getStartDate());
                    System.out.println("End Date: " + order.getEndDate());
                    System.out.println("Active: " + (order.isActive() ? "YES" : "NO"));
                    System.out.println("Failures: " + order.getFailures());
                    System.out.println("--------------------------");
                }
            }

        } else if (choice1 == 5) {
            //Pay Customer's Bill
            List<User> customers = UserManager.getInstance().getAllUsers().stream().filter(user -> user instanceof Customer).toList();

            if (customers.isEmpty()) {
                System.out.println("No customers found.");
                return flag1;
            }
        
            System.out.println("Select customer to pay bill for:");
            for (int i = 0; i < customers.size(); i++) {
                System.out.println((i + 1) + ". " + customers.get(i).getLegalName());
            }
        
            int customerChoice = CustomMenuScanner.getMenuChoice(customers.size());
            Customer selectedCustomer = (Customer) customers.get(customerChoice - 1);
        
            List<Bill> unpaidBills = BillManager.getInstance().getUnpaidBillsByCustomer(selectedCustomer);
            if (unpaidBills.isEmpty()) {
                System.out.println("This customer has no unpaid bills.");
                return flag1;
            }
        
            System.out.println("Select bill to pay:");
            for (int i = 0; i < unpaidBills.size(); i++) {
                System.out.println((i + 1) + ". " + unpaidBills.get(i));
            }
        
            int billChoice = CustomMenuScanner.getMenuChoice(unpaidBills.size());
            Bill selectedBill = unpaidBills.get(billChoice - 1);
        
            List<BankAccount> customerAccounts = AccountManager.getInstance().getAccountsByUser(selectedCustomer);
            if (customerAccounts.isEmpty()) {
                System.out.println("This customer has no accounts.");
                return flag1;
            }
        
            System.out.println("Select account to pay from:");
            for (int i = 0; i < customerAccounts.size(); i++) {
                System.out.println((i + 1) + ". IBAN: " + customerAccounts.get(i).getIban() + " | Balance: " + customerAccounts.get(i).getBalance());
            }
        
            int accChoice = CustomMenuScanner.getMenuChoice(customerAccounts.size());
            BankAccount fromAccount = customerAccounts.get(accChoice - 1);
        
            BankAccount toAccount = AccountManager.getInstance().getAccountsByUser(selectedBill.getIssuer()).get(0);
        
            try {
                TransactionManager.getInstance().executePayment(selectedCustomer, fromAccount, toAccount, selectedBill);
                System.out.println("Bill paid successfully.");
            } catch (IllegalArgumentException e) {
                System.out.println("Error during payment: " + e.getMessage());
            }
        } else if (choice1 == 6) {
            //Simulate Time Passing
            System.out.print("Enter date to simulate (YYYY-MM-DD): ");
            String input = PublicScanner.SCANNER.nextLine();

            try {
                LocalDate date = LocalDate.parse(input);
                SimulateTimePassing.simulate(date);
                System.out.println("Simulation completed for date: " + date);
            } catch (Exception e) {
                System.out.println("Invalid date format or error during simulation: " + e.getMessage());
            }
        } else if (choice1 == 7){
            flag1 = true;
        }

        return flag1;

    }

    private static boolean runIndividualMenu(boolean flag1, User currentUser) {
        System.out.println(IndividualCustomerMenu.getInstance().getOptions());
        int choice1 = CustomMenuScanner.getMenuChoice(3);

        if (choice1 == 1) {
            //Overview
            AccountManager.getInstance().printOverviewForUser(currentUser);

        } else if (choice1 == 2) {
            System.out.println(TransactionsSubMenu.getInstance().getOptions());
            int choice2 = CustomMenuScanner.getMenuChoice(4);

            if (choice2 == 1) {
                //Withdrawal
                List<BankAccount> userAccounts = AccountManager.getInstance().getAccountsByUser(currentUser);

                if (userAccounts.isEmpty()) {
                System.out.println("No bank accounts found.");
                } else {
                    System.out.println("Select account to withdraw from:");
                    for (int i = 0; i < userAccounts.size(); i++) {
                        BankAccount acc = userAccounts.get(i);
                        System.out.println((i + 1) + ". IBAN: " + acc.getIban() + " | Balance: " + acc.getBalance() + "€");
                    }

                    int selected = CustomMenuScanner.getMenuChoice(userAccounts.size());
                    BankAccount selectedAccount = userAccounts.get(selected - 1);

                    System.out.print("Enter amount to withdraw: ");
                    double amount = PublicScanner.SCANNER.nextDouble();
                    PublicScanner.SCANNER.nextLine(); // consume newline

                    System.out.print("Enter description: ");
                    String description = PublicScanner.SCANNER.nextLine();

                    try {
                        TransactionManager.getInstance().executeWithdraw(currentUser, selectedAccount, amount, description);
                        System.out.println("Withdrawal completed.");
                    } catch (IllegalArgumentException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                }   

            } else if (choice2 == 2) {
                //Deposit
                List<BankAccount> accounts = AccountManager.getInstance().getAccountsByUser(currentUser);
                if (accounts.isEmpty()) {
                    System.out.println("No accounts found.");
                    return flag1;
                }

                System.out.println("Select account to deposit to:");
                for (int i = 0; i < accounts.size(); i++) {
                    System.out.println((i + 1) + ". IBAN: " + accounts.get(i).getIban() + ", Balance: " + accounts.get(i).getBalance() + " €");
                }

                int choice = CustomMenuScanner.getMenuChoice(accounts.size());
                BankAccount selected = accounts.get(choice - 1);

                System.out.print("Enter amount to deposit: ");
                double amount = PublicScanner.SCANNER.nextDouble();
                PublicScanner.SCANNER.nextLine(); // consume newline

                System.out.print("Enter description: ");
                String description = PublicScanner.SCANNER.nextLine();

                TransactionManager.getInstance().executeDeposit(currentUser, selected, amount, description);

                System.out.println("Deposit completed successfully.");
            } else if (choice2 == 3) {
                //Transfer
                List<BankAccount> accounts = AccountManager.getInstance().getAccountsByUser(currentUser);
                if (accounts.isEmpty()) {
                    System.out.println("No accounts found.");
                    return flag1;
                }

                System.out.println("Select your source account (to transfer from):");
                for (int i = 0; i < accounts.size(); i++) {
                    System.out.println((i + 1) + ". IBAN: " + accounts.get(i).getIban() + ", Balance: " + accounts.get(i).getBalance() + " €");
                }

                int fromChoice = CustomMenuScanner.getMenuChoice(accounts.size());
                BankAccount fromAccount = accounts.get(fromChoice - 1);

                System.out.print("Enter destination IBAN: ");
                String destIban = PublicScanner.SCANNER.nextLine();
                BankAccount toAccount = AccountManager.getInstance().getAccountByIban(destIban);

                if (toAccount == null) {
                    System.out.println("Destination account not found.");
                    return flag1;
                }

                System.out.print("Enter amount to transfer: ");
                double amount = PublicScanner.SCANNER.nextDouble();
                PublicScanner.SCANNER.nextLine(); // consume newline

                System.out.print("Enter sender description: ");
                String senderDesc = PublicScanner.SCANNER.nextLine();

                System.out.print("Enter receiver description: ");
                String receiverDesc = PublicScanner.SCANNER.nextLine();

                try {
                    TransactionManager.getInstance().executeTransfer(currentUser, fromAccount, toAccount, amount, senderDesc, receiverDesc);
                    System.out.println("Transfer successful.");
                } catch (IllegalArgumentException e) {
                    System.out.println("Transfer failed: " + e.getMessage());
                }
            } else if (choice2 == 4) {
                //Pay Bill
                List<Bill> unpaidBills = BillManager.getInstance().getUnpaidBillsByCustomer((Customer) currentUser);

                if (unpaidBills.isEmpty()) {
                    System.out.println("No unpaid bills.");
                    return flag1;
                }

                System.out.println("Select a bill to pay:");
                for (int i = 0; i < unpaidBills.size(); i++) {
                    System.out.println((i + 1) + ". " + unpaidBills.get(i));
                }

                int billChoice = CustomMenuScanner.getMenuChoice(unpaidBills.size());
                Bill selectedBill = unpaidBills.get(billChoice - 1);

                // Εμφάνιση των λογαριασμών του χρήστη
                List<BankAccount> userAccounts = AccountManager.getInstance().getAccountsByUser(currentUser);

                System.out.println("Select account to pay from:");
                for (int i = 0; i < userAccounts.size(); i++) {
                    BankAccount acc = userAccounts.get(i);
                    System.out.println((i + 1) + ". IBAN: " + acc.getIban() + " | Balance: " + acc.getBalance());
                }

                int accountChoice = CustomMenuScanner.getMenuChoice(userAccounts.size());
                BankAccount fromAccount = userAccounts.get(accountChoice - 1);

                // Βρες τον λογαριασμό της εταιρείας που εξέδωσε τον λογαριασμό
                Company issuer = selectedBill.getIssuer();
                BankAccount companyAccount = AccountManager.getInstance().getAccountsByUser(issuer).get(0); // υποθέτουμε ότι υπάρχει μόνο ένας

                try {
                    TransactionManager.getInstance().executePayment(currentUser, fromAccount, companyAccount, selectedBill);
                    System.out.println("Bill paid successfully.");
                } catch (IllegalArgumentException e) {
                    System.out.println("Payment failed: " + e.getMessage());
                }

            }

        } else if (choice1 == 3){
            flag1 = true;
        }

        return flag1;

    }

    private static boolean runCompanyMenu(boolean flag1, User currentUser) {
        System.out.println(CompanyCustomerMenu.getInstance().getOptions());
        int choice1 = CustomMenuScanner.getMenuChoice(3);

        if (choice1 == 1) {
            AccountManager.getInstance().printOverviewForUser(currentUser);

        } else if (choice1 == 2) {
            System.out.println(BillsSubMenu.getInstance().getOptions());
            int choice2 = CustomMenuScanner.getMenuChoice(2);

            if (choice2 == 1) {
                //Load Issued Bills
                System.out.print("Enter the issue date of the bills to load (YYYY-MM-DD): ");
                String inputDate = PublicScanner.SCANNER.nextLine();

                try {
                    String filePath = "/src/data/bills/" + inputDate + ".csv";
                    CsvStorageManager.getInstance().load(BillManager.getInstance().getBills(), filePath);
                    System.out.println("Bills from " + inputDate + " loaded successfully.");
                } catch (Exception e) {
                    System.out.println("Failed to load bills for " + inputDate + ". File might not exist.");
                }

            } else if (choice2 == 2) {
                //Show Paid Bills
                if (!(currentUser instanceof Company company)) {
                    System.out.println("Current user is not a company.");
                    return flag1;
                }

                List<Bill> allBills = BillManager.getInstance().getBillsByIssuer(company);
                List<Bill> paidBills = allBills.stream()
                    .filter(Bill::isPaid)
                    .toList();

                if (paidBills.isEmpty()) {
                    System.out.println("No paid bills found.");
                } else {
                    System.out.println("Paid Bills:");
                    for (Bill bill : paidBills) {
                        System.out.println(bill);
                    }
                }

            }

        } else if (choice1 == 3){
            flag1 = true;
        }

        return flag1;

    }
}
