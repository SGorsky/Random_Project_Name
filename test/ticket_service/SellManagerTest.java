package ticket_service;

import java.util.ArrayList;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Hareesh Mathiyalagan
 */
public class SellManagerTest {

    public static ArrayList ticketsList = new ArrayList();
    public static AvailableTicket ticket;
    public static SellManager instance;
    public static Account testAdmin;
    public static Account testBuyStandard;
    public static Account testSellStandard;

    public SellManagerTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        testAdmin = new Account(
                "TestUsername",
                "TestPassword",
                Account.UserType.Admin,
                10000);
        testBuyStandard = new Account(
                "TestUsername",
                "TestPassword",
                Account.UserType.BuyStandard,
                10000);
        testSellStandard = new Account(
                "TestUsername",
                "TestPassword",
                Account.UserType.SellStandard,
                10000);

        ticket = new AvailableTicket(
                "TestEvent",
                "TestUsername",
                10,
                100.00
        );
        ticketsList.add(ticket);
        instance = new SellManager();
        instance.availableTicketsList = ticketsList;
        instance.myAccount = testAdmin;
    }

    @AfterClass
    public static void tearDownClass() {
    }

    /**
     * Test of padRight method, of class SellManager.
     */
    @Test
    public void testPadRight() {
        System.out.println("padRight");
        String s = "test";
        int n = 7;
        String expResult = "test   ";
        String result = SellManager.padRight(s, n);
        assertEquals(expResult, result);
    }

    /**
     * Test of CreateDialogue method, of class SellManager.
     */
    @Test
    public void testCreateDialogue() {
        System.out.println("CreateDialogue");
        boolean result;

        //Statement 1: userType == BuyStandard
        instance.myAccount = testBuyStandard;
        result = instance.CreateDialogue();
        assertEquals(false, result);
    }

    /**
     * Test of ParseEventTitle method, of class SellManager.
     */
    @Test
    public void testParseEventTitle() {
        System.out.println("ParseEventTitle");
        boolean result;

        //Statement 1: input.length <= 25
        instance.input = "sampleEventName";
        result = instance.ParseEventTitle(instance.input);
        assertEquals(true, result);

        //Statement 2: input.length >= 25
        instance.input = "longAndInvalidEventNameNotAllowed";
        result = instance.ParseEventTitle(instance.input);
        assertEquals(false, result);

        //Statement 3: input is empty
        instance.input = null;
        result = instance.ParseEventTitle(instance.input);
        assertEquals(false, result);
    }

    /**
     * Test of ParseNumTickets method, of class SellManager.
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
 
        //Statement 4: input <= 100
        instance.input = "20";
        result = instance.ParseNumTickets(instance.input);
        assertEquals(true, result);
        
          //Statement 5: input > 100
        instance.input = "150";
        result = instance.ParseNumTickets(instance.input);
        assertEquals(false, result);
    }

    /**
     * Test of ParseSalePrice method, of class SellManager.
     */
    @Test
    public void testParseSalePrice() {
        System.out.println("ParseSalePrice");
        boolean result;

        //Statement 1: input is a double
        instance.input = "1.11";
        result = instance.ParseSalePrice(instance.input);
        assertEquals(true, result);

        //Statement 2: input not a double
        instance.input = "xd";
        result = instance.ParseSalePrice(instance.input);
        assertEquals(false, result);

        //Statement 3: input is null
        instance.input = null;
        result = instance.ParseSalePrice(instance.input);
        assertEquals(false, result);

        //Statement 4: userType is admin and input > 4
        instance.myAccount = testAdmin;
        instance.input = "5";
        result = instance.ParseSalePrice(instance.input);
        assertEquals(true, result);
    }

    /**
     * Test of Confirm method, of class SellManager.
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
     * Test of Output method, of class SellManager.
     */
    @Test
    public void testOutput() {
        System.out.println("Output");
        String result;

        //Statement 1: newLine == true
        result = instance.Output(true, "test");
        assertEquals("SELL MANAGER | " + "test", result);

        //Statement 2: newLine == false
        result = instance.Output(false, "test");
        assertEquals("SELL MANAGER | " + "test", result);

        //Statement 3: input == null
        result = instance.Output(true, null);
        assertEquals("", result);
    }
}
