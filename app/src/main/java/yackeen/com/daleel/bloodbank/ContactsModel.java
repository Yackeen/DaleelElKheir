package yackeen.com.daleel.bloodbank;

/**
 * Created by Ibrahim on 07/02/2018.
 */

public class ContactsModel {

    private String name, phone;

    public ContactsModel() {

    }

    public ContactsModel(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
