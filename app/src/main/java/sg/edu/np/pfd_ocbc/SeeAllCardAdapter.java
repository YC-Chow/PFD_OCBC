package sg.edu.np.pfd_ocbc;
import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SeeAllCardAdapter extends RecyclerView.Adapter<SeeAllCardViewHolder>{
    ArrayList<Card> cardArrayList;
    Context mcontext;

    public SeeAllCardAdapter(Context c, ArrayList<Card> input) {
        this.mcontext = c;
        this.cardArrayList = input;
    }




    @NonNull
    @Override
    public SeeAllCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.see_all_card_recycler,
                parent,
                false);
        return new SeeAllCardViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull SeeAllCardViewHolder holder, int position) {
        Card card = cardArrayList.get(position);
        String accNo = card.getCardNo();
        holder.cardNo.setText(accNo.substring(accNo.length() - 4));
        if(card.getIssuingNetwork().matches("Visa")){
            holder.cardtype.setImageResource(R.drawable.visa_icon);
        }
        holder.balance.setText(card.getBalance());

        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPref = mcontext.getSharedPreferences("MySharedPref",MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("last4digits", accNo.substring(accNo.length() - 4));
                editor.putString("balanceAmt", card.getBalance());
                editor.putString("IssuingNetwork", card.getIssuingNetwork());
                editor.apply();
                Intent intent = new Intent(mcontext, HomeActivity.class);
                mcontext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return cardArrayList.size();
    }
}
