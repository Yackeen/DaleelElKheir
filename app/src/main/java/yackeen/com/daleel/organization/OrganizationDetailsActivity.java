package yackeen.com.daleel.organization;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import com.bumptech.glide.Glide;

import yackeen.com.daleel.R;

public class OrganizationDetailsActivity extends AppCompatActivity {

    ImageView thumbnail;
    TextView organization, category, address, region, description;
    String ID, Name, Description, Address, Area, Logo, Categories;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organization_details);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("");

        assert (getIntent().getExtras() != null);
        ID = getIntent().getExtras().getString("ID");
        Name = getIntent().getExtras().getString("Name");
        Description = getIntent().getExtras().getString("Description");
        Address = getIntent().getExtras().getString("Address");
        Area = getIntent().getExtras().getString("Area");
        Logo = getIntent().getExtras().getString("Logo");
        Categories = getIntent().getExtras().getString("Categories");

        thumbnail = findViewById(R.id.thumbnail);
        organization = findViewById(R.id.organization);
        category = findViewById(R.id.category);
        address = findViewById(R.id.address);
        region = findViewById(R.id.region);
        description = findViewById(R.id.description);

        Glide.with(this).load(Logo).into(thumbnail);

        organization.setText(Name);
        description.setText(Description);
        address.setText(Address);
        region.setText(Area);
        category.setText(Categories);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}