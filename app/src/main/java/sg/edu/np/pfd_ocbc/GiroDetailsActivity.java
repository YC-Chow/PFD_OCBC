package sg.edu.np.pfd_ocbc;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.RequestQueue;

public class GiroDetailsActivity extends AppCompatActivity {

    ImageButton back;
    Button nextBtn, cancelBtn;
    TextView bizName, descriptionBox;
    private String Mode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_giro_details);

        back = findViewById(R.id.giroOptBack);
        Mode = getIntent().getStringExtra("Mode");
        if(Mode.equals("Pending")){
            Giro giro = new Giro(this);
            giro.setGiro_acc_no(getIntent().getStringExtra("giroAcc"));
            giro.setGiro_id(getIntent().getStringExtra("giroID"));
            nextBtn = findViewById(R.id.giroDetailsNextBtn);
            cancelBtn = findViewById(R.id.giroDetailsCancelBtn);
            nextBtn.setVisibility(View.VISIBLE);
            cancelBtn.setVisibility(View.VISIBLE);

            nextBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    giro.GiroAcceptance(true);

                }
            });

            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    giro.GiroAcceptance(false);
                }
            });
        }

        bizName = findViewById(R.id.orgName);
        descriptionBox = findViewById(R.id.giroDescription);

        bizName.setText(getIntent().getStringExtra("bizName"));
        descriptionBox.setText(getIntent().getStringExtra("description"));



        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GiroDetailsActivity.this, GiroListActivity.class);
                intent.putExtra("Mode" , Mode);
                startActivity(intent);
            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(GiroDetailsActivity.this, GiroListActivity.class);
        intent.putExtra("Mode" , Mode);
        startActivity(intent);
    }
}