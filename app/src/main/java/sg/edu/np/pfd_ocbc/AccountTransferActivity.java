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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class AccountTransferActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_transfer);

        EditText enterAccNum = (EditText) findViewById(R.id.enterCardNum);
        ImageView nextBtn = (ImageView) findViewById(R.id.nextBtnBankTransfer);
        sharedPref = getSharedPreferences("MySharedPref", MODE_PRIVATE);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null){
            Intent intent = new Intent(AccountTransferActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        String senderAccNo = sharedPref.getString("accNo","");

        //Setting up transfer option bar
        BottomNavigationView optionBar = (BottomNavigationView) findViewById(R.id.TopBar);
        int menuSize = optionBar.getMenu().size();
        for (int i = 0; i < menuSize; i++)
        {
            optionBar.getMenu().getItem(i).setChecked(false);
        }
        optionBar.getMenu().findItem(R.id.cardTransfer).setChecked(true);
        optionBar.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.cardTransfer:
                        break;

                    case R.id.mobileTransfer:
                        Intent intent = new Intent(AccountTransferActivity.this, MobileNumberActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                        break;

                    case R.id.nricTransfer:
                        Intent b = new Intent(AccountTransferActivity.this, NricTransferActivity.class);
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
                        Intent a = new Intent(AccountTransferActivity.this, HomeActivity.class);
                        a.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(a);

                        break;

                    case R.id.page_2:

                        break;

                    case R.id.page_3:
                        Intent b = new Intent(AccountTransferActivity.this, ProfileActivity.class);
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
                String receiverCardNum = enterAccNum.getText().toString();
                if(receiverCardNum != "")
                {
                    String postUrl = "https://pfd-server.azurewebsites.net/getAccountUsingAccNo";

                    JSONObject postData = new JSONObject();
                    try {
                        postData.put("accNo", receiverCardNum);
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
                                    if (receiverName == null){
                                        receiverName = "Unknown";
                                    }
                                    Intent intent = new Intent(AccountTransferActivity.this, AmountConfirmationActivity.class);
                                    intent.putExtra("receiverName" , receiverName);
                                    intent.putExtra("receiverAccNo", receiverAccNo);
                                    intent.putExtra("senderAccNo", senderAccNo);
                                    startActivity(intent);
                                }
                                else
                                {
                                    Toast.makeText(AccountTransferActivity.this, "Invalid Account Number",Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(AccountTransferActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                            error.printStackTrace();
                        }
                    });
                    RequestQueue requestQueue = Volley.newRequestQueue(AccountTransferActivity.this);
                    requestQueue.add(jsonObjectRequest);

                }
                else
                {
                    Toast.makeText(AccountTransferActivity.this, "Please enter a valid account number",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}