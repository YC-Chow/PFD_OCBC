package sg.edu.np.pfd_ocbc;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class TransferConfirmationActivity extends AppCompatActivity {

    TextView transferAmt, receiverCardNumber, senderCardNumber;
    ImageView confirmBtn, backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_confirmation);

        transferAmt = findViewById(R.id.transferAmt);
        receiverCardNumber = findViewById(R.id.receiverCardNo);
        confirmBtn = findViewById(R.id.confirmBtnTransactionConfirm);
        backBtn = findViewById(R.id.backBtnTransactionConfirm);
        senderCardNumber = findViewById(R.id.senderCardNo);



        String receiverCardNo = getIntent().getStringExtra("to");
        String senderCardNo = getIntent().getStringExtra("from");
        int amount = getIntent().getIntExtra("amount", 0);

        transferAmt.setText("S$"+ amount);
        senderCardNumber.setText(senderCardNo);
        receiverCardNumber.setText(receiverCardNo);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TransferConfirmationActivity.this, AmountConfirmationActivity.class);
                intent.putExtra("receiverMobileNum", getIntent().getStringExtra("receiverMobileNum"));
                startActivity(intent);
                finish();
            }
        });

    }
}