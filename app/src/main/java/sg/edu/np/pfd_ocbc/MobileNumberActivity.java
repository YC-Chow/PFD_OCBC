package sg.edu.np.pfd_ocbc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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

public class MobileNumberActivity extends AppCompatActivity {

    EditText enterMobileNum;
    Button nextBtn, backBtn;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_number);

        enterMobileNum = findViewById(R.id.customMobileNum);
        nextBtn = findViewById(R.id.nextBtnMobileNumber);
        backBtn = findViewById(R.id.backBtnMobileNumber);
        backBtn.setVisibility(View.GONE);



        sharedPref = getSharedPreferences("MySharedPref", MODE_PRIVATE);

//        backBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MobileNumberActivity.this, HomeActivity.class);
//                startActivity(intent);
//                finish();
//            }
//        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String receiverMobileNum = enterMobileNum.getText().toString();
                if(Pattern.matches("^[89]\\d{7}$",receiverMobileNum))
                {
                    String postUrl = "https://pfd-server.azurewebsites.net/getAccountUsingPhoneNo";

                    JSONObject postData = new JSONObject();
                    try {
                        postData.put("phoneNo", receiverMobileNum);
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
                                    String senderAccNo = sharedPref.getString("accNo","");

                                    if (receiverName == null){
                                        receiverName = "Unknown";
                                    }

                                    Intent intent = new Intent(MobileNumberActivity.this, AmountConfirmationActivity.class);
                                    intent.putExtra("receiverName" , receiverName);
                                    intent.putExtra("receiverAccNo", receiverAccNo);
                                    intent.putExtra("senderAccNo", senderAccNo);
                                    startActivity(intent);
                                }
                                else
                                {
                                    Toast.makeText(MobileNumberActivity.this, "Invalid Account Number",Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(MobileNumberActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                            error.printStackTrace();
                        }
                    });
                    RequestQueue requestQueue = Volley.newRequestQueue(MobileNumberActivity.this);
                    requestQueue.add(jsonObjectRequest);
                }
                else
                {
                    Toast.makeText(MobileNumberActivity.this,"Please enter a valid number!",Toast.LENGTH_LONG).show();
                }
            }
        });

        //Setting up transfer option bar
        BottomNavigationView optionBar = (BottomNavigationView) findViewById(R.id.TopBar);
        int menuSize = optionBar.getMenu().size();
        for (int i = 0; i < menuSize; i++)
        {
            optionBar.getMenu().getItem(i).setChecked(false);
        }
        optionBar.getMenu().findItem(R.id.mobileTransfer).setChecked(true);
        optionBar.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.cardTransfer:
                        Intent intent = new Intent(MobileNumberActivity.this, AccountTransferActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                        break;

                    case R.id.mobileTransfer:
                        break;

                    case R.id.nricTransfer:
                        Intent b = new Intent(MobileNumberActivity.this, NricTransferActivity.class);
                        b.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(b);
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
                        Intent a = new Intent(MobileNumberActivity.this, HomeActivity.class);
                        startActivity(a);


                        break;

                    case R.id.page_2:

                        Intent intent = new Intent(MobileNumberActivity.this, MobileTransferActivity.class);
                        startActivity(intent);
                        break;

                    case R.id.page_3:
                        Intent b = new Intent(MobileNumberActivity.this, ProfileActivity.class);
                        startActivity(b);
                        break;




                }
                return false;
            }
        });
    }
}