package sg.edu.np.pfd_ocbc;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class ShowAllTransactions extends AppCompatActivity {
    RecyclerView recyclerView;
    Context mContext;
    SharedPreferences sharedPref;
    FirebaseAuth mAuth;
    private ArrayList<Transaction> transactionList;
    private ShimmerFrameLayout mFrameLayout;
    ImageView backBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all_transactions); // setting the layout first
        mAuth = FirebaseAuth.getInstance();
        sharedPref = getSharedPreferences("MySharedPref", MODE_PRIVATE);

        mFrameLayout = findViewById(R.id.shimmerLayoutShowAll);

        recyclerView = findViewById(R.id.showAllRV);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        transactionList = new ArrayList<>();

        backBtn = findViewById(R.id.transactionBackBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ShowAllTransactions.this,HomeActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(i);

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = mAuth.getCurrentUser();
        DBHandler dbHandler = new DBHandler(this);

        //Log.v("uid is:" ,user.getUid());
        String postUrlAccount = "https://pfd-server.azurewebsites.net/getAccountUsingUid";
        String postUrlTransactions = "https://pfd-server.azurewebsites.net/getTransactions";
        JSONObject postData = new JSONObject();

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

                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("accNo", accNo);
                    editor.apply();
                    //Log.v("accNumber is",accNo);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try{
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
                            for (int i = 0; i <jsonArray.length() ; i++) {
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

                                if(fromName == "null"){
                                    fromName = fromAcc;
                                }
                                else{
                                    fromName = fromName;
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
                RequestQueue requestQueue = Volley.newRequestQueue(ShowAllTransactions.this);
                requestQueue.add(jsonObjectRequest);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error yo", "onErrorResponse: ");
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(ShowAllTransactions.this);
        requestQueue.add(jsonObjectRequest);

    }
    @Override
    public void onBackPressed() {
        return;
    }
}

