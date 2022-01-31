package sg.edu.np.pfd_ocbc;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
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

public class ReceiverAdapter extends RecyclerView.Adapter<ReceiverVH>{
    private static final DecimalFormat df = new DecimalFormat("0.00");
    ArrayList<Card> cardArrayList;
    Context mcontext;
    private FirebaseAuth mAuth;


    public ReceiverAdapter(Context c, ArrayList<Card> input) {
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



                    String bank_pref = response.getString("bank_pref");


                    if(card.getAccNo().equals(bank_pref)){
                        holder.card.setStrokeWidth(8);
                        holder.card.setStrokeColor(Color.rgb(239, 75, 76));
                        holder.card.invalidate();


                    }else{
                        holder.card.setStrokeWidth(0);
                        holder.card.invalidate();
                    }



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




        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String getcard = "https://pfd-server.azurewebsites.net/updateBankPreference";
                mAuth = FirebaseAuth.getInstance();
                FirebaseUser user = mAuth.getCurrentUser();



                JSONObject nameData = new JSONObject();
                try {
                    nameData.put("uid", user.getUid());
                    nameData.put("acc_no", card.getAccNo());



                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //POST api to update name
                JsonObjectRequest nameObjectRequest = new JsonObjectRequest(Request.Method.POST, getcard, nameData, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        notifyDataSetChanged();









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
