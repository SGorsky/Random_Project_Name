package ticket_service;

/**
 *
 * @author Sean
 */
public class Account {
    //UserType enum  states the account type
    public enum UserType{
        Admin,
        FullStandard,
        BuyStandard,
        SellStandard
    }
    
    //username is the account's username
    private String username;
    
    //password is the account's password
    private String password;
    
    //type is the account type (either Admin, FullStandard, BuyStandard or SellStandard
    private UserType type;
    
    //credit is the credit on the account (max 999,999)
    private float credit;

    //Return the account's username
    public String getUsername() {
        return username;
    }

    //Set the account's username
    //Might delete this cause when would you need to change the username of the account
    public void setUsername(String username) {
        this.username = username;
    }

    //Return the account's password
    //May replace this with a validation function that will just return a boolean if the login
    //is correct. Safer than returning the password string itself
    public String getPassword() {
        return password;
    }

    //Set the account's password
    //Might delete this too
    public void setPassword(String password) {
        this.password = password;
    }

    //Return the account type
    public UserType getType() {
        return type;
    }

    //And will probably delete this too
    public void setType(UserType type) {
        this.type = type;
    }

    //Return the credit on the account
    public float getCredit() {
        return credit;
    }

    //Might replace this with an add or remove credit function 
    //so you can take out a specific amount or add a certain amount
    public void setCredit(float credit) {
        this.credit = credit;
    }

    //Constructor: Needs a username, password, UserType and a credit amount
    //Stores them accordingly
    public Account(String username, String password, UserType type, float credit) {
        this.username = username;
        this.password = password;
        this.type = type;
        this.credit = credit;
    }
    
    //Constructor: Takes in a line from the user accounts file and stores the data accordingly 
    public Account(String userAccountsFileLine) {
        if (userAccountsFileLine.length() == 37){
            String[] data = userAccountsFileLine.split(" +");
            username = data[0];
            credit = Float.valueOf(data[2]);
            this.password = data[3];
            switch(data[1]){
                case "AA":
                    type = UserType.Admin;
                    break;
                case "FS":
                    type = UserType.FullStandard;
                    break;
                case "BS":
                    type = UserType.BuyStandard;
                    break;
                case "SS":
                    type = UserType.SellStandard;
                    break;
            }
        }
    }

    @Override
    //A string representation of the account
    public String toString() {
        return "Username: " + username + "\nPassword: " + password + "\nType: " + type + 
                "\nAvailable Credit: " + credit;
    }
}
