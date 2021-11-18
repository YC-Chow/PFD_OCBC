package sg.edu.np.pfd_ocbc;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
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

public class PersonalDetailsActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_details);
        mAuth = FirebaseAuth.getInstance();


        EditText changemobileno = findViewById(R.id.changemobileno);
        EditText changename = findViewById(R.id.changeusername);
        EditText changetele = findViewById(R.id.changetelegram);
        Button save = findViewById(R.id.save);

        SharedPreferences sharedPref = getSharedPreferences("AccountHolder", MODE_PRIVATE);

        String username = sharedPref.getString("name", "");
        String userphone = sharedPref.getString("phoneno", "");
        String usertele = sharedPref.getString("tele", "");

        changemobileno.setText(userphone);
        changename.setText(username);
        changetele.setText(usertele);

        if(usertele.equals("null")){
            changetele.setText("");
        }

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
                String usertele = sharedPref.getString("tele", "");
                Log.d("hi", String.valueOf(changename.getText().toString().length()));
                if (changemobileno.getText().toString().equals(userphone) && changename.getText().toString().equals(username) && changetele.getText().toString().equals(usertele)) {
                    Toast.makeText(PersonalDetailsActivity.this, "Personal Details Have Not Been Edited",
                            Toast.LENGTH_SHORT).show();
                }
                //if user left name or phone number empty
                else if (changemobileno.getText().toString().equals("") || changename.getText().toString().equals("")) {
                    Toast.makeText(PersonalDetailsActivity.this, "Name Or Mobile Number Cannot be Empty",
                            Toast.LENGTH_SHORT).show();
                }
                //if user tries to change phone number to something a alphabet
                else if (changemobileno.getText().toString().matches("[a-zA-Z]+")) {
                    Toast.makeText(PersonalDetailsActivity.this, "Mobile Number Cannot Have Alphabets",
                            Toast.LENGTH_SHORT).show();
                }
                //if user tries to change phone number to something with more/less than 8 digits
                else if (changemobileno.getText().toString().length() != 8) {
                    Toast.makeText(PersonalDetailsActivity.this, "Mobile Number Nedds To Be 8 Digits",
                            Toast.LENGTH_SHORT).show();
                }
                //if name is less than 5 characters
                else if (changename.getText().toString().length() < 5) {
                    Toast.makeText(PersonalDetailsActivity.this, "Name needs to be at least 5 characters long",
                            Toast.LENGTH_SHORT).show();
                }
                else {
                    FirebaseUser user = mAuth.getCurrentUser();
                    String updatename = "https://pfd-server.azurewebsites.net/updateName";
                    String postUrlTelegram = "https://pfd-server.azurewebsites.net/updateTeleId";


                    JSONObject nameData = new JSONObject();
                    try {
                        nameData.put("uid", user.getUid());
                        nameData.put("name", changename.getText().toString());


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    //If user changed both name and phone number and telegramid
                    if ((!changemobileno.getText().toString().equals(userphone) && !changename.getText().toString().equals(username)) ||
                            (!changetele.getText().toString().equals(usertele) && !changemobileno.getText().toString().equals(userphone)) ||
                            (!changetele.getText().toString().equals(usertele) && !changemobileno.getText().toString().equals(userphone) && !changemobileno.getText().toString().equals(userphone))) {

                        //POST api to update name
                        JsonObjectRequest nameObjectRequest = new JsonObjectRequest(Request.Method.POST, updatename, nameData, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {

                                SharedPreferences sharedPref = getSharedPreferences("AccountHolder", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putString("name", changename.getText().toString());
                                editor.apply();

                                JSONObject postData = new JSONObject();

                                FirebaseUser user = mAuth.getCurrentUser();

                                try{
                                    postData.put("uid", user.getUid());
                                    postData.put("teleId", changetele.getText().toString());
                                }catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, postUrlTelegram, postData, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        //Log.d("lolza",response.toString());
                                        editor.putString("tele", changetele.getText().toString());
                                        editor.apply();
                                        //Log.v("accNumber is",accNo);

                                        //Send otp to updated number to verify that the user owns the phone number
                                        Intent intent = new Intent(PersonalDetailsActivity.this, OtpActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                        intent.putExtra("phoneNo", changemobileno.getText().toString());
                                        intent.putExtra("situation", "updatephoneNo");
                                        startActivity(intent);
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.d("Error yo", "onErrorResponse: ");

                                    }
                                });
                                RequestQueue requestQueue = Volley.newRequestQueue(PersonalDetailsActivity.this);
                                requestQueue.add(jsonObjectRequest);
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
                    else if (!changemobileno.getText().toString().equals(userphone)) {
                        //Send otp to updated number to verify that the user owns the phone number
                        Intent intent = new Intent(PersonalDetailsActivity.this, OtpActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        intent.putExtra("phoneNo", changemobileno.getText().toString());
                        intent.putExtra("situation", "updatephoneNo");
                        startActivity(intent);
                    }

                    else if(!changetele.getText().toString().equals(usertele) && !changename.getText().toString().equals(username)){

                        //POST api to update name
                        JsonObjectRequest nameObjectRequest = new JsonObjectRequest(Request.Method.POST, updatename, nameData, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {

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

                        JSONObject postData = new JSONObject();

                        try{
                            postData.put("uid", user.getUid());
                            postData.put("teleId", changetele.getText().toString());
                        }catch (JSONException e) {
                            e.printStackTrace();
                        }

                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, postUrlTelegram, postData, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                //Log.d("lolza",response.toString());
                                SharedPreferences sharedPref = getSharedPreferences("AccountHolder", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putString("tele", changetele.getText().toString());
                                editor.apply();
                                editor.apply();
                                //Log.v("accNumber is",accNo);
                                Toast.makeText(getApplicationContext(),
                                        "Personal Details Updated", Toast.LENGTH_SHORT).show();

                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d("Error yo", "onErrorResponse: ");

                            }
                        });
                        RequestQueue requestQueue = Volley.newRequestQueue(PersonalDetailsActivity.this);
                        requestQueue.add(jsonObjectRequest);

                    }
                    else if(!changetele.getText().toString().equals(usertele) ){

                        JSONObject postData = new JSONObject();


                        try{
                            postData.put("uid", user.getUid());
                            postData.put("teleId", changetele.getText().toString());
                        }catch (JSONException e) {
                            e.printStackTrace();
                        }

                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, postUrlTelegram, postData, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                //Log.d("lolza",response.toString());
                                SharedPreferences sharedPref = getSharedPreferences("AccountHolder", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putString("tele", changetele.getText().toString());
                                editor.apply();
                                Toast.makeText(getApplicationContext(),
                                        "Personal Details Updated", Toast.LENGTH_SHORT).show();


                                editor.apply();
                                //Log.v("accNumber is",accNo);








                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d("Error yo", "onErrorResponse: ");

                            }
                        });
                        RequestQueue requestQueue = Volley.newRequestQueue(PersonalDetailsActivity.this);
                        requestQueue.add(jsonObjectRequest);

                    }

                    else {
                        //POST api to update name
                        JsonObjectRequest nameObjectRequest = new JsonObjectRequest(Request.Method.POST, updatename, nameData, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {

                                SharedPreferences sharedPref = getSharedPreferences("AccountHolder", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putString("name", changename.getText().toString());
                                editor.apply();
                                Toast.makeText(getApplicationContext(),
                                        "Personal Details Updated", Toast.LENGTH_SHORT).show();
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
            }
        });

        ImageButton info = findViewById(R.id.info);

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // inflate the layout of the popup window
                LayoutInflater inflater = (LayoutInflater)
                        getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupView = inflater.inflate(R.layout.popupwindow, null);


                TextView botlink = popupView.findViewById(R.id.botlink);
                botlink.setMovementMethod(LinkMovementMethod.getInstance());

                // create the popup window
                int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                boolean focusable = true; // lets taps outside the popup also dismiss it
                final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

                // show the popup window
                // which view you pass in doesn't matter, it is only used for the window tolken
                popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);

                // dismiss the popup window when touched
                popupView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        popupWindow.dismiss();
                        return true;
                    }
                });
            }
        });
    }
}




















