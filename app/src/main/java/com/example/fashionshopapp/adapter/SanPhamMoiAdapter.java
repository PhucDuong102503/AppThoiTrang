package com.example.fashionshopapp.adapter;

import android.content.Context;
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
import com.example.fashionshopapp.model.SanPhamMoi;
import com.example.fashionshopapp.util.Utils; // ⭐ QUAN TRỌNG: THÊM IMPORT NÀY

import java.text.DecimalFormat;
import java.util.List;

public class SanPhamMoiAdapter extends RecyclerView.Adapter<SanPhamMoiAdapter.MyViewHolder> {
    Context context;
    List<SanPhamMoi> array;
    private ItemClickListener itemClickListener;

    public SanPhamMoiAdapter(Context context, List<SanPhamMoi> array, ItemClickListener itemClickListener) {
        this.context = context;
        this.array = array;
        this.itemClickListener = itemClickListener;
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

        // ⭐⭐ LOGIC XỬ LÝ HÌNH ẢNH "CHỐNG ĐẠN" (AN TOÀN TUYỆT ĐỐI) ⭐⭐
        String imageUrl = sanPhamMoi.getHinhanhsanpham();

        // 1. Kiểm tra xem imageUrl có hợp lệ không
        if (imageUrl != null && !imageUrl.isEmpty()) {
            String fullImageUrl;
            // 2. Nếu URL không bắt đầu bằng "http", nó là đường dẫn tương đối -> nối với BASE_URL
            if (!imageUrl.startsWith("http")) {
                fullImageUrl = Utils.BASE_URL + imageUrl;
            } else {
                // 3. Nếu đã là URL đầy đủ, dùng luôn
                fullImageUrl = imageUrl;
            }

            // 4. Dùng Glide để tải ảnh
            Glide.with(context)
                    .load(fullImageUrl)
                    .placeholder(R.drawable.ic_media_24) // Ảnh hiển thị khi đang tải
                    .error(R.drawable.ic_media_24)      // Ảnh hiển thị nếu lỗi
                    .into(holder.imghinhanh);
        } else {
            // 5. Nếu không có URL, hiển thị ảnh lỗi
            holder.imghinhanh.setImageResource(R.drawable.ic_media_24);
        }

        // Gán sự kiện click cho item
        holder.itemView.setOnClickListener(view -> {
            if (itemClickListener != null) {
                itemClickListener.onClick(view, position, false);
            }
        });
    }

    @Override
    public int getItemCount() {
        return array != null ? array.size() : 0;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtTen, txtGia;
        ImageView imghinhanh;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTen = itemView.findViewById(R.id.itemsp_ten);
            txtGia = itemView.findViewById(R.id.itemsp_gia);
            imghinhanh = itemView.findViewById(R.id.itemsp_image);
        }
    }
}
