package yackeen.com.daleel.events;

/**
 * Created by Ibrahim on 07/02/2018.
 */

public class EventsModel {
    private String id, title, location, duration, image, description, time, mobile,
            startDate, endDate, organization;

    public EventsModel() {

    }

    public EventsModel(String id, String title, String location, String duration, String image,
                       String description, String time, String mobile, String startDate, String endDate, String organization) {
        this.id = id;
        this.title = title;
        this.location = location;
        this.duration = duration;
        this.image = image;
        this.description = description;
        this.time = time;
        this.mobile = mobile;
        this.startDate = startDate;
        this.endDate = endDate;
        this.organization = organization;
    }


    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}
