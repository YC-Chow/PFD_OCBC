package sg.edu.np.pfd_ocbc;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class HomeTransactionAdapter extends RecyclerView.Adapter<HomeTransactionVH>{
    private static final DecimalFormat df = new DecimalFormat("0.00");
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

        //If im receiving money
        if(transaction.getDebitOrCredit().equals("-")){
            if(transaction.getRecipientName().equals("null")){
                holder.TransactionToOrFrom.setText("Sent to: " + transaction.getRecipientAccNo());
            }
            else{
                holder.TransactionToOrFrom.setText("Sent to: " + transaction.getRecipientName());
            }
            holder.TransactionAmt.setText((df.format(transaction.getTransactionAmt())));
            holder.TransactionDate.setText(transaction.getTransactionDate());
            holder.TransactionDebitOrCredit.setText(transaction.getDebitOrCredit());
        }
        else{
            if(transaction.getSenderName().equals("null")){
                holder.TransactionToOrFrom.setText("Received from: " + transaction.getSenderAccNo());
            }
            else{
                holder.TransactionToOrFrom.setText("Received from: " + transaction.getSenderName());
            }
            holder.TransactionAmt.setText((df.format(transaction.getTransactionAmt())));
            holder.TransactionDate.setText(transaction.getTransactionDate());
            holder.TransactionDebitOrCredit.setText(transaction.getDebitOrCredit());
        }
    }

    @Override
    public int getItemCount() {
        return transactionArrayList.size();
    }
}

