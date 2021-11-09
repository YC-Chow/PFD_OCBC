package sg.edu.np.pfd_ocbc;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SettingOtpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        Intent tempintent = new Intent(SettingOtpActivity.this, SettingsActivity.class);
        startActivity(tempintent);


        Intent intent = getIntent();
        String situation = intent.getStringExtra("situation");
        SharedPreferences sharedPref = getSharedPreferences("Profile", MODE_PRIVATE);
        String phoneNo = sharedPref.getString("Phone", "");

        TextView paragraph = findViewById(R.id.otptext);
        EditText opt = findViewById(R.id.otp);
        Button resend = findViewById(R.id.resend);
        Button submit = findViewById(R.id.submit);
        paragraph.setText("A one-time password has been sent to your phone ("+phoneNo+").");
        //requestSettingOTP(phoneNo);

        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestSettingOTP(phoneNo);
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!opt.getText().toString().equals("")){
                    //submitSettingOTP(phoneNo,opt.getText().toString(),situation);
                }
            }
        });

    }

    void requestSettingOTP(String phoneNo){
        String postUrl = "https://pfd-server.azurewebsites.net/requestOTP";
        JSONObject postData = new JSONObject();
        try{
            postData.put("phoneNo", phoneNo);
        }catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("validateAccount", "sending");
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, postUrl, postData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("OtpActivity", "onResponse: "+response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("OtpActivity", "onErrorResponse: ");
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(SettingOtpActivity.this);
        requestQueue.add(jsonObjectRequest);
    }
    void submitSettingOTP(String phoneNo, String code, String situation){
        String postUrl = "https://pfd-server.azurewebsites.net/submitOTP";
        JSONObject postData = new JSONObject();
        try{
            postData.put("phoneNo", phoneNo);
            postData.put("code", code);
        }catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("validateAccount", "sending");
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, postUrl, postData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("OtpActivity", "onResponse: "+response.toString());
                Map<String, Object> hashMap = new Gson().fromJson(
                        response.toString(), new TypeToken<HashMap<String, Object>>() {}.getType()
                );
                if (hashMap.get("result").equals("approved")){
                    Toast.makeText(SettingOtpActivity.this, "Approved",
                            Toast.LENGTH_SHORT).show();
                    if (situation.equals("settings")){
                        Log.d("SettingOtpActivity", "settings");
                        Intent intent = new Intent(SettingOtpActivity.this, SettingsActivity.class);
                        startActivity(intent);
                    }
                } else{
                    Toast.makeText(SettingOtpActivity.this, "Disapproved",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("OtpActivity", "onErrorResponse: "+error.getCause()+" "+error.getMessage());
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(SettingOtpActivity.this);
        requestQueue.add(jsonObjectRequest);
    }
}