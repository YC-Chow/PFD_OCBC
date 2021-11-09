package sg.edu.np.pfd_ocbc;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class HomeTransactionAdapter extends RecyclerView.Adapter<HomeTransactionVH>{
    ArrayList<Transaction> transactionArrayList;
    Context mcontext;

    public HomeTransactionAdapter(Context c, ArrayList<Transaction> input) {
        mcontext = c;
        transactionArrayList = input;
    }

    @NonNull
    @Override
    public HomeTransactionVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.activity_home_transaction_item,
                parent,
                false);
        return new HomeTransactionVH(item);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeTransactionVH holder, int position) {
        Transaction transaction = transactionArrayList.get(position);
        holder.TransactionTo.setText(transaction.getToIC());
        holder.TransactionAmt.setText(String.valueOf(transaction.getTransactionAmt()));
        holder.TransactionDate.setText(transaction.getTransactionDate());

    }

    @Override
    public int getItemCount() {
        return transactionArrayList.size();
    }
}

