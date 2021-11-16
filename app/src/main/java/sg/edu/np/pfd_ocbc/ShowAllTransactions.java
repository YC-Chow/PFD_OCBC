package sg.edu.np.pfd_ocbc;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        sharedPref = getSharedPreferences("MySharedPref", MODE_PRIVATE);

        recyclerView = findViewById(R.id.showAllRV);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,true));
        transactionList = new ArrayList<>();
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
                                String ReceivedOrTransferred = "";

                                if (fromAcc.equals(sharedPref.getString("accNo", ""))){
                                    Log.d("IM THE SENDER!","IM THE SENDER!");
                                    DebitOrCredit = "-";
                                    ReceivedOrTransferred = "Transferred to: ";
                                }
                                else{
                                    DebitOrCredit = "+";
                                    ReceivedOrTransferred = "Received from: ";
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
                                t.setReceivedOrSent(ReceivedOrTransferred);
                                transactionList.add(t);

                                HomeTransactionAdapter homeTransactionAdapter = new HomeTransactionAdapter(mContext,transactionList);
                                recyclerView.setAdapter(homeTransactionAdapter);
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
}
