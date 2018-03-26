package yackeen.com.daleel.register;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.google.firebase.crash.FirebaseCrash;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import yackeen.com.daleel.R;
import yackeen.com.daleel.connection.FetchData;
import yackeen.com.daleel.connection.PostData;
import yackeen.com.daleel.connection.VolleyCallBack;
import yackeen.com.daleel.home.MainActivity;
import yackeen.com.daleel.manager.PrefManager;
import yackeen.com.daleel.user.User;

import static yackeen.com.daleel.constants.Constants.LOGIN;
import static yackeen.com.daleel.constants.Constants.NORMAL_USER;
import static yackeen.com.daleel.constants.Constants.REGISTER;
import static yackeen.com.daleel.constants.Constants.USER_PROFILE;

public class RegisterActivity extends AppCompatActivity {


    public static final int PICK_IMAGE_REQUEST = 77;
    private static final String TAG = "RegisterActivity";
    ImageView profilePic, pickPic;
    EditText name, phone, email, pass, rePass;
    TextView regist, signIn;
    ProgressBar mProgressView;
    View mLoginFormView;
    private Bitmap bitmap;

    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        FirebaseCrash.log("Here comes the exception!");
        FirebaseCrash.report(new Exception("oops!"));

        initView();

        pickPic.setOnClickListener(new Listener());
        regist.setOnClickListener(new Listener());
        signIn.setOnClickListener(new Listener());


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            Uri filePath = data.getData();
            try {
                //Getting the Bitmap from Gallery
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);

                profilePic.setImageBitmap(bitmap);

            } catch (IOException ex) {

            }
        }

    }

    public String getStringImage(Bitmap bmp) {
        if (bmp == null)
            return "";
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 10, baos);
            byte[] imageBytes = baos.toByteArray();
            if (imageBytes.length > 1)
                return Base64.encodeToString(imageBytes, Base64.DEFAULT);
            else return "";
        } catch (Exception x) {
            x.printStackTrace();
            return "";
        }
    }

    private void register() {
        if (validateInput()) {
            try {
                final JSONObject jsonBody = new JSONObject();
                jsonBody.put("Email", email.getText().toString());
                jsonBody.put("Facebook_ID", "");
                jsonBody.put("google_ID", "");
                jsonBody.put("Mobile", phone.getText().toString());
                jsonBody.put("Name", name.getText().toString());
                jsonBody.put("Password", pass.getText().toString());
                jsonBody.put("Image", getStringImage(bitmap));

                PostData postData = new PostData(this, jsonBody, REGISTER, Request.Method.POST,
                        mProgressView, null);
                postData.post(new VolleyCallBack() {
                    @Override
                    public void onSuccess(JSONObject jsonObject) {
                        try {
                            boolean isSuccess = jsonObject.getBoolean("IsSuccess");
                            Log.e(TAG, "register.isSuccess");
                            if (isSuccess) {
                                sendLoginRequest(email.getText().toString(),
                                        pass.getText().toString());
                            } else {
                                Toast.makeText(RegisterActivity.this,
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

    private void sendLoginRequest(String email, String password) {
        Log.e(TAG, "sendLoginRequest.email= " + email + " ,pass= " + password);
        final HashMap<String, String> params = new HashMap<>();
        params.put("Email", email);
        params.put("Password", password);
        FetchData fetchData = new FetchData(this, TAG, mProgressView, LOGIN,
                Request.Method.POST, params, null);
        fetchData.getData(new VolleyCallBack() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                try {
                    boolean isSuccess = jsonObject.getBoolean("IsSuccess");
                    Log.e(TAG, "sendLoginRequest.IsSuccess= " + isSuccess);
                    if (isSuccess) {
                        JSONObject object = jsonObject.getJSONObject("Response");

                        final String userId = object.getString("UserID");
                        final String token = object.getString("SecurityToken");
                        HashMap<String, String> headers = new HashMap<>();
                        HashMap<String, String> param = new HashMap<>();
                        headers.put("SecurityToken", token);
                        param.put("UserID", userId);
                        FetchData fetch = new FetchData(RegisterActivity.this, TAG, null, USER_PROFILE,
                                Request.Method.POST, param, headers);
                        fetch.getData(new VolleyCallBack() {
                            @Override
                            public void onSuccess(JSONObject jsonObject) {
                                try {
                                    Log.d(TAG, "onSuccess: " + jsonObject + " " + userId + " " + token);
                                    JSONObject userOb = jsonObject.getJSONObject("Response");
                                    User user = new User(userId, userOb.getString("Name"),
                                            userOb.getString("Email"),
                                            userOb.getString("Mobile"),
                                            userOb.getString("Image"),
                                            token, pass.getText().toString());
                                    PrefManager manager = new PrefManager(RegisterActivity.this);
                                    manager.setUserType(NORMAL_USER);
                                    manager.createLogin(user);
                                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private boolean validateInput() {
        if (name.getText().length() == 0)
            return showError(name, "Name is required");
        else if (email.getText().length() == 0 || !isValidEmail(email.getText().toString()))
            return showError(email, "Validate your e-mail");
        else if (!validPass(pass.getText().toString(), rePass.getText().toString()))
            return showError(pass, "");
        else return true;
    }

    private boolean validPass(String pass, String repass) {
        if (pass.length() < 4 || repass.length() < 4) {
            Toast.makeText(this, R.string.passIsSmall, Toast.LENGTH_SHORT).show();
            return false;
        } else if (!repass.equals(pass)) {
            Toast.makeText(this, R.string.passNotSimilar, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;

    }

    boolean showError(EditText text, String error) {
        text.setError(error);
        text.requestFocus();
        return false;
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void initView() {
        profilePic = findViewById(R.id.profilePic);
        pickPic = findViewById(R.id.pickPhoto);
        name = findViewById(R.id.userName);
        phone = findViewById(R.id.userPhone);
        email = findViewById(R.id.userEmail);
        pass = findViewById(R.id.userPassword);
        rePass = findViewById(R.id.confirmPassword);
        regist = findViewById(R.id.regist);
        signIn = findViewById(R.id.signIn);
        mProgressView = findViewById(R.id.progress);
        mLoginFormView = findViewById(R.id.login_form);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    class Listener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            if (view == pickPic)
                openGallery();
            else if (view == regist)
                register();
            else if (view == signIn)
                onBackPressed();
        }
    }
}
