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
    private GioHangItemClickListener itemClickListener;


    public GioHangAdapter(Context context, List<GioHang> gioHangList, GioHangItemClickListener itemClickListener) {
        this.context = context;
        this.gioHangList = gioHangList;
        this.itemClickListener = itemClickListener;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_giohang, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final int currentPosition = holder.getAdapterPosition();
        if (currentPosition == RecyclerView.NO_POSITION) {
            return;
        }
        GioHang gioHang = gioHangList.get(currentPosition);

        holder.txtTenSp.setText(gioHang.getTensp());
        holder.txtSoLuong.setText(String.valueOf(gioHang.getSoluong()));

        if (gioHang.getSize() != null && !gioHang.getSize().isEmpty()) {
            holder.txtSize.setText("Size: " + gioHang.getSize());
            holder.txtSize.setVisibility(View.VISIBLE);
        } else {
            holder.txtSize.setVisibility(View.GONE);
        }

        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        holder.txtGiaSp.setText("Giá: " + decimalFormat.format(gioHang.getGiasp()) + "đ");

        long tongTien = gioHang.getGiasp() * gioHang.getSoluong();
        holder.txtTongTien.setText("Tổng: " + decimalFormat.format(tongTien) + "đ");

        Glide.with(context).load(gioHang.getHinhanh()).into(holder.imgAnh);

        // 🟩 Nút xóa sản phẩm
        holder.imgXoa.setOnClickListener(v ->
                itemClickListener.onItemClick(v, currentPosition, 3)
        );

        // 🟩 Nút tăng số lượng
        holder.btnCong.setOnClickListener(v ->
                itemClickListener.onItemClick(v, currentPosition, 1) // 1 = tăng
        );

        // 🟩 Nút giảm số lượng
        holder.btnTru.setOnClickListener(v ->
                itemClickListener.onItemClick(v, currentPosition, 2) // 2 = giảm
        );
    }


    @Override
    public int getItemCount() {
        // Trả về số lượng sản phẩm trong giỏ hàng
        return gioHangList.size();
    }

    /**
     * Lớp ViewHolder để lưu trữ các tham chiếu đến View của một item.
     * Giúp tăng hiệu năng bằng cách tránh gọi findViewById() nhiều lần.
     */
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAnh, imgXoa;
        TextView txtTenSp, txtGiaSp, txtSoLuong, txtSize, txtTongTien;
        Button btnCong, btnTru;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            // Ánh xạ các View từ file layout item_giohang.xml
            imgAnh = itemView.findViewById(R.id.item_giohang_image);
            imgXoa = itemView.findViewById(R.id.item_giohang_delete);
            txtTenSp = itemView.findViewById(R.id.item_giohang_tensp);
            txtGiaSp = itemView.findViewById(R.id.item_giohang_giasp);
            txtSoLuong = itemView.findViewById(R.id.item_giohang_soluong);
            // Lưu ý: Đảm bảo ID này đúng với file item_giohang.xml của bạn
            txtSize = itemView.findViewById(R.id.itemgiohangsize);
            txtTongTien = itemView.findViewById(R.id.item_giohang_tongtien);
            btnCong = itemView.findViewById(R.id.item_giohang_cong);
            btnTru = itemView.findViewById(R.id.item_giohang_tru);

        }
    }
}
