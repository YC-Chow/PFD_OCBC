package sg.edu.np.pfd_ocbc;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Calendar;
import java.util.Date;

public class AmountConfirmationActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    EditText senderAmount;
    Button nextBtn, cancelBtn;
    TextView receiverName, recieverAccNo,senderAccNo, senderBal;
    private String receiverAccNum, senderAccNum, nameOfReceiver;
    EditText date;
    private ProgressDialog progressDialog;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amount_confirmation);

        senderAmount = findViewById(R.id.enterTransferAmt);
        date = findViewById(R.id.date);
        date.setText(LocalDateTime.now().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)));
        nextBtn = findViewById(R.id.nextBtnAmtConfirm);
        cancelBtn = findViewById(R.id.cancelBtnAmtConfirm);
        receiverName = findViewById(R.id.receiverName);
        recieverAccNo = findViewById(R.id.recieverAccNo);
        senderAccNo = findViewById(R.id.senderAccNo);
        senderBal = findViewById(R.id.senderBal);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimeDialog(date);
            }
        });

        if (user == null){
            Intent intent = new Intent(AmountConfirmationActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        receiverAccNum = getIntent().getStringExtra("receiverAccNo");
        senderAccNum = getIntent().getStringExtra("senderAccNo");
        nameOfReceiver = getIntent().getStringExtra("receiverName");
        if (nameOfReceiver == null){
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
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                try {
                    double amount = Double.parseDouble(senderAmount.getText().toString());
                    if (amount <= 0)
                    {
                        Toast.makeText(AmountConfirmationActivity.this, "Please enter a valid amount!", Toast.LENGTH_SHORT).show();
                    }
                    else if (amount > Double.parseDouble(senderBal.getText().toString())){
                        Toast.makeText(AmountConfirmationActivity.this, "Not enough balance!", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Intent intent = new Intent(AmountConfirmationActivity.this, TransferConfirmationActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        intent.putExtra("from", senderAccNum);
                        intent.putExtra("to", receiverAccNum);
                        intent.putExtra("amount", amount);
                        intent.putExtra("name", receiverName.getText().toString());


                        LocalDateTime today = LocalDateTime.now();

                        LocalDateTime when = LocalDateTime.parse(date.getText().toString(), DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT));


                        long diff = Duration.between(today, when).toHours();

                        intent.putExtra("hours", diff);
                        intent.putExtra("by", when.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)));
                        startActivity(intent);
                    }
                }catch (NumberFormatException e){
                    Toast.makeText(AmountConfirmationActivity.this, "Please enter a valid amount!", Toast.LENGTH_SHORT).show();
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

    private void showDateTimeDialog(final EditText date_time_in) {
        final Calendar calendar=Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR,year);
                calendar.set(Calendar.MONTH,month);
                calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);

                TimePickerDialog.OnTimeSetListener timeSetListener=new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
                        calendar.set(Calendar.MINUTE,minute);

                        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("d/M/yy, h:mm a");

                        date_time_in.setText(simpleDateFormat.format(calendar.getTime()));
                    }
                };

                new TimePickerDialog(AmountConfirmationActivity.this,timeSetListener,calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),false).show();
            }
        };

        new DatePickerDialog(AmountConfirmationActivity.this,dateSetListener,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();

    }
}