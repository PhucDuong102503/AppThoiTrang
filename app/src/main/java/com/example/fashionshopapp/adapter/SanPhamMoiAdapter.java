package com.example.fashionshopapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fashionshopapp.Interface.ItemClickListener; // 1. IMPORT INTERFACE
import com.example.fashionshopapp.R;
import com.example.fashionshopapp.activity.ChiTietActivity;
import com.example.fashionshopapp.model.SanPhamMoi;

import java.text.DecimalFormat;
import java.util.List;

public class SanPhamMoiAdapter extends RecyclerView.Adapter<SanPhamMoiAdapter.MyViewHolder> {
    Context context;
    List<SanPhamMoi> array;
    // 2. KHAI BÁO BIẾN LISTENER
    private ItemClickListener itemClickListener;

    // 3. SỬA HÀM KHỞI TẠO ĐỂ NHẬN LISTENER
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

        holder.txtTen.setText(sanPhamMoi.getTensanpham());
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        holder.txtGia.setText("Giá: " + decimalFormat.format(Double.parseDouble(sanPhamMoi.getGiasanpham())) + "đ"); // Sửa lại format giá cho đúng

        Glide.with(context).load(sanPhamMoi.getHinhanhsanpham()).into(holder.imghinhanh);

        // 4. GÁN LISTENER CHO VIEW HOLDER
        holder.setOnClickListener(this.itemClickListener);
    }

    @Override
    public int getItemCount() {
        return array.size();
    }

    // 5. SỬA LẠI MYVIEW HOLDER
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txtTen, txtGia;
        ImageView imghinhanh;
        private ItemClickListener onClickListener; // Đổi tên để không nhầm lẫn

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTen = itemView.findViewById(R.id.itemsp_ten);
            txtGia = itemView.findViewById(R.id.itemsp_gia);
            imghinhanh = itemView.findViewById(R.id.itemsp_image);
            // Đăng ký sự kiện click cho toàn bộ item
            itemView.setOnClickListener(this);
        }

        public void setOnClickListener(ItemClickListener onClickListener) {
            this.onClickListener = onClickListener;
        }

        @Override
        public void onClick(View v) {
            // Khi click, gọi ngược lại hàm của Activity/Fragment
            if (onClickListener != null) {
                onClickListener.onClick(v, getAdapterPosition(), false);
            }
        }
    }
}
