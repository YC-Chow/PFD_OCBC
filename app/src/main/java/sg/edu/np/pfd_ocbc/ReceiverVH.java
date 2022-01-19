package sg.edu.np.pfd_ocbc;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class ReceiverVH extends RecyclerView.ViewHolder {

    TextView balance;
    TextView accname;


    public ReceiverVH(View itemView) {
        super(itemView);

        balance = itemView.findViewById(R.id.balance);
        accname = itemView.findViewById(R.id.accname);

    }
}
