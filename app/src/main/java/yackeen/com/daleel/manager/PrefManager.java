package yackeen.com.daleel.manager;

import android.content.Context;
import android.content.SharedPreferences;

import yackeen.com.daleel.user.User;

import static yackeen.com.daleel.constants.Constants.NORMAL_USER;

/**
 * Created by Ibrahim on 05/02/2018.
 */

public class PrefManager {

    public static final String IS_FROM_NOTIFICATION = "isFromNotification";
    public static final String NOTIFICATION_TYPE = "notificationType";
    private static final String PREF_NAME = "Daleel";
    // SharedPreference Keys
    private static final String USER_ID = "user_id";
    private static final String USER_NAME = "user_name";
    private static final String USER_IMAGE = "user_image";
    private static final String USER_EMAIL = "user_email";
    private static final String USER_PHONE = "user_phone";
    private static final String USER_TOKEN = "user_token";
    private static final String USER_PASSWORD = "user_pass";
    private static final String IS_LOGIN = "isLoggedIN";
    private static final String APP_LANGUAGE = "app_language";
    private static final String APP_NOTIFICATION = "app_notification";
    private static final String USER_TYPE = "user_type";
    private static final String TOKEN_ADDED = "token_added";
    private static final String USER_KEY = "user_key";
    // SharedPreference Objects
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private int PRIVATE_MODE = 0;
    // Context
    private Context context;


    public PrefManager(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = preferences.edit();
    }

    public void createLogin(User user) {
        editor.putString(USER_ID, user.getId());
        editor.putString(USER_NAME, user.getName());
        editor.putString(USER_IMAGE, user.getThumbnail());
        editor.putString(USER_EMAIL, user.getEmail());
        editor.putString(USER_PHONE, user.getPhone());
        editor.putString(USER_TOKEN, user.getToken());
        editor.putBoolean(IS_LOGIN, true);
        editor.commit();
    }

    public boolean isLoggedIn() {
        return preferences.getBoolean(IS_LOGIN, false);
    }


    public void setAppNotification(int statue) {
        editor.putInt(APP_NOTIFICATION, statue).apply();
    }

    public int getNotificationStatue() {
        return preferences.getInt(APP_NOTIFICATION, 0);
    }

    public void setIsFromNotification(boolean val) {
        editor.putBoolean(IS_FROM_NOTIFICATION, val).commit();
    }

    public boolean isFromNotification() {
        return preferences.getBoolean(IS_FROM_NOTIFICATION, false);
    }

    public String getNotificationType() {
        return preferences.getString(NOTIFICATION_TYPE, null);
    }

    public void setNotificationType(String type) {
        editor.putString(NOTIFICATION_TYPE, type).commit();
    }

    public String getAppLanguage() {
        return preferences.getString(APP_LANGUAGE, null);
    }

    public void setAppLanguage(String language) {
        editor.putString(APP_LANGUAGE, language).apply();
    }

    public boolean isTokenAdded() {
        return preferences.getBoolean(TOKEN_ADDED, false);
    }

    public void setTokenAdded(boolean isAdded) {
        editor.putBoolean(TOKEN_ADDED, isAdded).commit();
    }

    public String getUserKey() {
        return preferences.getString(USER_KEY, null);
    }

    public void setUserKey(String userKey) {
        editor.putString(USER_KEY, userKey).commit();
    }

    public int getUserType() {
        return preferences.getInt(USER_TYPE, NORMAL_USER);
    }

    public void setUserType(int userType) {
        editor.putInt(USER_TYPE, userType).commit();
    }

    public void clearSession() {
        editor.clear();
        editor.commit();
    }

    public User getUser() {

        if (preferences.getString(USER_ID, null) != null) {
            String id, name, email, phone, thumbnail, token, password;
            id = preferences.getString(USER_ID, null);
            name = preferences.getString(USER_NAME, null);
            email = preferences.getString(USER_EMAIL, null);
            phone = preferences.getString(USER_PHONE, null);
            thumbnail = preferences.getString(USER_IMAGE, null);
            token = preferences.getString(USER_TOKEN, null);
            password = preferences.getString(USER_PASSWORD, null);

            return new User(id, name, email, phone, thumbnail, token, password);
        }
        return null;

    }

}
