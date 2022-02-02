package sg.edu.np.pfd_ocbc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class GiroListActivity extends AppCompatActivity {

    private ArrayList<Giro> giroList = new ArrayList<Giro>();
    private  TextView title;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_giro_list);

        ImageButton back = findViewById(R.id.giroOptBack);
        title = findViewById(R.id.giroTitle);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GiroListActivity.this, GiroOptionsActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //Setting up bottom nav bar
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        int size = navigation.getMenu().size();
        for (int i = 0; i < size; i++) {
            navigation.getMenu().getItem(i).setChecked(false);
        }
        navigation.getMenu().findItem(R.id.page_3).setChecked(true);
        navigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {


                    case R.id.page_1:
                        Intent b = new Intent(GiroListActivity.this, HomeActivity.class);
                        b.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(b);
                        break;

                    case R.id.page_2:
                        Intent a = new Intent(GiroListActivity.this, AccountTransferActivity.class);
                        a.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(a);
                        break;

                    case R.id.page_3:
                        break;

                }
                return false;
            }
        });

        LoadDataPart1();
    }

    @Override
    protected void onStart() {
        super.onStart();

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Please wait");
        progressDialog.show();
    }

    private  void LoadDataPart1(){
        String postUrl = "https://pfd-server.azurewebsites.net/getAccountUsingUid";
        JSONObject postData = new JSONObject();
        RequestQueue requestQueue = Volley.newRequestQueue(GiroListActivity.this);
        try{
            postData.put("uid", FirebaseAuth.getInstance().getUid());
        }catch (JSONException e){
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, postUrl, postData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray data = response.getJSONArray("data");
                    JSONArray accounts = new JSONArray();
                    for (int i = 0; i < data.length(); i++){
                        accounts.put(data.getJSONObject(i).getString("acc_no"));
                        System.out.println(data.getJSONObject(i).getString("acc_no"));
                    }
                    LoadDataPart2(requestQueue, accounts);

                }catch (JSONException e){
                    e.printStackTrace();
                    Toast.makeText(GiroListActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });


        requestQueue.add(jsonObjectRequest);
    }

    private void LoadDataPart2(RequestQueue requestQueue, JSONArray accounts){

        String postUrl;
        postUrl = "https://pfd-server.azurewebsites.net/getGiro";

        JSONObject postData = new JSONObject();
        try {
            postData.put("giro_acc_no", accounts);
        }catch (JSONException e)
        {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, postUrl, postData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray giroArray = response.getJSONArray("data");
                    for (int i = 0; i < giroArray.length(); i++){
                        Giro giro = new Giro(GiroListActivity.this);
                        giro.setGiro_acc_no(giroArray.getJSONObject(i).getString("giro_acc_no"));
                        giro.setGiro_id(giroArray.getJSONObject(i).getString("giro_id"));
                        giro.setReferenceNo(giroArray.getJSONObject(i).getString("reference_no"));
                        giro.setBusiness(new Business(giroArray.getJSONObject(i).getString("business_uen"),
                                giroArray.getJSONObject(i).getString("business_name"),
                                giroArray.getJSONObject(i).getString("business_acc_no")));

                        giroList.add(giro);

                        updateRecyclerView();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();

                    AlertDialog.Builder builder = new AlertDialog.Builder(GiroListActivity.this);
                    builder.setTitle("Giro");
                    builder.setMessage("No Giro records found!");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            progressDialog.dismiss();
                            finish();
                        }
                    });

                    builder.create().show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                AlertDialog.Builder builder = new AlertDialog.Builder(GiroListActivity.this);
                builder.setTitle("Error");
                builder.setMessage("Error Retrieving");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        progressDialog.dismiss();
                        finish();
                    }
                });

                builder.create().show();
            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    private void updateRecyclerView(){
        if (giroList != null){
            RecyclerView recyclerView = findViewById(R.id.giroRecyclerView);
            GiroListAdapter adapter = new GiroListAdapter(giroList);
            LinearLayoutManager manager = new LinearLayoutManager(this);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(manager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            progressDialog.dismiss();
        }
        else{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("TEMPORARY WARNING");
            builder.setMessage("TEMPORARY WARNING FOR NO EMPTY LIST");
            builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            AlertDialog alertDialog = builder.create();
            progressDialog.dismiss();
            alertDialog.show();
        }
    }
}