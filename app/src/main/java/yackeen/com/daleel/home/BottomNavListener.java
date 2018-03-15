package yackeen.com.daleel.home;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Spinner;

import yackeen.com.daleel.R;
import yackeen.com.daleel.about.AboutFragment;
import yackeen.com.daleel.chat.ChatListFragment;
import yackeen.com.daleel.events.EventsFragment;
import yackeen.com.daleel.organization.OrganizationsFragment;

import static yackeen.com.daleel.constants.Constants.ABOUT_FRAGMENT;
import static yackeen.com.daleel.constants.Constants.CHAT_FRAGMENT;
import static yackeen.com.daleel.constants.Constants.EVENT_FRAGMENT;
import static yackeen.com.daleel.constants.Constants.HOME_FRAGMENT;
import static yackeen.com.daleel.constants.Constants.ORGANIZATION_FRAGMENT;

/**
 * Created by Ibrahim on 10/02/2018.
 */

public class BottomNavListener implements BottomNavigationView.OnNavigationItemSelectedListener {

    static Context context;
    private Spinner spinner;

    public BottomNavListener(Context context, Spinner spinner) {
        this.context = context;
        this.spinner = spinner;

    }

    public static void showFragment(Fragment fragment, String title, String tag) {
        ((Activity) context).setTitle(title);

        android.app.FragmentTransaction ft = ((Activity) context).getFragmentManager().beginTransaction();
        // Using android framework animation to navigate between fragment
        ft.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
        // Replace the new fragment
        ft.replace(R.id.content, fragment, tag);
        // Start the animated transition.
        ft.commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                showFragment(new NewsFeedFragment(), context.getResources().getString(R.string.home), HOME_FRAGMENT);
                spinner.setVisibility(View.VISIBLE);
                break;
            case R.id.about:
                showFragment(new AboutFragment(), context.getResources().getString(R.string.about), ABOUT_FRAGMENT);
                break;
            case R.id.organization:
                showFragment(new OrganizationsFragment(), context.getResources().getString(R.string.organizations), ORGANIZATION_FRAGMENT);
                spinner.setVisibility(View.GONE);
                break;
            case R.id.event:
                showFragment(new EventsFragment(), context.getResources().getString(R.string.events), EVENT_FRAGMENT);
                spinner.setVisibility(View.VISIBLE);
                break;
            case R.id.chat:
                showFragment(new ChatListFragment(), context.getResources().getString(R.string.chat), CHAT_FRAGMENT);
//                context.startActivity(new Intent(context, ChatingActivity.class));
                break;

        }
        return true;
    }
}
