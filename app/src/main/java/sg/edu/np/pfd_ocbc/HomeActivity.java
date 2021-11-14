package sg.edu.np.pfd_ocbc;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
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
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Account userAccount;
    RecyclerView recyclerView;
    HomeTransactionAdapter TransactionAdapter;
    Context mContext;
    SharedPreferences sharedPref;
    private ArrayList<Transaction> transactionList;
    private static final String TAG = "HomeActivity";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mAuth =FirebaseAuth.getInstance();

        sharedPref = getSharedPreferences("MySharedPref", MODE_PRIVATE);


        TextView acc_HolderName = findViewById(R.id.userName);
        String accHolderName = sharedPref.getString("accHolderName","");
        acc_HolderName.setText(accHolderName);

        //cardDetails
        TextView last4Digit = findViewById(R.id.last4Digit);
        String fourDigit = sharedPref.getString("last4Digits","");
        last4Digit.setText("* " + fourDigit);

        TextView cardBalanceAmt = findViewById(R.id.balanceAmt);
        String cardBalance = sharedPref.getString("cardBal","");
        cardBalanceAmt.setText(cardBalance);

        //card type or issuingNetwork
        ImageView issuer = findViewById(R.id.cardType);
        String issuingNetwork = sharedPref.getString("issuingNetwork","");
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
                        Intent a = new Intent(HomeActivity.this, AccountTransferActivity.class);
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

        //Log.v("uid is:" ,user.getUid());
        String postUrlAccount = "https://pfd-server.azurewebsites.net/getAccountUsingUid";
        String postUrlTransactions = "https://pfd-server.azurewebsites.net/getTransactions";
        JSONObject postData = new JSONObject();
        JSONArray postArrayData = new JSONArray();



        try{
            postData.put("uid", user.getUid());
        }catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, postUrlAccount, postData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //Log.d("lolza",response.toString());
                try {
                    String accNo = response.getString("acc_no");
                    String accName = response.getString("acc_name");
                    String accHolderName = response.getString("account_holder_name");
                    Double cardBal = response.getDouble("balance");
                    String cardNumber = response.getString("card_number");
                    String issuingNetwork = response.getString("issuing_network");
                    Card c = new Card(cardNumber,accName,issuingNetwork,cardBal,accNo);

                    String lastFourDigits = "";     //substring containing last 4 characters
                    if (c.getCardNo().length() > 4)
                    {
                        lastFourDigits = c.getCardNo().substring(c.getCardNo().length() - 4);
                    }
                    Log.d("lolza", lastFourDigits);

                    // Creating an Editor object to edit(write to the file)
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("accNo", accNo);
                    editor.putString("accName", accName);
                    editor.putString("accHolderName",accHolderName);
                    editor.putString("cardBal", cardBal.toString());
                    editor.putString("fullCardNumber", cardNumber);
                    editor.putString("last4Digits", lastFourDigits);
                    editor.putString("issuingNetwork", issuingNetwork);
                    editor.putString("accNo", accNo);
                    editor.apply();


                } catch (JSONException e) {
                    e.printStackTrace();
                }


                JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST, postUrlTransactions, postArrayData,  new Response.Listener <JSONArray> () {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());
                            try {
                                JSONArray jsonArray = new JSONArray(response);
                                for (int i = 0; i <jsonArray.length() ; i++) {
                                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                                    Log.i("LOLZERS",jsonObject.getString("to_name"));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                    }

                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error yo", "onErrorResponse: ");
                    }
                });
                RequestQueue requestQueue = Volley.newRequestQueue(HomeActivity.this);
                requestQueue.add(jsonArrayRequest);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error yo", "onErrorResponse: ");
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(HomeActivity.this);
        requestQueue.add(jsonObjectRequest);
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
/*String DebitOrCredit = "+";
                        for (Map<String,Object> value : jsonMap.values()) {


                        }*/