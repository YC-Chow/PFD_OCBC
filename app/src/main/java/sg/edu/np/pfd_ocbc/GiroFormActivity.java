package sg.edu.np.pfd_ocbc;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class GiroFormActivity extends AppCompatActivity {

    Spinner bizDrop, accDrop;
    private HashMap<String,String> bizMap;
    private Button confirmBtn;
    private ImageButton backBtn;
    private EditText enterRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_giro_form);

        bizDrop = findViewById(R.id.bizDropList);
        accDrop = findViewById(R.id.accDropList);
        confirmBtn = findViewById(R.id.giroFormConfirm);
        backBtn = findViewById(R.id.giroFormBack);
        enterRef = findViewById(R.id.enterRefNo);

        RequestQueue queue = Volley.newRequestQueue(this);
        loadAccList(queue);
        loadBizMap(queue);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GiroFormActivity.this, GiroOptionsActivity.class);
                startActivity(intent);
                finish();
            }
        });

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!enterRef.getText().toString().equals("")){
                    createGiro(queue);
                }
                else {
                    Toast.makeText(GiroFormActivity.this,"Please enter a reference number", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void loadAccList(RequestQueue queue){
        String uid = FirebaseAuth.getInstance().getUid();
        String postUrl = "https://pfd-server.azurewebsites.net/getAccountUsingUid";
        JSONObject postData = new JSONObject();
        try{
            postData.put("uid", FirebaseAuth.getInstance().getUid());
        }catch (JSONException e){
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, postUrl, postData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    ArrayList<String> accList = new ArrayList<>();
                    JSONArray data = response.getJSONArray("data");
                    for (int i = 0; i < data.length(); i++){
                        accList.add(data.getJSONObject(i).getString("acc_no"));
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(GiroFormActivity.this,
                            android.R.layout.simple_spinner_item, accList);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    accDrop.setAdapter(adapter);
                }catch (JSONException e){
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

    private void loadBizMap(RequestQueue queue){
        String postUrl = "https://pfd-server.azurewebsites.net/getBillingOrganisations";
        JSONObject postData = new JSONObject();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, postUrl, postData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    ArrayList<String> bizList = new ArrayList<String>();
                    JSONArray bizArray = response.getJSONArray("data");
                    bizMap = new HashMap<>();
                    for (int i = 0; i < bizArray.length(); i++){
                        bizMap.put(bizArray.getJSONObject(i).getString("acc_name"),
                                bizArray.getJSONObject(i).getString("business_uen"));
                        bizList.add(bizArray.getJSONObject(i).getString("acc_name"));
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(GiroFormActivity.this,
                            android.R.layout.simple_spinner_item, bizList);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    bizDrop.setAdapter(adapter);
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

    private void createGiro(RequestQueue queue){
        ProgressDialog progressDialog = new ProgressDialog(GiroFormActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Please wait");
        progressDialog.show();

        String postUrl = "https://pfd-server.azurewebsites.net/createGiro";
        JSONObject postData = new JSONObject();
        try {
            postData.put("giro_acc_no", accDrop.getSelectedItem().toString());
            postData.put("business_uen", bizMap.get(bizDrop.getSelectedItem().toString()));
            postData.put("reference_no", enterRef.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, postUrl, postData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    progressDialog.dismiss();
                    AlertDialog.Builder builder = new AlertDialog.Builder(GiroFormActivity.this);
                    builder.setTitle("Giro Application");
                    builder.setCancelable(false);
                    if (response.getBoolean("success")){
                        builder.setMessage("Giro application is successful");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(GiroFormActivity.this, HomeActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                    }
                    else{
                        builder.setMessage("Giro application is unsuccessful\n Do you want to try again?");
                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(GiroFormActivity.this, HomeActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                        builder.setPositiveButton("Yes", null);
                    }
                    builder.create().show();
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