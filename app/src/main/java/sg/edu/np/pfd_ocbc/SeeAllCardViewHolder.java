package sg.edu.np.pfd_ocbc;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class SeeAllCardViewHolder extends RecyclerView.ViewHolder{
    TextView cardNo;
    ImageView cardtype;
    TextView balance;
    CardView card;


    public SeeAllCardViewHolder(View itemView) {
        super(itemView);
        cardNo = itemView.findViewById(R.id.last4Digit);
        cardtype = itemView.findViewById(R.id.cardType);
        balance = itemView.findViewById(R.id.balanceAmt);
        card = itemView.findViewById(R.id.card);
    }

}
