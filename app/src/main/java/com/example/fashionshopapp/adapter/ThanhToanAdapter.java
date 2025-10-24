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
import com.example.fashionshopapp.R;
import com.example.fashionshopapp.model.GioHang;

import java.util.List;

public class ThanhToanAdapter extends RecyclerView.Adapter<ThanhToanAdapter.MyViewHolder> {

    Context context;
    List<GioHang> gioHangList;

    public ThanhToanAdapter(Context context, List<GioHang> gioHangList) {
        this.context = context;
        this.gioHangList = gioHangList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_thanhtoan, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        GioHang gioHang = gioHangList.get(position);
        holder.txtTenSp.setText(gioHang.getTensp());
        holder.txtSoLuong.setText("x" + gioHang.getSoluong());
        Glide.with(context).load(gioHang.getHinhanh()).into(holder.imgAnh);

        // Logic hiển thị size
        if (gioHang.getSize() != null && !gioHang.getSize().isEmpty()) {
            holder.txtSize.setText("Size: " + gioHang.getSize());
            holder.txtSize.setVisibility(View.VISIBLE);
        } else {
            holder.txtSize.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return gioHangList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAnh;
        TextView txtTenSp, txtSize, txtSoLuong;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAnh = itemView.findViewById(R.id.item_thanhtoan_image);
            txtTenSp = itemView.findViewById(R.id.item_thanhtoan_tensp);
            txtSize = itemView.findViewById(R.id.item_thanhtoan_size);
            txtSoLuong = itemView.findViewById(R.id.item_thanhtoan_soluong);
        }
    }
}
