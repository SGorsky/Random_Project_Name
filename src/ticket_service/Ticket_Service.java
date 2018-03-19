package ticket_service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Scanner;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Sean
 */
public class Ticket_Service {

    static String helpString = "Login: Login to a specific user account\nLogout: Logout of the "
            + "current account\nCreate: Create a new user account (can only be done by an admin)\n"
            + "Delete: Remove a user account (can only be done by an admin)\nSell: Sell a ticket "
            + "or tickets to an event\nBuy: Purchase a ticket or tickets to an event\nRefund: "
            + "Issue a credit to a buyer from a seller's account\nAddcredit: Add credit to an "
            + "account (can only be done by an admin)\nExit: Closes the application";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //An arraylist for the list of accounts
        //ArrayList<Account> accountsList = new ArrayList<>();

        //A hashtable that stores the username and the Account itself
        Hashtable<String, Account> accountsHash = new Hashtable<String, Account>();

        ArrayList<AvailableTicket> availableTicketsList = new ArrayList<>();
        BuyManager buyManager = new BuyManager();
        SellManager sellManager = new SellManager();

        //currentAccount is the account of the current user or null if there is no user logged in
        Account currentAccount = null;
//        System.out.println("Ticket Selling System\n"
//                + "These are the valid input commands that you can enter. At any point, enter help "
//                + "to see this list again (commands are not case sensitive).\n\n" + helpString + "\n");

        //Read in the accounts from the Current User Accounts file and store each in
        //the accountsHash hash table
        try (BufferedReader br = new BufferedReader(new FileReader(args[0]))) {
            String line = br.readLine();
            while (!line.equals("END")) {
                Account newAccount = new Account(line);
                //accountsList.add(newAccount);
                accountsHash.put(newAccount.getUsername(), newAccount);
                line = br.readLine();
            }
            br.close();
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }

        //Read in the available tickets from the "Available Tickets File" file and store each in
        //the accountsHash hash table
        try (BufferedReader br = new BufferedReader(new FileReader(args[1]))) {
            String line = br.readLine();
            while (!line.equals("END")) {
                AvailableTicket ticket = new AvailableTicket(line);
                availableTicketsList.add(ticket);
                line = br.readLine();
            }
            br.close();
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }

        //Read in input from the user and handle it accordingly
        Scanner scanner = new Scanner(System.in);
        String input = "Not Exit";
        while (!input.equals("exit")) {
            System.out.print("Please enter a command: ");
            input = scanner.nextLine().toLowerCase();

            switch (input) {
                case "login":
                    //If there is no account currently logged in, continue, else display a prompt
                    if (currentAccount == null) {
                        boolean attemptingLogin = true;

                        //Continue to allow the user to attempt to login while they still want to
                        while (attemptingLogin) {
                            //Read in username and password
                            System.out.print("Enter your username: ");
                            input = scanner.nextLine();
                            System.out.print("Enter your password: ");
                            String password = scanner.nextLine();

                            //Check if the username exists and if it does, check if the password
                            //is correct for that user. If yes, log them in
                            if (accountsHash.containsKey(input)
                                    && accountsHash.get(input).getPassword().equals(password)) {
                                //&& accountsList.get(accountsHash.get(input)).getPassword().equals(password)) {
                                currentAccount = accountsHash.get(input);//accountsList.get(accountsHash.get(input));
                                attemptingLogin = false;
                                System.out.println("Welcome " + currentAccount.getUsername());
                            } else {
                                //Invalid username or password. Ask them if the want to try again
                                //If y then let them try again. If n then go back to the main menu
                                //Loop until you get either y or n
                                boolean validInput = false;
                                while (!validInput) {
                                    System.out.print("Invalid user credentials. Would you like to "
                                            + "try again (y/n): ");
                                    input = scanner.nextLine().toLowerCase();
                                    switch (input) {
                                        case "y":
                                            validInput = true;
                                            attemptingLogin = true;
                                            break;
                                        case "n":
                                            validInput = true;
                                            attemptingLogin = false;
                                            break;
                                    }
                                }
                            }
                        }
                    } else {
                        System.out.println("You are currently logged in as "
                                + currentAccount.getUsername() + ". Please logout in order to "
                                + "login as a different user");
                    }
                    break;
                case "logout":
                    //If there is an account currently logged in, log them out
                    //If not display a prompt saying you must be logged in to logout
                    if (currentAccount != null) {
                        System.out.println(currentAccount.getUsername()
                                + " successfully logged out.");
                        currentAccount = null;
                    } else {
                        System.out.println("You must be logged in to use the logout command.");
                    }
                    break;
                case "create":
                    //@author Errol Britto
                    if (currentAccount != null) {
                        boolean attemptingCreate = true;
                        while (attemptingCreate) {
                            if (currentAccount.getType() != Account.UserType.Admin) {
                                System.out.println("You must be logged in as an administator to create an account.");
                                break;
                            }
                            boolean invalidInput = false;
                            boolean anotherAccount = false;
                            float cred = 0;
                            Account newAccount = new Account(null, null, null, cred);
                            float credit;
                            System.out.print("Enter a username: ");
                            input = scanner.nextLine();
                            if (accountsHash.containsKey(input)) {
                                invalidInput = true;
                                System.out.println("Username is already taken. Please enter another username.");
                            } else if (input.length() > 15) {
                                invalidInput = true;
                                System.out.println("Username can be a maximum of 15 characters.");
                            } else {
                                newAccount.setUsername(input);
                                System.out.print("Enter a password: ");
                                input = scanner.nextLine();
                                if (input.length() > 8) {
                                    invalidInput = true;
                                    System.out.println("Password can be a maximum of 8 characters.");
                                } else if (input.isEmpty()) {
                                    invalidInput = true;
                                    System.out.println("Password cannot be empty. You must have a password.");
                                } else {
                                    newAccount.setPassword(input);
                                    System.out.print("Enter a UserType(AA, FS, BS or SS): ");
                                    input = scanner.nextLine().toLowerCase();
                                    switch (input) {
                                        case "aa":
                                            newAccount.setType(Account.UserType.Admin);
                                            break;
                                        case "fs":
                                            newAccount.setType(Account.UserType.FullStandard);
                                            break;
                                        case "bs":
                                            newAccount.setType(Account.UserType.BuyStandard);
                                            break;
                                        case "ss":
                                            newAccount.setType(Account.UserType.SellStandard);
                                            break;
                                        default:
                                            invalidInput = true;
                                            System.out.println("Invalid user type. A user type must be AA, FS, BS or SS.");
                                    }
                                    if (!invalidInput) {
                                        System.out.print("Enter a credit amount: ");
                                        credit = scanner.nextFloat();
                                        scanner.nextLine();
                                        //code to check for maximum 2 decimal places
                                        String[] splitter = String.valueOf(credit).split("\\.");
                                        int decimalLength = splitter[1].length();
                                        if (decimalLength > 2) {
                                            invalidInput = true;
                                            System.out.println("Credit can have a maximum of 2 decimal places.");
                                        } else if (credit < 0) {
                                            invalidInput = true;
                                            System.out.println("Credit cannot be a negative number.");
                                        } else if (credit > 1000000.00) {
                                            invalidInput = true;
                                            System.out.println("Credit cannot be over 1,000,000.");
                                        } else {
                                            newAccount.setCredit(credit);
                                            accountsHash.put(newAccount.getUsername(), newAccount);
                                            System.out.println("Account " + newAccount.getUsername() + " created");
                                            try {
                                                File f = new File("Current User Accounts.txt");
                                                f.delete();
                                                FileWriter fileWriter = new FileWriter("Current User Accounts.txt", true);
                                                BufferedWriter bw = new BufferedWriter(fileWriter);
                                                for (String key : accountsHash.keySet()) {
                                                    bw.write(accountsHash.get(key).toFileString());
                                                    bw.newLine();
                                                }
                                                bw.write("END");
                                                bw.close();

                                                FileWriter fileWriter1 = new FileWriter("Daily Transactions File.txt", true);
                                                BufferedWriter bw1 = new BufferedWriter(fileWriter1);
                                                bw1.newLine();
                                                bw1.write("Created user with username " + newAccount.getUsername());
                                                bw1.close();
                                            } catch (IOException e) {
                                                Logger.getLogger(Ticket_Service.class.getName()).log(Level.SEVERE, null, e);
                                            }
                                            anotherAccount = true;
                                        }
                                    }
                                }
                                while (invalidInput || anotherAccount) {
                                    System.out.print("Would you like to try account creation again (y/n): ");
                                    input = scanner.nextLine().toLowerCase();
                                    switch (input) {
                                        case "y":
                                            invalidInput = false;
                                            anotherAccount = false;
                                            attemptingCreate = true;
                                            break;
                                        case "n":
                                            attemptingCreate = false;
                                            invalidInput = false;
                                            anotherAccount = false;
                                            break;
                                    }
                                }
                            }
                        }
                    } else {
                        System.out.println("You must be logged in to use the create command.");
                    }
                    break;
                case "delete":
                    //@author Errol Britto
                    if (currentAccount != null) {
                        boolean attemptingDelete = true;
                        while (attemptingDelete) {
                            if (currentAccount.getType() != Account.UserType.Admin) {
                                System.out.println("You must be logged in as an administator to delete an account.");
                                break;
                            }
                            boolean invalidInput = false;
                            boolean anotherAccount = false;
                            System.out.print("Enter the username of the account you wish to delete: ");
                            input = scanner.nextLine();
                            if (accountsHash.containsKey(input)) {
                                accountsHash.remove(input);
                                anotherAccount = true;
                                System.out.println("User " + input + " has been deleted.");
                                try {
                                    File f = new File("Current User Accounts.txt");
                                    f.delete();
                                    FileWriter fileWriter = new FileWriter("Current User Accounts.txt", true);
                                    BufferedWriter bw = new BufferedWriter(fileWriter);
                                    for (String key : accountsHash.keySet()) {
                                        bw.write(accountsHash.get(key).toFileString());
                                        bw.newLine();
                                    }
                                    bw.write("END");
                                    bw.close();
                                    FileWriter fileWriter1 = new FileWriter("Daily Transactions File.txt", true);
                                    BufferedWriter bw1 = new BufferedWriter(fileWriter1);
                                    bw1.newLine();
                                    bw1.write("Deleted user with username " + input);
                                    bw1.close();
                                } catch (IOException e) {
                                    Logger.getLogger(Ticket_Service.class.getName()).log(Level.SEVERE, null, e);
                                }
                            } else {
                                invalidInput = true;
                                System.out.println("No users found with this username.");
                            }
                            while (invalidInput || anotherAccount) {
                                System.out.print("Would you like to try account deletion again (y/n): ");
                                input = scanner.nextLine().toLowerCase();
                                switch (input) {
                                    case "y":
                                        invalidInput = false;
                                        anotherAccount = false;
                                        attemptingDelete = true;
                                        break;
                                    case "n":
                                        attemptingDelete = false;
                                        anotherAccount = false;
                                        invalidInput = false;
                                        break;
                                }
                            }
                        }
                    } else {
                        System.out.println("You must be logged in to use the delete command.");
                    }
                    break;
                case "sell":
                    if (currentAccount != null) {
                        sellManager.Sell(availableTicketsList, currentAccount);
                    } else {
                        System.out.println("You must be logged in to use the sell command.");
                    }
                    break;
                case "buy":
                    if (currentAccount != null) {
                        buyManager.Buy(availableTicketsList, currentAccount, accountsHash);
                    } else {
                        System.out.println("You must be logged in to use the buy command.");
                    }
                    break;
                
                case "refund":
                    //@author Deepsimrat Rataul
                    if (currentAccount != null) {
                        if (currentAccount.getType() == Account.UserType.SellStandard) {
                            System.out.println("Type the username of the user you wish to refund money to:  ");
                            String input3 = scanner.nextLine();

                            if (accountsHash.containsKey(input)) {
                                System.out.println("Enter the amount you wish to add:  ");
                                String valueInput = scanner.nextLine();
                                try {
                                    float input2 = Float.parseFloat(valueInput);
                                    if (currentAccount.getCredit() < input2) {
                                        System.out.println("You cannot refund more "
                                                + "than you have in your account");
                                    } else {
                                        accountsHash.get(input3).setCredit(accountsHash.get(input3).getCredit() + input2);
                                        currentAccount.setCredit(currentAccount.getCredit() - input2);

                                        System.out.println("buyer: " + accountsHash.get(input3).getCredit());
                                        System.out.println("seller: " + currentAccount.getCredit());
                                        try {
                                            File f = new File("Current User Accounts.txt");
                                            f.delete();
                                            FileWriter fileWriter = new FileWriter("Current User Accounts.txt", true);
                                            BufferedWriter bw = new BufferedWriter(fileWriter);
                                            for (String key : accountsHash.keySet()) {
                                                bw.write(accountsHash.get(key).toFileString());
                                                bw.newLine();
                                            }
                                            bw.write("END");
                                            bw.close();
                                        } catch (IOException e) {
                                            Logger.getLogger(Ticket_Service.class.getName()).log(Level.SEVERE, null, e);
                                        }
                                        try {
                                            FileWriter fileWriter = new FileWriter("Daily Transactions File.txt", true);
                                            BufferedWriter bw = new BufferedWriter(fileWriter);
                                            bw.newLine();
                                            bw.write("05" + " " + String.format("%-15s", accountsHash.get(input3).getUsername())
                                                    + " " + String.format("%-15s", currentAccount.getUsername()) + " " + input2);
                                            bw.close();
                                        } catch (IOException e) {
                                            Logger.getLogger(Ticket_Service.class.getName()).log(Level.SEVERE, null, e);
                                        }
                                    }
                                } catch (NumberFormatException nfEx) {
                                    System.out.println("Invalid credit amount entered");
                                }
                            } else {
                                System.out.println("The Account you entered does not exist!");
                            }
                        } else {
                            System.out.println("You must be a SellStandard account to be able to refund");
                        }

                    } else {
                        System.out.println("You must be logged in to use the refund command.");
                    }
                    break;

                case "addcredit":
                    //@author Deepsimrat Rataul
                    if (currentAccount != null) {
                        if (currentAccount.getType() == Account.UserType.Admin) {
                            String input6 = "";
                            System.out.println("Type the username of the user you wish to add money to:  ");
                            input = scanner.nextLine();

                            if (accountsHash.containsKey(input)) {
                                System.out.println("Enter the amount you wish to add:  ");
                                String creditInput = scanner.nextLine();

                                try {
                                    float addCreditAmount = Float.parseFloat(creditInput);

                                    if (addCreditAmount <= 0 || addCreditAmount > 1000) {
                                        System.out.println("You must enter a value greater than 0 and "
                                                + "less than or equal to 1000");
                                    } else {
                                        if (accountsHash.get(input).getType() == Account.UserType.Admin) {
                                            input6 = "AA";
                                        }
                                        if (accountsHash.get(input).getType() == Account.UserType.FullStandard) {
                                            input6 = "FS";
                                        }
                                        if (accountsHash.get(input).getType() == Account.UserType.BuyStandard) {
                                            input6 = "BS";
                                        }
                                        if (accountsHash.get(input).getType() == Account.UserType.SellStandard) {
                                            input6 = "SS";
                                        }

                                        if (accountsHash.get(input).getCredit() + addCreditAmount < 999999) {
                                            accountsHash.get(input).setCredit(accountsHash.get(input).getCredit() + addCreditAmount);

                                            System.out.println(accountsHash.get(input).getCredit());

                                            File f = new File("Current User Accounts.txt");
                                            f.delete();
                                            FileWriter fileWriter = new FileWriter("Current User Accounts.txt", true);
                                            BufferedWriter bw = new BufferedWriter(fileWriter);
                                            for (String key : accountsHash.keySet()) {
                                                bw.write(accountsHash.get(key).toFileString());
                                                bw.newLine();
                                            }
                                            bw.write("END");
                                            bw.close();

                                            fileWriter = new FileWriter("Daily Transactions File.txt", true);
                                            bw = new BufferedWriter(fileWriter);
                                            bw.newLine();
                                            bw.write("02" + " " + String.format("%-15s", accountsHash.get(input).getUsername()) + " "
                                                    + input6 + " " + accountsHash.get(input).getCredit());
                                            bw.close();
                                        } else {
                                            System.out.println("You cannot have more than $1,000,000 credit");
                                        }
                                    }
                                } catch (NumberFormatException nfEx) {
                                    System.out.println("Invalid credit amount entered");
                                } catch (IOException e) {
                                    Logger.getLogger(Ticket_Service.class.getName()).log(Level.SEVERE, null, e);
                                }

                            }
                        } else {
                            System.out.println("You must be an admin to be able to add credit");
                        }

                    } else {
                        System.out.println("You must be logged in to use the addcredit command.");
                    }
                    break;
                case "help":
                    //Display the helpString
                    System.out.println(helpString + "\n");
                    break;
                case "exit":
                    //Display a prompt and exit
                    System.out.println("Good bye");
                    break;
                default:
                    //Display a prompt letting them know invalid input was entered
                    System.out.println("Sorry. You have not entered a valid input. Please try "
                            + "again. If you would like to see the list of commands, enter "
                            + "\"help\" (without quotations)\n");
                    break;
            }
        }
    }
}
