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

    public GiroListAdapter(ArrayList<Giro> input){
        data = input;
    }

    public GiroListVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.giro_accepted_recyclerview,parent, false);
        return new GiroListVH(item);
    }

    public void onBindViewHolder(GiroListVH holder, int position) {
        Giro giro = data.get(position);

        //temporary set to biz id, no way of getting biz name
        holder.businessName.setText(giro.getBusiness().getName());
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), GiroDetailsActivity.class);
                intent.putExtra("giroID", giro.getGiro_id());
                intent.putExtra("bizName", giro.getBusiness().getName());
                intent.putExtra("giroAcc", giro.getGiro_acc_no());
                intent.putExtra("ref_no", giro.getReferenceNo());
                intent.putExtra("date", giro.getDate());
                v.getContext().startActivity(intent);
                ((Activity) v.getContext()).finish();
            }
        });


    }

    public int getItemCount() {
        return data.size();
    }


}
