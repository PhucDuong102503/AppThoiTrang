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

import java.text.DecimalFormat;
import java.util.List;

public class ChiTietDonHangAdapter extends RecyclerView.Adapter<ChiTietDonHangAdapter.MyViewHolder> {

    private Context context;
    private List<GioHang> itemList;

    public ChiTietDonHangAdapter(Context context, List<GioHang> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Chúng ta sẽ dùng lại layout item_thanhtoan.xml vì nó phù hợp
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_thanhtoan, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        GioHang item = itemList.get(position);

        holder.txtTenSp.setText(item.getTensp());
        holder.txtSoLuong.setText("x" + item.getSoluong());

        Glide.with(context)
                .load(item.getHinhanh())
                .placeholder(R.drawable.ic_media_24)
                .into(holder.imgAnh);

        if (item.getSize() != null && !item.getSize().isEmpty() && !item.getSize().equalsIgnoreCase("Phụ kiện")) {
            holder.txtSize.setText("Size: " + item.getSize());
            holder.txtSize.setVisibility(View.VISIBLE);
        } else {
            holder.txtSize.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
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
