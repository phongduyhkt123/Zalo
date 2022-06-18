package hcmute.edu.vn.nhom6.zalo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Map;

import hcmute.edu.vn.nhom6.zalo.databinding.ListRowContactGroupBinding;
import hcmute.edu.vn.nhom6.zalo.listeners.UserListener;
import hcmute.edu.vn.nhom6.zalo.models.User;

/** Adapter hiện nhóm liên hệ (ví dụ như tất cả, bạn thân, bạn trong danh bạ, ...) */
public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> {

    private Map<String, ArrayList<User>> groupList; // danh sách liên hệ và nhóm liên hệ với key là tên nhóm liên hệ, value là các liên hệ trong nhóm
    private Context context;
    private UserListener userListener; // sự kiện lắng nghe dòng liên hệ để truyền cho RowContactAdapter

    // Khởi tạo
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
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String key = groupList.keySet().toArray()[position].toString(); // Lấy tên của nhóm liên hệ ở vị trí position
        holder.binding.tvName.setText(key); // đặt giá trị cho text view tên nhóm liên hệ
        ArrayList<User> rv_member_list = groupList.get(key); // Lấy danh sách các liên hệ trong nhóm liên hệ này

        // Thiết lập hiển thị danh sách liên hệ của nhóm liên hệ này
        RowContactAdapter mb_adt = new RowContactAdapter(rv_member_list, userListener); // Tạo adapter hiển thị danh sách các liên hệ của nhóm liên hệ này
        holder.binding.rvMember.setAdapter(mb_adt);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        holder.binding.rvMember.setLayoutManager(linearLayoutManager);
        if(groupList.get(key).size() == 0) // nếu ko có liên hệ nào thì hiện textview ko có liên hệ
            holder.binding.tvNobody.setVisibility(View.VISIBLE);

        // Tạo đường phân cách giữa các item trong recycleView
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(context, DividerItemDecoration.VERTICAL);
        holder.binding.rvMember.addItemDecoration(itemDecoration);
    }

    @Override
    public int getItemCount() {
        return groupList.size();
    }
}

