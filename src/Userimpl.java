public class Userimpl implements User {
    private int id;
    private String forenames = "";
    private String surnames = "";
    private String date_of_birth;
    private String address = "";
    private String email_address = "";
    private String identity = "";

    public Userimpl(String forenames, String surnames, String date_of_birth, String address, String email_address, String identity) {

        this.forenames = forenames;
        this.surnames = surnames;
        this.date_of_birth = date_of_birth;
        this.address = address;
        this.email_address = email_address;
        this.identity = identity;
    }

    public String getname() {
        return forenames + surnames;
    }

    public int getid() {
        return id;
    }

    public String getmail() {
        return email_address;
    }

    public String getaddress() {
        return address;
    }

    public String getidentity() {
        return identity;
    }

    public String getdateofbirth() {
        return date_of_birth;
    }
}