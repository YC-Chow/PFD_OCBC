package sg.edu.np.pfd_ocbc;

import android.Manifest;
import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.nfc.cardemulation.HostNfcFService;
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
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.text.format.Formatter;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Account userAccount;
    RecyclerView recyclerView;
    TextView emptyText;
    SharedPreferences accholdersharedpref;
    HomeTransactionAdapter TransactionAdapter;
    Context mContext;
    SharedPreferences sharedPref;
    private ArrayList<Transaction> transactionList;
    private static final String TAG = "HomeActivity";
    private static final DecimalFormat df = new DecimalFormat("0.00");
    private ShimmerFrameLayout mFrameLayout;
    private ShimmerFrameLayout mFrameLayoutBalance;
    TextView cardBalText;
    String mobile_mac_address;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mAuth =FirebaseAuth.getInstance();

        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        String ipAddress = Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress());
        Log.v("IP Address",ipAddress);

        CardView card = findViewById(R.id.cardView);

        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, AccountSelectorActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.putExtra("situation", "home");
                startActivity(intent);
            }
        });

        sharedPref = getSharedPreferences("MySharedPref", MODE_PRIVATE);

        accholdersharedpref = getSharedPreferences("AccountHolder", MODE_PRIVATE);
        mFrameLayout = findViewById(R.id.shimmerLayout);
        mFrameLayoutBalance = findViewById(R.id.shimmerLayout2);

        TextView acc_HolderName = findViewById(R.id.userName);
        String accHolderName = accholdersharedpref.getString("name","");
        acc_HolderName.setText(accHolderName);

        //cardDetails
        CardView cardView = findViewById(R.id.cardView);
        cardView.setPreventCornerOverlap(false);

        TextView last4Digit = findViewById(R.id.last4Digit);
        String fourDigit = sharedPref.getString("last4Digits","");
        last4Digit.setText("XXXX XXXX XXXX " + fourDigit);

        cardBalText = findViewById(R.id.balanceAmt);
        //String cardBalance = sharedPref.getString("cardBal","");


        emptyText = findViewById(R.id.empty_view);

        //card type or issuingNetwork
        ImageView issuer = findViewById(R.id.cardType);
        String issuingNetwork = sharedPref.getString("issuingNetwork","");
        //Log.v("",issuingNetwork);
        if(issuingNetwork.equals("Visa")){
            issuer.setImageResource(R.drawable.visa_icon);
        }
        TextView cardName = findViewById(R.id.cardName);
        String NameonCard = sharedPref.getString("accName","");
        recyclerView = findViewById(R.id.transactionRV);
        cardName.setText(NameonCard);



        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        transactionList = new ArrayList<>();

        TextView showAllBtn = findViewById(R.id.showAllBtn);
        showAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(HomeActivity.this, ShowAllTransactions.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(i);
            }
        });
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
        DBHandler dbHandler = new DBHandler(this);

        //Log.v("uid is:" ,user.getUid());
        String postUrlAccount = "https://pfd-server.azurewebsites.net/getAccountUsingPhoneNo";
        String postUrlTransactions = "https://pfd-server.azurewebsites.net/getTransactions";
        JSONObject postData = new JSONObject();
        RequestQueue requestQueue = Volley.newRequestQueue(HomeActivity.this);

        try{
            if(!sharedPref.getString("accNo", "").equals("")){
                postData.put("accNo", sharedPref.getString("accNo", ""));
                Log.v("dfs", "hisa");
                postUrlAccount = "https://pfd-server.azurewebsites.net/getAccountUsingAccNo";
            }
            else {
                postData.put("phoneNo", accholdersharedpref.getString("phoneno", ""));
            }

        }catch (JSONException e) {
            e.printStackTrace();
        }
        mFrameLayoutBalance.startShimmer();
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
                    cardBalText.setText(df.format(cardBal));
                    Log.d("lolza", lastFourDigits);
                    mFrameLayoutBalance.setVisibility(View.GONE);

                    // Creating an Editor object to edit(write to the file)
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("accNo", accNo);
                    editor.putString("accName", accName);
                    editor.putString("accHolderName",accHolderName);
                    editor.putString("cardBal", df.format(cardBal));
                    editor.putString("fullCardNumber", cardNumber);
                    editor.putString("last4Digits", lastFourDigits);
                    editor.putString("issuingNetwork", issuingNetwork);
                    editor.apply();
                    //Log.v("accNumber is",accNo);

                    CheckForFailedTransaction(accNo,requestQueue);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try{
                    Log.d("accNo", "onResponse: "+sharedPref.getString("accNo", ""));
                    postData.put("accNo", sharedPref.getString("accNo", ""));
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, postUrlTransactions, postData,  new Response.Listener <JSONObject> () {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.d("SUCCESS", response.toString());
                        try {
                            JSONArray jsonArray = new JSONArray(response.getJSONArray("data").toString());
                            if (jsonArray.length() == 0 || jsonArray == null){
                                mFrameLayout.startShimmer();
                                mFrameLayout.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.GONE);
                                emptyText.setVisibility(View.VISIBLE);

                            }
                            else {
                                for (int i = jsonArray.length()-1; i >=0 ; i--) {
                                    if (i == jsonArray.length()-6){
                                        break;
                                    }
                                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);

                                    String toName = jsonObject.getString("to_name");
                                    String fromName = jsonObject.getString("from_name");
                                    String toAcc = jsonObject.getString("to_acc");
                                    String fromAcc = jsonObject.getString("from_acc");
                                    String date = jsonObject.getString("date").substring(0,10);
                                    Double amt = Double.parseDouble(jsonObject.getString("amount"));

                                    String DebitOrCredit = "";

                                    if (fromAcc.equals(sharedPref.getString("accNo", ""))){
                                        DebitOrCredit = "-";
                                    }
                                    else{
                                        DebitOrCredit = "+";
                                    }

                                    Transaction t = new Transaction();
                                    t.setRecipientName(toName);
                                    t.setSenderName(fromName);
                                    t.setRecipientAccNo(toAcc);
                                    t.setSenderAccNo(fromAcc);
                                    t.setTransactionAmt(amt);
                                    t.setTransactionDate(date);
                                    t.setDebitOrCredit(DebitOrCredit);
                                    transactionList.add(0,t);



                                    HomeTransactionAdapter homeTransactionAdapter = new HomeTransactionAdapter(mContext,transactionList);
                                    recyclerView.setAdapter(homeTransactionAdapter);
                                    mFrameLayout.startShimmer();
                                    mFrameLayout.setVisibility(View.GONE);
                                    recyclerView.setVisibility(View.VISIBLE);
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error yo", "onErrorResponse: ");
                        error.printStackTrace();
                    }
                });

                requestQueue.add(jsonObjectRequest);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error yo", "onErrorResponse: ");
            }
        });
        requestQueue.add(jsonObjectRequest);



    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        mFrameLayout.stopShimmer();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mFrameLayout.startShimmer();
        super.onResume();
    }

    private void CheckForFailedTransaction(String accNo, RequestQueue queue){
        DBHandler dbHandler = new DBHandler(HomeActivity.this);
        Transaction transaction = dbHandler.CheckFailedTransaction(accNo);
        ValidateTransaction(transaction, dbHandler, queue);

    }

    @Override
    public void onBackPressed() {
        return;
    }

    private void ValidateTransaction(Transaction transaction, DBHandler dbHandler, RequestQueue queue){
        if (transaction != null){
            dbHandler.DeleteTransaction(transaction.getUniqueCode());
            String postUri = "https://pfd-server.azurewebsites.net/validateTransaction";

            JSONObject postData = new JSONObject();
            try {
                postData.put("uniqueKey", transaction.getUniqueCode());
            }catch (JSONException e)
            {
                e.printStackTrace();
            }

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, postUri, postData, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        boolean success = response.getBoolean("success");
                        if (success){
                            AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                            builder.setCancelable(false);
                            builder.setTitle("Failed Transaction Found!");
                            builder.setMessage("You tried to transfer S$" + String.format("%.2f", transaction.getTransactionAmt())
                                    + " to " + transaction.getRecipientName()
                                    + "\nDo you want to retry?");
                            builder.setNegativeButton("No", null);
                            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(HomeActivity.this, TransferConfirmationActivity.class);
                                    intent.putExtra("to", transaction.getRecipientAccNo());
                                    intent.putExtra("from", transaction.getSenderAccNo());
                                    intent.putExtra("amount", transaction.getTransactionAmt());
                                    intent.putExtra("name", transaction.getRecipientName());
                                    intent.putExtra("uniqueCode", transaction.getUniqueCode());
                                    startActivity(intent);
                                    finish();
                                }
                            });
                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                        }
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

            queue.add(jsonObjectRequest);
        }
    }
}