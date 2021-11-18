package sg.edu.np.pfd_ocbc;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

public class NotificationsActivity extends AppCompatActivity {
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        mAuth = FirebaseAuth.getInstance();

        ImageButton back = findViewById(R.id.notiback);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NotificationsActivity.this, SettingsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });

        SharedPreferences sharedPref = getSharedPreferences("AccountHolder", MODE_PRIVATE);
        String pref = sharedPref.getString("preference", "");

        RadioGroup notipref = findViewById(R.id.notifpref);



        RadioButton telegram = findViewById(R.id.telegram);
        RadioButton sms = findViewById(R.id.sms);

        String postUrlTelegram = "https://pfd-server.azurewebsites.net/updatePreference";

        if(pref.equals("sms")){
            sms.setChecked(true);
            telegram.setChecked(false);

        }
        else {
            telegram.setChecked(true);
            sms.setChecked(false);
        }

        telegram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject postData = new JSONObject();
                FirebaseUser user = mAuth.getCurrentUser();


                try{
                    postData.put("uid", user.getUid());
                    postData.put("preference", "tele");
                }catch (JSONException e) {
                    e.printStackTrace();
                }

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, postUrlTelegram, postData, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(getApplicationContext(),
                                "Preference changed to Telegram", Toast.LENGTH_SHORT).show();

                        SharedPreferences.Editor editor = sharedPref.edit();

                        editor.putString("preference", "tele");
                        editor.apply();

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error yo", "onErrorResponse: ");

                    }
                });
                RequestQueue requestQueue = Volley.newRequestQueue(NotificationsActivity.this);
                requestQueue.add(jsonObjectRequest);
            }
        });

        sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject postData = new JSONObject();
                FirebaseUser user = mAuth.getCurrentUser();


                try{
                    postData.put("uid", user.getUid());
                    postData.put("preference", "sms");
                }catch (JSONException e) {
                    e.printStackTrace();
                }

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, postUrlTelegram, postData, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(getApplicationContext(),
                                "Preference changed to SMS", Toast.LENGTH_SHORT).show();
                        SharedPreferences.Editor editor = sharedPref.edit();

                        editor.putString("preference", "sms");
                        editor.apply();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error yo", "onErrorResponse: ");

                    }
                });
                RequestQueue requestQueue = Volley.newRequestQueue(NotificationsActivity.this);
                requestQueue.add(jsonObjectRequest);
            }
        });


    }
}