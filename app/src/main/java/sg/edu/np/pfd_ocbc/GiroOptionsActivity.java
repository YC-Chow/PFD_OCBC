package sg.edu.np.pfd_ocbc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class GiroOptionsActivity extends AppCompatActivity {

    ConstraintLayout giroAcceptedButton;
    ConstraintLayout giroPendingButton;
    ImageButton back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_giro_options);

        /*giroAcceptedButton = findViewById(R.id.giroCheckAcceptedButton);
        giroPendingButton = findViewById(R.id.giroCheckPendingButton);
        back = findViewById(R.id.giroOptBack);*/

        //Setting up bottom nav bar
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        int size = navigation.getMenu().size();
        for (int i = 0; i < size; i++) {
            navigation.getMenu().getItem(i).setChecked(false);
        }
        navigation.getMenu().findItem(R.id.page_3).setChecked(true);
        navigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {


                    case R.id.page_1:
                        Intent b = new Intent(GiroOptionsActivity.this, HomeActivity.class);
                        b.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(b);
                        break;

                    case R.id.page_2:
                        Intent a = new Intent(GiroOptionsActivity.this, AccountTransferActivity.class);
                        a.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(a);
                        break;

                    case R.id.page_3:
                        break;

                }
                return false;
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GiroOptionsActivity.this, ProfileActivity.class);
                startActivity(intent);
                finish();
            }
        });

        giroAcceptedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GiroOptionsActivity.this, GiroListActivity.class);
                intent.putExtra("Mode" , "Accept");
                startActivity(intent);
            }
        });

        giroPendingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GiroOptionsActivity.this, GiroListActivity.class);
                intent.putExtra("Mode" , "Pending");
                startActivity(intent);
            }
        });
    }
}