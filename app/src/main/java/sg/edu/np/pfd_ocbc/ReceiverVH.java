package sg.edu.np.pfd_ocbc;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

public class ReceiverVH extends RecyclerView.ViewHolder {


    TextView last4digit;
    ImageView cardtype;
    TextView accno;
    MaterialCardView card;
    ImageView cardpic;




    public ReceiverVH(View itemView) {
        super(itemView);

        last4digit = itemView.findViewById(R.id.last4Digit);
        card = itemView.findViewById(R.id.cardView);

        cardtype = itemView.findViewById(R.id.cardType);
        accno = itemView.findViewById(R.id.accno);
        cardpic = itemView.findViewById(R.id.creditcardbg);

    }
}
