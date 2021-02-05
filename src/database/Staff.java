package database;

import java.io.Serializable;

public class Staff extends UserImpl implements User, Serializable {
    private String sector;
    private String phone_number;

    public Staff(String forenames, String surnames, String date_of_birth, String address, String email_address, String identity, String sector, String phone_number) {
        super(forenames, surnames, date_of_birth, address, email_address, identity);
        this.sector = sector;
        this.phone_number = phone_number;
    }

    public String getSector() {
        return sector;
    }

    public String getphone_number() {
        return phone_number;
    }

    public void setphone_number(String phone_number) {
        this.phone_number = phone_number;
    }
}