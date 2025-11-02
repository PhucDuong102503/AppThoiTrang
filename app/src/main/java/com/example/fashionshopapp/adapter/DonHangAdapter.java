package com.example.fashionshopapp.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fashionshopapp.R;
import com.example.fashionshopapp.model.DonHang;
// ⭐ Đảm bảo chỉ có duy nhất dòng import Item này
import com.example.fashionshopapp.model.Item;

import java.text.DecimalFormat;
import java.util.List;

public class DonHangAdapter extends RecyclerView.Adapter<DonHangAdapter.MyViewHolder> {

    public interface OnHuyDonClickListener {
        void onHuyDonClick(DonHang donHang);
    }

    private final Context context;
    private final List<DonHang> listDonHang;
    private final OnHuyDonClickListener listener;

    public DonHangAdapter(Context context, List<DonHang> listDonHang, OnHuyDonClickListener listener) {
        this.context = context;
        this.listDonHang = listDonHang;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_donhang, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        // Lấy ra đối tượng DonHang tương ứng với vị trí item
        DonHang donHang = listDonHang.get(position);

        // --- Phần hiển thị thông tin chung của đơn hàng (giữ nguyên) ---
        holder.txtMaDonHang.setText("Đơn hàng #" + donHang.getId());
        holder.txtTrangThai.setText(donHang.getTrangthai());
        holder.txtNgayDat.setText("Ngày đặt: " + donHang.getNgaydathang());

        try {
            DecimalFormat decimalFormat = new DecimalFormat("###,###,###'đ'");
            double tongTienValue = Double.parseDouble(donHang.getTongtien());
            holder.txtTongTien.setText("Tổng tiền: " + decimalFormat.format(tongTienValue));
        } catch (NumberFormatException e) {
            holder.txtTongTien.setText("Tổng tiền: " + donHang.getTongtien() + "đ");
        }

        // --- Logic hiển thị nút Hủy đơn (giữ nguyên) ---
        if ("Chờ giao hàng".equals(donHang.getTrangthai())) {
            holder.btnHuyDon.setVisibility(View.VISIBLE);
            holder.btnHuyDon.setOnClickListener(v -> {
                if (listener != null) {
                    new AlertDialog.Builder(context)
                            .setTitle("Xác nhận hủy đơn hàng")
                            .setMessage("Bạn có chắc chắn muốn hủy đơn hàng #" + donHang.getId() + "?")
                            .setPositiveButton("Đồng ý", (dialog, which) -> listener.onHuyDonClick(donHang))
                            .setNegativeButton("Không", null)
                            .show();
                }
            });
        } else {
            holder.btnHuyDon.setVisibility(View.GONE);
        }

        // --- ⭐⭐⭐ SỬA LỖI TẠI ĐÂY: CÀI ĐẶT RECYCLERVIEW CON ⭐⭐⭐ ---

        // 1. Tạo LayoutManager cho RecyclerView con
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                holder.recyclerChiTiet.getContext(),
                LinearLayoutManager.VERTICAL,
                false
        );
        holder.recyclerChiTiet.setLayoutManager(layoutManager); // Set layout manager trước

        // 2. Kiểm tra xem đơn hàng có danh sách sản phẩm (items) không
        if (donHang.getItems() != null && !donHang.getItems().isEmpty()) {
            // 3. ⭐ SỬA DÒNG NÀY: Khởi tạo ChiTietDonHangAdapter và truyền `donHang` vào
            ChiTietDonHangAdapter chiTietAdapter = new ChiTietDonHangAdapter(context, donHang.getItems(), donHang);

            // 4. Set Adapter cho RecyclerView con
            holder.recyclerChiTiet.setAdapter(chiTietAdapter);
        }
    }

    @Override
    public int getItemCount() {
        return listDonHang.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtMaDonHang, txtTongTien, txtTrangThai, txtNgayDat;
        RecyclerView recyclerChiTiet;
        Button btnHuyDon;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtMaDonHang = itemView.findViewById(R.id.iddonhang);
            txtTongTien = itemView.findViewById(R.id.tongtiendonhang);
            txtTrangThai = itemView.findViewById(R.id.trangthaidonhang);
            txtNgayDat = itemView.findViewById(R.id.ngaydatdonhang);
            recyclerChiTiet = itemView.findViewById(R.id.recycleview_chitiet);
            btnHuyDon = itemView.findViewById(R.id.btn_huydon);
        }
    }
}
