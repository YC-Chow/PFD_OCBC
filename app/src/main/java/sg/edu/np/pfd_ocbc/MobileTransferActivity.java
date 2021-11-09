package sg.edu.np.pfd_ocbc;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;


public class MobileTransferActivity extends AppCompatActivity {

    ImageView transferBtn;
    TextView customNum;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_transfer);
        customNum = findViewById(R.id.customNumber);

        customNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MobileTransferActivity.this, MobileNumberActivity.class);
                startActivity(intent);
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
                        Intent intent = new Intent(MobileTransferActivity.this, CardTransferActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                        break;

                    case R.id.mobileTransfer:
                        break;

                    case R.id.nricTransfer:
                        Intent b = new Intent(MobileTransferActivity.this, NricTransferActivity.class);
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
                        Intent a = new Intent(MobileTransferActivity.this, HomeActivity.class);
                        a.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(a);


                        break;

                    case R.id.page_2:

                        break;

                    case R.id.page_3:
                        Intent b = new Intent(MobileTransferActivity.this, ProfileActivity.class);
                        b.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(b);
                        break;




                }
                return false;
            }
        });

    }
}