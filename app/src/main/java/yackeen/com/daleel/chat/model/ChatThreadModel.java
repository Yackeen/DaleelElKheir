package yackeen.com.daleel.chat.model;

/**
 * Created by Ibrahim on 21/02/2018.
 */

public class ChatThreadModel {

    private String caseName;
    private int UserID;
    private int ID;
    private String Image;

    public ChatThreadModel() {

    }

    public ChatThreadModel(String caseName) {
        this.caseName = caseName;
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

    public String getCaseName() {
        return caseName;
    }

    public void setCaseName(String caseName) {
        this.caseName = caseName;
    }
}
