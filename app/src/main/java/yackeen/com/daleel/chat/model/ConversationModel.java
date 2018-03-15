package yackeen.com.daleel.chat.model;

/**
 * Created by Ibrahim on 21/02/2018.
 */

public class ConversationModel {
    private boolean isAdmin;
    private String Message;
    private int id;

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
