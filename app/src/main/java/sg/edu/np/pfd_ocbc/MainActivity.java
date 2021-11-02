package sg.edu.np.pfd_ocbc;

import static android.content.ContentValues.TAG;

import android.os.Bundle;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        EditText name = findViewById(R.id.name);
        EditText email = findViewById(R.id.email);
        EditText icno = findViewById(R.id.icno);
        EditText password= findViewById(R.id.password);

        Date currentTime = Calendar.getInstance().getTime();
        Log.d(TAG, currentTime.toString());

        Button signup = findViewById(R.id.signup);

        mAuth =FirebaseAuth.getInstance();

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               String emailtext = email.getText().toString();
               String passwordtext = password.getText().toString();
               mAuth.createUserWithEmailAndPassword(emailtext, passwordtext)
                       .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                           @Override
                           public void onComplete(@NonNull Task<AuthResult> task) {
                               if(task.isSuccessful()){

                                   FirebaseUser user = mAuth.getCurrentUser();


                                   Log.d(TAG, user.getIdToken(false).getResult().getToken());


                                   String postUrl = "https://reqres.in/api/users";
                                   RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);

                                   JSONObject postData = new JSONObject();

                                   try{
                                       postData.put("uid", user.getUid());
                                       postData.put("email", user.getEmail());
                                       postData.put("name", user.getDisplayName());
                                   }
                                   catch (JSONException e) {
                                       e.printStackTrace();
                                   }



                               }
                               else {
                                   // If sign in fails, display a message to the user.
                                   Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                   Toast.makeText(MainActivity.this, "Authentication failed.",
                                           Toast.LENGTH_SHORT).show();

                               }
                           }
                       });


            }
        });


    }
}