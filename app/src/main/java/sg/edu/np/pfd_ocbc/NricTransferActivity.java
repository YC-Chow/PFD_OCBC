package sg.edu.np.pfd_ocbc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

public class NricTransferActivity extends AppCompatActivity {

    EditText enterNric;
    ImageView nextBtn;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nric_transfer);

        nextBtn = findViewById(R.id.nextBtnNricTransfer);
        enterNric = findViewById(R.id.enterNric);
        sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);

        //Setting up transfer option bar
        BottomNavigationView optionBar = (BottomNavigationView) findViewById(R.id.TopBar);
        int menuSize = optionBar.getMenu().size();
        for (int i = 0; i < menuSize; i++)
        {
            optionBar.getMenu().getItem(i).setChecked(false);
        }
        optionBar.getMenu().findItem(R.id.nricTransfer).setChecked(true);
        optionBar.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.cardTransfer:
                        Intent intent = new Intent(NricTransferActivity.this, AccountTransferActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                        break;

                    case R.id.mobileTransfer:
                        Intent b = new Intent(NricTransferActivity.this, MobileNumberActivity.class);
                        b.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(b);
                        break;

                    case R.id.nricTransfer:
                        break;
                }
                return false;
            }
        });

        //Setting up bottom nav bar
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        int size = navigation.getMenu().size();
        for (int i = 0; i < size; i++) {
            navigation.getMenu().getItem(i).setChecked(false);
        }
        navigation.getMenu().findItem(R.id.page_2).setChecked(true);
        navigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {


                    case R.id.page_1:
                        Intent a = new Intent(NricTransferActivity.this, HomeActivity.class);
                        a.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(a);

                        break;

                    case R.id.page_2:

                        break;

                    case R.id.page_3:
                        Intent b = new Intent(NricTransferActivity.this, ProfileActivity.class);
                        b.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(b);
                        break;
                }
                return false;
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String receiverMobileNum = enterNric.getText().toString();
                if(Pattern.matches("^[STFG]\\d{7}[A-Z]$",receiverMobileNum))
                {
                    String postUrl = "https://pfd-server.azurewebsites.net/getAccountUsingIcNo";

                    JSONObject postData = new JSONObject();
                    try {
                        postData.put("icNo", receiverMobileNum);
                    }catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, postUrl, postData, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if (response.has("acc_no"))
                                {
                                    String receiverAccNo = response.getString("acc_no");
                                    String receiverName = response.getString("account_holder_name");
                                    String senderAccNo = sharedPreferences.getString("accNo","");

                                    if (receiverName == null){
                                        receiverName = "Unknown";
                                    }

                                    Intent intent = new Intent(NricTransferActivity.this, AmountConfirmationActivity.class);
                                    intent.putExtra("receiverName" , receiverName);
                                    intent.putExtra("receiverAccNo", receiverAccNo);
                                    intent.putExtra("senderAccNo", senderAccNo);
                                    startActivity(intent);
                                }
                                else
                                {
                                    Toast.makeText(NricTransferActivity.this, "Invalid Account Number",Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(NricTransferActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                            error.printStackTrace();
                        }
                    });
                    RequestQueue requestQueue = Volley.newRequestQueue(NricTransferActivity.this);
                    requestQueue.add(jsonObjectRequest);
                }
                else
                {
                    Toast.makeText(NricTransferActivity.this,"Please enter a valid NRIC!",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}