package sg.edu.np.pfd_ocbc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();

        SharedPreferences sharedPreferences = getSharedPreferences("AccountHolder", MODE_PRIVATE);

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
                        Intent a = new Intent(ProfileActivity.this, HomeActivity.class);
                        a.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(a);


                        break;

                    case R.id.page_2:
                        Intent b = new Intent(ProfileActivity.this, AccountTransferActivity.class);
                        b.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(b);
                        break;

                    case R.id.page_3:

                        break;




                }
                return false;
            }
        });

        ConstraintLayout settings = findViewById(R.id.settingsContainer);

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, SettingOtpActivity.class);
                intent.putExtra("situation", "settings");
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });

        TextView name = findViewById(R.id.username);
        TextView phone = findViewById(R.id.mobileno);
        TextView email = findViewById(R.id.profileemail);



        String username = sharedPreferences.getString("name", "");
        String userphone = sharedPreferences.getString("phoneno", "");
        String useremail = sharedPreferences.getString("email", "");

        name.setText(username);
        phone.setText(userphone);
        email.setText(useremail);
        Log.v("hi", userphone);

        ConstraintLayout signout = findViewById(R.id.logoutContainer);

        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });







    }

    @Override
    public void onBackPressed() {
        return;
    }



}