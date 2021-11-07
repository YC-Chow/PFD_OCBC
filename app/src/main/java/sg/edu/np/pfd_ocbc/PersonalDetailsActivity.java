package sg.edu.np.pfd_ocbc;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

public class PersonalDetailsActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_details);

        mAuth = FirebaseAuth.getInstance();
        EditText changemobileno = findViewById(R.id.changemobileno);
        EditText changename = findViewById(R.id.changeusername);

        ImageButton back = findViewById(R.id.pdback);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PersonalDetailsActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });


        FirebaseUser user = mAuth.getCurrentUser();

        String postUrl = "https://pfd-server.azurewebsites.net/createAccount";
        RequestQueue requestQueue = Volley.newRequestQueue(this);



        JSONObject postData = new JSONObject();

        try{
            postData.put("uid", user.getUid());
            postData.put("jwtToken", user.getIdToken(true).getResult().getToken() );
        }
        catch (JSONException e) {
            e.printStackTrace();
        }


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, postUrl, postData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                System.out.println(response);
                try {
                    changename.setText(response.get("name").toString());
                    changemobileno.setText(response.get("phoneNo").toString());

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

        requestQueue.add(jsonObjectRequest);


    }
}