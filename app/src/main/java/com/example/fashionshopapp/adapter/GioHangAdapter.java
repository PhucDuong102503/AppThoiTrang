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
        // << 1. LẤY SẢN PHẨM MỘT CÁCH AN TOÀN >>
        // Sử dụng holder.getAdapterPosition() để đảm bảo lấy đúng vị trí ngay cả khi có thay đổi
        final int currentPosition = holder.getAdapterPosition();
        // Kiểm tra để tránh lỗi "No position" khi RecyclerView đang cập nhật
        if (currentPosition == RecyclerView.NO_POSITION) {
            return;
        }
        GioHang gioHang = gioHangList.get(currentPosition);

        // Gán dữ liệu lên các View
        holder.txtTenSp.setText(gioHang.getTensp());
        holder.txtSoLuong.setText("Số lượng: " + gioHang.getSoluong());

        // << 2. LOGIC ẨN/HIỆN SIZE ĐÃ ĐƯỢC THÊM VÀO >>
        // Kiểm tra xem sản phẩm này có thông tin size hay không
        if (gioHang.getSize() != null && !gioHang.getSize().isEmpty()) {
            // Nếu CÓ size (size không phải null và không phải chuỗi rỗng)
            holder.txtSize.setText("Size: " + gioHang.getSize());
            holder.txtSize.setVisibility(View.VISIBLE); // Hiện TextView lên
        } else {
            // Nếu KHÔNG có size (là phụ kiện)
            holder.txtSize.setVisibility(View.GONE); // Ẩn TextView đi
        }

        // Định dạng giá tiền cho dễ đọc
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        holder.txtGiaSp.setText("Giá: " + decimalFormat.format(gioHang.getGiasp()) + "đ");

        // Tính tổng tiền cho từng item
        long tongTien = gioHang.getGiasp() * gioHang.getSoluong();
        holder.txtTongTien.setText("Tổng: " + decimalFormat.format(tongTien) + "đ");

        // Tải hình ảnh sản phẩm bằng Glide
        Glide.with(context).load(gioHang.getHinhanh()).into(holder.imgAnh);

        // Gán sự kiện click cho nút xóa
        holder.imgXoa.setOnClickListener(v -> {
            // << 3. TRUYỀN VỊ TRÍ AN TOÀN KHI CLICK >>
            // Gọi đến hàm onItemClick và truyền vào vị trí chính xác tại thời điểm click
            itemClickListener.onItemClick(v, currentPosition, 3); // 3 là mã quy ước cho sự kiện xóa
        });
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
        }
    }
}
