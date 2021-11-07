package sg.edu.np.pfd_ocbc;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();

        EditText mobile = findViewById(R.id.mobile_input);



        EditText pin = findViewById(R.id.pin_input);
        pin.setTransformationMethod(PasswordTransformationMethod.getInstance());

        Button login = findViewById(R.id.login);

        mAuth.signOut();
        SharedPreferences sharedPref = getSharedPreferences("Profile", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear().apply();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mobiletxt = mobile.getText().toString();
                String pintxt = pin.getText().toString();

                if(mobiletxt.equals("") || pintxt.equals("")){
                    Toast.makeText(LoginActivity.this, "Please Complete All Required Fields",
                            Toast.LENGTH_SHORT).show();


                }
                else {
                    FirebaseUser user = mAuth.getCurrentUser();

                    mAuth.signOut();

                    String postUrl = "https://pfd-server.azurewebsites.net/getEmailUsingPhone";
                    RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);



                    JSONObject postData = new JSONObject();

                    try{
                        postData.put("phoneNo", mobiletxt);

                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }




                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, postUrl, postData, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            String email = "";
                            System.out.println(response);
                            try {
                                email = response.getString("email");

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            mAuth.signInWithEmailAndPassword(email, pintxt)
                                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                String postUrl = "https://pfd-server.azurewebsites.net/getAccountHolder";
                                                RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);
                                                // Sign in success, update UI with the signed-in user's information
                                                Log.d(TAG, "signInWithEmail:success");
                                                FirebaseUser user = mAuth.getCurrentUser();



                                                JSONObject postData = new JSONObject();




                                                user.getIdToken(true).addOnSuccessListener(new OnSuccessListener<GetTokenResult>() {
                                                    @Override
                                                    public void onSuccess(GetTokenResult result) {
                                                        String idToken = result.getToken();




                                                        try {
                                                            postData.put("uid", user.getUid());
                                                            postData.put("jwtToken", idToken );


                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }

                                                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, postUrl, postData, new Response.Listener<JSONObject>() {
                                                            @Override
                                                            public void onResponse(JSONObject response) {
                                                                System.out.println(response);
                                                                try {

                                                                    editor.putString("Name", response.getString("name"));
                                                                    editor.putString("Phone", response.getString("phoneNo"));
                                                                    editor.putString("Email", response.getString("email"));
                                                                    editor.putString("icNo", response.getString("icNo"));
                                                                    editor.putString("startDate", response.getString("startDate"));

                                                                    editor.apply();

                                                                } catch (JSONException e) {
                                                                    e.printStackTrace();
                                                                }
                                                            }
                                                        }, new Response.ErrorListener() {
                                                            @Override
                                                            public void onErrorResponse(VolleyError error) {
                                                                error.printStackTrace();

                                                            }
                                                        });

                                                        requestQueue.add(jsonObjectRequest);

                                                    }
                                                });

                                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);

                                            }
                                            else {
                                                // If sign in fails, display a message to the user.
                                                Log.w(TAG, "signInWithEmail:failure", task.getException());


                                            }
                                        }
                                    });
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                            Toast.makeText(LoginActivity.this, "Invalid Login Credentials",
                                    Toast.LENGTH_SHORT).show();
                        }


                    });


                    requestQueue.add(jsonObjectRequest);
                }

            }
        });

        Button signup = findViewById(R.id.signup);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(LoginActivity.this, OtpActivity.class);
//                intent.putExtra("situation", "signup");
//                intent.putExtra("phoneNo", "85751562");
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });


    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            currentUser.reload();
        }
    }
}