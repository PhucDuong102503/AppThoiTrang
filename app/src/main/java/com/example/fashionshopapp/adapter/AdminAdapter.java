package com.example.fashionshopapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.fashionshopapp.R;
import com.example.fashionshopapp.activity.ChatActivity;
import com.example.fashionshopapp.model.User;
import com.example.fashionshopapp.util.Utils;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;

public class AdminAdapter extends RecyclerView.Adapter<AdminAdapter.MyViewHolder> {
    private final Context context;
    private final List<User> adminList;

    public AdminAdapter(Context context, List<User> adminList) {
        this.context = context;
        this.adminList = adminList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        User admin = adminList.get(position);
        if (admin == null) return;

        holder.txtAdminName.setText(admin.getHoten());

        if (admin.getHinhanh() != null && !admin.getHinhanh().isEmpty()) {
            String relativePath = admin.getHinhanh();
            String fullImageUrl;
            if (relativePath.startsWith("http")) {
                fullImageUrl = relativePath;
            } else {
                String finalRelativePath = relativePath.startsWith("/") ? relativePath.substring(1) : relativePath;
                fullImageUrl = Utils.BASE_URL + finalRelativePath;
            }
            Glide.with(context).load(fullImageUrl).placeholder(R.drawable.profile).error(R.drawable.profile).into(holder.imgAvatar);
        } else {
            holder.imgAvatar.setImageResource(R.drawable.profile);
        }

        // ⭐ SỬA LẠI LOGIC CLICK CUỐI CÙNG ⭐
        // Gán sự kiện cho itemView (chính là CardView) và không dùng Flag
        holder.itemView.setOnClickListener(v -> {
            Log.d("AdminAdapter", "Item clicked. Starting ChatActivity for: " + admin.getHoten());

            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra("admin_id", String.valueOf(admin.getId()));
            intent.putExtra("admin_name", admin.getHoten());

            // Không cần Flag vì context là một Activity
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return adminList != null ? adminList.size() : 0;
    }

    // ViewHolder đơn giản, không cần tham chiếu đến layout con
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtAdminName;
        CircleImageView imgAvatar;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtAdminName = itemView.findViewById(R.id.text_admin_name);
            imgAvatar = itemView.findViewById(R.id.image_admin_avatar);
        }
    }
}
