package sg.edu.np.pfd_ocbc;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

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

public class AccountSelectorActivity extends AppCompatActivity {
    Context mContext;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_selector);
        mAuth = FirebaseAuth.getInstance();
        mContext = AccountSelectorActivity.this;
        Intent intent = getIntent();
        String situation = intent.getStringExtra("situation");

        RecyclerView recyclerView = findViewById(R.id.recycler);





        String getcard = "https://pfd-server.azurewebsites.net/getAccountUsingUid";
        FirebaseUser user = mAuth.getCurrentUser();

        ArrayList <Card> cardList = new ArrayList<Card>();



        JSONObject nameData = new JSONObject();
        try {
            nameData.put("uid", user.getUid());


        } catch (JSONException e) {
            e.printStackTrace();
        }

        //POST api to update name
        JsonObjectRequest nameObjectRequest = new JsonObjectRequest(Request.Method.POST, getcard, nameData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONArray jsonArray = new JSONArray(response.getJSONArray("data").toString());


                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject jsonObject = (JSONObject) jsonArray.get(i);

                        String accno = jsonObject.getString("acc_no");
                        String accname = jsonObject.getString("acc_name");
                        String balance = jsonObject.getString("balance");
                        String card_number = jsonObject.getString("card_number");
                        String issuingnetwork = jsonObject.getString("issuing_network");




                        Card card = new Card();
                        card.setNameOnCard(accname);
                        card.setCardNo(card_number);
                        card.setIssuingNetwork(issuingnetwork);
                        card.setAccNo(accno);
                        card.setBalance(Double.valueOf(balance));

                        cardList.add(card);




                    }
                    if(situation.equals("receiveracc")){
                        ReceiverAdapter receiverAdapter= new ReceiverAdapter(mContext, cardList);
                        LinearLayoutManager mLayoutManager = new LinearLayoutManager(mContext);
                        recyclerView.setLayoutManager(mLayoutManager);
                        recyclerView.setAdapter(receiverAdapter);
                        recyclerView.setVisibility(View.VISIBLE);
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                    }
                    if(situation.equals("home")){
                        ChangeAccountAdapter changeAccountAdapter = new ChangeAccountAdapter(mContext, cardList);
                        LinearLayoutManager mLayoutManager = new LinearLayoutManager(mContext);
                        recyclerView.setLayoutManager(mLayoutManager);
                        recyclerView.setAdapter(changeAccountAdapter);
                        recyclerView.setVisibility(View.VISIBLE);
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(AccountSelectorActivity.this, "Update Error",
                        Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue namerequestQueue = Volley.newRequestQueue(AccountSelectorActivity.this);
        namerequestQueue.add(nameObjectRequest);
    }
}