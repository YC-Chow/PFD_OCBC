package sg.edu.np.pfd_ocbc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

import org.json.JSONException;
import org.json.JSONObject;

public class AmountConfirmationActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    EditText senderAmount;
    ImageView nextBtn, cancelBtn;
    TextView receiverName, senderAccNo, senderBal;
    private String receiverAccNum, senderAccNum, nameOfReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amount_confirmation);

        senderAmount = findViewById(R.id.enterTransferAmt);
        nextBtn = findViewById(R.id.nextBtnAmtConfirm);
        cancelBtn = findViewById(R.id.cancelBtnAmtConfirm);
        receiverName = findViewById(R.id.receiverName);
        senderAccNo = findViewById(R.id.senderAccNo);
        senderBal = findViewById(R.id.senderBal);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        if (user == null){
            Intent intent = new Intent(AmountConfirmationActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        receiverAccNum = getIntent().getStringExtra("receiverAccNo");
        senderAccNum = getIntent().getStringExtra("senderAccNo");
        nameOfReceiver = getIntent().getStringExtra("receiverName");


        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AmountConfirmationActivity.this, AccountTransferActivity.class);
                startActivity(intent);
                finish();
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double amount = Integer.parseInt(senderAmount.getText().toString());
                if (amount <= 0)
                {
                    Toast.makeText(AmountConfirmationActivity.this, "Please enter a valid amount!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Intent intent = new Intent(AmountConfirmationActivity.this, TransferConfirmationActivity.class);
                    intent.putExtra("from", senderAccNum);
                    intent.putExtra("to", receiverAccNum);
                    intent.putExtra("amount", amount);
                    intent.putExtra("name", receiverName.getText().toString());
                    startActivity(intent);
                }
            }
        });



    }

    @Override
    protected void onStart() {
        super.onStart();
        //getting receiver information
        RequestQueue requestQueue = Volley.newRequestQueue(AmountConfirmationActivity.this);

        setReceiverName(requestQueue, receiverAccNum);
        setSenderInfo(requestQueue, senderAccNum);
    }

    private void setReceiverName(RequestQueue queue, String accNo)
    {
        if (nameOfReceiver == "Unknown")
        {
            receiverName.setText(receiverAccNum);
        }
        else
        {
            String requestUrl = "https://pfd-server.azurewebsites.net/getAccountUsingAccNo";
            JSONObject postData = new JSONObject();
            try {
                postData.put("accNo", accNo);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, requestUrl, postData, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String responseReceiverName = response.getString("acc_name");
                        Log.v("AmountConfirmation", responseReceiverName);
                        receiverName.setText(responseReceiverName);
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

            queue.add(request);
        }

    }

    private void setSenderInfo(RequestQueue queue, String accNo)
    {
        String requestUrl = "https://pfd-server.azurewebsites.net/getAccountUsingAccNo";
        JSONObject postData = new JSONObject();
        try {
            postData.put("accNo", accNo);
        }catch (JSONException e)
        {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, requestUrl, postData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    int responseSenderBal = response.getInt("balance");
                    senderAccNo.setText(senderAccNum);
                    senderBal.setText(String.valueOf(responseSenderBal));
                }catch (JSONException e)
                {
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