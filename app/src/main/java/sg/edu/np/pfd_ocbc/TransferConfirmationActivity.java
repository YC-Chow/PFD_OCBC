package sg.edu.np.pfd_ocbc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.VoiceInteractor;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TransferConfirmationActivity extends AppCompatActivity {

    private TextView transferAmt, receiverCardNumber, senderCardNumber, receiverName;
    ImageView confirmBtn, backBtn;
    DBHandler dbHandler;
    private FirebaseAuth mAuth;
    private String receiverAccNum, senderAccNum, receiveName;
    private double amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_confirmation);

        //finding layout things
        transferAmt = findViewById(R.id.transferAmt);
        receiverCardNumber = findViewById(R.id.receiverCardNo);
        confirmBtn = findViewById(R.id.confirmBtnTransactionConfirm);
        backBtn = findViewById(R.id.backBtnTransactionConfirm);
        senderCardNumber = findViewById(R.id.senderAccNo);
        receiverName = findViewById(R.id.receiverName);

        //getting values
        dbHandler = new DBHandler(this);
        receiverAccNum = getIntent().getStringExtra("to");
        senderAccNum = getIntent().getStringExtra("from");
        amount = getIntent().getDoubleExtra("amount", 0);
        receiveName = getIntent().getStringExtra("name");

        //setting info
        transferAmt.setText("S$"+ String.format("%.2f", amount));
        senderCardNumber.setText(senderAccNum);
        receiverCardNumber.setText(receiverAccNum);
        receiverName.setText(receiveName);


        //kicks users out if not sign in
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null){
            Intent intent = new Intent(TransferConfirmationActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }



        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getIntent().getStringExtra("uniqueCode") != ""){
                    Intent intent = new Intent(TransferConfirmationActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    Intent intent = new Intent(TransferConfirmationActivity.this, AmountConfirmationActivity.class);
                    intent.putExtra("receiverCardNumber", receiverAccNum);
                    intent.putExtra("senderCardNumber", senderAccNum);
                    startActivity(intent);
                    finish();
                }
            }
        });

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Transaction transaction = SetTransactionIntoDB(senderAccNum, receiverAccNum, amount);
                RequestQueue queue = Volley.newRequestQueue(TransferConfirmationActivity.this);

                MakeTransaction(queue, transaction);
            }
        });

    }

    private Transaction SetTransactionIntoDB(String senderCardNumber, String receiverCardNumber, double amount)
    {
        Transaction transaction = new Transaction();
        if (getIntent().getStringExtra("uniqueCode") != ""){
            transaction.setUniqueCode(getIntent().getStringExtra("uniqueCode"));
        }
        else {
            transaction.setUniqueCode((new RandomString()).nextString());
        }
        transaction.setSenderAccNo(senderCardNumber);
        transaction.setRecipientAccNo(receiverCardNumber);
        transaction.setTransactionAmt(amount);
        transaction.setRecipientName(receiveName);

        dbHandler.MakeTransaction(transaction);
        return transaction;
    }

    private void MakeTransaction(RequestQueue queue, Transaction transaction)
    {
        String queryUrl = "https://pfd-server.azurewebsites.net/createTransaction";
        JSONObject postData = new JSONObject();
        try {
            postData.put("uniqueKey", transaction.getUniqueCode());
            postData.put("amount", String.format("%.2f", transaction.getTransactionAmt()));
            postData.put("from", transaction.getSenderAccNo());
            postData.put("to", transaction.getRecipientAccNo());
        }catch (JSONException e)
        {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, queryUrl, postData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (response.has("success")){
                    dbHandler.DeleteTransaction(transaction.getUniqueCode());
                    SuccessDialogBuilder(transaction);
                }
                else {
                    try {
                        Toast.makeText(TransferConfirmationActivity.this, response.getString("error_message"),Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(TransferConfirmationActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                ErrorDialogBuilder(queue, transaction);
            }
        });

        queue.add(jsonObjectRequest);

    }

    private void ErrorDialogBuilder(RequestQueue queue, Transaction transaction) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage("Do you want to try again");
        builder.setTitle("Transaction Failed");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MakeTransaction(queue, transaction);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dbHandler.DeleteTransaction(transaction.getUniqueCode());
                Intent intent = new Intent(TransferConfirmationActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void SuccessDialogBuilder(Transaction transaction)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage("You have transferred S$" + String.format("%.2f", transaction.getTransactionAmt()) + " to " + receiveName);
        builder.setTitle("Transaction Success");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(TransferConfirmationActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();


    }
}