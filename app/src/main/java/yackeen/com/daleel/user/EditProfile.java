package yackeen.com.daleel.user;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import yackeen.com.daleel.R;
import yackeen.com.daleel.connection.FetchData;
import yackeen.com.daleel.connection.PostData;
import yackeen.com.daleel.connection.VolleyCallBack;
import yackeen.com.daleel.constants.Constants;
import yackeen.com.daleel.home.model.SpinnerAdapterModel;
import yackeen.com.daleel.manager.PrefManager;
import yackeen.com.daleel.user.adapters.EditProfileCategoryAdapter;
import yackeen.com.daleel.user.adapters.FilterSpinnerAdapter;
import yackeen.com.daleel.user.models.FavouriteCategoriesModel;

import static yackeen.com.daleel.constants.Constants.ADD_CATEGORY;
import static yackeen.com.daleel.constants.Constants.FAV_CATEGORIES;
import static yackeen.com.daleel.constants.Constants.SOCIAL_MEDIA_USER;
import static yackeen.com.daleel.constants.Constants.UPDATE_PROFILE;

public class EditProfile extends AppCompatActivity {


    public static final int PICK_IMAGE_REQUEST = 77;
    private static final String TAG = "EditProfile";
    private EditText userName, userPhone, pass, rePass;
    private TextView addCategory, save;
    private ImageView userPic, picPhoto;
    private Spinner catSpinner;
    private PrefManager manager;
    private User user;
    private String chosenCatId = " ";
    private ProgressBar progress;
    private ScrollView wholeView;
    private RecyclerView recyclerView;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        initView();

        manager = new PrefManager(this);
        user = manager.getUser();

        userName.setText(user.getName());
        userPhone.setText(user.getPhone());
        pass.setText(user.getPass());
        rePass.setText(user.getPass());

        Log.d(TAG, "onCreate: " + manager.getUserType());
        if (manager.getUserType() == SOCIAL_MEDIA_USER) {
            pass.setVisibility(View.GONE);
            rePass.setVisibility(View.GONE);
        }


        byte[] decodedString = Base64.decode(user.getThumbnail(), Base64.DEFAULT);
        bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);


        setSpinner(catSpinner, Constants.GET_CATEGORIES, getResources().getString(R.string.add_category));

        addCategory();

        setRecyclerView();


        save();

        picPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });


        Glide.with(this).load(decodedString)
                .placeholder(getResources().getDrawable(R.drawable.prof)).dontAnimate().into(userPic);

    }

    private void save() {
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final JSONObject jsonBody = new JSONObject();
                HashMap<String, String> headers = new HashMap<>();
                wholeView.setVisibility(View.GONE);

                try {
                    jsonBody.put("UserID", user.getId());
                    jsonBody.put("Name", userName.getText().toString());
                    if (manager.getUserType() != SOCIAL_MEDIA_USER)
                        jsonBody.put("Password", pass.getText().toString());
                    jsonBody.put("Image", getStringImage(bitmap));
                    jsonBody.put("Mobile", userPhone.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                headers.put("SecurityToken", user.getToken());
                PostData postData = new PostData(EditProfile.this, jsonBody, UPDATE_PROFILE,
                        Request.Method.POST, progress, headers);
                try {
                    postData.post(new VolleyCallBack() {
                        @Override
                        public void onSuccess(JSONObject jsonObject) {
                            wholeView.setVisibility(View.VISIBLE);
                            try {
                                boolean isSuccess = jsonObject.getBoolean("IsSuccess");
                                if (isSuccess) {
                                    User newUser = new User(user.getId(), userName.getText().toString(), user.getEmail(),
                                            userPhone.getText().toString(), getStringImage(bitmap), user.getToken(), pass.getText().toString());
                                    manager.createLogin(newUser);
                                    Intent intent = new Intent(EditProfile.this, ProfileActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);

                                    finish();
                                } else {
                                    Toast.makeText(EditProfile.this,
                                            jsonObject.getString("ErrorMessage"), Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            Uri filePath = data.getData();
            try {
                //Getting the Bitmap from Gallery
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);

                userPic.setImageBitmap(bitmap);

                Glide.with(this).load(filePath).into(userPic);
            } catch (IOException ex) {

            }
        }

    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void addCategory() {
        addCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, String> params = new HashMap<>();
                HashMap<String, String> headers = new HashMap<>();
                params.put("UserID", user.getId());
                params.put("CategoryID", chosenCatId);
                headers.put("SecurityToken", user.getToken());
                FetchData fetchData = new FetchData(EditProfile.this, TAG, progress, ADD_CATEGORY,
                        Request.Method.POST, params, headers);
                fetchData.getData(new VolleyCallBack() {
                    @Override
                    public void onSuccess(JSONObject jsonObject) {
                        try {
                            boolean isSuccess = jsonObject.getBoolean("IsSuccess");
                            if (isSuccess) {
                                setRecyclerView();
//                                Toast.makeText(EditProfile.this, getResources().getString(R.string.cat_added), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    private void setRecyclerView() {

        HashMap<String, String> params = new HashMap<>();
        HashMap<String, String> headers = new HashMap<>();
        params.put("UserID", user.getId());
        headers.put("SecurityToken", user.getToken());
        FetchData fetchData = new FetchData(EditProfile.this, TAG, progress, FAV_CATEGORIES,
                Request.Method.POST, params, headers);
        fetchData.getData(new VolleyCallBack() {
            @Override
            public void onSuccess(JSONObject jsonObject) {

                Log.d(TAG, "onSuccess: " + jsonObject);
                try {
                    boolean isSuccess = jsonObject.getBoolean("IsSuccess");
                    if (isSuccess) {
                        List<FavouriteCategoriesModel> list = new ArrayList<>();
                        JSONArray jsonArray = jsonObject.getJSONArray("Response");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            FavouriteCategoriesModel model = new FavouriteCategoriesModel();
                            JSONObject object = jsonArray.getJSONObject(i);
                            model.setName(object.getString("Name"));
                            model.setId(object.getString("ID"));
                            list.add(model);
                        }

                        // set recycler to adapter
                        recyclerView.setAdapter(new EditProfileCategoryAdapter(EditProfile.this, list));
                        recyclerView.setLayoutManager(new GridLayoutManager(EditProfile.this, 2));


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public String getStringImage(Bitmap bmp) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 10, baos);
            byte[] imageBytes = baos.toByteArray();
            if (imageBytes.length > 1)
                return Base64.encodeToString(imageBytes, Base64.DEFAULT);
            else return " ";
        } catch (NullPointerException x) {
            x.printStackTrace();
        }
        return " ";
    }

    private void setSpinner(final Spinner spinner, String URL, final String hint) {
        final List<SpinnerAdapterModel> catList = new ArrayList<>();

        FetchData fetchData = new FetchData(EditProfile.this, TAG, null,
                URL, Request.Method.POST, new HashMap<String, String>(), null);

        fetchData.getData(new VolleyCallBack() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                try {
                    boolean isSuccess = jsonObject.getBoolean("IsSuccess");
                    if (isSuccess) {
                        List<FavouriteCategoriesModel> list = new ArrayList<>();
                        JSONArray jsonArray = jsonObject.getJSONArray("Response");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            FavouriteCategoriesModel catModel = new FavouriteCategoriesModel();
                            SpinnerAdapterModel model = new SpinnerAdapterModel();
                            model.setId(jsonArray.getJSONObject(i).getString("ID"));
                            catModel.setId(jsonArray.getJSONObject(i).getString("ID"));
                            model.setName(jsonArray.getJSONObject(i).getString("Name"));
                            catModel.setName(jsonArray.getJSONObject(i).getString("Name"));

                            catList.add(model);
                            list.add(catModel);
                        }
                        FilterSpinnerAdapter adapter = new FilterSpinnerAdapter(EditProfile.this, hint, catList);
                        adapter.setAdapter(spinner, new ChosenCat() {
                            @Override
                            public void chosenCategory(String catId) {
                                chosenCatId = catId;
                            }
                        });

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initView() {
        userName = findViewById(R.id.userName);
        userPhone = findViewById(R.id.userPhone);
        pass = findViewById(R.id.userPassword);
        rePass = findViewById(R.id.confirmPassword);
        addCategory = findViewById(R.id.addCategory);
        userPic = findViewById(R.id.profilePic);
        save = findViewById(R.id.save);
        picPhoto = findViewById(R.id.pickPhoto);
        catSpinner = findViewById(R.id.catSpinner);
        progress = findViewById(R.id.progress);
        wholeView = findViewById(R.id.login_form);
        recyclerView = findViewById(R.id.recycler);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
