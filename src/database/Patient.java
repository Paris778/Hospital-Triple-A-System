package database;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Patient extends UserImpl implements User, Serializable {
    private int primaryDoctorId;
    private String registrationDate;

    public Patient(String forenames, String surnames, String date_of_birth, String address, String email_address, String identity) {
        super(forenames, surnames, date_of_birth, address, email_address, identity);
        Date dateTime = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        registrationDate = df.format(dateTime);
    }

    public int getPrimaryDoctorId() {
        return primaryDoctorId;
    }

    public void setPrimaryDoctorId(int id) {
        primaryDoctorId = id;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }
}