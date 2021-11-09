package sg.edu.np.pfd_ocbc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
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
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

public class CardTransferActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_transfer);

        EditText enterCardNum = (EditText) findViewById(R.id.enterCardNum);
        ImageView nextBtn = (ImageView) findViewById(R.id.nextBtnBankTransfer);

        mAuth = FirebaseAuth.getInstance();

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
                        Intent intent = new Intent(CardTransferActivity.this, MobileTransferActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                        break;

                    case R.id.nricTransfer:
                        Intent b = new Intent(CardTransferActivity.this, NricTransferActivity.class);
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
                        Intent a = new Intent(CardTransferActivity.this, HomeActivity.class);
                        a.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(a);

                        break;

                    case R.id.page_2:

                        break;

                    case R.id.page_3:
                        Intent b = new Intent(CardTransferActivity.this, ProfileActivity.class);
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
                String receiverCardNum = enterCardNum.getText().toString();
                if(receiverCardNum != "" && (Pattern.matches("^[4]\\d{15}$",receiverCardNum)))
                {
                    String postUrl = "https://pfd-server.azurewebsites.net/getAccount";

                    JSONObject postData = new JSONObject();
                    FirebaseUser user = mAuth.getCurrentUser();
                    user.getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                        @Override
                        public void onComplete(@NonNull Task<GetTokenResult> task) {
                            if(task.isSuccessful())
                            {
                                String token = task.getResult().getToken();
                                try
                                {
                                    postData.put("cardNo", receiverCardNum);
                                    postData.put("jwtToken", token);
                                }catch (JSONException e)
                                {
                                    e.printStackTrace();
                                }

                                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, postUrl, postData, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        try {
                                            if (response.has("cardNumber"))
                                            {
                                                String cardNumber = response.getString("cardNumber");
                                                Intent intent = new Intent(CardTransferActivity.this, AmountConfirmationActivity.class);
                                                intent.putExtra("cardNumber", cardNumber);
                                                startActivity(intent);
                                            }
                                            else 
                                            {
                                                Toast.makeText(CardTransferActivity.this, "Invalid Card Number",Toast.LENGTH_SHORT).show();
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(CardTransferActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                                        error.printStackTrace();
                                    }
                                });
                                RequestQueue requestQueue = Volley.newRequestQueue(CardTransferActivity.this);
                                requestQueue.add(jsonObjectRequest);
                            }
                        }
                    });

                }
                else
                {
                    Toast.makeText(CardTransferActivity.this, "Please enter a valid card number",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}