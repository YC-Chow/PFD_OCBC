package sg.edu.np.pfd_ocbc;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuth;


import org.json.JSONException;
import org.json.JSONObject;

public class ReceiverAccountActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receiver_account);

        CardView changeacc = findViewById(R.id.changeacc);

        TextView accname = findViewById(R.id.accountname);

        SharedPreferences sharedPref = getSharedPreferences("AccountHolder", MODE_PRIVATE);

        ImageButton back = findViewById(R.id.notiback3);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReceiverAccountActivity.this, SettingsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });


        String userphone = sharedPref.getString("phoneno", "");




        changeacc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReceiverAccountActivity.this, AccountSelectorActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });




        String getcard = "https://pfd-server.azurewebsites.net/getAccountUsingPhoneNo";



        JSONObject nameData = new JSONObject();
        try {
            nameData.put("phoneNo", userphone);



        } catch (JSONException e) {
            e.printStackTrace();
        }

        //POST api to update name
        JsonObjectRequest nameObjectRequest = new JsonObjectRequest(Request.Method.POST, getcard, nameData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    String accNo = response.getString("acc_no");
                    accname.setText(accNo);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ReceiverAccountActivity.this, "Update Error",
                        Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue namerequestQueue = Volley.newRequestQueue(ReceiverAccountActivity.this);
        namerequestQueue.add(nameObjectRequest);




    }

    @Override
    public void onBackPressed() {
        return;
    }


}