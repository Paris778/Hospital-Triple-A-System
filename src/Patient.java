import java.text.SimpleDateFormat;
import java.util.Date;

public class Patient extends Userimpl implements User {
    private int primary_doctor_id;
    private String registration_date;

    public Patient(String forenames, String surnames, String date_of_birth, String address, String email_address, String identity) {
        super(forenames, surnames, date_of_birth, address, email_address, identity);
        Date dateTime = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        registration_date = df.format(dateTime);
    }

    public int getprimary_doctor_id() {
        return primary_doctor_id;
    }

    public void setprimary_doctor_id(int id) {
        primary_doctor_id = id;
    }

    public String getregistration_date() {
        return registration_date;
    }
}