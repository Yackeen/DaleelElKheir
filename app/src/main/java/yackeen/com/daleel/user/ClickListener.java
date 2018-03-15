package yackeen.com.daleel.user;

import android.content.Context;
import android.content.Intent;
import android.view.View;

/**
 * Created by Ibrahim on 11/02/2018.
 */

public class ClickListener implements View.OnClickListener {

    private Context context;

    public ClickListener(Context context) {
        this.context = context;
    }

    @Override
    public void onClick(View view) {
        context.startActivity(new Intent(context, ProfileActivity.class));
    }
}
