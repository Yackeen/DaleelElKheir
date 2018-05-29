package yackeen.com.daleel.connection;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import yackeen.com.daleel.manager.PrefManager;

/**
 * Created by Ibrahim on 26/02/2018.
 */

public class PostData {

    private final String TAG = "fawzy.PostData";
    private Context context;
    private JSONObject jsonBody = new JSONObject();
    private int requestMethod;
    private ProgressBar progressBar;
    private HashMap<String, String> headers = new HashMap<>();
    private String url;
    private PrefManager manager;

    public PostData(Context context, JSONObject jsonBody, String url, int requestMethod, ProgressBar progressBar,
                    HashMap<String, String> headers) {
        this.context = context;
        this.jsonBody = jsonBody;
        this.requestMethod = requestMethod;
        this.progressBar = progressBar;
        this.headers = headers;
        this.url = url;
        manager = new PrefManager(context);
    }

    public void post(final VolleyCallBack volleyCallBack) throws JSONException {
        Log.e(TAG, "getData.url= " + url.toString());
        if (headers != null)
            Log.e(TAG, "getData.header= " + headers.toString());
        Log.e(TAG, "getData.jsonBody= " + jsonBody.toString());

        if (progressBar != null)
            progressBar.setVisibility(View.VISIBLE);

        String lang; // Default language
        if (manager == null || manager.getAppLanguage() == null)
            lang = "en";
        else lang = manager.getAppLanguage();
        jsonBody.put("Lang", lang);

        RequestQueue requestQueue = Volley.newRequestQueue(context);

        JsonObjectRequest request_json = new JsonObjectRequest(requestMethod, url, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject responseObject) {
                        Log.e(TAG, "post.onResponse= " + responseObject.toString());
                        try {
                            volleyCallBack.onSuccess(responseObject);
                            if (!responseObject.getBoolean("IsSuccess")) {
                                Log.e(TAG, "getData.onResponse= " + responseObject.getString("ErrorMessage"));
//                                Toast.makeText(context, responseObject.getString("ErrorMessage"), Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (progressBar != null)
                            progressBar.setVisibility(View.GONE);


                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (progressBar != null)
                    progressBar.setVisibility(View.GONE);
                VolleyLog.e("Error: ", error.getMessage());
            }


        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                if (headers == null)
                    return super.getHeaders();
                else return headers;
            }
        };
        requestQueue.add(request_json);

    }
}
