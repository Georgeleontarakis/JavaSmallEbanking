package cli.Scanner;

import static cli.Scanner.PublicScanner.SCANNER;

public class CustomMenuScanner {

    public static int getMenuChoice(int maxChoice) {
        
        int choice;

        
        while (true) {
        
            System.out.print("Choose (1-" + maxChoice + "): ");
            String input = SCANNER.nextLine().trim();

            try {
        
                choice = Integer.parseInt(input);
        
                if (choice >= 1 && choice <= maxChoice) {
        
                    return choice;
        
                } else {
        
                    System.out.println("Can only choose from 1 to " + maxChoice + ".");
        
                }
        
            } catch (NumberFormatException e) {
        
                System.out.println("Invalid input. Please enter a number.");
        
            }
        
        }
    
    }    

}
