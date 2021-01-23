import java.io.Serializable;

public class UserImpl implements User, Serializable {
    private int id;
    private String forenames = "";
    private String surnames = "";
    private String date_of_birth;
    private String address = "";
    private String email_address = "";
    private String identity = "";

    public UserImpl(String forenames, String surnames, String date_of_birth, String address, String email_address, String identity) {

        this.forenames = forenames;
        this.surnames = surnames;
        this.date_of_birth = date_of_birth;
        this.address = address;
        this.email_address = email_address;
        this.identity = identity;
    }

    public String getForenames() {
        return forenames;
    }

    public String getSurnames() {
        return surnames;
    }

    public int getId() {
        return id;
    }

    public String getEmail() {
        return email_address;
    }

    public String getAddress() {
        return address;
    }

    public String getIdentity() {
        return identity;
    }

    public String getDoB() {
        return date_of_birth;
    }
}