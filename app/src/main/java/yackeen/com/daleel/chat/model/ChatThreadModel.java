package yackeen.com.daleel.chat.model;

/**
 * Created by Ibrahim on 21/02/2018.
 */

public class ChatThreadModel {

    private String message;
    private int UserID;
    private int ID;
    private String Image;

    public ChatThreadModel() {

    }

    public ChatThreadModel(String message) {
        this.message = message;
    }

    public int getUserID() {
        return UserID;
    }

    public void setUserID(int userID) {
        UserID = userID;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
