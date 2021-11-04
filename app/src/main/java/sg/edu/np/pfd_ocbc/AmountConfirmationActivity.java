package sg.edu.np.pfd_ocbc;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class AmountConfirmationActivity extends AppCompatActivity {

    TextView receiverMobileNum;
    EditText senderAmount;
    ImageView nextBtn, cancelBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amount_confirmation);

        receiverMobileNum = findViewById(R.id.receiverMobileNum);
        senderAmount = findViewById(R.id.senderAmt);
        nextBtn = findViewById(R.id.nextBtnAmtConfirm);
        cancelBtn = findViewById(R.id.cancelBtnAmtConfirm);

        String mobileNum = getIntent().getStringExtra("receiverMobileNum");
        receiverMobileNum.setText("+65 " + mobileNum);

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
                    intent.putExtra("amount", amount);
                    intent.putExtra("receiverMobileNum", mobileNum);
                    startActivity(intent);
                }
            }
        });
    }
}