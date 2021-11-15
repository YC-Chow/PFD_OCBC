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
    private String receiverAccNum, senderAccNum;
    private double amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_confirmation);

        transferAmt = findViewById(R.id.transferAmt);
        receiverCardNumber = findViewById(R.id.receiverCardNo);
        confirmBtn = findViewById(R.id.confirmBtnTransactionConfirm);
        backBtn = findViewById(R.id.backBtnTransactionConfirm);
        senderCardNumber = findViewById(R.id.senderAccNo);
        receiverName = findViewById(R.id.receiverName);

        dbHandler = new DBHandler(this);
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
                Intent intent = new Intent(TransferConfirmationActivity.this, AmountConfirmationActivity.class);
                intent.putExtra("receiverCardNumber", receiverAccNum);
                intent.putExtra("senderCardNumber", senderAccNum);
                startActivity(intent);
                finish();
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
        transaction.setSenderAccNo(senderCardNumber);
        transaction.setToBankNum(receiverCardNumber);
        transaction.setTransactionAmt(amount);
        transaction.setTransactionDate(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(Calendar.getInstance().getTime()));

        dbHandler.MakeTransaction(transaction);
        return transaction;
    }

    private void MakeTransaction(RequestQueue queue, Transaction transaction)
    {
        String queryUrl = "https://pfd-server.azurewebsites.net/createTransaction";
        JSONObject postData = new JSONObject();
        try {
            postData.put("amount", transaction.getTransactionAmt());
            postData.put("from", transaction.getSenderAccNo());
            postData.put("to", transaction.getToBankNum());
        }catch (JSONException e)
        {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, queryUrl, postData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                dbHandler.DeleteTransaction(transaction);
                SuccessDialogBuilder(transaction);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                ErrorDialogBuilder(queue, transaction);
            }
        });

        RetryPolicy policy = new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(policy);
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
                Intent intent = new Intent(TransferConfirmationActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void SuccessDialogBuilder(Transaction transaction)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage("You have transferred " + transaction.getTransactionAmt() + " to " + transaction.getToBankNum());
        builder.setTitle("Transaction Success");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(TransferConfirmationActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
        builder.setNegativeButton("No", null);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();

        receiverAccNum = getIntent().getStringExtra("to");
        senderAccNum = getIntent().getStringExtra("from");
        amount = getIntent().getIntExtra("amount", 0);
        String receiveName = getIntent().getStringExtra("name");

        transferAmt.setText("S$"+ amount);
        senderCardNumber.setText(senderAccNum);
        receiverCardNumber.setText(receiverAccNum);
        receiverName.setText(receiveName);
    }
}