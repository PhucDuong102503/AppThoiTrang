package com.example.fashionshopapp.adapter;import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fashionshopapp.R;
import com.example.fashionshopapp.activity.WriteReviewActivity;
import com.example.fashionshopapp.model.DonHang;
import com.example.fashionshopapp.model.Item;
import com.example.fashionshopapp.model.SanPhamMoi;
import com.example.fashionshopapp.util.Utils;

import java.util.List;

public class ChiTietDonHangAdapter extends RecyclerView.Adapter<ChiTietDonHangAdapter.MyViewHolder> {

    private final Context context;
    private final List<Item> itemList;
    private final DonHang donHang; //Lưu thông tin đơn hàng cha

    public ChiTietDonHangAdapter(Context context, List<Item> itemList, DonHang donHang) {
        this.context = context;
        this.itemList = itemList;
        this.donHang = donHang; // Lưu lại
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Layout này cần có Button với id là 'btn_item_danhgia'
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_thanhtoan, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Item item = itemList.get(position);

        holder.txtTenSp.setText(item.getTensanpham());
        holder.txtSoLuong.setText("x" + item.getSoluong());

        String imageUrl = item.getHinhanhsanpham();
        // Giả sử API trả về đường dẫn tương đối
        if (imageUrl != null && !imageUrl.startsWith("http")) {
            imageUrl = Utils.BASE_URL + "images/" + imageUrl;
        }
        Glide.with(context).load(imageUrl).placeholder(R.drawable.ic_media_24).into(holder.imgAnh);

        if (item.getTensize() != null && !item.getTensize().isEmpty()) {
            holder.txtSize.setText("Size: " + item.getTensize());
            holder.txtSize.setVisibility(View.VISIBLE);
        } else {
            holder.txtSize.setVisibility(View.GONE);
        }

        // Chỉ hiển thị nút "Đánh giá" nếu đơn hàng đã giao thành công
        if (donHang != null && "Đã giao hàng".equals(donHang.getTrangthai())) {
            holder.btnDanhGia.setVisibility(View.VISIBLE);
        } else {
            holder.btnDanhGia.setVisibility(View.GONE);
        }

        holder.btnDanhGia.setOnClickListener(v -> {
            Intent intent = new Intent(context, WriteReviewActivity.class);

            SanPhamMoi sanPhamReview = new SanPhamMoi();
            sanPhamReview.setId(item.getSanpham_id()); // Cần có getSanpham_id() trong model Item
            sanPhamReview.setTensanpham(item.getTensanpham());
            sanPhamReview.setHinhanhsanpham(item.getHinhanhsanpham());

            intent.putExtra("san_pham_review", sanPhamReview);
            intent.putExtra("don_hang_id", donHang.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        if (itemList == null) return 0;
        return itemList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAnh;
        TextView txtTenSp, txtSize, txtSoLuong;
        Button btnDanhGia;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAnh = itemView.findViewById(R.id.item_thanhtoan_image);
            txtTenSp = itemView.findViewById(R.id.item_thanhtoan_tensp);
            txtSize = itemView.findViewById(R.id.item_thanhtoan_size);
            txtSoLuong = itemView.findViewById(R.id.item_thanhtoan_soluong);
            btnDanhGia = itemView.findViewById(R.id.btn_item_danhgia);
        }
    }
}
