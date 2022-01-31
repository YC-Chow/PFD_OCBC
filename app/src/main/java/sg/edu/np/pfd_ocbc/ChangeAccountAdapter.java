package sg.edu.np.pfd_ocbc;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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

import java.text.DecimalFormat;
import java.util.ArrayList;

public class ChangeAccountAdapter extends RecyclerView.Adapter<ReceiverVH> {

    private static final DecimalFormat df = new DecimalFormat("0.00");
    ArrayList<Card> cardArrayList;
    Context mcontext;
    private FirebaseAuth mAuth;


    public ChangeAccountAdapter(Context c, ArrayList<Card> input) {
        this.mcontext = c;
        this.cardArrayList = input;
    }

    @NonNull
    @Override
    public ReceiverVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.multiple_accounts_recycler,
                parent,
                false);
        return new ReceiverVH(item);
    }

    @Override
    public void onBindViewHolder(@NonNull ReceiverVH holder, int position) {
        Card card = cardArrayList.get(position);

        holder.accno.setText(card.getNameOnCard() + ", " + card.getAccNo());
        holder.last4digit.setText(card.getCardNo().substring(card.getCardNo().length() - 4));
        if(card.getIssuingNetwork().equals("Visa")){
            holder.cardtype.setImageResource(R.drawable.visa_icon);
        }


        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String getcard = "https://pfd-server.azurewebsites.net/getAccountUsingAccNo";
                mAuth = FirebaseAuth.getInstance();
                FirebaseUser user = mAuth.getCurrentUser();



                JSONObject nameData = new JSONObject();
                try {

                    nameData.put("accNo", card.getAccNo());



                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //POST api to update name
                JsonObjectRequest nameObjectRequest = new JsonObjectRequest(Request.Method.POST, getcard, nameData, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        SharedPreferences sharedPref = mcontext.getSharedPreferences("MySharedPref", MODE_PRIVATE);

                        SharedPreferences.Editor editor = sharedPref.edit();

                        Log.v("hihih", "sdkggslk");


                        try {
                            String accNo = response.getString("acc_no");
                            String accName = response.getString("acc_name");
                            String accHolderName = response.getString("account_holder_name");
                            Double cardBal = response.getDouble("balance");
                            String cardNumber = response.getString("card_number");
                            String issuingNetwork = response.getString("issuing_network");
                            Card c = new Card(cardNumber,accName,issuingNetwork,cardBal,accNo);


                            String lastFourDigits = "";     //substring containing last 4 characters
                            if (c.getCardNo().length() > 4)
                            {
                                lastFourDigits = c.getCardNo().substring(c.getCardNo().length() - 4);
                            }


                            // Creating an Editor object to edit(write to the file)

                            editor.putString("accNo", accNo);
                            editor.putString("accName", accName);
                            editor.putString("accHolderName",accHolderName);
                            editor.putString("cardBal", df.format(cardBal));
                            editor.putString("fullCardNumber", cardNumber);
                            editor.putString("last4Digits", lastFourDigits);
                            editor.putString("issuingNetwork", issuingNetwork);
                            editor.apply();

                            Intent intent = new Intent(mcontext, HomeActivity .class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            mcontext.startActivity(intent);
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
            }
        });




    }

    @Override
    public int getItemCount() {
        return cardArrayList.size();
    }
}
