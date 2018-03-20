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

    private String input;
    private Scanner scanner;
    private boolean gotEventTitle, gotSellerUsername, gotNumTickets;
    private ArrayList<AvailableTicket> availableTicketsList;
    private ArrayList<AvailableTicket> selectedTicketsList;
    private AvailableTicket selectedTicket;
    private Hashtable<String, Account> accountsHash;
    private Account myAccount;
    private int numberToPurchase;
    private double totalCost;
    private DecimalFormat df;

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
    private void CreateDialogue() {
        if (myAccount.getType() != UserType.SellStandard) {
            Output(true, "Enter return at any time to cancel operation.");
            while (!input.equals("return")) {
                if (gotSellerUsername) {
                    if (gotEventTitle) {
                        if (gotNumTickets) {
                            if (Confirm()) {
                                input = "return";
                            }
                        } else {
                            gotNumTickets = ParseNumTickets();
                        }
                    } else {
                        gotEventTitle = ParseEventTitle();
                    }
                } else {
                    gotSellerUsername = ParseSellerUsername();
                }
            }
        } else {
            Output(true, "Your account does not have access to buying tickets.");
        }

        // Clean up and return to main loop.
        Output(true, "Exiting...");
    }

    // Get input for seller username to buy from and check if valid.
    private boolean ParseSellerUsername() {
        Output(false, "Enter a seller's username to check their inventory: ");
        input = scanner.nextLine(); 

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
    private boolean ParseEventTitle() {
        Output(false, "Enter the title of the event you'd like: ");
        input = scanner.nextLine();  

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
    private boolean ParseNumTickets() {
        Output(false, "Enter the number of tickets you want to purchase: ");
        input = scanner.nextLine().toLowerCase();

        String text = input.replaceAll("[^0-9]+", "");
        if (!text.isEmpty() && text.length() != 0) {
            if (myAccount.getType() != UserType.Admin) {
                if (Integer.parseInt(input) > 4) {
                    Output(true, "You are only allowed to buy at most 4 tickets.");
                }
            }

            if (Integer.parseInt(input) <= selectedTicket.GetNumberTickets()) {
                numberToPurchase = Integer.parseInt(input);
                return true;
            } else {
                Output(true, "The seller does not have enough tickets, try again.");
                return false;
            }
        } else {
            Output(true, "Invalid input, try again.");
            return false;
        }
    }

    // Ask user for confirmation before putting in buy order.
    private boolean Confirm() {
        totalCost = selectedTicket.GetTicketPrice() * numberToPurchase;

        Output(true, "The total cost is $" + df.format(totalCost) + " at a price of $"
                + df.format(selectedTicket.GetTicketPrice()) + " per ticket.");
        Output(false, "Enter 'yes' to purchase or 'no' to return: ");
        input = scanner.nextLine().toLowerCase();

        if (input.equals("yes")) {
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
        } else if (input.equals("no")) {
            Output(true, "You have cancelled the transaction.");
        }

        return true;
    }

    // Formats the output for visibility.
    private void Output(boolean newLine, String s) {
        if (newLine) {
            System.out.println("BUY MANAGER | " + s);
        } else {
            System.out.print("BUY MANAGER | " + s);
        }
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
            Output(true, accountsHash.get(user).toString());
            accountsHash.get(user).setCredit(accountsHash.get(user).getCredit() + credit);
            File f = new File("Current User Accounts.txt");
            f.delete();
            FileWriter fileWriter = new FileWriter("Current User Accounts.txt", true);
            BufferedWriter bw = new BufferedWriter(fileWriter);
            for (String key : accountsHash.keySet()) {
                String username = accountsHash.get(key).getUsername();
                String paddedUsername = String.format("%-15s", username);
                String password = accountsHash.get(key).getPassword();
                String paddedPassword = String.format("%-8s", password);
                Float amount = accountsHash.get(key).getCredit();
                String paddedAmount = String.format("%.2f", amount);
                String finalAmount = String.format("%9s", paddedAmount).replace(' ', '0');

                String type = null;
                if (null != accountsHash.get(key).getType()) {
                    switch (accountsHash.get(key).getType()) {
                        case BuyStandard:
                            type = "BS";
                            break;
                        case SellStandard:
                            type = "SS";
                            break;
                        case Admin:
                            type = "AA";
                            break;
                        case FullStandard:
                            type = "FS";
                            break;
                        default:
                            break;
                    }
                }
                bw.write(paddedUsername + " " + type + " " + finalAmount + " " + paddedPassword);
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
            Output(true, accountsHash.get(user).toString());
            accountsHash.get(user).setCredit(accountsHash.get(user).getCredit() - credit);
            File f = new File("Current User Accounts.txt");
            f.delete();
            FileWriter fileWriter = new FileWriter("Current User Accounts.txt", true);
            BufferedWriter bw = new BufferedWriter(fileWriter);
            for (String key : accountsHash.keySet()) {
                String username = accountsHash.get(key).getUsername();
                String paddedUsername = String.format("%-15s", username);
                String password = accountsHash.get(key).getPassword();
                String paddedPassword = String.format("%-8s", password);
                Float amount = accountsHash.get(key).getCredit();
                String paddedAmount = String.format("%.2f", amount);
                String finalAmount = String.format("%9s", paddedAmount).replace(' ', '0');

                String type = null;
                if (null != accountsHash.get(key).getType()) {
                    switch (accountsHash.get(key).getType()) {
                        case BuyStandard:
                            type = "BS";
                            break;
                        case SellStandard:
                            type = "SS";
                            break;
                        case Admin:
                            type = "AA";
                            break;
                        case FullStandard:
                            type = "FS";
                            break;
                        default:
                            break;
                    }
                }
                bw.write(paddedUsername + " " + type + " " + finalAmount + " " + paddedPassword);
                bw.newLine();
            }
            bw.write("END");
            bw.close();
        } catch (IOException e) {
            Logger.getLogger(Ticket_Service.class.getName()).log(Level.SEVERE, null, e);
        }
    }
}
