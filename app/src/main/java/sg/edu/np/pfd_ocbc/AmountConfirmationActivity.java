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
    EditText senderAmount;
    ImageView nextBtn, cancelBtn;
    TextView receiverName, senderAccNo, senderBal;
    private String receiverAccNum, senderAccNum, token;

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
        FirebaseUser user = mAuth.getCurrentUser();

        receiverAccNum = getIntent().getStringExtra("receiverCardNumber");
        senderAccNum = getIntent().getStringExtra("senderCardNumber");


        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AmountConfirmationActivity.this, MobileNumberActivity.class);
                startActivity(intent);
                finish();
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int amount = Integer.parseInt(senderAmount.getText().toString());
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
                    startActivity(intent);
                }
            }
        });

        //getting receiver information
        RequestQueue requestQueue = Volley.newRequestQueue(AmountConfirmationActivity.this);

        user.getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
            @Override
            public void onComplete(@NonNull Task<GetTokenResult> task) {
                if(task.isSuccessful())
                {
                    token = task.getResult().getToken();

                    setReceiverName(requestQueue, receiverAccNum);
                    setSenderInfo(requestQueue, senderAccNum);

                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();



        //getting receiver information


    }

    private void setReceiverName(RequestQueue queue, String accNo)
    {
        String requestUrl = "https://pfd-server.azurewebsites.net/getAccount";
        JSONObject postData = new JSONObject();
        try {
            postData.put("accNo", accNo);
            postData.put("jwtToken",token);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, requestUrl, postData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String responseReceiverName = response.getString("name");
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

    private void setSenderInfo(RequestQueue queue, String accNo)
    {
        String requestUrl = "https://pfd-server.azurewebsites.net/getAccount";
        JSONObject postData = new JSONObject();
        try {
            postData.put("accNo", accNo);
            postData.put("jwtToken", token);
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