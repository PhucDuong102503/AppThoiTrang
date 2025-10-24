package com.example.fashionshopapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fashionshopapp.Interface.GioHangItemClickListener;
import com.example.fashionshopapp.R;
import com.example.fashionshopapp.model.GioHang;

import java.text.DecimalFormat;
import java.util.List;

public class GioHangAdapter extends RecyclerView.Adapter<GioHangAdapter.MyViewHolder> {

    Context context;
    List<GioHang> gioHangList;
    private GioHangItemClickListener listener; // Đổi tên cho nhất quán


    public GioHangAdapter(Context context, List<GioHang> gioHangList, GioHangItemClickListener itemClickListener) {
        this.context = context;
        this.gioHangList = gioHangList;
        this.listener = itemClickListener; // Sửa ở đây
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_giohang, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        GioHang gioHang = gioHangList.get(position);

        // Hiển thị dữ liệu lên View
        holder.txtTenSp.setText(gioHang.getTensp());
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        holder.txtGiaSp.setText("Giá: " + decimalFormat.format(gioHang.getGiasp()) + "đ");
        holder.txtSoLuong.setText(String.valueOf(gioHang.getSoluong()));
        Glide.with(context).load(gioHang.getHinhanh()).into(holder.imgAnh);

        // Hiển thị Size (nếu có)
        if (gioHang.getSize() != null && !gioHang.getSize().isEmpty() && !gioHang.getSize().equals("Phụ kiện")) {
            holder.txtSize.setText("Size: " + gioHang.getSize());
            holder.txtSize.setVisibility(View.VISIBLE);
        } else {
            holder.txtSize.setVisibility(View.GONE);
        }

        // --- SỬA LẠI TOÀN BỘ LOGIC GỌI SỰ KIỆN Ở ĐÂY CHO ĐÚNG VỚI TÊN BIẾN ---

        // Nút trừ (btnTru) phải gửi đi mã 1 (Giảm)
        holder.btnTru.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(v, holder.getAdapterPosition(), 1);
            }
        });

        // Nút cộng (btnCong) phải gửi đi mã 2 (Tăng)
        holder.btnCong.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(v, holder.getAdapterPosition(), 2);
            }
        });

        // Nút xóa (imgXoa - thùng rác) gửi đi mã 3
        holder.imgXoa.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(v, holder.getAdapterPosition(), 3);
            }
        });
    }


    @Override
    public int getItemCount() {
        return gioHangList.size();
    }

    // --- SỬA LẠI TÊN BIẾN TRONG MyViewHolder CHO ĐÚNG VỚI FILE LAYOUT CỦA BẠN ---
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAnh, imgXoa;
        TextView txtTenSp, txtGiaSp, txtSoLuong, txtSize; // Bỏ txtTongTien vì nó ở ngoài
        Button btnCong, btnTru;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            // Ánh xạ các View từ file layout item_giohang.xml
            imgAnh = itemView.findViewById(R.id.item_giohang_image);
            imgXoa = itemView.findViewById(R.id.item_giohang_delete); // Thùng rác để xóa
            txtTenSp = itemView.findViewById(R.id.item_giohang_tensp);
            txtGiaSp = itemView.findViewById(R.id.item_giohang_giasp);
            txtSoLuong = itemView.findViewById(R.id.item_giohang_soluong);
            txtSize = itemView.findViewById(R.id.itemgiohangsize);
            btnCong = itemView.findViewById(R.id.item_giohang_cong); // Nút cộng
            btnTru = itemView.findViewById(R.id.item_giohang_tru);   // Nút trừ
        }
    }
}
