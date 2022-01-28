package sg.edu.np.pfd_ocbc;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ViewQRActivity extends AppCompatActivity {

    TextView text;
    public final static int QRCodeWidth = 500;
    Bitmap bitmap;
    private ImageView QRCode;
    private FirebaseAuth mAuth;
    SharedPreferences accNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_qractivity);
        QRCode = findViewById(R.id.QRCodeImg);

    }

    private Bitmap textToImageEncode(String value) throws WriterException {
        BitMatrix bitMatrix;

        try {
            bitMatrix = new MultiFormatWriter().encode(value, BarcodeFormat.DATA_MATRIX.QR_CODE, QRCodeWidth, QRCodeWidth, null);
            }
        catch (IllegalArgumentException e) {
            return null;
        }
        int bitMatrixWidth = bitMatrix.getWidth();
        int bitMatrixHeight = bitMatrix.getHeight();
        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

        for (int y = 0; y < bitMatrixHeight; y++) {
            int offSet = y * bitMatrixWidth;
            for (int x = 0; x < bitMatrixWidth; x++) {
                pixels[offSet + x] = bitMatrix.get(x, y) ?
                        getResources().getColor(R.color.black) : getResources().getColor(R.color.white);
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);
        bitmap.setPixels(pixels, 0, 500, 0, 0, bitMatrixWidth, bitMatrixHeight);
        return bitmap;
        }

    @Override
    protected void onStart() {
        SharedPreferences sharedPref = getSharedPreferences("AccountHolder", MODE_PRIVATE);
        accNo = getSharedPreferences("accNo", MODE_PRIVATE);

        String getcard = "https://pfd-server.azurewebsites.net/getAccountUsingPhoneNo";
        String userphone = sharedPref.getString("phoneno", "");

        JSONObject nameData = new JSONObject();
        try {
            nameData.put("phoneNo", userphone);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest nameObjectRequest = new JsonObjectRequest(Request.Method.POST, getcard, nameData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    String accNo = response.getString("acc_no");

                    try{
                        //change hello to store the account number instead.
                        bitmap = textToImageEncode(accNo);
                        QRCode.setImageBitmap(bitmap);
                    }catch (WriterException e){
                        e.printStackTrace();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ViewQRActivity.this, "Update Error",
                        Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue namerequestQueue = Volley.newRequestQueue(ViewQRActivity.this);
        namerequestQueue.add(nameObjectRequest);
        super.onStart();
    }
}