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
import com.example.fashionshopapp.model.SanPhamMoi;

import java.text.DecimalFormat;
import java.util.List;

public class AoAdapter extends RecyclerView.Adapter<AoAdapter.MyViewHolder> {
    Context context;
    List<SanPhamMoi> array;

    public AoAdapter(Context context, List<SanPhamMoi> array) {
        this.context = context;
        this.array = array;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ao, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        SanPhamMoi sanPham = array.get(position);
        holder.tensp.setText(sanPham.getTensanpham());
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        holder.giasp.setText("Giá: đ" + decimalFormat.format(Double.parseDouble(sanPham.getGiasanpham())));
        holder.motasp.setText(sanPham.getMotasanpham());
        Glide.with(context).load(sanPham.getHinhanhsanpham()).into(holder.hinhanh);
    }

    @Override
    public int getItemCount() {
        return array.size();
    }


    public class  MyViewHolder extends RecyclerView.ViewHolder {
        TextView tensp, giasp, motasp;
        ImageView hinhanh;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tensp = itemView.findViewById(R.id.itemao_ten);
            giasp = itemView.findViewById(R.id.itemao_gia);
            motasp = itemView.findViewById(R.id.itemao_mota);
            hinhanh = itemView.findViewById(R.id.itemao_image);
        }
    }
}
