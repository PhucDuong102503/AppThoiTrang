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
// ⭐ QUAN TRỌNG: Đổi import từ GioHang sang Item
import com.example.fashionshopapp.model.Item;

import java.util.List;

// ⭐ ĐÃ SỬA ĐỂ LÀM VIỆC VỚI List<Item>
public class ChiTietDonHangAdapter extends RecyclerView.Adapter<ChiTietDonHangAdapter.MyViewHolder> {

    private final Context context;
    // ⭐ Sửa kiểu dữ liệu của list
    private final List<Item> itemList;

    // ⭐ Sửa constructor để nhận List<Item>
    public ChiTietDonHangAdapter(Context context, List<Item> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Dùng lại layout item_thanhtoan vì nó phù hợp
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_thanhtoan, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        // ⭐ Giờ đây 'item' là đối tượng của lớp Item
        Item item = itemList.get(position);

        // ⭐ Lấy tên sản phẩm từ getTensanpham() của model Item
        holder.txtTenSp.setText(item.getTensanpham());
        holder.txtSoLuong.setText("Số lượng: " + item.getSoluong());

        // ⭐ Lấy ảnh từ getHinhanhsanpham() của model Item
        Glide.with(context)
                .load(item.getHinhanhsanpham())
                .placeholder(R.drawable.ic_media_24)
                .into(holder.imgAnh);

        // ⭐ Lấy tên size từ getTensize() của model Item
        if (item.getTensize() != null && !item.getTensize().isEmpty()) {
            holder.txtSize.setText("Size: " + item.getTensize());
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
