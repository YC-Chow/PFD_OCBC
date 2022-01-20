package sg.edu.np.pfd_ocbc;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class GiroAcceptedViewHolder extends RecyclerView.ViewHolder{
    TextView businessName;
    ImageButton moreInfoBtn;
    public GiroAcceptedViewHolder(View itemView){
        super(itemView);
        businessName = itemView.findViewById(R.id.giroBusinessName);
        moreInfoBtn = itemView.findViewById(R.id.giroMoreInfoBtn);
    }
}
