package sg.edu.np.pfd_ocbc;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.RequestQueue;

public class GiroDetailsActivity extends AppCompatActivity {

    ImageButton back;
    Button cancelBtn;
    TextView bizName, descriptionBox;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_giro_details);

        back = findViewById(R.id.giroOptBack);
        bizName = findViewById(R.id.orgName);
        descriptionBox = findViewById(R.id.giroDescription);
        cancelBtn = findViewById(R.id.giroDetailsCancelBtn);

        Giro giro = new Giro(GiroDetailsActivity.this);
        giro.setGiro_id(getIntent().getStringExtra("giroID"));
        giro.setGiro_acc_no(getIntent().getStringExtra("giroAcc"));
        bizName.setText(getIntent().getStringExtra("bizName"));
        descriptionBox.setText(getIntent().getStringExtra("ref_no"));



        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GiroDetailsActivity.this, GiroListActivity.class);
                startActivity(intent);
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // insert giro cancel function
                AlertDialog.Builder builder = new AlertDialog.Builder(GiroDetailsActivity.this);
                builder.setTitle("Cancel Giro");
                builder.setMessage("Are you sure you want to cancel this Giro");
                builder.setNegativeButton("No", null);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        giro.CancelGiro();
                    }
                });

                builder.create().show();
            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(GiroDetailsActivity.this, GiroListActivity.class);
        startActivity(intent);
    }
}