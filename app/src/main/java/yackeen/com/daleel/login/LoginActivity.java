package yackeen.com.daleel.login;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.rogalabs.lib.Callback;
import com.rogalabs.lib.LoginView;
import com.rogalabs.lib.SocialUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import yackeen.com.daleel.R;
import yackeen.com.daleel.connection.FetchData;
import yackeen.com.daleel.connection.PostData;
import yackeen.com.daleel.connection.VolleyCallBack;
import yackeen.com.daleel.home.MainActivity;
import yackeen.com.daleel.manager.PrefManager;
import yackeen.com.daleel.register.RegisterActivity;
import yackeen.com.daleel.user.User;

import static yackeen.com.daleel.constants.Constants.FORGET_PASSWORD;
import static yackeen.com.daleel.constants.Constants.LOGIN;
import static yackeen.com.daleel.constants.Constants.NORMAL_USER;
import static yackeen.com.daleel.constants.Constants.REGISTER;
import static yackeen.com.daleel.constants.Constants.RESET_PASSWORD;
import static yackeen.com.daleel.constants.Constants.SOCIAL_MEDIA_USER;
import static yackeen.com.daleel.constants.Constants.USER_PROFILE;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends LoginView implements OnClickListener {

    private static final String TAG = "LoginActivity";
    private static int CURRENT_USER = NORMAL_USER;
    Button signUpWithFace, signUpWithGoogle;
    private EditText mEmailView, mPasswordView;
    private View mLoginFormView;
    private ProgressBar mProgressView;
    private TextView newAccount, login, forgetPass, langTV;
    private PrefManager manager;
    private boolean isFaceBookUser = false;
    private boolean isGoogleUser = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

//        FirebaseCrash.log("Here comes the exception!");
//        FirebaseCrash.report(new Exception("oops!"));
        manager = new PrefManager(this);
        setViews();
        setSocial();
//        setLang(manager.getAppLanguage().equals("en") ? "ar" : "en");
    }

    private void setSocial() {
    }


    private void setViews() {
        // Set up the login form.
        mEmailView = findViewById(R.id.email);
        login = findViewById(R.id.login);
        forgetPass = findViewById(R.id.forgetPassword);
        signUpWithFace = findViewById(R.id.facebook);
        signUpWithGoogle = findViewById(R.id.google);
        mPasswordView = (EditText) findViewById(R.id.password);
        mLoginFormView = findViewById(R.id.login_form);
        langTV = findViewById(R.id.setLang);
        mProgressView = findViewById(R.id.progress);
        newAccount = findViewById(R.id.newAccount);
        newAccount.setOnClickListener(this);
        login.setOnClickListener(this);
        forgetPass.setOnClickListener(this);
        signUpWithGoogle.setOnClickListener(this);
        signUpWithFace.setOnClickListener(this);
        setLangSwitch();
    }

    private void setLangSwitch() {
        langTV.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (manager.getAppLanguage() != null) {
                    updateLanguage(manager.getAppLanguage().equals("en") ? "ar" : "en");
                }
            }
        });
    }

    private void updateLanguage(String language) {
        manager.setAppLanguage(language);
        final Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Resources res = getResources();
        Configuration cfg = new Configuration(res.getConfiguration());
        cfg.locale = locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            cfg.setLayoutDirection(locale);
        }
        res.updateConfiguration(cfg, res.getDisplayMetrics());

        Intent i = getIntent();
//        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        this.finish();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        this.finish();
    }

    private void attemptLogin() {

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            focusView.requestFocus();
        } else {
            // perform the user login attempt.
            showProgress(true);
            sendLoginRequest(email, password);
        }
    }

    private void sendLoginRequest(final String email, final String password) {
        final HashMap<String, String> params = new HashMap<>();

        if (isFaceBookUser) {
            params.put("Facebook_ID", password);
        } else if (isGoogleUser) {
            params.put("google_ID", password);
        } else {
            params.put("Email", email);
            params.put("Password", password);
        }

        if (CURRENT_USER == SOCIAL_MEDIA_USER)
            manager.setUserType(SOCIAL_MEDIA_USER);

        FetchData fetchData = new FetchData(this, TAG, null, LOGIN,
                Request.Method.POST, params, null);
        fetchData.getData(new VolleyCallBack() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                try {
                    boolean isSuccess = jsonObject.getBoolean("IsSuccess");
                    if (isSuccess) {
                        JSONObject object = jsonObject.getJSONObject("Response");

                        final String userId = object.getString("UserID");
                        final String token = object.getString("SecurityToken");
                        Log.e(TAG, "onSuccess: " + token);
                        HashMap<String, String> map = new HashMap<>();
                        HashMap<String, String> head = new HashMap<>();
                        head.put("SecurityToken", token);
                        map.put("UserID", userId);
                        FetchData fetch = new FetchData(LoginActivity.this, TAG, null, USER_PROFILE,
                                Request.Method.POST, map, head);
                        fetch.getData(new VolleyCallBack() {
                            @Override
                            public void onSuccess(JSONObject jsonObject) {

                                Log.e(TAG, "onSuccess: " + jsonObject);
                                try {
                                    JSONObject userOb = jsonObject.getJSONObject("Response");
                                    User user = new User(userId, userOb.getString("Name"),
                                            userOb.getString("Email"),
                                            userOb.getString("Mobile"),
                                            userOb.getString("Image"),
                                            token, password);
                                    PrefManager manager = new PrefManager(LoginActivity.this);
                                    manager.createLogin(user);
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                    } else {
                        showProgress(false);
                        Toast.makeText(LoginActivity.this, jsonObject.getString("ErrorMessage"), Toast.LENGTH_SHORT).show();
//                        register(email, password);
//                        Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

//            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
//                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
//            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.newAccount:
                launchRegister();
                break;
            case R.id.login:
                attemptLogin();
                break;
            case R.id.facebook:
                loginWithFB();
                break;
            case R.id.google:
                loginWithGl();
                break;
            case R.id.forgetPassword:
                resetPassDialog();
                break;
        }
    }

    private void loginWithFB() {
        loginWithFacebook(new Callback() {
            @Override
            public void onSuccess(SocialUser socialUser) {
                CURRENT_USER = SOCIAL_MEDIA_USER;
                isFaceBookUser = true;
                register(socialUser.getEmail() + "@facebook.com", socialUser.getId());
            }

            @Override
            public void onError(Throwable throwable) {
                throwable.printStackTrace();
                Toast.makeText(LoginActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loginWithGl() {
        loginWithGoogle(new Callback() {
            @Override
            public void onSuccess(SocialUser socialUser) {
                CURRENT_USER = SOCIAL_MEDIA_USER;
                isGoogleUser = true;
                register(socialUser.getEmail() + "@google.com", socialUser.getId());
            }

            @Override
            public void onError(Throwable throwable) {
                throwable.printStackTrace();
                Toast.makeText(LoginActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("fawzy.fail with google", throwable.toString());
            }
        });
    }

    private void resetPassDialog() {
        final AlertDialog.Builder dialog;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            dialog = new AlertDialog.Builder(this);
        }
        View layout = LayoutInflater.from(this).inflate(R.layout.forget_password, null);
        final EditText email, code, password;
        final ProgressBar progressBar = layout.findViewById(R.id.progress);
        final TextView send = layout.findViewById(R.id.send);
        final TextView reset = layout.findViewById(R.id.reset);
        email = layout.findViewById(R.id.email);
        code = layout.findViewById(R.id.code);
        password = layout.findViewById(R.id.password);
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
//                    dialog.setView(layout);
        //else
        dialog.setView(layout);
        dialog.show();
        code.setVisibility(View.GONE);
        password.setVisibility(View.GONE);
        reset.setVisibility(View.GONE);
        send.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, String> params = new HashMap<>();
                params.put("Email", email.getText().toString());
                FetchData fetchData = new FetchData(LoginActivity.this, TAG, progressBar, FORGET_PASSWORD,
                        Request.Method.POST, params, null);
                fetchData.getData(new VolleyCallBack() {
                    @Override
                    public void onSuccess(JSONObject jsonObject) {
                        try {
                            boolean isSuccess = jsonObject.getBoolean("IsSuccess");
                            if (isSuccess) {
                                code.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.GONE);
                                password.setVisibility(View.VISIBLE);
                                reset.setVisibility(View.VISIBLE);
                                send.setVisibility(View.GONE);
                                reset.setOnClickListener(new OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        HashMap<String, String> param = new HashMap<>();
                                        param.put("Email", email.getText().toString());
                                        param.put("Code", code.getText().toString());
                                        param.put("Password", password.getText().toString());
                                        FetchData fetch = new FetchData(LoginActivity.this, TAG, progressBar, RESET_PASSWORD,
                                                Request.Method.POST, param, null);
                                        fetch.getData(new VolleyCallBack() {
                                            @Override
                                            public void onSuccess(JSONObject jsonObject) {
                                                //Log.e(TAG, "onSuccess: " + jsonObject);
                                                try {
                                                    boolean isSuccess = jsonObject.getBoolean("IsSuccess");
                                                    if (isSuccess) {
                                                        Toast.makeText(LoginActivity.this,
                                                                jsonObject.getString("Response"), Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(LoginActivity.this,
                                                                jsonObject.getString("ErrorMessage"), Toast.LENGTH_SHORT).show();
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        });
                                    }
                                });
                            } else {
                                Toast.makeText(LoginActivity.this, jsonObject.getString("ErrorMessage"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    private void launchRegister() {
        startActivity(new Intent(this, RegisterActivity.class));
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void register(final String Email, final String ID) {
        try {
            final JSONObject jsonBody = new JSONObject();
            jsonBody.put("Email", Email);
            if (isFaceBookUser) {
                jsonBody.put("Facebook_ID", ID);
                jsonBody.put("google_ID", "");
            }
            if (isGoogleUser) {
                jsonBody.put("google_ID", ID);
                jsonBody.put("Facebook_ID", "");
            }
            jsonBody.put("Mobile", "");
            jsonBody.put("Name", "");
            jsonBody.put("Password", "");
            jsonBody.put("Image", "");

            PostData postData = new PostData(this, jsonBody, REGISTER, Request.Method.POST,
                    mProgressView, null);
            postData.post(new VolleyCallBack() {
                @Override
                public void onSuccess(JSONObject jsonObject) {
                    try {
                        boolean isSuccess = jsonObject.getBoolean("IsSuccess");
                        Log.e(TAG, "register.isSuccess");
                        if (isSuccess) {
                            sendLoginRequest(Email, ID);
                        } else {
                            Toast.makeText(LoginActivity.this,
                                    jsonObject.getString("ErrorMessage"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }
}

