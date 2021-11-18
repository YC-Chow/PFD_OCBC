package sg.edu.np.pfd_ocbc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
    Button nextBtn, cancelBtn;
    TextView receiverName, recieverAccNo,senderAccNo, senderBal;
    private String receiverAccNum, senderAccNum, nameOfReceiver;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amount_confirmation);

        senderAmount = findViewById(R.id.enterTransferAmt);
        nextBtn = findViewById(R.id.nextBtnAmtConfirm);
        cancelBtn = findViewById(R.id.cancelBtnAmtConfirm);
        receiverName = findViewById(R.id.receiverName);
        recieverAccNo = findViewById(R.id.recieverAccNo);
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
        if (nameOfReceiver.isEmpty()){
            nameOfReceiver = "Unknown";
        }


        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AmountConfirmationActivity.this, AccountTransferActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                finish();
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double amount = Double.parseDouble(senderAmount.getText().toString());
                if (amount <= 0)
                {
                    Toast.makeText(AmountConfirmationActivity.this, "Please enter a valid amount!", Toast.LENGTH_SHORT).show();
                }
                else if (amount > Double.parseDouble(senderBal.getText().toString())){
                    Toast.makeText(AmountConfirmationActivity.this, "No enough balance!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Intent intent = new Intent(AmountConfirmationActivity.this, TransferConfirmationActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
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
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Please wait");
        progressDialog.show();

        //getting receiver information
        RequestQueue requestQueue = Volley.newRequestQueue(AmountConfirmationActivity.this);

        setReceiverName(requestQueue, receiverAccNum);
        setSenderInfo(requestQueue, senderAccNum);


    }

    private void setReceiverName(RequestQueue queue, String accNo)
    {
        receiverName.setText(nameOfReceiver);
        recieverAccNo.setText(receiverAccNum);
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
                    double responseSenderBal = response.getDouble("balance");
                    senderAccNo.setText(senderAccNum);
                    senderBal.setText(String.format("%.2f", responseSenderBal));
                    progressDialog.dismiss();
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