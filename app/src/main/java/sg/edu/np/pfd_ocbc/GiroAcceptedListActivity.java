package sg.edu.np.pfd_ocbc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class GiroAcceptedListActivity extends AppCompatActivity {

    ImageButton back;
    private ArrayList<Giro> giroList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_giro_accepted_list);

        /*back = findViewById(R.id.giroOptBack);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GiroAcceptedListActivity.this, GiroOptionsActivity.class);
                startActivity(intent);
                finish();
            }
        });*/

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
                        Intent b = new Intent(GiroAcceptedListActivity.this, HomeActivity.class);
                        b.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(b);
                        break;

                    case R.id.page_2:
                        Intent a = new Intent(GiroAcceptedListActivity.this, AccountTransferActivity.class);
                        a.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(a);
                        break;

                    case R.id.page_3:
                        break;

                }
                return false;
            }
        });

        if (giroList != null){
            RecyclerView recyclerView = findViewById(R.id.giroRecyclerView);
            GiroAcceptedAdapter adapter = new GiroAcceptedAdapter(giroList);
            LinearLayoutManager manager = new LinearLayoutManager(this);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(manager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
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
            alertDialog.show();
        }
    }

    private void LoadData(){

        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        String accNo = sharedPreferences.getString("accNo", "");

        String postUrl = "https://pfd-server.azurewebsites.net/getAcceptedGiro";

        JSONObject postData = new JSONObject();
        try {
            postData.put("giro_acc_no", accNo);
        }catch (JSONException e)
        {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, postUrl, postData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray giroArray = response.getJSONArray("data");
                    //Server function not returning an array
                    for (int i = 0; i < giroArray.length(); i++){
                        Giro giro = new Giro();
                        giro.setDescription(giroArray.getJSONObject(i).getString("description"));
                        giro.setBiz_id(giroArray.getJSONObject(i).getInt("business_id"));
                        giroList.add(giro);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(GiroAcceptedListActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(GiroAcceptedListActivity.this);
        requestQueue.add(jsonObjectRequest);
    }
}