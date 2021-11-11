package sg.edu.np.pfd_ocbc;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static android.content.ContentValues.TAG;

public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Account userAccount;
    RecyclerView recyclerView;
    HomeTransactionAdapter TransactionAdapter;
    Context mContext;
    private ArrayList<Card> cardList;
    private ArrayList<Transaction> transactionList;
    private static final String TAG = "HomeActivity";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth =FirebaseAuth.getInstance();
        LocalDate today = LocalDate.now();
        String formattedDate = today.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT));

        //Initialize Account obj
        SharedPreferences profilePref = getSharedPreferences("AccountHolder", MODE_PRIVATE);
        userAccount = new Account();
        userAccount.setName(profilePref.getString("Name",""));
        userAccount.setphoneNo(profilePref.getString("Phone",""));
        userAccount.setEmail(profilePref.getString("Email",""));
        userAccount.setIcNo(profilePref.getString("icNo",""));
        cardList = new ArrayList<Card>();
        userAccount.setCardList(cardList);

        // username when enter home
        TextView uName = findViewById(R.id.userName);
        SharedPreferences AHsharedPref = getSharedPreferences("AccountHolder",MODE_PRIVATE);
        String userName = AHsharedPref.getString("Name","");
        uName.setText(userName);
        //Log.v("name",userName);

        //card details
        SharedPreferences sharedPref = getSharedPreferences("MySharedPref",MODE_PRIVATE);
        TextView last4Digit = findViewById(R.id.last4Digit);
        String fourDigit = sharedPref.getString("last4digits","");
        last4Digit.setText("* " + fourDigit);
        TextView cardBalanceAmt = findViewById(R.id.balanceAmt);
        String cardBalance = sharedPref.getString("balanceAmt","");
        cardBalanceAmt.setText(cardBalance);

        //card type or issuingNetwork
        ImageView issuer = findViewById(R.id.cardType);
        String issuingNetwork = sharedPref.getString("IssuingNetwork","");
        //Log.v("",issuingNetwork);
        if(issuingNetwork.equals("Visa")){
            issuer.setImageResource(R.drawable.visa_icon);
        }

        recyclerView = findViewById(R.id.transactionRV);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        transactionList = new ArrayList<>();

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
                        Intent a = new Intent(HomeActivity.this,CardTransferActivity.class);
                        a.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        for (int i =0; i < userAccount.getCardList().size(); i++)
                        {
                            a.putExtra("cardNum" + i, userAccount.getCardList().get(i).getCardNo());
                        }
                        a.putExtra("numOfCard", userAccount.getCardList().size());
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
//        //get userName
//        user.getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
//            public void onComplete(@NonNull Task<GetTokenResult> task) {
//                if (task.isSuccessful()) {
//                    String token = task.getResult().getToken();
//                    try {
//                        postData.put("uid", user.getUid());
//                        postData.put("jwtToken", token);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//
//                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, postUrl, postData, new Response.Listener<JSONObject>() {
//                        @Override
//                        public void onResponse(JSONObject response) {
//                            try {
//                                editor.putString("Name", response.getString("name"));
//                                editor.putString("Phone", response.getString("phoneNo"));
//                                editor.putString("Email", response.getString("email"));
//                                editor.putString("icNo", response.getString("icNo"));
//                                editor.putString("startDate", response.getString("startDate"));
//
//                                editor.apply();
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }, new Response.ErrorListener() {
//                        @Override
//                        public void onErrorResponse(VolleyError error) {
//                            error.printStackTrace();
//                        }
//                    });
//                    RequestQueue requestQueue = Volley.newRequestQueue(HomeActivity.this);
//                    requestQueue.add(jsonObjectRequest);
//                }
//            }
//        });


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
                                String accNo = value.get("accNo").toString();
                                Double cardBalance = (Double) value.get("balance");
                                String issuingNetwork = value.get("issuingNetwork").toString();
                                AddToCardList(cardNum);

                                String lastFourDigits = "";     //substring containing last 4 characters
                                if (cardNum.length() > 4)
                                {
                                    lastFourDigits = cardNum.substring(cardNum.length() - 4);
                                }
                                editor.putString("last4digits",lastFourDigits);
                                editor.putString("balanceAmt",cardBalance.toString());
                                editor.putString("IssuingNetwork",issuingNetwork);
                                editor.putString("AccNumber",accNo);
                                editor.apply();
                                //System.out.println(lastFourDigits);
                                //System.out.println(value.get("cardNumber"));
                            }
                            userAccount.setCardList(cardList);
                            Log.v(TAG, "" + userAccount.getCardList());
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                        }
                    });
                    RequestQueue requestQueue = Volley.newRequestQueue(HomeActivity.this);
                    requestQueue.add(jsonObjectRequest);

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //Log.v(TAG, "The address output is:" + userAccount.getCardList());
                        }
                    },5000);
                }
            }
        });

        //get transaction details
        user.getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
            public void onComplete(@NonNull Task<GetTokenResult> task) {
                if (task.isSuccessful()) {
                    String token = task.getResult().getToken();

                    try{
                        //how to freaking put the card number into the post
                        postData.put("cardNo", "4716954771726994" );
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
                            //System.out.println(jsonMap);

                            for (Map<String,Object> value : jsonMap.values()) {
                                //System.out.println(value.get("transactionId"));

                                String from = value.get("from").toString();
                                String to = value.get("to").toString();
                                Double amount = (Double) value.get("amount");
                                String date = value.get("date").toString();
                                Transaction t = new Transaction();
                                t.setToIC(from);
                                t.setTransactionAmt(amount);
                                t.setTransactionDate(date);
                                transactionList.add(t);

                                editor.putString("from",from);
                                editor.putString("to",to);
                                editor.putString("amount",amount.toString());
                                editor.putString("date",date.toString());
                                editor.apply();

                                editor.putString("from",from);
                                HomeTransactionAdapter homeTransactionAdapter = new HomeTransactionAdapter(mContext,transactionList);
                                recyclerView.setAdapter(homeTransactionAdapter);
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

    public void AddToCardList(String cardNum)
    {

        cardList.add(new Card(cardNum));
    }

}