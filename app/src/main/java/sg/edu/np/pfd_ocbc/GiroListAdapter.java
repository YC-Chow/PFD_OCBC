package sg.edu.np.pfd_ocbc;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class GiroListAdapter extends RecyclerView.Adapter<GiroListVH> {
    ArrayList<Giro> data;
    String Mode;

    public GiroListAdapter(ArrayList<Giro> input, String mode){data = input; this.Mode = Mode;}

    public GiroListVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.giro_accepted_recyclerview,parent, false);
        return new GiroListVH(item);
    }

    public void onBindViewHolder(GiroListVH holder, int position) {
        Giro giro = data.get(position);

        //temporary set to biz id, no way of getting biz name
        holder.businessName.setText(giro.getBiz_id());
        holder.moreInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), GiroDetailsActivity.class);
                intent.putExtra("Mode", Mode);
                v.getContext().startActivity(intent);
            }
        });
    }

    public int getItemCount() {
        return data.size();
    }


}
