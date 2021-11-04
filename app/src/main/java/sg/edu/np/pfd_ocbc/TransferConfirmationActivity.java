package sg.edu.np.pfd_ocbc;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class TransferConfirmationActivity extends AppCompatActivity {

    TextView transferAmt, receiverMobileNum;
    ImageView confirmBtn, backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_confirmation);

        transferAmt = findViewById(R.id.transferAmt);
        receiverMobileNum = findViewById(R.id.receiverMobileNum);
        confirmBtn = findViewById(R.id.confirmBtnTransactionConfirm);
        backBtn = findViewById(R.id.backBtnTransactionConfirm);

        transferAmt.setText("S$"+ getIntent().getIntExtra("amount", 0));
        receiverMobileNum.setText(getIntent().getStringExtra("receiverMobileNum"));

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