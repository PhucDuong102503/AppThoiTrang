// Đường dẫn: C:/Users/PC/Documents/GitHub/AppThoiTrang/app/src/main/java/com/example/fashionshopapp/adapter/SanPhamMoiAdapter.java

package com.example.fashionshopapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fashionshopapp.Interface.ItemClickListener;
import com.example.fashionshopapp.R;
import com.example.fashionshopapp.activity.ChiTietActivity;
import com.example.fashionshopapp.model.SanPhamMoi;

import java.text.DecimalFormat;
import java.util.List;

public class SanPhamMoiAdapter extends RecyclerView.Adapter<SanPhamMoiAdapter.MyViewHolder> {
    Context context;
    List<SanPhamMoi> array;

    public SanPhamMoiAdapter(Context context, List<SanPhamMoi> array) {
        this.context = context;
        this.array = array;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sp_moi, parent, false);
        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        SanPhamMoi sanPhamMoi = array.get(position);
        if (sanPhamMoi == null) return;

        holder.txtTen.setText(sanPhamMoi.getTensanpham());
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        holder.txtGia.setText("Giá: " + decimalFormat.format(Double.parseDouble(sanPhamMoi.getGiasanpham())) + "đ");

        // ⭐⭐⭐ LOGIC ĐƠN GIẢN NHẤT: TIN TƯỞNG SERVER LUÔN TRẢ VỀ URL ĐÚNG ⭐⭐⭐
        String finalImageUrl = sanPhamMoi.getHinhanhsanpham();

        if (finalImageUrl != null && !finalImageUrl.isEmpty()) {
            Log.d("GlideLoader", "Final URL from server: " + finalImageUrl);

            // Chỉ việc dùng Glide để tải URL mà server trả về
            Glide.with(context)
                    .load(finalImageUrl)
                    .placeholder(R.drawable.ic_media_24)
                    .error(R.drawable.ic_media_24)
                    .into(holder.imghinhanh);
        } else {
            // Nếu không có thông tin ảnh, hiển thị ảnh mặc định
            holder.imghinhanh.setImageResource(R.drawable.ic_media_24);
        }

        // Gán sự kiện click cho item
        holder.setItemClickListener((view, pos, isLongClick) -> {
            if (!isLongClick) {
                Intent intent = new Intent(context, ChiTietActivity.class);
                intent.putExtra("chitiet", array.get(pos));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return array != null ? array.size() : 0;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txtTen, txtGia;
        ImageView imghinhanh;
        private ItemClickListener itemClickListener;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTen = itemView.findViewById(R.id.itemsp_ten);
            txtGia = itemView.findViewById(R.id.itemsp_gia);
            imghinhanh = itemView.findViewById(R.id.itemsp_image);
            itemView.setOnClickListener(this);
        }

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View view) {
            if (itemClickListener != null) {
                itemClickListener.onClick(view, getAdapterPosition(), false);
            }
        }
    }
}
