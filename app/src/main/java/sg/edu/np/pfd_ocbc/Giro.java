package sg.edu.np.pfd_ocbc;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class Giro {
    private int giro_id;
    private String biz_id;
    private String biz_name;
    private String giro_acc_no;
    private Date giro_date;
    private String description;
    private boolean verified;
    private double giro_amount;
    private  Context context;

    public int getGiro_id() {
        return giro_id;
    }

    public void setGiro_id(int giro_id) {
        this.giro_id = giro_id;
    }

    public String getBiz_id() {
        return biz_id;
    }

    public void setBiz_id(String biz_id) {
        this.biz_id = biz_id;
    }

    public String getGiro_acc_no() {
        return giro_acc_no;
    }

    public void setGiro_acc_no(String giro_acc_no) {
        this.giro_acc_no = giro_acc_no;
    }

    public Date getGiro_date() {
        return giro_date;
    }

    public void setGiro_date(Date giro_date) {
        this.giro_date = giro_date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public double getGiro_amount() {
        return giro_amount;
    }

    public void setGiro_amount(double giro_amount) {
        this.giro_amount = giro_amount;
    }

    public String getBiz_name() { return biz_name; }

    public void setBiz_name(String biz_name) { this.biz_name = biz_name; }

    public Giro(Context context) {
        this.context = context;
    }

    public void GiroAcceptance(boolean accept, RequestQueue queue){
        String giroAcceptUri = "https://pfd-server.azurewebsites.net/updateGiroVerification";
        JSONObject postData = new JSONObject();
        try {
            postData.put("giro_acc_no", this.getGiro_acc_no());
            postData.put("giro_id", this.getGiro_id());
            if (accept){
                postData.put("verified", "true");
            }
            else {
                postData.put("verified", "false");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, giroAcceptUri, postData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    boolean success = response.getBoolean("success");
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Giro");
                    if (success){
                        builder.setMessage("You have successfully confirmed Giro request");
                    }
                    else{
                        builder.setMessage("Giro request confirmation failed");
                    }

                    builder.setPositiveButton("OK", null);
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        queue.add(jsonObjectRequest);
    }
}
