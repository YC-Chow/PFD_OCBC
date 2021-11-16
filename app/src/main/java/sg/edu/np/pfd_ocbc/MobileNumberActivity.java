package sg.edu.np.pfd_ocbc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.regex.Pattern;

public class MobileNumberActivity extends AppCompatActivity {

    EditText enterMobileNum;
    ImageView nextBtn, backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_number);

        enterMobileNum = findViewById(R.id.customMobileNum);
        nextBtn = findViewById(R.id.nextBtnMobileNumber);
        backBtn = findViewById(R.id.backBtnMobileNumber);
        backBtn.setVisibility(View.GONE);

//        backBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MobileNumberActivity.this, HomeActivity.class);
//                startActivity(intent);
//                finish();
//            }
//        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String receiverMobileNum = enterMobileNum.getText().toString();
                if(Pattern.matches("^[89]\\d{7}$",receiverMobileNum))
                {
                    Intent intent = new Intent(MobileNumberActivity.this, AmountConfirmationActivity.class);
                    intent.putExtra("receiverMobileNum", receiverMobileNum);
                    startActivity(intent);
                }
                else
                {
                    Toast.makeText(MobileNumberActivity.this,"Please enter a valid number!",Toast.LENGTH_LONG).show();
                }
            }
        });

        //Setting up transfer option bar
        BottomNavigationView optionBar = (BottomNavigationView) findViewById(R.id.TopBar);
        int menuSize = optionBar.getMenu().size();
        for (int i = 0; i < menuSize; i++)
        {
            optionBar.getMenu().getItem(i).setChecked(false);
        }
        optionBar.getMenu().findItem(R.id.mobileTransfer).setChecked(true);
        optionBar.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.cardTransfer:
                        Intent intent = new Intent(MobileNumberActivity.this, AccountTransferActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                        break;

                    case R.id.mobileTransfer:
                        break;

                    case R.id.nricTransfer:
                        Intent b = new Intent(MobileNumberActivity.this, NricTransferActivity.class);
                        b.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(b);
                        break;
                }
                return false;
            }
        });

        //Setting up bottom nav bar
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        int size = navigation.getMenu().size();
        for (int i = 0; i < size; i++) {
            navigation.getMenu().getItem(i).setChecked(false);
        }
        navigation.getMenu().findItem(R.id.page_2).setChecked(true);
        navigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {


                    case R.id.page_1:
                        Intent a = new Intent(MobileNumberActivity.this, HomeActivity.class);
                        startActivity(a);


                        break;

                    case R.id.page_2:

                        Intent intent = new Intent(MobileNumberActivity.this, MobileTransferActivity.class);
                        startActivity(intent);
                        break;

                    case R.id.page_3:
                        Intent b = new Intent(MobileNumberActivity.this, ProfileActivity.class);
                        startActivity(b);
                        break;




                }
                return false;
            }
        });
    }
}