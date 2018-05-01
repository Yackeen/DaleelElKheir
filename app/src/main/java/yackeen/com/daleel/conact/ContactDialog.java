package yackeen.com.daleel.conact;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import yackeen.com.daleel.R;
import yackeen.com.daleel.connection.FetchData;
import yackeen.com.daleel.connection.VolleyCallBack;
import yackeen.com.daleel.user.ChosenCat;

import static yackeen.com.daleel.constants.Constants.CONTACT_INFO;
import static yackeen.com.daleel.constants.Constants.CONTACT_US;

/**
 * Created by Ibrahim on 14/02/2018.
 */

public class ContactDialog extends DialogFragment {


    private static final String TAG = "fawzy.ContactDialog";
    int mNum;
    private ProgressBar progressBar;
    private TextView facebook, phone, email, location, send;
    private EditText contactUserName, contactEmail, contactPhone, contactMsg;
    private Spinner spinner;
    private String chosenItem = "";

    /**
     * Create a new instance of MyDialogFragment, providing "num"
     * as an argument.
     */
    static ContactDialog newInstance(int num) {
        ContactDialog f = new ContactDialog();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("num", num);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNum = getArguments().getInt("num");


    }

    @Override
    public void onResume() {
        super.onResume();
//        getDialog().getWindow().setLayout(
//                getResources().getDisplayMetrics().widthPixels,
//                getResources().getDisplayMetrics().heightPixels
//        );

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(getActivity(), R.style.DialogStyle);

        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.contact_dialog, container, false);

        email = v.findViewById(R.id.contactEmail);
        facebook = v.findViewById(R.id.contactFacebook);
        phone = v.findViewById(R.id.contactPhoneNum);
        location = v.findViewById(R.id.contactLocation);
        contactUserName = v.findViewById(R.id.contactUserName);
        contactEmail = v.findViewById(R.id.contactUserEmail);
        contactMsg = v.findViewById(R.id.contactMessage);
        contactPhone = v.findViewById(R.id.contactUserPhone);
        spinner = v.findViewById(R.id.contactUserCategory);
        send = v.findViewById(R.id.sendContactRequest);


        SpinnerAdapter adapter = new SpinnerAdapter(getActivity(), getResources().getString(R.string.volunteer_in));
        adapter.setAdapter(spinner, new ChosenCat() {
            @Override
            public void chosenCategory(String catId) {
                chosenItem = catId;
            }
        });
        FetchData fetchData = new FetchData(getActivity(), TAG, progressBar, CONTACT_INFO,
                Request.Method.POST, new HashMap<String, String>(), null);
        fetchData.getData(new VolleyCallBack() {
            @Override
            public void onSuccess(JSONObject jsonObject) {

                //Log.d(TAG, "onSuccess: " + jsonObject);
                try {
                    boolean isSuccess = jsonObject.getBoolean("IsSuccess");
                    if (isSuccess) {

                        JSONObject response = jsonObject.getJSONObject("Response");
                        email.setText(response.getString("Email"));
                        facebook.setText(response.getString("FacebookCount"));
                        phone.setText(response.getString("Mobile"));
                        location.setText(response.getString("Address"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, String> params = new HashMap<>();
                params.put("Name", contactUserName.getText().toString());
                params.put("Email", contactEmail.getText().toString());
                params.put("Mobile", contactPhone.getText().toString());
                params.put("DonorIn", chosenItem);
                params.put("Message", contactMsg.getText().toString());
                FetchData fetchData = new FetchData(getActivity(), TAG, progressBar, CONTACT_US,
                        Request.Method.POST, params, null);
                fetchData.getData(new VolleyCallBack() {
                    @Override
                    public void onSuccess(JSONObject jsonObject) {

                        //Log.d(TAG, "onSuccess: " + jsonObject);
                        try {
                            boolean isSuccess = jsonObject.getBoolean("IsSuccess");
                            if (isSuccess) {
                                Toast.makeText(getActivity(), "Thank you for contacting us, we'll get back to you soon", Toast.LENGTH_SHORT).show();
                                OnDialogStopListner listner = (OnDialogStopListner) getActivity();
                                listner.onDialogDestroy();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
        return v;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        OnDialogStopListner listner = (OnDialogStopListner) getActivity();
        listner.onDialogDestroy();
    }
}
