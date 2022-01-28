package sg.edu.np.pfd_ocbc;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class GiroListAdapter extends RecyclerView.Adapter<GiroListVH> {
    ArrayList<Giro> data;
    String Mode;

    public GiroListAdapter(ArrayList<Giro> input, String mode){
        data = input;
        this.Mode = mode;
    }

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
                intent.putExtra("giroID", giro.getGiro_id());

                //using id, no way of getting biz name
                intent.putExtra("bizName", giro.getBiz_id());
                intent.putExtra("giroAcc", giro.getGiro_acc_no());
                intent.putExtra("description", giro.getDescription());
                v.getContext().startActivity(intent);
                ((Activity) v.getContext()).finish();
            }
        });
    }

    public int getItemCount() {
        return data.size();
    }


}
