public class Staff extends Userimpl implements User
{
    private String role_title;
    private String phone_number;

    public Staff(String forenames, String surnames,String date_of_birth,String address,String email_address,String identity,String role_title,String phone_number)
    {
        super(forenames,surnames,date_of_birth,address,email_address,identity);
        this.role_title = role_title;
        this.phone_number = phone_number;
        
    }

    public String getrole_title()
    {
        return role_title;
    }
    
    public void setrole_title(String role_title)
    {
        this.role_title = role_title;
    }

    public String getphone_number()
    {
        return phone_number;
    }

    public void setphone_number(String phone_number)
    {
        this.phone_number = phone_number;
    }
}