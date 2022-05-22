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
import hcmute.edu.vn.nhom6.zalo.databinding.ListRowContactGroupBinding;
import hcmute.edu.vn.nhom6.zalo.listeners.UserListener;
import hcmute.edu.vn.nhom6.zalo.models.User;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> {

    private Map<String, ArrayList<User>> groupList;
    private Context context;
    private UserListener userListener;

    public GroupAdapter(Context context, Map<String, ArrayList<User>> groupList, UserListener userListener){
        this.context = context;
        this.groupList = groupList;
        this.userListener = userListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ListRowContactGroupBinding binding;
        public ViewHolder(ListRowContactGroupBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListRowContactGroupBinding binding = ListRowContactGroupBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new ViewHolder(binding);
//        View view = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.list_row_contact_group, parent, false);
//        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String key = groupList.keySet().toArray()[position].toString();
        holder.binding.tvName.setText(key);
        ArrayList<User> rv_member_list = groupList.get(key);

        RowContactAdapter mb_adt = new RowContactAdapter(rv_member_list, userListener);
        holder.binding.rvMember.setAdapter(mb_adt);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        holder.binding.rvMember.setLayoutManager(linearLayoutManager);
        if(groupList.get(key).size() == 0)
            holder.binding.tvNobody.setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return groupList.size();
    }
}

