package yackeen.com.daleel.connection;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import yackeen.com.daleel.R;
import yackeen.com.daleel.manager.PrefManager;

/**
 * Created by Ibrahim on 05/02/2018.
 */

public class FetchData {

    private static String TAG = "fawzy.FetchData";
    private Context context;
    private ProgressBar progressBar;
    private String URl;
    private int requestMethod;
    private HashMap<String, String> params = new HashMap<>();
    private HashMap<String, String> header = new HashMap<>();
    private PrefManager manager;
    private StringRequest stringRequest;


    /**
     * Public Constructor
     */
    public FetchData() {

    }

    /**
     * @param context       The app context
     * @param TAG           Tht tag to context
     * @param progressBar   ProgressBar can be null
     * @param URl           The Request URL
     * @param requestMethod The request type
     * @param params        the params to post
     * @param header        the header to pos
     */
    public FetchData(Context context, String TAG, ProgressBar progressBar, String URl, int requestMethod,
                     HashMap<String, String> params, HashMap<String, String> header) {
        this.context = context;
        this.TAG = TAG;
        this.progressBar = progressBar;
        this.URl = URl;
        this.requestMethod = requestMethod;
        this.params = params;
        this.header = header;
        manager = new PrefManager(context);

    }


    public void getData(final VolleyCallBack volleyCallBack) {
        Log.e(TAG, "getData.url= " + URl.toString());
        if (header != null)
            Log.e(TAG, "getData.header= " + header.toString());
        Log.e(TAG, "getData.params= " + params.toString());
        if (progressBar != null)
            progressBar.setVisibility(View.VISIBLE);

        stringRequest = new StringRequest(requestMethod, URl,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        try {
                            Log.e(TAG, "getData.onResponse.response=  " + response);
                            JSONObject responseObject = new JSONObject(response);
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
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.e(TAG, "getData.onErrorResponse.error=  " + volleyError.getMessage());
                        String errorMessage = null;
                        if (volleyError instanceof ParseError) {
                            errorMessage = context.getResources().getString(R.string.check_internet);
                        } else if (volleyError instanceof NetworkError) {
                            errorMessage = context.getResources().getString(R.string.check_internet);
                        } else if (volleyError instanceof ServerError) {
                            errorMessage = "The server could not be found. Please try again after some time!!";
                        } else if (volleyError instanceof AuthFailureError) {
                            errorMessage = context.getResources().getString(R.string.check_internet);
                        } else if (volleyError instanceof NoConnectionError) {
                            errorMessage = context.getResources().getString(R.string.check_internet);
                        } else if (volleyError instanceof TimeoutError) {
                            errorMessage = "Connection TimeOut! Please check your internet connection.";
                        }
                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                        if (progressBar != null)
                            progressBar.setVisibility(View.GONE);

                    }

                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                String lang; // Default language
                if (manager == null || manager.getAppLanguage() == null)
                    lang = "en";
                else lang = manager.getAppLanguage();
                params.put("Lang", lang);

                return params;

            }


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                if (header == null)
                    return super.getHeaders();
                else return header;

            }

        };


        RequestQueue requestQueue = Volley.newRequestQueue(context);
        int x = 0;// retry count
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 48,
                x, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);

    }

    /**
     * Method to cancel the request
     */
    public void cancelRequest() {
        stringRequest.cancel();
    }

    public String getURl() {
        return URl;
    }

    public void setURl(String URl) {
        this.URl = URl;
    }

    public int getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(int requestMethod) {
        this.requestMethod = requestMethod;
    }

    public HashMap<String, String> getParams() {
        return params;
    }

    public void setParams(HashMap<String, String> params) {
        this.params = params;
    }

    public HashMap<String, String> getHeader() {
        return header;
    }

    public void setHeader(HashMap<String, String> header) {
        this.header = header;
    }
}
