package yackeen.com.daleel.bloodbank;

/**
 * Created by Ibrahim on 07/02/2018.
 */

public class BloodBankModel {

    private String id, name, title, city, governorate;


    public BloodBankModel() {
    }

    public BloodBankModel(String id, String name, String title, String city, String governorate) {
        this.id = id;
        this.name = name;
        this.title = title;
        this.city = city;
        this.governorate = governorate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getGovernorate() {
        return governorate;
    }

    public void setGovernorate(String governorate) {
        this.governorate = governorate;
    }
}
