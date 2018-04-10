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

    public String input;
    public Scanner scanner;
    public String eventTitle;
    public int numTickets;
    public double salePrice;
    public boolean gotEventTitle, gotSalePrice, gotNumTickets;
    public Account myAccount;
    public DecimalFormat df;
    public ArrayList<AvailableTicket> availableTicketsList;

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
    public boolean CreateDialogue() {
        if (myAccount.getType() != UserType.BuyStandard) {
            Output(true, "Enter return at any time to cancel operation.");
            while (!input.equals("return")) {
                if (gotEventTitle) {
                    if (gotNumTickets) {
                        if (gotSalePrice) {
                            Output(true, "You are selling " + numTickets + " tickets"
                                    + " to the event '" + eventTitle + "'"
                                    + " for $" + df.format(salePrice) + " per ticket.");
                            Output(false, "Enter 'yes' to confirm or 'no' to return: ");
                            input = scanner.nextLine().toLowerCase();
                            if (Confirm(input)) {
                                input = "return";
                                return true;
                            }
                        } else {
                            Output(false, "Enter a price you'd like to sell each ticket for: ");
                            input = scanner.nextLine();
                            gotSalePrice = ParseSalePrice(input);
                        }
                    } else {
                        Output(false, "Enter the number of tickets you want to sell: ");
                        input = scanner.nextLine().toLowerCase();
                        gotNumTickets = ParseNumTickets(input);
                    }
                } else {
                    Output(false, "Enter the title of the event you'd like to sell: ");
                    input = scanner.nextLine();
                    gotEventTitle = ParseEventTitle(input);
                }
            }
        } else {
            Output(true, "Your account does not have access to selling tickets.");
            return false;
        }

        // Clean up and return to main loop.
        Output(true, "Exiting...");
        return true;
    }

    // Get input from event title to sell and check if valid.
    public boolean ParseEventTitle(String input) {
        if (input != null) {
            if (input.length() <= 25) {
                eventTitle = input;
                return true;
            } else {
                Output(true, "Event title too long, must be max 25 characters.");
                return false;
            }
        } else {
            return false;
        }
    }

    // Get input for number of tickets to sell and check if valid.
    public boolean ParseNumTickets(String input) {
        if (input != null) {
            String text = input.replaceAll("[^0-9]+", "");
            if (!text.isEmpty() && text.length() != 0) {
                if (Integer.parseInt(input) <= 100) {
                    numTickets = Integer.parseInt(input);
                    return true;
                } else {
                    Output(true, "You can only sell at most 100 tickets.");
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

    // Get input for sale price and check if valid.
    public boolean ParseSalePrice(String input) {
        try {
            salePrice = Double.parseDouble(input);
            return true;
        } catch (Exception e) {
            Output(true, "Invalid input, try again.");
            return false;
        }
    }

    // Ask user for confirmation before putting in sell order.
    public boolean Confirm(String input) {
        if (input == null) {
            Output(true, "You have cancelled the transaction.");
        } else if (input.equals("yes")) {
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
    public String Output(boolean newLine, String s) {
        String str;
        if (s != null) {
            str = "SELL MANAGER | " + s;
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
