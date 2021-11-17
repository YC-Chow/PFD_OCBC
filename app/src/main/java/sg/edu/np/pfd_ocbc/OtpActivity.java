package sg.edu.np.pfd_ocbc;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class OtpActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private String otpId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        Intent intent = getIntent();
        String phoneNo = intent.getStringExtra("phoneNo");
        String situation = intent.getStringExtra("situation");
        TextView paragraph = findViewById(R.id.otptext);
        EditText opt = findViewById(R.id.otp);
        Button resend = findViewById(R.id.resend);
        Button submit = findViewById(R.id.submit);
        paragraph.setText("A one-time password has been sent to your phone ("+phoneNo+").");
        requestOTP(phoneNo);
//        createAccountHolder();
        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestOTP(phoneNo);
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!opt.getText().toString().equals("")){
                    submitOTP(phoneNo,opt.getText().toString(),situation);
                }
            }
        });
    }
    void requestOTP(String phoneNo){
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
                System.out.println(response);
                try {
                    otpId = response.getString("otpId");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("OtpActivity", "onErrorResponse: ");
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(OtpActivity.this);
        requestQueue.add(jsonObjectRequest);
    }
    void submitOTP(String phoneNo, String code, String situation){
        String postUrl = "https://pfd-server.azurewebsites.net/submitOTP";
        JSONObject postData = new JSONObject();
        try{
            postData.put("otpId", otpId);
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
                    Toast.makeText(OtpActivity.this, "Approved",
                            Toast.LENGTH_SHORT).show();
                    if (situation.equals("signup")){
                        Log.d("OtpActivity", "signup:");
                        createAccountHolder();
                    }else if(situation.equals("signin")){
                        Intent intent = getIntent();
                        String email = intent.getStringExtra("email");
                        String pintxt = intent.getStringExtra("pintxt");
                        mAuth = FirebaseAuth.getInstance();
                        mAuth.signInWithEmailAndPassword(email, pintxt)
                                .addOnCompleteListener(OtpActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            afterlogin();
                                        }
                                        else {
                                            // If sign in fails, display a message to the user.
                                            Log.w(TAG, "signInWithEmail:failure", task.getException());


                                        }
                                    }
                                });
                        afterlogin();
                    }
                } else{
                    Toast.makeText(OtpActivity.this, "Disapproved",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("OtpActivity", "onErrorResponse: "+error.getCause()+" "+error.getMessage());
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(OtpActivity.this);
        requestQueue.add(jsonObjectRequest);
    }
    private void afterlogin(){
        SharedPreferences sharedPreferences = getSharedPreferences("AccountHolder", MODE_PRIVATE);

        //Log.v("uid is:" ,user.getUid());
        String postUrlAccountHolder = "https://pfd-server.azurewebsites.net/getAccountHolderUsingUid";

        JSONObject postData = new JSONObject();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        try{
            postData.put("uid", user.getUid());
        }catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, postUrlAccountHolder, postData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //Log.d("lolza",response.toString());
                try {
                    String phoneno = response.getString("phone_no");
                    String holdername = response.getString("name");
                    String email = response.getString("email");
                    Log.d("data123", "onResponse: "+phoneno+holdername+" "+email);

                    // Creating an Editor object to edit(write to the file)
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("name", holdername);
                    editor.putString("phoneno", phoneno);
                    editor.putString("email", email);

                    editor.apply();
                    //Log.v("accNumber is",accNo);

                    Intent intent = new Intent(OtpActivity.this, HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error yo", "onErrorResponse: ");
                Toast.makeText(OtpActivity.this, "Ensure internet is secure",
                        Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(OtpActivity.this);
        requestQueue.add(jsonObjectRequest);
    }
    void createAccountHolder(){
        Intent intent = getIntent();
        String phoneNo = intent.getStringExtra("phoneNo");
        String email = intent.getStringExtra("email");
        String name = intent.getStringExtra("name");
        String icNo = intent.getStringExtra("icNo");
        String password = intent.getStringExtra("password");
        mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(email, password)
                       .addOnCompleteListener(OtpActivity.this, new OnCompleteListener<AuthResult>() {
                           @RequiresApi(api = Build.VERSION_CODES.O)
                           @Override
                           public void onComplete(@NonNull Task<AuthResult> task) {
                               if(task.isSuccessful()){
                                   FirebaseUser user = mAuth.getCurrentUser();
                                   String postUrl = "https://pfd-server.azurewebsites.net/createAccountHolder";
                                   JSONObject postData = new JSONObject();
                                   try{
                                       postData.put("uid", user.getUid());
                                       postData.put("email", email);
                                       postData.put("phoneNo", phoneNo);
                                       postData.put("name", name);
                                       postData.put("icNo", icNo);
                                   }
                                   catch (JSONException e) {
                                       Log.d("error", "onSuccess: "+e.toString());
                                   }

                                   JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, postUrl, postData, new Response.Listener<JSONObject>() {
                                       @Override
                                       public void onResponse(JSONObject response) {
                                           Intent intent = new Intent(OtpActivity.this, HomeActivity.class);
                                           intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                           intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                           startActivity(intent);
                                       }
                                   }, new Response.ErrorListener() {
                                       @Override
                                       public void onErrorResponse(VolleyError error) {
                                           Log.d("error", "onSuccess: "+error.toString());
                                       }
                                   });
                                   RequestQueue requestQueue = Volley.newRequestQueue(OtpActivity.this);
                                   requestQueue.add(jsonObjectRequest);
                               }
                               else {
                                   // If sign in fails, display a message to the user.
                                   Log.w("error", "createUserWithEmail:failure", task.getException());
                                   Toast.makeText(OtpActivity.this, "Authentication failed.",
                                           Toast.LENGTH_SHORT).show();

                               }
                           }
                       });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("onDestroy", "onDestroy1: ");
        mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();
        Log.d("onDestroy", "onDestroy2: "+mAuth.getCurrentUser());
    }
}