package yackeen.com.daleel;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.rogalabs.lib.LoginApplication;

/**
 * Created by fawzy on 7/29/18.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FacebookSdk.setApplicationId(getResources().getString(R.string.facebook_app_id));
        LoginApplication.startSocialLogin(this);
    }
}
