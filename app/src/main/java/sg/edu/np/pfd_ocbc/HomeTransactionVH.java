package sg.edu.np.pfd_ocbc;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class HomeTransactionVH extends RecyclerView.ViewHolder{
    TextView TransactionToOrFrom;
    TextView TransactionDate;
    TextView TransactionAmt;

    public HomeTransactionVH(View itemView) {
        super(itemView);
        TransactionToOrFrom = itemView.findViewById(R.id.transactionTo);
        TransactionDate= itemView.findViewById(R.id.transactionDateTime);
        TransactionAmt = itemView.findViewById(R.id.transactionAmt);
    }
}
