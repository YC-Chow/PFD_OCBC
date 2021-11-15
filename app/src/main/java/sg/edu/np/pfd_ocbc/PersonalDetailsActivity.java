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

        String username = sharedPref.getString("name", "");
        String userphone = sharedPref.getString("phoneno", "");

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
                String username = sharedPref.getString("name", "");
                String userphone = sharedPref.getString("phoneno", "");
                Log.d("hi", String.valueOf(changename.getText().toString().length() ));
                if(changemobileno.getText().toString().equals(userphone) && changename.getText().toString().equals(username)){
                    Toast.makeText(PersonalDetailsActivity.this, "Personal Details Have Not Been Edited",
                            Toast.LENGTH_SHORT).show();
                }
                //if user left name or phone number empty
                else if(changemobileno.getText().toString().equals("") || changename.getText().toString().equals("")){
                    Toast.makeText(PersonalDetailsActivity.this, "Name Or Mobile Number Cannot be Empty",
                            Toast.LENGTH_SHORT).show();
                }
                //if user tries to change phone number to something a alphabet
                else if(changemobileno.getText().toString().matches("[a-zA-Z]+")){
                    Toast.makeText(PersonalDetailsActivity.this, "Mobile Number Cannot Have Alphabets",
                            Toast.LENGTH_SHORT).show();
                }
                //if user tries to change phone number to something with more/less than 8 digits
                else if(changemobileno.getText().toString().length() != 8){
                    Toast.makeText(PersonalDetailsActivity.this, "Mobile Number Nedds To Be 8 Digits",
                            Toast.LENGTH_SHORT).show();
                }
                else {
                    FirebaseUser user = mAuth.getCurrentUser();
                    String updatename = "https://pfd-server.azurewebsites.net/updateName";

                    JSONObject nameData = new JSONObject();


                    user.getIdToken(true).addOnSuccessListener(new OnSuccessListener<GetTokenResult>() {
                        @Override
                        public void onSuccess(GetTokenResult result) {
                            String idToken = result.getToken();
                            try {
                                nameData.put("uid", user.getUid());
                                nameData.put("name", changename.getText().toString());





                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            //If user changed both name and phone number
                            if(!changemobileno.getText().toString().equals(userphone) && !changename.getText().toString().equals(username)){

                                //POST api to update name
                                JsonObjectRequest nameObjectRequest = new JsonObjectRequest(Request.Method.POST, updatename, nameData, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {

                                        SharedPreferences sharedPref = getSharedPreferences("AccountHolder", MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedPref.edit();
                                        editor.putString("name", changename.getText().toString());
                                        editor.apply();




                                        //Send otp to updated number to verify that the user owns the phone number
                                        Intent intent = new Intent(PersonalDetailsActivity.this, PersonalDetailsOtpActivity.class);
                                        intent.putExtra("phoneNo", changemobileno.getText().toString());
                                        intent.putExtra("situation", "updatephoneNo");
                                        startActivity(intent);

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
                                //Send otp to updated number to verify that the user owns the phone number
                                Intent intent = new Intent(PersonalDetailsActivity.this, PersonalDetailsOtpActivity.class);
                                intent.putExtra("phoneNo", changemobileno.getText().toString());
                                intent.putExtra("situation", "updatephoneNo");
                                startActivity(intent);
                            }

                            //if user only changed name
                            else{
                                JsonObjectRequest nameObjectRequest = new JsonObjectRequest(Request.Method.POST, updatename, nameData, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        Toast.makeText(PersonalDetailsActivity.this, "Details Updated",
                                                Toast.LENGTH_SHORT).show();
                                        SharedPreferences sharedPref = getSharedPreferences("AccountHolder", MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedPref.edit();
                                        editor.putString("name", changename.getText().toString());
                                        editor.apply();



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