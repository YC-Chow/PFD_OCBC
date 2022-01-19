package sg.edu.np.pfd_ocbc;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class ReciverAdapter extends RecyclerView.Adapter<ReceiverVH>{
    private static final DecimalFormat df = new DecimalFormat("0.00");
    ArrayList<Card> cardArrayList;
    Context mcontext;

    public ReciverAdapter(Context c, ArrayList<Card> input) {
        mcontext = c;
        cardArrayList = input;
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

        holder.accname.setText(card.getAccNo());
        holder.balance.setText(card.getBalance().toString());


    }

    @Override
    public int getItemCount() {
        return cardArrayList.size();
    }
}
