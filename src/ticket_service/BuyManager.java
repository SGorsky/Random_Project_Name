package ticket_service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import ticket_service.Account.UserType;
import static ticket_service.SellManager.padRight;

/**
 *
 * @author Hareesh Mathiyalagan
 */
public class BuyManager {

    public String input;
    public Scanner scanner;
    public boolean gotEventTitle, gotSellerUsername, gotNumTickets;
    public ArrayList<AvailableTicket> availableTicketsList;
    public ArrayList<AvailableTicket> selectedTicketsList;
    public AvailableTicket selectedTicket;
    public Hashtable<String, Account> accountsHash;
    public Account myAccount;
    public int numberToPurchase;
    public double totalCost;
    public DecimalFormat df;

    public BuyManager() {
        selectedTicketsList = new ArrayList<>();
        df = new DecimalFormat("####0.00");
    }

    // Initialize variables and create dialogue for user to complete.
    public void Buy(ArrayList<AvailableTicket> list, Account currentAccount,
            Hashtable<String, Account> accounts) {
        input = "Not Return";
        gotEventTitle = false;
        gotSellerUsername = false;
        gotNumTickets = false;
        availableTicketsList = list;
        scanner = new Scanner(System.in);
        myAccount = currentAccount;
        accountsHash = accounts;
        numberToPurchase = 0;
        totalCost = 0.00;
        CreateDialogue();
    }

    // Ask for user input in a specific order, allowing user to enter inputs
    // again if mistake is made or return back to main loop at any time.
    public boolean CreateDialogue() {
        if (myAccount.getType() != UserType.SellStandard) {
            Output(true, "Enter return at any time to cancel operation.");
            while (!input.equals("return")) {
                if (gotSellerUsername) {
                    if (gotEventTitle) {
                        if (gotNumTickets) {
                            totalCost = selectedTicket.GetTicketPrice() * numberToPurchase;
                            Output(true, "The total cost is $" + df.format(totalCost) + " at a price of $"
                                    + df.format(selectedTicket.GetTicketPrice()) + " per ticket.");
                            Output(false, "Enter 'yes' to purchase or 'no' to return: ");
                            input = scanner.nextLine();
                            if (Confirm(input)) {
                                input = "return";
                                return true;
                            }
                        } else {
                            Output(false, "Enter the number of tickets you want to purchase: ");
                            input = scanner.nextLine();
                            gotNumTickets = ParseNumTickets(input);
                        }
                    } else {
                        Output(false, "Enter the title of the event you'd like: ");
                        input = scanner.nextLine();
                        gotEventTitle = ParseEventTitle(input);
                    }
                } else {
                    Output(false, "Enter a seller's username to check their inventory: ");
                    input = scanner.nextLine();
                    gotSellerUsername = ParseSellerUsername(input);
                }
            }
        } else {
            Output(true, "Your account does not have access to buying tickets.");
            return false;
        }

        // Clean up and return to main loop.
        Output(true, "Exiting...");
        return true;
    }

    // Get input for seller username to buy from and check if valid.
    public boolean ParseSellerUsername(String input) {
        boolean exists = false;
        for (AvailableTicket t : availableTicketsList) {
            Output(true, t.toString());
            if (t.GetSellerUsername().trim().equals(input)) {
                selectedTicketsList.add(t);
                Output(true, "Available Event: " + t.GetEventName());
                exists = true;
            }
        }

        if (exists) {
            return true;
        } else {
            Output(true, "Seller username does not exist, try again.");
            return false;
        }
    }

    // Get input for event title to buy and check if valid.
    public boolean ParseEventTitle(String input) {
        for (AvailableTicket t : selectedTicketsList) {
            if (t.GetEventName().trim().equals(input)) {
                selectedTicket = t;
                Output(true, "Number of tickets available: " + t.GetNumberTickets()
                        + " | Price: " + df.format(t.GetTicketPrice()));
                return true;
            }
        }

        Output(true, "The selected event does not exist, try again.");
        return false;
    }

    // Get input for number of tickets to buy and check if valid.
    public boolean ParseNumTickets(String input) {
        if (input != null) {
            input = input.toLowerCase();
            String text = input.replaceAll("[^0-9]+", "");
            if (!text.isEmpty() && text.length() != 0) {
                if (myAccount.getType() != UserType.Admin) {
                    if (Integer.parseInt(input) > 4) {
                        Output(true, "You are only allowed to buy at most 4 tickets.");
                        return false;
                    }
                }

                if (Integer.parseInt(input) <= selectedTicket.GetNumberTickets()) {
                    if (Integer.parseInt(input) > 0) {
                        if (myAccount.getCredit() >= Integer.parseInt(input) * selectedTicket.GetTicketPrice()) {
                            numberToPurchase = Integer.parseInt(input);
                            return true;
                        } else {
                            Output(true, "You do not have enough money for this purchase, try again.");
                            return false;
                        }
                    } else {
                        Output(true, "Enter a positive number, try again.");
                        return false;
                    }
                } else {
                    Output(true, "The seller does not have enough tickets, try again.");
                    return false;
                }
            } else {
                Output(true, "Invalid input, try again.");
                return false;
            }
        } else {
            Output(true, "Invalid input, try again.");
            return false;
        }
    }

    // Ask user for confirmation before putting in buy order.
    public boolean Confirm(String input) {
        if (input == null) {
            Output(true, "You have cancelled the transaction.");
        } else if (input.equals("yes")) {
            AvailableTicket t = new AvailableTicket(
                    selectedTicket.GetEventName(),
                    selectedTicket.GetSellerUsername(),
                    numberToPurchase,
                    selectedTicket.GetTicketPrice()
            );
            WriteToDailyTransactionsFile(t, "04");
            AddCredit(selectedTicket.GetSellerUsername(), (float) totalCost);
            RemoveCredit(myAccount.getUsername(), (float) totalCost);
            Output(true, "You have successfully purchased the tickets!");
        } else {
            Output(true, "You have cancelled the transaction.");
        }

        return true;
    }

    // Formats the output for visibility.
    public String Output(boolean newLine, String s) {
        String str;
        if (s != null) {
            str = "BUY MANAGER | " + s;
            if (newLine) {
                System.out.println(str);
            } else {
                System.out.print(str);
            }
        } else {
            str = "";
        }
        return str;
    }

    // Writes to daily transactions file.
    public static void WriteToDailyTransactionsFile(AvailableTicket t, String code) {
        PrintWriter writer;
        try {
            writer = new PrintWriter(new FileOutputStream(
                    new File("Daily Transactions File.txt"),
                    true));
            String transaction = code;
            transaction += " ";
            transaction += padRight(t.GetEventName(), 19);
            transaction += " ";
            transaction += padRight(t.GetSellerUsername(), 13);
            transaction += " ";
            transaction += padRight("" + t.GetNumberTickets(), 3);
            transaction += " ";
            transaction += padRight("" + (int) t.GetTicketPrice(), 6);
            writer.println(transaction);
            writer.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SellManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // Writes to user accounts file.
    public static void WriteToCurrentUserAccountsFile(ArrayList<AvailableTicket> availableTicketsList) {
        PrintWriter writer;
        try {
            writer = new PrintWriter(new FileOutputStream(
                    new File("Available Tickets File.txt"),
                    true));
            for (AvailableTicket t : availableTicketsList) {
                String ticket = padRight(t.GetEventName(), 19);
                ticket += " ";
                ticket += padRight(t.GetSellerUsername(), 13);
                ticket += " ";
                ticket += padRight("" + t.GetNumberTickets(), 3);
                ticket += " ";
                ticket += padRight("" + (int) t.GetTicketPrice(), 6);
                writer.println(ticket);
            }
            writer.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SellManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void AddCredit(String user, float credit) {
        try {
            accountsHash.get(user).setCredit(accountsHash.get(user).getCredit() + credit);
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
    }

    public void RemoveCredit(String user, float credit) {
        try {
            accountsHash.get(user).setCredit(accountsHash.get(user).getCredit() - credit);
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
    }
}
