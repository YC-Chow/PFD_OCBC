package sg.edu.np.pfd_ocbc;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class SignupActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);


        EditText name = findViewById(R.id.name);
        EditText email = findViewById(R.id.email);
        EditText icno = findViewById(R.id.icno);
        EditText password= findViewById(R.id.password);

        LocalDate today = LocalDate.now();


        String formattedDate = today.format(DateTimeFormatter.ofPattern("dd-MM-yy"));



        Button signup = findViewById(R.id.signup);

        mAuth =FirebaseAuth.getInstance();

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               String emailtext = email.getText().toString();
               String passwordtext = password.getText().toString();
               mAuth.createUserWithEmailAndPassword(emailtext, passwordtext)
                       .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                           @Override
                           public void onComplete(@NonNull Task<AuthResult> task) {
                               if(task.isSuccessful()){

                                   FirebaseUser user = mAuth.getCurrentUser();


                                   Log.d(TAG, user.getIdToken(false).getResult().getToken());


                                   String postUrl = "https://pfd-server.azurewebsites.net/createAccount";
                                   RequestQueue requestQueue = Volley.newRequestQueue(SignupActivity.this);

                                   LocalDate today = LocalDate.now();
                                   String formattedDate = today.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT));

                                   JSONObject postData = new JSONObject();

                                   try{
                                       postData.put("uid", user.getUid());
                                       postData.put("email", user.getEmail());
                                       postData.put("name", name.getText().toString());
                                       postData.put("startDate", formattedDate);
                                       postData.put("icNo", icno.getText().toString());
                                       postData.put("expiryDate", "1/2/3");
                                       postData.put("cvv", "123");
                                       postData.put("jwtToken", user.getIdToken(false).getResult().getToken() );
                                   }
                                   catch (JSONException e) {
                                       e.printStackTrace();
                                   }


                                   JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, postUrl, postData, new Response.Listener<JSONObject>() {
                                       @Override
                                       public void onResponse(JSONObject response) {
                                           System.out.println(response);
                                       }
                                   }, new Response.ErrorListener() {
                                       @Override
                                       public void onErrorResponse(VolleyError error) {
                                           error.printStackTrace();
                                       }
                                   });

                                   requestQueue.add(jsonObjectRequest);

                                   Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                                   startActivity(intent);



                               }
                               else {
                                   // If sign in fails, display a message to the user.
                                   Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                   Toast.makeText(SignupActivity.this, "Authentication failed.",
                                           Toast.LENGTH_SHORT).show();

                               }
                           }
                       });


            }
        });
    }
}