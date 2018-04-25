package yackeen.com.daleel.organization.model;

/**
 * Created by Ibrahim on 07/02/2018.
 */

public class OrganizationModel {

    private String id, name, Description, location, region, logo, category;

    public OrganizationModel() {

    }

    public OrganizationModel(String id, String name, String category, String location,
                             String region, String logo, String Description) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.location = location;
        this.region = region;
        this.logo = logo;
        this.Description = Description;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }
}
