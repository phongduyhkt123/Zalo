package hcmute.edu.vn.nhom6.zalo.activities.contact;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Map;

import hcmute.edu.vn.nhom6.zalo.R;
import hcmute.edu.vn.nhom6.zalo.models.User;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> {

    private Map<String, ArrayList<User>> groupList;
    private Context context;

    public GroupAdapter(Context context, Map<String, ArrayList<User>> groupList){
        this.context = context;
        this.groupList = groupList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView tv_name;
        RecyclerView rv_member;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            rv_member = itemView.findViewById(R.id.rv_member);
        }

        public TextView getTv_name() {
            return tv_name;
        }

        public RecyclerView getRv_member() {
            return rv_member;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_contact_group, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String key = groupList.keySet().toArray()[position].toString();
        holder.getTv_name().setText(key);
        ArrayList<User> rv_member_list = groupList.get(key);

        RowContactAdapter mb_adt = new RowContactAdapter(rv_member_list);
        holder.getRv_member().setAdapter(mb_adt);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        holder.getRv_member().setLayoutManager(linearLayoutManager);
    }

    @Override
    public int getItemCount() {
        return groupList.size();
    }
}

