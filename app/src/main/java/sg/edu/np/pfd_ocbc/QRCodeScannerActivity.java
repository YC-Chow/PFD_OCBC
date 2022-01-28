package sg.edu.np.pfd_ocbc;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.provider.Settings;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.Result;

import org.json.JSONException;
import org.json.JSONObject;

public class QRCodeScannerActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 200;
    CodeScanner codeScanner;
    CodeScannerView scanView;
    TextView resultData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_scanner);

        SharedPreferences sharedPref = getSharedPreferences("AccountHolder", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);

        scanView = findViewById(R.id.codeScannerView);
        codeScanner = new CodeScanner(this,scanView);

        codeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run()
                    {
                        String postUrl = "https://pfd-server.azurewebsites.net/getAccountUsingAccNo";

                        JSONObject postData = new JSONObject();
                        try {
                            postData.put("accNo", result.getText());
                        }catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, postUrl, postData, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    if (response.has("acc_no"))
                                    {
                                        String receiverAccNo = response.getString("acc_no");
                                        String receiverName = response.getString("account_holder_name");
                                        if (receiverName == null){
                                            receiverName = "Unknown";
                                        }
                                        Intent intent = new Intent(QRCodeScannerActivity.this, AmountConfirmationActivity.class);
                                        intent.putExtra("receiverName" , receiverName);
                                        intent.putExtra("receiverAccNo", receiverAccNo);
                                        intent.putExtra("senderAccNo", sharedPreferences.getString("accNo",""));
                                        startActivity(intent);
                                    }
                                    else
                                    {
                                        Toast.makeText(QRCodeScannerActivity.this, "Invalid Account Number",Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(QRCodeScannerActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                                error.printStackTrace();
                            }
                        });
                        RequestQueue requestQueue = Volley.newRequestQueue(QRCodeScannerActivity.this);
                        requestQueue.add(jsonObjectRequest);

                    }
                });
            }
        });

        if (checkPermission()) {
            scanView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    codeScanner.startPreview();
                }
            });
        }
        else{
            requestPermission();
        }

    }

    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            return false;
        }
        return true;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_SHORT).show();

                    // main logic
                } else {
                    Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                                != PackageManager.PERMISSION_GRANTED) {
                            showMessageOKCancel("You need to allow access permissions",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermission();
                                            }
                                        }
                                    });
                        }
                    }
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(QRCodeScannerActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    public void onResume() {
        super.onResume();
        codeScanner.startPreview();
    }

    @Override
    public void onPause() {
        codeScanner.releaseResources();
        super.onPause();
    }
}