package com.example.fashionshopapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
// ⭐ SỬA LẠI IMPORT: Thêm JsonElement
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ThanhToanActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextView txtTongTien, txtSoDienThoai, txtEmail;
    TextInputEditText edtDiaChi;
    Button btnDatHang, btnThanhToanVNPAY; // Nút Đặt hàng (COD) và nút VNPAY
    RecyclerView recyclerView;
    long tongtien;
    List<GioHang> danhSachDaChon;
    int tongSoLuongSanPham;

    ApiBanHang apiBanHang;
    AppDatabase appDatabase;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    // Biến để nhận kết quả từ VNPAYActivity
    private ActivityResultLauncher<Intent> vnpayLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thanh_toan);
        initView();
        getIntentData();
        initControl(); // initControl() phải được gọi sau initView() và getIntentData()
        displaySelectedProducts();
        countItem();
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbarthanhtoan);
        txtTongTien = findViewById(R.id.txtTongTienThanhToan);
        txtSoDienThoai = findViewById(R.id.txtSdtThanhToan);
        txtEmail = findViewById(R.id.txtEmailThanhToan);
        edtDiaChi = findViewById(R.id.edtDiaChiThanhToan);
        btnDatHang = findViewById(R.id.btnDatHang);
        btnThanhToanVNPAY = findViewById(R.id.btnThanhToanVNPAY); // Ánh xạ nút VNPAY
        recyclerView = findViewById(R.id.recycleview_sanpham_thanhtoan);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
        appDatabase = AppDatabase.getInstance(this);
    }

    private void initControl() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        // Đăng ký Launcher để nhận kết quả từ VNPAYActivity
        vnpayLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == AppCompatActivity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            String status = data.getStringExtra("status");
                            if ("success".equals(status)) {
                                // Nếu VNPAY thành công, lúc này mới gọi hàm đặt hàng lên server
                                Toast.makeText(this, "Thanh toán VNPAY thành công! Đang tạo đơn hàng...", Toast.LENGTH_SHORT).show();
                                datHang("vnpay");
                            } else {
                                Toast.makeText(this, "Giao dịch VNPAY thất bại hoặc đã bị hủy.", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }
        );

        // Hiển thị thông tin người dùng
        if (Utils.user_current != null) {
            txtEmail.setText(Utils.user_current.getEmail());
            txtSoDienThoai.setText(Utils.user_current.getSodienthoai());
            edtDiaChi.setText(Utils.user_current.getDiachi());
        }

        // Sự kiện cho nút Đặt hàng (COD)
        btnDatHang.setOnClickListener(v -> datHang("cod"));

        // Sự kiện cho nút Thanh toán VNPAY
        btnThanhToanVNPAY.setOnClickListener(v -> {
            if (TextUtils.isEmpty(edtDiaChi.getText().toString().trim())) {
                Toast.makeText(getApplicationContext(), "Vui lòng nhập địa chỉ giao hàng", Toast.LENGTH_SHORT).show();
                return;
            }
            if (danhSachDaChon == null || danhSachDaChon.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Không có sản phẩm để thanh toán", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                String tongTienString = txtTongTien.getText().toString()
                        .replace(",", "") // Thay thế dấu phẩy
                        .replace(".", "") // Thay thế dấu chấm (dự phòng)
                        .replace("đ", "")  // Bỏ chữ "đ"
                        .trim();               // Cắt khoảng trắng thừa

                long tongTienThanhToan = Long.parseLong(tongTienString);

                Log.d("VNPAY_DEBUG", "Số tiền đã xử lý để gửi đi: " + tongTienThanhToan);

                btnThanhToanVNPAY.setEnabled(false); // Vô hiệu hóa nút

                // Gọi API để lấy URL thanh toán
                compositeDisposable.add(apiBanHang.createVnpayPayment(tongTienThanhToan)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                (JsonElement response) -> { // SỬA 1: Nhận response là JsonElement
                                    btnThanhToanVNPAY.setEnabled(true); // Mở lại nút

                                    // SỬA 2: Kiểm tra và chuyển đổi an toàn sang JsonObject
                                    if (response != null && response.isJsonObject()) {
                                        JsonObject jsonObject = response.getAsJsonObject(); // Chuyển đổi an toàn

                                        if (jsonObject.has("code") && "00".equals(jsonObject.get("code").getAsString())) {
                                            String paymentUrl = jsonObject.get("data").getAsString();
                                            Log.d("VNPAY_DEBUG", "URL nhận được: " + paymentUrl);

                                            // Mở VNPAYActivity với URL nhận được
                                            Intent intent = new Intent(ThanhToanActivity.this, VNPAYActivity.class);
                                            intent.putExtra("url", paymentUrl);
                                            vnpayLauncher.launch(intent);
                                        } else {
                                            Toast.makeText(this, "Không thể tạo yêu cầu thanh toán. Phản hồi từ server không hợp lệ.", Toast.LENGTH_LONG).show();
                                            Log.e("VNPAY_ERROR", "Phản hồi server không hợp lệ: " + jsonObject.toString());
                                        }
                                    } else {
                                        Toast.makeText(this, "Phản hồi từ server không phải là định dạng JSON hợp lệ.", Toast.LENGTH_LONG).show();
                                        Log.e("VNPAY_ERROR", "Phản hồi không phải JSON: " + (response != null ? response.toString() : "null"));
                                    }
                                },
                                throwable -> {
                                    btnThanhToanVNPAY.setEnabled(true);
                                    Toast.makeText(this, "Lỗi kết nối khi tạo thanh toán: " + throwable.getMessage(), Toast.LENGTH_LONG).show();
                                    Log.e("VNPAY_ERROR", "Lỗi khi gọi createVnpayPayment: ", throwable);
                                }
                        ));

            } catch (NumberFormatException e) {
                // Bắt lỗi nếu không chuyển đổi chuỗi thành số được
                Toast.makeText(this, "Lỗi định dạng số tiền.", Toast.LENGTH_SHORT).show();
                Log.e("VNPAY_ERROR", "NumberFormatException khi xử lý chuỗi tiền: '" + txtTongTien.getText().toString() + "'", e);
                btnThanhToanVNPAY.setEnabled(true); // Mở lại nút nếu có lỗi
            }
        });
    }

    // Hàm đặt hàng lên server (dùng cho cả COD và VNPAY)
    private void datHang(String phuongThucThanhToan) {
        String diaChiGiaoHang = edtDiaChi.getText().toString().trim();
        if (TextUtils.isEmpty(diaChiGiaoHang)) {
            Toast.makeText(getApplicationContext(), "Vui lòng nhập địa chỉ giao hàng", Toast.LENGTH_SHORT).show();
            return;
        }

        btnDatHang.setEnabled(false);
        btnThanhToanVNPAY.setEnabled(false);

        String chitietJson = new Gson().toJson(danhSachDaChon);
        String tongtien_str = String.valueOf(tongtien);

        // Gửi yêu cầu đặt hàng lên server
        compositeDisposable.add(
                apiBanHang.datHang(Utils.user_current.getId(), diaChiGiaoHang, Utils.user_current.getSodienthoai(), Utils.user_current.getEmail(), tongSoLuongSanPham, tongtien_str, chitietJson)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                messageModel -> {
                                    if (messageModel.isSuccess()) {
                                        clearPurchasedItemsInDatabase(danhSachDaChon);
                                    } else {
                                        Toast.makeText(getApplicationContext(), messageModel.getMessage(), Toast.LENGTH_LONG).show();
                                        btnDatHang.setEnabled(true);
                                        btnThanhToanVNPAY.setEnabled(true);
                                    }
                                },
                                throwable -> {
                                    Toast.makeText(getApplicationContext(), "Lỗi kết nối khi đặt hàng: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                    Log.e("ThanhToanActivity", "datHang error: ", throwable);
                                    btnDatHang.setEnabled(true);
                                    btnThanhToanVNPAY.setEnabled(true);
                                }
                        ));
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

    private void clearPurchasedItemsInDatabase(List<GioHang> purchasedItems) {
        for (GioHang item : purchasedItems) {
            appDatabase.gioHangDAO().deleteByPrimaryKey(item.getIdsp(), item.getSizeId())
                    .subscribeOn(Schedulers.io())
                    .subscribe();
        }
        Toast.makeText(getApplicationContext(), "Đặt hàng thành công!", Toast.LENGTH_LONG).show();
        Utils.mangmuahang.clear();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}
