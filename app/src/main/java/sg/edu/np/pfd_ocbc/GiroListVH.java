package sg.edu.np.pfd_ocbc;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

public class GiroListVH extends RecyclerView.ViewHolder{
    TextView businessName;
    ConstraintLayout container;
    public GiroListVH(View itemView){
        super(itemView);
        businessName = itemView.findViewById(R.id.giroBusinessName);
        container = itemView.findViewById(R.id.giroContainer);
    }
}
