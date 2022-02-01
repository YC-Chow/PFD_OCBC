package sg.edu.np.pfd_ocbc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.nfc.NfcAdapter.OnNdefPushCompleteCallback;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

public class NfcActivity extends Activity implements
        CreateNdefMessageCallback, OnNdefPushCompleteCallback{

    private FirebaseAuth mAuth;
    Context mcontext;

    NfcAdapter nfcAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc);


        String getcard = "https://pfd-server.azurewebsites.net/getAccountHolderUsingUid";
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();



        JSONObject nameData = new JSONObject();
        try {
            nameData.put("uid", user.getUid());




        } catch (JSONException e) {
            e.printStackTrace();
        }

        //POST api to update name
        JsonObjectRequest nameObjectRequest = new JsonObjectRequest(Request.Method.POST, getcard, nameData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                JSONObject nameData = new JSONObject();
                try {

                    SharedPreferences sharedPref = getSharedPreferences("MySharedPref", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();



                    String bank_pref = response.getString("bank_pref");

                    editor.putString("bank_pref", bank_pref);
                    editor.apply();




                } catch (JSONException e) {
                    e.printStackTrace();
                }




            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(mcontext, "Update Error",
                        Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue namerequestQueue = Volley.newRequestQueue(mcontext);
        namerequestQueue.add(nameObjectRequest);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if(nfcAdapter==null){
            Toast.makeText(NfcActivity.this,
                    "nfcAdapter==null, no NFC adapter exists",
                    Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(NfcActivity.this,
                    "Set Callback(s)",
                    Toast.LENGTH_LONG).show();
            nfcAdapter.setNdefPushMessageCallback(this, this);
            nfcAdapter.setOnNdefPushCompleteCallback(this, this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        String action = intent.getAction();
        if(action.equals(NfcAdapter.ACTION_NDEF_DISCOVERED)){
            Parcelable[] parcelables =
                    intent.getParcelableArrayExtra(
                            NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage inNdefMessage = (NdefMessage)parcelables[0];
            NdefRecord[] inNdefRecords = inNdefMessage.getRecords();
            NdefRecord NdefRecord_0 = inNdefRecords[0];
            String inMsg = new String(NdefRecord_0.getPayload());
            Intent transferintent = new Intent(NfcActivity.this, TestActivity.class);
            intent.putExtra("accno", inMsg);
            startActivity(transferintent);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
    }

    @Override
    public void onNdefPushComplete(NfcEvent event) {

        final String eventString = "onNdefPushComplete\n" + event.toString();
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(getApplicationContext(),
                        eventString,
                        Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        SharedPreferences sharedPref = getSharedPreferences("MySharedPref", MODE_PRIVATE);


        String stringOut = sharedPref.getString("bank_pref", "");;
        byte[] bytesOut = stringOut.getBytes();

        NdefRecord ndefRecordOut = new NdefRecord(
                NdefRecord.TNF_MIME_MEDIA,
                "text/plain".getBytes(),
                new byte[] {},
                bytesOut);

        NdefMessage ndefMessageout = new NdefMessage(ndefRecordOut);
        return ndefMessageout;
    }

}