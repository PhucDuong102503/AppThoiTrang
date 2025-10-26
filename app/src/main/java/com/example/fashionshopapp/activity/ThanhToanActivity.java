package com.example.fashionshopapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log; // ⭐ Thêm import Log
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fashionshopapp.R;
import com.example.fashionshopapp.adapter.ThanhToanAdapter;
import com.example.fashionshopapp.model.AppDatabase;
import com.example.fashionshopapp.model.GioHang;
import com.example.fashionshopapp.retrofit.ApiBanHang;
import com.example.fashionshopapp.retrofit.RetrofitClient;
import com.example.fashionshopapp.util.Utils;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
// okhttp3.RequestBody không còn cần thiết, chúng ta sẽ gửi dưới dạng @Field

public class ThanhToanActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextView txtTongTien, txtSoDienThoai, txtEmail;
    TextInputEditText edtDiaChi;
    Button btnDatHang;
    RecyclerView recyclerView;
    long tongtien;
    List<GioHang> danhSachDaChon;

    ApiBanHang apiBanHang;
    AppDatabase appDatabase; // Giữ lại để xóa sản phẩm trong RoomDB
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    int tongSoLuongSanPham; // ⭐ BIẾN MỚI: Để lưu tổng số lượng sản phẩm

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thanh_toan);
        initView();
        getIntentData();
        initControl(); // Chuyển initControl() xuống sau để danhSachDaChon được khởi tạo trước
        displaySelectedProducts();
        countItem(); // ⭐ GỌI HÀM MỚI: Tính tổng số lượng
    }

    private void getIntentData() {
        tongtien = getIntent().getLongExtra("tongtien", 0);
        danhSachDaChon = (List<GioHang>) getIntent().getSerializableExtra("danhsachmua");
        if (danhSachDaChon == null) {
            danhSachDaChon = new ArrayList<>();
        }

        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        txtTongTien.setText(decimalFormat.format(tongtien) + "đ");
    }

    // ⭐ HÀM MỚI: Để tính tổng số lượng sản phẩm
    private void countItem() {
        tongSoLuongSanPham = 0;
        if (danhSachDaChon != null) {
            for (int i = 0; i < danhSachDaChon.size(); i++) {
                tongSoLuongSanPham = tongSoLuongSanPham + danhSachDaChon.get(i).getSoluong();
            }
        }
    }

    private void displaySelectedProducts() {
        if (danhSachDaChon != null && !danhSachDaChon.isEmpty()) {
            ThanhToanAdapter adapter = new ThanhToanAdapter(this, danhSachDaChon);
            recyclerView.setAdapter(adapter);
        }
    }

    private void initControl() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        if (Utils.user_current != null) {
            txtEmail.setText(Utils.user_current.getEmail());
            txtSoDienThoai.setText(Utils.user_current.getSodienthoai());
            if (Utils.user_current.getDiachi() != null && !Utils.user_current.getDiachi().isEmpty()) {
                edtDiaChi.setText(Utils.user_current.getDiachi());
            }
        }

        btnDatHang.setOnClickListener(v -> {
            String diaChiGiaoHang = edtDiaChi.getText().toString().trim();
            if (TextUtils.isEmpty(diaChiGiaoHang)) {
                Toast.makeText(getApplicationContext(), "Vui lòng nhập địa chỉ giao hàng", Toast.LENGTH_SHORT).show();
                return;
            }
            if (Utils.user_current == null) {
                Toast.makeText(getApplicationContext(), "Lỗi: Không tìm thấy thông tin người dùng. Vui lòng đăng nhập lại.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (danhSachDaChon == null || danhSachDaChon.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Không có sản phẩm nào được chọn để đặt hàng.", Toast.LENGTH_SHORT).show();
                return;
            }

            btnDatHang.setEnabled(false); // Vô hiệu hóa nút để tránh click nhiều lần

            // ⭐ GỌI HÀM ĐẶT HÀNG ĐÃ SỬA
            postOrderToServer(diaChiGiaoHang, danhSachDaChon);
        });
    }

    // ⭐ SỬA LẠI HÀM NÀY ĐỂ GỬI DỮ LIỆU DẠNG @Field
    private void postOrderToServer(String diaChiGiaoHang, List<GioHang> gioHangList) {
        String chitietJson = new Gson().toJson(gioHangList);
        String tongtien_str = String.valueOf(tongtien);

        // Lấy thông tin người dùng
        int user_id = Utils.user_current.getId();
        String sodienthoai = Utils.user_current.getSodienthoai();
        String email = Utils.user_current.getEmail();

        compositeDisposable.add(apiBanHang.datHang(user_id, diaChiGiaoHang, sodienthoai, email, tongSoLuongSanPham, tongtien_str, chitietJson)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        messageModel -> { // ⭐ Thay đổi: Giờ server trả về MessageModel
                            if (messageModel.isSuccess()) {
                                clearPurchasedItemsInDatabase(gioHangList); // Gọi hàm xóa sản phẩm đã mua
                            } else {
                                Toast.makeText(getApplicationContext(), messageModel.getMessage(), Toast.LENGTH_LONG).show();
                                btnDatHang.setEnabled(true); // Mở lại nút nếu có lỗi
                            }
                        },
                        throwable -> {
                            Toast.makeText(getApplicationContext(), "Lỗi kết nối: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.e("ThanhToanActivity", "postOrderToServer error: " + throwable.getMessage());
                            btnDatHang.setEnabled(true); // Mở lại nút nếu có lỗi
                        }
                ));
    }

    // Hàm này giữ nguyên để xóa sản phẩm trong RoomDB sau khi đặt hàng thành công
    private void clearPurchasedItemsInDatabase(List<GioHang> purchasedItems) {
        for (GioHang item : purchasedItems) {
            appDatabase.gioHangDAO().deleteByPrimaryKey(item.getIdsp(), item.getSizeId())
                    .subscribeOn(Schedulers.io())
                    .subscribe();
        }

        Toast.makeText(getApplicationContext(), "Đặt hàng thành công!", Toast.LENGTH_LONG).show();
        // Xóa danh sách mua hàng tạm thời trong Utils nếu có
        if (Utils.mangmuahang != null) {
            Utils.mangmuahang.clear();
        }

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void initView() {
        // ID của bạn có thể khác, tôi giữ nguyên ID từ file bạn cung cấp
        toolbar = findViewById(R.id.toolbarthanhtoan);
        txtTongTien = findViewById(R.id.txtTongTienThanhToan);
        txtSoDienThoai = findViewById(R.id.txtSdtThanhToan);
        txtEmail = findViewById(R.id.txtEmailThanhToan);
        edtDiaChi = findViewById(R.id.edtDiaChiThanhToan);
        btnDatHang = findViewById(R.id.btnDatHang);
        recyclerView = findViewById(R.id.recycleview_sanpham_thanhtoan);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
        appDatabase = AppDatabase.getInstance(this);
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}
