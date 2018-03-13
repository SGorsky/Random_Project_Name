package ticket_service;

/**
 *
 * @author Hareesh Mathiyalagan
 */
public class AvailableTicket {

    private String eventName;
    private String sellerUsername;
    private int numberTickets;
    private double ticketPrice;

    // Create a ticket given inputs manually.
    public AvailableTicket(String event, String seller, int num, double price) {
        this.eventName = event;
        this.sellerUsername = seller;
        this.numberTickets = num;
        this.ticketPrice = price;
    }

    // Create a ticket given line from file.
    public AvailableTicket(String userAccountsFileLine) {
        if (userAccountsFileLine.length() == 45) {
            eventName = userAccountsFileLine.substring(0, 19);
            sellerUsername = userAccountsFileLine.substring(20, 33);
            numberTickets = Integer.parseInt(userAccountsFileLine.substring(34, 37));
            ticketPrice = Double.parseDouble(userAccountsFileLine.substring(38, 44));
        }
    }

    // Return name of the event for this ticket.
    public String GetEventName() {
        return eventName;
    }

    // Return username of the seller of this ticket.
    public String GetSellerUsername() {
        return sellerUsername;
    }

    // Return the number of tickets available.
    public int GetNumberTickets() {
        return numberTickets;
    }

    // Return the price per ticket.
    public double GetTicketPrice() {
        return ticketPrice;
    }

    // Format a string for output of this ticket.
    @Override
    public String toString() {
        return "\n      sellerUsername: " + sellerUsername
                + "\n       eventName: " + eventName
                + "\n       numberTickets: " + numberTickets
                + "\n       ticketPrice: " + ticketPrice;
    }
}
