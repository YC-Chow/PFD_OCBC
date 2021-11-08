package sg.edu.np.pfd_ocbc;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth =FirebaseAuth.getInstance();
        LocalDate today = LocalDate.now();
        String formattedDate = today.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT));

        // username when enter home
        TextView uName = findViewById(R.id.userName);
        SharedPreferences sharedPref = getSharedPreferences("MySharedPref",MODE_PRIVATE);
        String userName = sharedPref.getString("Name","");
        uName.setText(userName);
        //Log.v("name",userName);

        //card details
        TextView last4Digit = findViewById(R.id.last4Digit);
        String fourDigit = sharedPref.getString("last4digits","");
        last4Digit.setText("* " + fourDigit);

        //card type or issuingNetwork
        ImageView issuer = findViewById(R.id.cardType);
        String issuingNetwork = sharedPref.getString("IssuingNetwork","");
        //Log.v("",issuingNetwork);
        if(issuingNetwork.equals("Visa")){
            issuer.setImageResource(R.drawable.visa_icon);
        }

        //Setting up bottom nav bar
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        int size = navigation.getMenu().size();
        for (int i = 0; i < size; i++) {
            navigation.getMenu().getItem(i).setChecked(false);
        }
        navigation.getMenu().findItem(R.id.page_1).setChecked(true);
        navigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {


                    case R.id.page_1:


                        break;

                    case R.id.page_2:
                        Intent a = new Intent(HomeActivity.this,MobileTransferActivity.class);
                        a.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(a);
                        break;

                    case R.id.page_3:
                        Intent b = new Intent(HomeActivity.this, ProfileActivity.class);
                        b.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(b);
                        break;




                }
                return false;
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();

        String postUrl = "https://pfd-server.azurewebsites.net/getAccountHolder";
        String postUrlAccounts = "https://pfd-server.azurewebsites.net/getAccounts";
        String postUrlTransactions = "https://pfd-server.azurewebsites.net/getTransactions";

        // Storing data into SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
        // Creating an Editor object to edit(write to the file)
        SharedPreferences.Editor editor = sharedPreferences.edit();

        JSONObject postData = new JSONObject();
        //get userName
        user.getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
            public void onComplete(@NonNull Task<GetTokenResult> task) {
                if (task.isSuccessful()) {
                    String token = task.getResult().getToken();
                    try {
                        postData.put("uid", user.getUid());
                        postData.put("jwtToken", token);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, postUrl, postData, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String Name = response.getString("name");
                                editor.putString("Name", Name);
                                editor.apply();
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
                    RequestQueue requestQueue = Volley.newRequestQueue(HomeActivity.this);
                    requestQueue.add(jsonObjectRequest);
                }
            }
        });

        //get Account details
        user.getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
            public void onComplete(@NonNull Task<GetTokenResult> task) {
                if (task.isSuccessful()) {
                    String token = task.getResult().getToken();
                    try{
                        postData.put("uid", user.getUid());
                        postData.put("jwtToken", token );
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }

                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, postUrlAccounts, postData, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            //System.out.println(response);
                            Type mapTokenType = new TypeToken<Map<String,Map<String,Object>>>(){}.getType();
                            Map<String, Map<String,Object>> jsonMap = new Gson().fromJson(response.toString(), mapTokenType);
                            System.out.println(jsonMap);
                            for (Map<String,Object> value : jsonMap.values()) {
                                String cardNum = value.get("cardNumber").toString();

                                String lastFourDigits = "";     //substring containing last 4 characters
                                if (cardNum.length() > 4)
                                {
                                    lastFourDigits = cardNum.substring(cardNum.length() - 4);
                                }
                                editor.putString("last4digits",lastFourDigits);

                                //checking if visa
                                String issuingNetwork = value.get("issuingNetwork").toString();

                                editor.putString("IssuingNetwork",issuingNetwork);
                                editor.apply();
                                //System.out.println(lastFourDigits);
                                //System.out.println(value.get("cardNumber"));
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                        }
                    });
                    RequestQueue requestQueue = Volley.newRequestQueue(HomeActivity.this);
                    requestQueue.add(jsonObjectRequest);
                }
            }
        });

        //get transaction details
        /*user.getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
            public void onComplete(@NonNull Task<GetTokenResult> task) {
                if (task.isSuccessful()) {
                    String token = task.getResult().getToken();
                    try{
                        postData.put("cardNo", card.getCardNo());
                        postData.put("jwtToken", token );
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }

                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, postUrlTransactions, postData, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            //System.out.println(response);
                            Type mapTokenType = new TypeToken<Map<String,Map<String,Object>>>(){}.getType();
                            Map<String, Map<String,Object>> jsonMap = new Gson().fromJson(response.toString(), mapTokenType);
                            System.out.println(jsonMap);
                            for (Map<String,Object> value : jsonMap.values()) {
                                System.out.println(value);
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                        }
                    });
                    RequestQueue requestQueue = Volley.newRequestQueue(HomeActivity.this);
                    requestQueue.add(jsonObjectRequest);
                }
            }
        });*/
    }
    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}