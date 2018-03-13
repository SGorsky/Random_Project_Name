package ticket_service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import ticket_service.Account.UserType;
import static ticket_service.BuyManager.WriteToDailyTransactionsFile;

/**
 *
 * @author Hareesh Mathiyalagan
 */
public class SellManager {

    private String input;
    private Scanner scanner;
    private String eventTitle;
    private int numTickets;
    private double salePrice;
    private boolean gotEventTitle, gotSalePrice, gotNumTickets;
    private Account myAccount;
    private DecimalFormat df;
    private ArrayList<AvailableTicket> availableTicketsList;

    public SellManager() {
        df = new DecimalFormat("####0.00");
        availableTicketsList = new ArrayList();
    }

    // Initialize variables and create dialogue for user to complete.
    public void Sell(ArrayList<AvailableTicket> availableTickets, Account currentAccount) {
        input = "Not Return";
        gotEventTitle = false;
        gotSalePrice = false;
        gotNumTickets = false;
        eventTitle = "";
        numTickets = 0;
        salePrice = 0.00;
        scanner = new Scanner(System.in);
        myAccount = currentAccount;
        CreateDialogue();
    }

    // Ask for user input in a specific order, allowing user to enter inputs
    // again if mistake is made or return back to main loop at any time.
    private void CreateDialogue() {
        if (myAccount.getType() != UserType.BuyStandard) {
            Output(true, "Enter return at any time to cancel operation.");
            while (!input.equals("return")) {
                if (gotEventTitle) {
                    if (gotNumTickets) {
                        if (gotSalePrice) {
                            if (Confirm()) {
                                input = "return";
                            }
                        } else {
                            gotSalePrice = ParseSalePrice();
                        }
                    } else {
                        gotNumTickets = ParseNumTickets();
                    }
                } else {
                    gotEventTitle = ParseEventTitle();
                }
            }
        } else {
            Output(true, "Your account does not have access to selling tickets.");
        }

        // Clean up and return to main loop.
        Output(true, "Exiting...");
    }

    // Get input fro event title to sell and check if valid.
    private boolean ParseEventTitle() {
        Output(false, "Enter the title of the event you'd like to sell: ");
        input = scanner.nextLine();
        // TODO: Parse Input  
        // TODO: Check if input less than 25 characters

        eventTitle = input;
        return true;
    }

    // Get input for number of tickets to sell and check if valid.
    private boolean ParseNumTickets() {
        Output(false, "Enter the number of tickets you want to sell: ");
        input = scanner.nextLine().toLowerCase();
        // TODO: Parse Input
        // TODO: Check if number of tickets less than 100

        numTickets = Integer.parseInt(input);
        return true;
    }

    // Get input for sale price and check if valid.
    private boolean ParseSalePrice() {
        Output(false, "Enter a price you'd like to sell each ticket for: ");
        input = scanner.nextLine();
        // TODO: Parse Input    
        // TODO: Check if price less than 999.99

        salePrice = Double.parseDouble(input);
        return true;
    }

    // Ask user for confirmation before putting in sell order.
    private boolean Confirm() {
        Output(true, "You are selling " + numTickets + " tickets"
                + " to the event '" + eventTitle + "'"
                + " for $" + df.format(salePrice) + " per ticket.");
        Output(false, "Enter 'yes' to confirm or 'no' to return: ");
        input = scanner.nextLine().toLowerCase();
        // TODO: Parse Input

        if (input.equals("yes")) {
            AvailableTicket t = new AvailableTicket(
                    eventTitle,
                    myAccount.getUsername(),
                    numTickets,
                    salePrice
            );
            availableTicketsList.add(t);
            WriteToDailyTransactionsFile(t, "03");
            WriteToAvailableTicketsFile(availableTicketsList);
            Output(true, "You have successfully put the tickets for sale.");
        } else if (input.equals("no")) {
            Output(true, "You have cancelled the transaction.");
        }

        return true;
    }

    // Formats the output for visibility.
    private void Output(boolean newLine, String s) {
        if (newLine) {
            System.out.println("SELL MANAGER | " + s);
        } else {
            System.out.print("SELL MANAGER | " + s);
        }
    }

    // Writes to available tickets file.
    public static void WriteToAvailableTicketsFile(ArrayList<AvailableTicket> availableTicketsList) {
        PrintWriter writer;
        try {
            writer = new PrintWriter("Available Tickets File.txt", "UTF-8");
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
            writer.println("END");
            writer.close();
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Logger.getLogger(SellManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static String padRight(String s, int n) {
        return String.format("%1$-" + n + "s", s);
    }
}
