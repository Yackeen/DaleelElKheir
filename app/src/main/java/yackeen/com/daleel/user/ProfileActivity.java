package yackeen.com.daleel.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.crash.FirebaseCrash;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import yackeen.com.daleel.R;
import yackeen.com.daleel.circleimage.CircleImageView;
import yackeen.com.daleel.manager.PrefManager;
import yackeen.com.daleel.user.fragments.FavouriteCategories;
import yackeen.com.daleel.user.fragments.FollowedCases;
import yackeen.com.daleel.user.fragments.FollowedOrganizations;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        FirebaseCrash.log("Here comes the exception!");
        FirebaseCrash.report(new Exception("oops!"));

        Toolbar toolbar = findViewById(R.id.toolbar);
//        getActionBar().setDisplayHomeAsUpEnabled(true);
        TextView userName = findViewById(R.id.userName);
        TextView userEmail = findViewById(R.id.email);
        TextView userPhone = findViewById(R.id.userPhone);
        CircleImageView userPic = findViewById(R.id.profilePic);
        TextView editProfile = findViewById(R.id.editProfile);

        PrefManager manager = new PrefManager(this);

        User user = manager.getUser();
        userName.setText(user.getName());
        userEmail.setText(user.getEmail());
        userPhone.setText(user.getPhone());

        byte[] decodedImg = Base64.decode(user.getThumbnail(), Base64.DEFAULT);
        Glide.with(this).load(decodedImg)
                .placeholder(getResources().getDrawable(R.drawable.prof)).dontAnimate().into(userPic);


        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ViewPager viewPager = findViewById(R.id.viewPager);


        setupViewPager(viewPager);

        TabLayout tableLayout = findViewById(R.id.tableProfile);
        tableLayout.setupWithViewPager(viewPager);

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this, EditProfile.class));
            }
        });
    }


    private void setupViewPager(ViewPager viewPager) {
        PagerAdapterProfile adapter = new PagerAdapterProfile(getSupportFragmentManager());
        adapter.addFragment(new FavouriteCategories(), getResources().getString(R.string.favourite_categories));
        adapter.addFragment(new FollowedOrganizations(), getResources().getString(R.string.organizations));
        adapter.addFragment(new FollowedCases(), getResources().getString(R.string.followed_cases));

        viewPager.setAdapter(adapter);
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            // Respond to the action bar's Up/Home button
//            case android.R.id.home:
//                NavUtils.navigateUpFromSameTask(this);
//                return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
