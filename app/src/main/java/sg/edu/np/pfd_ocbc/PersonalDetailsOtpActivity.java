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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PersonalDetailsOtpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);





        Intent intent = getIntent();
        String situation = intent.getStringExtra("situation");
        String phoneNo = intent.getStringExtra("phoneNo");

        TextView paragraph = findViewById(R.id.otptext);
        EditText opt = findViewById(R.id.otp);
        Button resend = findViewById(R.id.resend);
        Button submit = findViewById(R.id.submit);
        paragraph.setText("A one-time password has been sent to your phone ("+phoneNo+").");
        //requestSettingOTP(phoneNo);


        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //requestSettingOTP(phoneNo);
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!opt.getText().toString().equals("")){
                    submitSettingOTP(phoneNo,opt.getText().toString(),situation);
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
        RequestQueue requestQueue = Volley.newRequestQueue(PersonalDetailsOtpActivity.this);
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

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, postUrl, postData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("OtpActivity", "onResponse: "+response.toString());
                Map<String, Object> hashMap = new Gson().fromJson(
                        response.toString(), new TypeToken<HashMap<String, Object>>() {}.getType()
                );
                if (hashMap.get("result").equals("approved")){
                    Toast.makeText(PersonalDetailsOtpActivity.this, "Approved",
                            Toast.LENGTH_SHORT).show();
                    if (situation.equals("updatephoneNo")){
                        Log.d("situation", "updatephoneNo");
                        String updatephoneno = "https://pfd-server.azurewebsites.net/updatePhoneNo";
                        FirebaseAuth mAuth;
                        mAuth = FirebaseAuth.getInstance();
                        FirebaseUser user = mAuth.getCurrentUser();
                        JSONObject phoneData = new JSONObject();

                        user.getIdToken(true).addOnSuccessListener(new OnSuccessListener<GetTokenResult>() {
                            @Override
                            public void onSuccess(GetTokenResult result) {
                                String idToken = result.getToken();
                                try {


                                    phoneData.put("uid", user.getUid());
                                    phoneData.put("phoneNo", phoneNo);



                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                //POST api to update phone number
                                JsonObjectRequest phoneObjectRequest = new JsonObjectRequest(Request.Method.POST, updatephoneno, phoneData, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        SharedPreferences sharedPref = getSharedPreferences("AccountHolder", MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedPref.edit();

                                        editor.putString("phoneno", phoneNo);
                                        editor.apply();
                                        Toast.makeText(PersonalDetailsOtpActivity.this, "Personal Details Updated",
                                                Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(PersonalDetailsOtpActivity.this, PersonalDetailsActivity.class);
                                        startActivity(intent);




                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(PersonalDetailsOtpActivity.this, "Error: Phone Number Belongs to Another Account Holder",
                                                Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(PersonalDetailsOtpActivity.this, PersonalDetailsActivity.class);
                                        startActivity(intent);
                                    }
                                });
                                RequestQueue phonerequestQueue = Volley.newRequestQueue(PersonalDetailsOtpActivity.this);
                                phonerequestQueue.add(phoneObjectRequest);
                            }
                        });

                    }
                } else{
                    Toast.makeText(PersonalDetailsOtpActivity.this, "Disapproved",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("OtpActivity", "onErrorResponse: "+error.getCause()+" "+error.getMessage());
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(PersonalDetailsOtpActivity.this);
        requestQueue.add(jsonObjectRequest);
    }



}