package sg.edu.np.pfd_ocbc;

import static android.content.ContentValues.TAG;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;


import android.os.Build;
import android.os.Bundle;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.security.keystore.UserNotAuthenticatedException;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;


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

import java.io.IOException;
import java.nio.charset.Charset;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.concurrent.Executor;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;


public class LoginActivity extends AppCompatActivity {

    private static final int VALIDITY_DURATION_SECONDS = 30;
    private static final int ALLOWED_AUTHENTICATORS = 0;
    private FirebaseAuth mAuth;
    private KeyguardManager mKeyguardManager;
    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;




    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();

        EditText mobile = findViewById(R.id.mobile_input);
        //Clear account info
        SharedPreferences mysharedPref = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        SharedPreferences.Editor myeditor = mysharedPref.edit();
        myeditor.putBoolean("firsttime", true);
        myeditor.apply();



        EditText pin = findViewById(R.id.pin_input);
        pin.setTransformationMethod(PasswordTransformationMethod.getInstance());

        Button login = findViewById(R.id.login);


        if(mAuth.getCurrentUser() != null){
            mKeyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);

            executor = ContextCompat.getMainExecutor(this);
            biometricPrompt = new BiometricPrompt(LoginActivity.this,
                    executor, new BiometricPrompt.AuthenticationCallback() {
                @Override
                public void onAuthenticationError(int errorCode,
                                                  @NonNull CharSequence errString) {
                    super.onAuthenticationError(errorCode, errString);

                }

                @Override
                public void onAuthenticationSucceeded(
                        @NonNull BiometricPrompt.AuthenticationResult result) {
                    super.onAuthenticationSucceeded(result);
                    Toast.makeText(getApplicationContext(),
                            "Authentication succeeded!", Toast.LENGTH_SHORT).show();
                    afterlogin();
                }

                @Override
                public void onAuthenticationFailed() {
                    super.onAuthenticationFailed();
                    Toast.makeText(getApplicationContext(), "Authentication failed",
                            Toast.LENGTH_SHORT)
                            .show();
                }
            });

            promptInfo = new BiometricPrompt.PromptInfo.Builder()
                    .setTitle("Biometric login")
                    .setSubtitle("Log in using your biometric credential or phone pin")
                    .setDeviceCredentialAllowed(true)
                    .build();



            biometricPrompt.authenticate(promptInfo);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                try {
                    generateSecretKey(new KeyGenParameterSpec.Builder(
                            "KeyName",
                            KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                            .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                            .setUserAuthenticationRequired(true)
                            .setUserAuthenticationParameters(VALIDITY_DURATION_SECONDS,
                                    KeyProperties.AUTH_DEVICE_CREDENTIAL)
                            .build());
                } catch (NoSuchProviderException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (InvalidAlgorithmParameterException e) {
                    e.printStackTrace();
                }
            }

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                KeyGenParameterSpec authPerOpKeyGenParameterSpec =
                        new KeyGenParameterSpec.Builder("myKeystoreAlias", KeyProperties.PURPOSE_ENCRYPT)
                                // Accept either a biometric credential or a device credential.
                                // To accept only one type of credential, include only that type as the
                                // second argument.
                                .setUserAuthenticationParameters(0 /* duration */,
                                        KeyProperties.AUTH_BIOMETRIC_STRONG |
                                                KeyProperties.AUTH_DEVICE_CREDENTIAL)
                                .build();
            }
        }


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
                                                afterlogin();


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

        TextView signup = findViewById(R.id.signup);

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
        //Clear account holder info
        SharedPreferences sharedPref = getSharedPreferences("AccountHolder", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear().apply();

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            currentUser.reload();
        }
    }

    private void afterlogin(){
        SharedPreferences sharedPreferences = getSharedPreferences("AccountHolder", MODE_PRIVATE);

        //Log.v("uid is:" ,user.getUid());
        String postUrlAccountHolder = "https://pfd-server.azurewebsites.net/getAccountHolderUsingUid";

        JSONObject postData = new JSONObject();

        FirebaseUser user = mAuth.getCurrentUser();

        try{
            postData.put("uid", user.getUid());
        }catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, postUrlAccountHolder, postData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //Log.d("lolza",response.toString());
                try {
                    String phoneno = response.getString("phone_no");
                    String holdername = response.getString("name");
                    String email = response.getString("email");
                    String tele = "";
                    if(response.getString("telegram_id") != null){
                        tele = response.getString("telegram_id");
                    }


                    // Creating an Editor object to edit(write to the file)
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("name", holdername);
                    editor.putString("phoneno", phoneno);
                    editor.putString("email", email);
                    editor.putString("tele", tele);


                    editor.apply();
                    //Log.v("accNumber is",accNo);

                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error yo", "onErrorResponse: ");
                Toast.makeText(LoginActivity.this, "Ensure internet is secure",
                        Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);
        requestQueue.add(jsonObjectRequest);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void generateSecretKey(KeyGenParameterSpec keyGenParameterSpec) throws NoSuchProviderException, NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
        keyGenerator.init(keyGenParameterSpec);
        keyGenerator.generateKey();
    }

    private SecretKey getSecretKey() throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException, UnrecoverableKeyException {
        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");

        // Before the keystore can be accessed, it must be loaded.
        keyStore.load(null);
        return ((SecretKey)keyStore.getKey("KeyName", null));
    }

    private Cipher getCipher() throws NoSuchPaddingException, NoSuchAlgorithmException {
        return Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
                + KeyProperties.BLOCK_MODE_CBC + "/"
                + KeyProperties.ENCRYPTION_PADDING_PKCS7);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void encryptSecretInformation() throws UnrecoverableKeyException, CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, NoSuchPaddingException {
        // Exceptions are unhandled for getCipher() and getSecretKey().
        Cipher cipher = getCipher();
        SecretKey secretKey = getSecretKey();
        try {
            // NullPointerException is unhandled; use Objects.requireNonNull().
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedInfo = cipher.doFinal(
                    "hi".getBytes(Charset.defaultCharset()));
        } catch (InvalidKeyException e) {
            Log.e("MY_APP_TAG", "Key is invalid.");
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onBackPressed() {
        return;
    }

}