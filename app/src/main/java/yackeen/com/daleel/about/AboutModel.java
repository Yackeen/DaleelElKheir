package yackeen.com.daleel.about;

/**
 * Created by Ibrahim on 07/02/2018.
 */

public class AboutModel {

    private String name, image;

    public AboutModel() {

    }

    public AboutModel(String name, String image) {
        this.name = name;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
