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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);


        EditText cardNo = findViewById(R.id.cardNo);
        EditText sixpin = findViewById(R.id.sixpin);
        EditText name = findViewById(R.id.name);
        EditText icNo= findViewById(R.id.icNo);
        EditText phoneNo = findViewById(R.id.phoneNo);
        EditText email = findViewById(R.id.email);
        EditText password= findViewById(R.id.password);

        Button back = findViewById(R.id.signup2);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });

        LocalDate today = LocalDate.now();


        String formattedDate = today.format(DateTimeFormatter.ofPattern("dd-MM-yy"));



        Button signup = findViewById(R.id.signup);

        mAuth =FirebaseAuth.getInstance();

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("validateAccount", "click");
               String cardNoText = cardNo.getText().toString();
               String sixpinText = sixpin.getText().toString();
               String nameText = name.getText().toString();
               String icNoText = icNo.getText().toString();
               String phoneNoText = phoneNo.getText().toString();
               String emailText = email.getText().toString();
               String passwordText = password.getText().toString();
//                String cardNoText = "71726994";
//                String sixpinText = "417369";
//                String nameText = "Wen Kang";
//                String icNoText = "T12345678Z";
//                String phoneNoText = "85751563";
//                String emailText = "wk123@gmail.com";
//                String passwordText = "123456";
                Log.d("validateAccount", "cardNoText"+cardNoText.equals(""));
               if (cardNoText.equals("")||sixpinText.equals("")||nameText.equals("")||icNoText.equals("")||phoneNoText.equals("")||emailText.equals("")||passwordText.equals("")){
                   Toast.makeText(SignupActivity.this, "All inputs required",
                           Toast.LENGTH_SHORT).show();
               }else if (passwordText.length() <6){
                   Toast.makeText(SignupActivity.this, "Password too weak",
                           Toast.LENGTH_SHORT).show();
               } else{
                   checkAccountStatus(cardNoText, sixpinText, icNoText, phoneNoText, nameText, emailText, passwordText);
               }

//               mAuth.createUserWithEmailAndPassword(emailtext, passwordtext)
//                       .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
//                           @Override
//                           public void onComplete(@NonNull Task<AuthResult> task) {
//                               if(task.isSuccessful()){
//
//                                   FirebaseUser user = mAuth.getCurrentUser();
//
//
//                                   Log.d(TAG, user.getIdToken(false).getResult().getToken());
//
//
//                                   String postUrl = "https://pfd-server.azurewebsites.net/createAccount";
//                                   RequestQueue requestQueue = Volley.newRequestQueue(SignupActivity.this);
//
//                                   LocalDate today = LocalDate.now();
//                                   String formattedDate = today.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT));
//
//                                   JSONObject postData = new JSONObject();
//
//                                   try{
//                                       postData.put("uid", user.getUid());
//                                       postData.put("email", user.getEmail());
//                                       postData.put("name", name.getText().toString());
//                                       postData.put("startDate", formattedDate);
//                                       postData.put("icNo", icno.getText().toString());
//                                       postData.put("expiryDate", "1/2/3");
//                                       postData.put("cvv", "123");
//                                       postData.put("jwtToken", user.getIdToken(true).getResult().getToken() );
//                                   }
//                                   catch (JSONException e) {
//                                       e.printStackTrace();
//                                   }
//
//
//                                   JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, postUrl, postData, new Response.Listener<JSONObject>() {
//                                       @Override
//                                       public void onResponse(JSONObject response) {
//                                           System.out.println(response);
//                                       }
//                                   }, new Response.ErrorListener() {
//                                       @Override
//                                       public void onErrorResponse(VolleyError error) {
//                                           error.printStackTrace();
//                                       }
//                                   });
//
//                                   requestQueue.add(jsonObjectRequest);
//
//                                   Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
//                                   startActivity(intent);
//
//
//
//                               }
//                               else {
//                                   // If sign in fails, display a message to the user.
//                                   Log.w(TAG, "createUserWithEmail:failure", task.getException());
//                                   Toast.makeText(SignupActivity.this, "Authentication failed.",
//                                           Toast.LENGTH_SHORT).show();
//
//                               }
//                           }
//                       });


            }
        });
    }
    void checkAccountStatus(String cardNo, String sixpin, String icNo, String phoneNo, String name, String email, String password){
        String postUrl = "https://pfd-server.azurewebsites.net/validateAccount";
        JSONObject postData = new JSONObject();
        try{
            postData.put("last8digits", cardNo);
            postData.put("6pin", sixpin);
            postData.put("icNo", icNo);
            postData.put("phoneNo", phoneNo);
            postData.put("email", email);
        }catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("validateAccount", "sending");
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, postUrl, postData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("validateAccount", "onResponse: "+response.toString());
                Intent intent = new Intent(SignupActivity.this, OtpActivity.class);
                intent.putExtra("phoneNo", phoneNo);
                intent.putExtra("email", email);
                intent.putExtra("name", name);
                intent.putExtra("icNo", icNo);
                intent.putExtra("password", password);
                intent.putExtra("situation", "signup");
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String body;
                //get status code here
                String statusCode = String.valueOf(error.networkResponse.statusCode);
                //get response body and parse with appropriate encoding
                if(error.networkResponse.data!=null) {
                    try {
                        body = new String(error.networkResponse.data,"UTF-8");
                        Map<String, String> hashMap = new Gson().fromJson(
                                body.toString(), new TypeToken<HashMap<String, Object>>() {}.getType()
                        );
                        String myError = hashMap.get("error_message");
                        Log.d("ERRRRRRRORRR", "onErrorResponse: "+myError);
                        if (myError.equals("NRIC, phone number or email already in System")){
                            Toast.makeText(SignupActivity.this, "NRIC, phone number or email already in System",
                                    Toast.LENGTH_SHORT).show();
                        }else if(myError.equals("Card number or pass code is wrong")){
                            Toast.makeText(SignupActivity.this, "Card number or pass code is wrong",
                                    Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(SignupActivity.this, "Authenthication Error",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } catch (UnsupportedEncodingException e) {
                        Toast.makeText(SignupActivity.this, "Authenthication Error",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(SignupActivity.this);
        requestQueue.add(jsonObjectRequest);
    }
}