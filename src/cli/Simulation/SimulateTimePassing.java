package cli.Simulation;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import managers.AccountManager;
import managers.BillManager;
import managers.StandingOrderManager;
import storage.CsvStorageManager;

public class SimulateTimePassing {

    public static void simulate(LocalDate targetDate) throws IOException {

        LocalDate currentDate = LocalDate.of(2025, 5, 1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        while (!currentDate.isAfter(targetDate)) {
            System.out.println("\n--- Simulating Time Passing: " + currentDate + " ---");

            // 1. Execute due standing orders
            System.out.println("Executing standing orders...");
            StandingOrderManager.getInstance().executeDueOrders(currentDate);

            // 2. Load bills for this date if any
            String formattedDate = currentDate.format(formatter);
            String billFilePath = "src/data/bills/" + formattedDate + ".csv";
            File billFile = new File(billFilePath);

            if (billFile.exists()) {
                System.out.println("Loading bills for " + formattedDate + "...");
                BillManager.getInstance().loadBill(billFilePath);
            }

            // 3. Calculate daily interest (EVERY DAY!)
            System.out.println("Calculating daily interest...");
            AccountManager.getInstance().calculateDailyInterest();

            // 4. On the 1st of each month: post interest and charge fees
            if(currentDate.getDayOfMonth() == 1){
                System.out.println("Applying interest to all accounts...");
                AccountManager.getInstance().postMonthlyInterest();

                System.out.println("Charging maintenance fees...");
                AccountManager.getInstance().chargeMaintenanceFees();
            }

            currentDate = currentDate.plusDays(1);
        }

        System.out.println("\n=== Time Simulation Complete ===\n");
    }
}