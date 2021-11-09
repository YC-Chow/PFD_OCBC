package sg.edu.np.pfd_ocbc;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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

import org.json.JSONException;
import org.json.JSONObject;

public class PersonalDetailsActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_details);
        mAuth = FirebaseAuth.getInstance();


        EditText changemobileno = findViewById(R.id.changemobileno);
        EditText changename = findViewById(R.id.changeusername);

        Button save = findViewById(R.id.save);



        SharedPreferences sharedPref = getSharedPreferences("AccountHolder", MODE_PRIVATE);

        String username = sharedPref.getString("Name", "");
        String userphone = sharedPref.getString("Phone", "");

        changemobileno.setText(userphone);
        changename.setText(username);

        ImageButton back = findViewById(R.id.pdback);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PersonalDetailsActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPref = getSharedPreferences("AccountHolder", MODE_PRIVATE);
                String username = sharedPref.getString("Name", "");
                String userphone = sharedPref.getString("Phone", "");
                if(changemobileno.getText().toString().equals(userphone) && changename.getText().toString().equals(username)){
                    Toast.makeText(PersonalDetailsActivity.this, "Personal Details Have Not Been Edited",
                            Toast.LENGTH_SHORT).show();
                }
                else {
                    FirebaseUser user = mAuth.getCurrentUser();
                    String updatename = "https://pfd-server.azurewebsites.net/updateName";
                    String updatephoneno = "https://pfd-server.azurewebsites.net/updatePhoneNo";
                    JSONObject nameData = new JSONObject();
                    JSONObject phoneData = new JSONObject();

                    user.getIdToken(true).addOnSuccessListener(new OnSuccessListener<GetTokenResult>() {
                        @Override
                        public void onSuccess(GetTokenResult result) {
                            String idToken = result.getToken();
                            try {
                                nameData.put("uid", user.getUid());
                                nameData.put("name", changename.getText().toString());
                                nameData.put("jwtToken", idToken);

                                phoneData.put("uid", user.getUid());
                                phoneData.put("phoneNo", changemobileno.getText().toString());
                                phoneData.put("jwtToken", idToken);


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            //If user changed both name and phone number
                            if(!changemobileno.getText().toString().equals(userphone) && !changename.getText().toString().equals(username)){


                                //POST api to update phone number
                                JsonObjectRequest phoneObjectRequest = new JsonObjectRequest(Request.Method.POST, updatephoneno, phoneData, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        SharedPreferences sharedPref = getSharedPreferences("Profile", MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedPref.edit();
                                        try {
                                            editor.putString("Phone", response.getString("phoneNo"));
                                            editor.apply();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }


                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(PersonalDetailsActivity.this, "Error: Phone Number Belongs to Another Account Holder",
                                                Toast.LENGTH_SHORT).show();
                                        changemobileno.setText(userphone);
                                    }
                                });
                                RequestQueue phonerequestQueue = Volley.newRequestQueue(PersonalDetailsActivity.this);
                                phonerequestQueue.add(phoneObjectRequest);

                                //POST api to update name
                                JsonObjectRequest nameObjectRequest = new JsonObjectRequest(Request.Method.POST, updatename, nameData, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        Toast.makeText(PersonalDetailsActivity.this, "Details Updated",
                                                Toast.LENGTH_SHORT).show();
                                        SharedPreferences sharedPref = getSharedPreferences("Profile", MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedPref.edit();
                                        try {
                                            editor.putString("Name", response.getString("name"));
                                            editor.apply();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(PersonalDetailsActivity.this, "Update Error",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                                RequestQueue namerequestQueue = Volley.newRequestQueue(PersonalDetailsActivity.this);
                                namerequestQueue.add(nameObjectRequest);
                            }

                            //if user only changed phone number
                            else if(!changemobileno.getText().toString().equals(userphone)){
                                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, updatephoneno, phoneData, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        Toast.makeText(PersonalDetailsActivity.this, "Details Updated",
                                                Toast.LENGTH_SHORT).show();
                                        SharedPreferences sharedPref = getSharedPreferences("Profile", MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedPref.edit();
                                        try {
                                            editor.putString("Phone", response.getString("phoneNo"));
                                            editor.apply();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(PersonalDetailsActivity.this, "Error: Phone Number Belongs to Another Account Holder",
                                                Toast.LENGTH_SHORT).show();
                                        changemobileno.setText(userphone);
                                    }
                                });
                                RequestQueue requestQueue = Volley.newRequestQueue(PersonalDetailsActivity.this);
                                requestQueue.add(jsonObjectRequest);
                            }

                            //if user only changed name
                            else{
                                JsonObjectRequest nameObjectRequest = new JsonObjectRequest(Request.Method.POST, updatename, nameData, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        Toast.makeText(PersonalDetailsActivity.this, "Details Updated",
                                                Toast.LENGTH_SHORT).show();
                                        SharedPreferences sharedPref = getSharedPreferences("Profile", MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedPref.edit();
                                        try {
                                            editor.putString("Name", response.getString("name"));
                                            editor.apply();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(PersonalDetailsActivity.this, "Update Error",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                                RequestQueue namerequestQueue = Volley.newRequestQueue(PersonalDetailsActivity.this);
                                namerequestQueue.add(nameObjectRequest);
                            }



                        }

                    });










                }
            }
        });

















    }


}