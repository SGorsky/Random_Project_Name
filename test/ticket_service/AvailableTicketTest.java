package ticket_service;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Sean
 */
public class AvailableTicketTest {
    
    public AvailableTicketTest() {
    }

    /**
     * Test of GetEventName method, of class AvailableTicket.
     */
    @Test
    public void testGetEventName() {
        System.out.println("GetEventName");
        AvailableTicket instance = new AvailableTicket("Sample Event Name   administrator 456 123    ");
        String expResult = "Sample Event Name  ";
        String result = instance.GetEventName();
        assertEquals(expResult, result);
    }

    /**
     * Test of GetSellerUsername method, of class AvailableTicket.
     */
    @Test
    public void testGetSellerUsername() {
        System.out.println("GetSellerUsername");
        AvailableTicket instance = new AvailableTicket("Sample Event Name   administrator 456 123    ");
        String expResult = "administrator";
        String result = instance.GetSellerUsername();
        assertEquals(expResult, result);
    }

    /**
     * Test of GetNumberTickets method, of class AvailableTicket.
     */
    @Test
    public void testGetNumberTickets() {
        System.out.println("GetNumberTickets");
        AvailableTicket instance = new AvailableTicket("Sample Event Name   administrator 456 123    ");
        int expResult = 456;
        int result = instance.GetNumberTickets();
        assertEquals(expResult, result);
    }

    /**
     * Test of GetTicketPrice method, of class AvailableTicket.
     */
    @Test
    public void testGetTicketPrice() {
        System.out.println("GetTicketPrice");
        AvailableTicket instance = new AvailableTicket("Sample Event Name   administrator 456 123    ");
        double expResult = 123;
        double result = instance.GetTicketPrice();
        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of toString method, of class AvailableTicket.
     */
    @Test
    public void testToString() {
        System.out.println("toString");
        String sellerUsername = "administrator";
        String eventName = "Event Name";
        int numberTickets = 500;
        double ticketPrice = 59.99;
        AvailableTicket instance = new AvailableTicket(eventName, sellerUsername, numberTickets, ticketPrice);
        String expResult = "\n      sellerUsername: " + sellerUsername 
                + "\n       eventName: " + eventName
                + "\n       numberTickets: " + numberTickets
                + "\n       ticketPrice: " + ticketPrice;
        String result = instance.toString();
        assertEquals(expResult, result);
    }
    
}
