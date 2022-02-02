package sg.edu.np.pfd_ocbc;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.appcompat.app.AlertDialog;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class Giro {

    private  Context context;
    private String giro_id;
    private String giro_acc_no;
    private String referenceNo;
    private Business business;

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String getGiro_id() {
        return giro_id;
    }

    public void setGiro_id(String giro_id) {
        this.giro_id = giro_id;
    }

    public String getGiro_acc_no() {
        return giro_acc_no;
    }

    public void setGiro_acc_no(String giro_acc_no) {
        this.giro_acc_no = giro_acc_no;
    }

    public String getReferenceNo() {
        return referenceNo;
    }

    public void setReferenceNo(String referenceNo) {
        this.referenceNo = referenceNo;
    }

    public Business getBusiness() {
        return business;
    }

    public void setBusiness(Business business) {
        this.business = business;
    }

    public Giro(Context context) {
        this.context = context;
    }


    public void CancelGiro(){
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Please wait");
        progressDialog.show();

        String giroAcceptUri = "https://pfd-server.azurewebsites.net/deleteGiro";
        JSONObject postData = new JSONObject();
        try {
            postData.put("giro_acc_no", this.getGiro_acc_no());
            postData.put("giro_id", this.getGiro_id());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, giroAcceptUri, postData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progressDialog.dismiss();
                try {
                    boolean success = response.getBoolean("success");
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Giro");
                    if (success){
                        builder.setMessage("You have successfully cancel Giro");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(context, GiroListActivity.class);
                                context.startActivity(intent);
                                ((Activity) context).finish();
                            }
                        });

                        builder.setNegativeButton("Home", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(context, HomeActivity.class);
                                context.startActivity(intent);
                                ((Activity) context).finish();
                            }
                        });
                    }
                    else{
                        builder.setMessage("Giro cancellation failed\n Do you want to try again?");
                        builder.setPositiveButton("Yes", null);
                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(context, GiroListActivity.class);
                                context.startActivity(intent);
                                ((Activity) context).finish();
                            }
                        });
                    }
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
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(jsonObjectRequest);
    }
}
