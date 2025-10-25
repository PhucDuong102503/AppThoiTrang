package com.example.fashionshopapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fashionshopapp.R;
import com.example.fashionshopapp.model.DonHang;

import java.text.DecimalFormat;
import java.util.List;

public class DonHangAdapter extends RecyclerView.Adapter<DonHangAdapter.MyViewHolder> {
    private Context context;
    private List<DonHang> listDonHang;

    public DonHangAdapter(Context context, List<DonHang> listDonHang) {
        this.context = context;
        this.listDonHang = listDonHang;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_donhang, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        DonHang donHang = listDonHang.get(position);
        holder.txtMaDonHang.setText("Mã đơn hàng: #" + donHang.getId());
        //DecimalFormat decimalFormat = new DecimalFormat("###,###,###'đ'");
        //holder.txtTongTien.setText("Tổng tiền: " + decimalFormat.format(donHang.getTongtien()));

        try {
            // Chỉ khai báo DecimalFormat một lần ở đây
            DecimalFormat decimalFormat = new DecimalFormat("###,###,###'đ'");

            // Chuyển đổi String từ API thành số để định dạng
            double tongTienValue = Double.parseDouble(donHang.getTongtien());

            // Định dạng và hiển thị
            holder.txtTongTien.setText("Tổng tiền: " + decimalFormat.format(tongTienValue));
        } catch (NumberFormatException e) {
            // Nếu có lỗi (ví dụ: giá trị không phải là số), hiển thị giá trị gốc
            holder.txtTongTien.setText("Tổng tiền: " + donHang.getTongtien() + "đ");
        }

        // RecyclerView con để hiển thị chi tiết sản phẩm
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                holder.recyclerChiTiet.getContext(),
                LinearLayoutManager.VERTICAL,
                false
        );
        layoutManager.setInitialPrefetchItemCount(donHang.getChitiet().size());

        // Adapter cho chi tiết
        ChiTietDonHangAdapter chiTietAdapter = new ChiTietDonHangAdapter(context, donHang.getChitiet());
        holder.recyclerChiTiet.setLayoutManager(layoutManager);
        holder.recyclerChiTiet.setAdapter(chiTietAdapter);
    }

    @Override
    public int getItemCount() {
        return listDonHang.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtMaDonHang, txtTongTien;
        RecyclerView recyclerChiTiet;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtMaDonHang = itemView.findViewById(R.id.iddonhang);
            txtTongTien = itemView.findViewById(R.id.tongtiendonhang);
            recyclerChiTiet = itemView.findViewById(R.id.recycleview_chitiet);
        }
    }
}
