package ticket_service;

import java.util.ArrayList;
import java.util.Hashtable;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import ticket_service.Account.UserType;

/**
 *
 * @author Hareesh Mathiyalagan
 */
public class BuyManagerTest {

    public static ArrayList ticketsList = new ArrayList();
    public static AvailableTicket ticket;
    public static BuyManager instance;
    public static Account testAdmin;
    public static Account testBuyStandard;
    public static Account testSellStandard;

    public BuyManagerTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        testAdmin = new Account(
                "TestUsername",
                "TestPassword",
                UserType.Admin,
                10000);
        testBuyStandard = new Account(
                "TestUsername",
                "TestPassword",
                UserType.BuyStandard,
                10000);
        testSellStandard = new Account(
                "TestUsername",
                "TestPassword",
                UserType.SellStandard,
                10000);

        ticket = new AvailableTicket(
                "TestEvent",
                "TestUsername",
                10,
                100.00
        );
        ticketsList.add(ticket);
        instance = new BuyManager();
        instance.availableTicketsList = ticketsList;
        instance.selectedTicket = ticket;
        instance.myAccount = testAdmin;
        instance.numberToPurchase = 1;
        instance.totalCost = instance.numberToPurchase * ticket.GetTicketPrice();
    }

    @AfterClass
    public static void tearDownClass() {
    }

    /**
     * Test of CreateDialogue method, of class BuyManager.
     */
    @Test
    public void testCreateDialogue() {
        System.out.println("CreateDialogue");
        boolean result;

        //Statement 1: userType == SellStandard
        instance.myAccount = testSellStandard;
        result = instance.CreateDialogue();
        assertEquals(false, result);
    }

    /**
     * Test of ParseSellerUsername method, of class BuyManager.
     */
    @Test
    public void testParseSellerUsername() {
        System.out.println("ParseSellerUsername");
        boolean result;

        //Statement 1: input == sellerUsername
        instance.input = ticket.GetSellerUsername();
        result = instance.ParseSellerUsername(instance.input);
        assertEquals(true, result);

        //Statement 2: input != sellerUsername
        instance.input = "wrongUsername";
        result = instance.ParseSellerUsername(instance.input);
        assertEquals(false, result);

        //Statement 3: input is null
        instance.input = null;
        result = instance.ParseSellerUsername(instance.input);
        assertEquals(false, result);
    }

    /**
     * Test of ParseEventTitle method, of class BuyManager.
     */
    @Test
    public void testParseEventTitle() {
        System.out.println("ParseEventTitle");
        boolean result;

        //Statement 1: input == eventTitle
        instance.input = ticket.GetEventName();
        result = instance.ParseEventTitle(instance.input);
        assertEquals(true, result);

        //Statement 2: input != eventTitle
        instance.input = "wrongEventTitle";
        result = instance.ParseEventTitle(instance.input);
        assertEquals(false, result);

        //Statement 3: input is empty
        instance.input = null;
        result = instance.ParseEventTitle(instance.input);
        assertEquals(false, result);
    }

    /**
     * Test of ParseNumTickets method, of class BuyManager.
     */
    @Test
    public void testParseNumTickets() {
        System.out.println("ParseNumTickets");
        boolean result;

        //Statement 1: input is an integer
        instance.input = "1";
        result = instance.ParseNumTickets(instance.input);
        assertEquals(true, result);

        //Statement 2: input not an integer
        instance.input = "xd";
        result = instance.ParseNumTickets(instance.input);
        assertEquals(false, result);

        //Statement 3: input is null
        instance.input = null;
        result = instance.ParseNumTickets(instance.input);
        assertEquals(false, result);

        //Statement 4: userType is admin and input > 4
        instance.myAccount = testAdmin;
        instance.input = "5";
        result = instance.ParseNumTickets(instance.input);
        assertEquals(true, result);

        //Statement 5: userType is admin and input <= 4
        instance.myAccount = testAdmin;
        instance.input = "2";
        result = instance.ParseNumTickets(instance.input);
        assertEquals(true, result);

        //Statement 6: userType is not admin and input > 4
        instance.myAccount = testBuyStandard;
        instance.input = "5";
        result = instance.ParseNumTickets(instance.input);
        assertEquals(false, result);

        //Statement 7: userType is not admin and input <= 4
        instance.myAccount = testBuyStandard;
        instance.input = "2";
        result = instance.ParseNumTickets(instance.input);
        assertEquals(true, result);

        //Statement 8: input > available tickets
        instance.myAccount = testAdmin;
        instance.input = "20";
        result = instance.ParseNumTickets(instance.input);
        assertEquals(false, result);
    }

    /**
     * Test of Confirm method, of class BuyManager.
     */
    @Test
    public void testConfirm() {
        System.out.println("Confirm");
        boolean result;

        //Statement 1: input != yes
        instance.input = "randomInput";
        result = instance.Confirm(instance.input);
        assertEquals(true, result);

        //Statement 2: input == null
        instance.input = null;
        result = instance.Confirm(instance.input);
        assertEquals(true, result);
        
        //Statement 3: input == "Yes"
        instance.input = "Yes";
        result = instance.Confirm(instance.input);
        assertEquals(true, result);

    }

    /**
     * Test of Output method, of class BuyManager.
     */
    @Test
    public void testOutput() {
        System.out.println("Output");
        String result;

        //Statement 1: newLine == true
        result = instance.Output(true, "test");
        assertEquals("BUY MANAGER | " + "test", result);

        //Statement 2: newLine == false
        result = instance.Output(false, "test");
        assertEquals("BUY MANAGER | " + "test", result);

        //Statement 3: input == null
        result = instance.Output(true, null);
        assertEquals("", result);
    }
}
