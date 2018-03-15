package yackeen.com.daleel.user;

/**
 * Created by Ibrahim on 05/02/2018.
 */

public class User {

    private String id, name, email, phone, thumbnail, token, pass;

    public User() {
    }

    public User(String id, String name, String email, String phone, String thumbnail, String token, String pass) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.thumbnail = thumbnail;
        this.token = token;
        this.pass = pass;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
}
