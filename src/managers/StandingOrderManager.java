package managers;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import entities.Accounts.BankAccount;
import entities.Bills.Bill;
import entities.Orders.PaymentOrder;
import entities.Orders.StandingOrder;
import entities.Orders.TransferOrder;
import entities.Users.Admin;
import entities.Users.Company;
import entities.Users.Individual;
import entities.Users.User;
import storage.CsvStorageManager;
import storage.Storable;
import storage.StorableList;
import storage.UnMarshalingException;

public class StandingOrderManager{

    private static StandingOrderManager instance;
    private  StorableList<StandingOrder> orders = new StorableList<>();
    private  StorableList<StandingOrder> failedOrders = new StorableList<>();
    private  StorableList<StandingOrder> expiredOrders = new StorableList<>();

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("dd");

    private StandingOrderManager(){}

    public static StandingOrderManager getInstance(){

        if(instance == null){
            
            instance = new StandingOrderManager();

        }    

        return instance;

    }

    public void addOrder(StandingOrder order) {
        orders.add(order);
    }

    public void removeOrder(StandingOrder order) {
        orders.remove(order);
    }

    public List<StandingOrder> getAllOrders() {
        return orders;
    }

    public void executeDueOrders(LocalDate date) throws IOException {
        orders.sort(Comparator.comparing(order -> {LocalDate end = order.getEndDate();
            return end != null ? end : LocalDate.MAX;}));

        for (StandingOrder order : orders) {
            if (!order.isActive()) continue;

            boolean shouldExecute = false;
            boolean success = false;

            if (order instanceof TransferOrder t) {
                LocalDate start = t.getStartDate();
                LocalDate end = t.getEndDate();
                int frequency = t.getFrequencyInMonths();
                long day = t.getDayOfMonth();

                if (frequency > 0 &&
                    !date.isBefore(start) && !date.isAfter(end) &&
                    date.getDayOfMonth() == day) {

                    int monthsBetween = Period.between(start, date).getMonths() + Period.between(start, date).getYears() * 12;
                    if (monthsBetween >= 0 && monthsBetween % frequency == 0) {
                        shouldExecute = true;
                    }

                }

                if (shouldExecute) {
                    success = TransactionManager.getInstance().processTransferOrder(t);
                }

                } else if (order instanceof PaymentOrder p) {
                    // Πάντα προσπαθούμε να το εκτελέσουμε (αν υπάρχουν απλήρωτοι λογαριασμοί)
                    success = TransactionManager.getInstance().processPaymentOrder(p);
                    shouldExecute = success;
                }

                if (!success && shouldExecute) {
                    order.incrementFailures();
                    if (order.getFailures() >= 3) {
                        order.setActive(false);
                        failedOrders.add(order);
                    }
                }    
            
            }
    }

    public List<StandingOrder> getActiveOrders() {
    return orders.stream()
        .filter(StandingOrder::isActive)
        .toList();
    }


    //public void setActiveOrders() {
     //   List<StandingOrder> activeOrders = getActiveOrders();
     //   this.orders.addAll(activeOrders);
    //}

    public List<StandingOrder> getExpiredOrders() {
        return orders.stream()
            .filter(order -> !order.isActive() && order.getFailures() < 3 && order.getEndDate().isBefore(LocalDate.now()))
            .toList();
    }

    public void setExpiredOrders() {
        List<StandingOrder> expiredOrdersCopy = StandingOrderManager.getInstance().getExpiredOrders();
        this.expiredOrders.addAll(expiredOrdersCopy);
    }

    public List<StandingOrder> getFailedOrders() {
        return orders.stream()
            .filter(order -> order.getFailures() >= 3)
            .toList();
    }

    public void setFailedOrders() {
        List<StandingOrder> failedOrdersCopy = StandingOrderManager.getInstance().getFailedOrders();
        this.failedOrders.addAll(failedOrdersCopy);
    }



    /*--------not needed---------------///
    private void saveFailedOrder(StandingOrder order) throws IOException {
        StorableList<StandingOrder> failedOrders = new StorableList<>();
        failedOrders.add(order);
    }*/


    public void saveFailedOrders(){
        CsvStorageManager sm = CsvStorageManager.getInstance();

        try {
            setFailedOrders();
            sm.save(failedOrders, "src/data/orders/failed.csv", false);
            System.out.println("Failed orders stored successfully!");
        } catch (IOException e) {
            System.out.println("Error saving failed Orders: " + e.getMessage());
        }
    }

    public void loadFailedOrders() {
        CsvStorageManager sm = CsvStorageManager.getInstance();

        try {
            sm.load(failedOrders, "src/data/orders/failed.csv");
            System.out.println(failedOrders.size() + " Failed orders loaded!");
        } catch (IOException | UnMarshalingException e) {
            System.out.println("Error loading failed orders: " + e.getMessage());
        }
    }

    public void saveExpiredOrders(){
        CsvStorageManager sm = CsvStorageManager.getInstance();

        try {
            setExpiredOrders();
            sm.save(expiredOrders, "src/data/orders/expired.csv", false);
            System.out.println("Expired orders stored successfully!");
        } catch (IOException e) {
            System.out.println("Error saving expired Orders: " + e.getMessage());
        }
    }

    public void loadExpiredOrders() {
        CsvStorageManager sm = CsvStorageManager.getInstance();


        try {
            sm.load(expiredOrders, "src/data/orders/expired.csv");
            System.out.println(expiredOrders.size() + " Expired orders loaded!");
        } catch (IOException | UnMarshalingException e) {
            System.out.println("Error loading expired orders: " + e.getMessage());
        }
    }





    public void saveOrders(){
        CsvStorageManager sm = CsvStorageManager.getInstance();

        try {

            sm.save(orders, "src/data/orders/active.csv", false);
            System.out.println("Orders stored successfully!");
        } catch (IOException e) {
            System.out.println("Error saving Orders: " + e.getMessage());
        }
    }

    public void loadOrders() {
        CsvStorageManager sm = CsvStorageManager.getInstance();

        try {
            sm.load(orders, "src/data/orders/active.csv");
            System.out.println(orders.size() + " orders loaded!");
        } catch (IOException | UnMarshalingException e) {
            System.out.println("Error loading orders: " + e.getMessage());
        }
    }
    
}
