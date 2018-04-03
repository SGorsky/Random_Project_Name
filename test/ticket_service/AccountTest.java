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
public class AccountTest {
    /**
     * Test of getUsername method, of class Account.
     */
    @Test
    public void testGetUsername() {
        System.out.println("getUsername");
        Account instance = new Account("username", "password", Account.UserType.BuyStandard, 123);
        String expResult = "username";
        String result = instance.getUsername();
        assertEquals(expResult, result);
    }

    /**
     * Test of setUsername method, of class Account.
     */
    @Test
    public void testSetUsername() {
        System.out.println("setUsername");
        String username = "testSetUsername";
        Account instance = new Account(null, null, null, 1);
        instance.setUsername(username);
        assertEquals(username, instance.getUsername());
    }

    /**
     * Test of getPassword method, of class Account.
     */
    @Test
    public void testGetPassword() {
        System.out.println("getPassword");
        Account instance = new Account("username", "password", Account.UserType.BuyStandard, 123);
        String expResult = "password";
        String result = instance.getPassword();
        assertEquals(expResult, result);
    }

    /**
     * Test of setPassword method, of class Account.
     */
    @Test
    public void testSetPassword() {
        System.out.println("setPassword");
        String password = "testSetPassword";
        Account instance = new Account("username", "password", Account.UserType.BuyStandard, 123);
        instance.setPassword(password);
        assertEquals(password, instance.getPassword());
    }

    /**
     * Test of getType method, of class Account.
     */
    @Test
    public void testGetType() {
        System.out.println("getType");
        Account instance = new Account("username", "password", Account.UserType.BuyStandard, 123);
        Account.UserType expResult = Account.UserType.BuyStandard;
        Account.UserType result = instance.getType();
        assertEquals(expResult, result);
    }

    /**
     * Test of setType method, of class Account.
     */
    @Test
    public void testSetType() {
        System.out.println("setType");
        Account.UserType type = Account.UserType.Admin;
        Account instance = new Account("username", "password", Account.UserType.BuyStandard, 123);
        instance.setType(type);
        assertEquals(type, instance.getType());
    }

    /**
     * Test of getCredit method, of class Account.
     */
    @Test
    public void testGetCredit() {
        System.out.println("getCredit");
        float expResult = 100.0F;
        Account instance = new Account("username", "password", Account.UserType.BuyStandard, expResult);
        float result = instance.getCredit();
        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of setCredit method, of class Account.
     */
    @Test
    public void testSetCredit() {
        System.out.println("setCredit");
        float credit = 1234.0F;
        Account instance = new Account("username", "password", Account.UserType.BuyStandard, 123);
        instance.setCredit(credit);
        assertEquals(credit, instance.getCredit(), 0.0);
    }

    /**
     * Test of toString method, of class Account.
     */
    @Test
    public void testToString() {
        System.out.println("toString");
        String username = "username";
        String password = "password";
        Account.UserType type = Account.UserType.Admin;
        float credit = 1234.52F;
        Account instance = new Account(username, password, type, credit);
        String expResult = "Username: " + username + "\nPassword: " + password + "\nType: " + type
                + "\nAvailable Credit: " + credit;
        String result = instance.toString();
        assertEquals(expResult, result);
    }

    /**
     * Test of toFileString method, of class Account.
     */
    @Test
    public void testToFileStringAA() {
        System.out.println("toFileStringAA");
        Account instance = new Account("administrator   AA 013206.00 password");
        String expResult = "administrator   AA 013206.00 password";
        String result = instance.toFileString();
        assertEquals(expResult, result);
    }
    @Test
    public void testToFileStringFS() {
        System.out.println("toFileStringFS");
        Account instance = new Account("administrator   FS 013206.00 password");
        String expResult = "administrator   FS 013206.00 password";
        String result = instance.toFileString();
        assertEquals(expResult, result);
    }
    @Test
    public void testToFileStringBS() {
        System.out.println("toFileStringBS");
        Account instance = new Account("administrator   BS 013206.00 password");
        String expResult = "administrator   BS 013206.00 password";
        String result = instance.toFileString();
        assertEquals(expResult, result);
    }
    @Test
    public void testToFileStringSS() {
        System.out.println("toFileStringSS");
        Account instance = new Account("administrator   SS 013206.00 password");
        String expResult = "administrator   SS 013206.00 password";
        String result = instance.toFileString();
        assertEquals(expResult, result);
    }
    @Test (expected = NullPointerException.class)
    public void testToFileStringException() {
        System.out.println("toFileStringException");
        Account instance = new Account("administrator   AB 013206.00 password");
        String expResult = "";
        String result = instance.toFileString();
        assertEquals(expResult, result);
    }
}
