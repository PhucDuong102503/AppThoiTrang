package com.example.fashionshopapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class ThanhToanActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextView txtTongTien, txtSoDienThoai, txtEmail;
    TextInputEditText edtDiaChi;
    Button btnDatHang;
    RecyclerView recyclerView;
    long tongtien;

    ApiBanHang apiBanHang;
    AppDatabase appDatabase;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thanh_toan);
        initView();
        initControl();
        // Lấy tổng tiền từ Intent (màn hình giỏ hàng đã tính)
        getIntentData();
        // Tải danh sách sản phẩm từ RoomDB để hiển thị
        loadProductListFromDb();
    }

    private void getIntentData() {
        tongtien = getIntent().getLongExtra("tongtien", 0);
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        txtTongTien.setText("Tổng tiền: " + decimalFormat.format(tongtien) + "đ");
    }

    private void loadProductListFromDb() {
        compositeDisposable.add(appDatabase.gioHangDAO().getAllCartItems()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        gioHangList -> {
                            ThanhToanAdapter adapter = new ThanhToanAdapter(this, gioHangList);
                            recyclerView.setAdapter(adapter);
                        },
                        throwable -> {
                            Toast.makeText(this, "Không thể tải danh sách sản phẩm", Toast.LENGTH_SHORT).show();
                        }
                )
        );
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
            // Bắt đầu quá trình đặt hàng
            processCheckout(diaChiGiaoHang);
        });
    }

    private void processCheckout(String diaChiGiaoHang) {
        btnDatHang.setEnabled(false);

        // Lấy danh sách sản phẩm MỘT LẦN NỮA từ RoomDB để đảm bảo dữ liệu là mới nhất trước khi gửi đi
        compositeDisposable.add(appDatabase.gioHangDAO().getAllCartItems()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        gioHangList -> {
                            if (gioHangList == null || gioHangList.isEmpty()) {
                                Toast.makeText(getApplicationContext(), "Giỏ hàng của bạn đang trống.", Toast.LENGTH_SHORT).show();
                                btnDatHang.setEnabled(true);
                                return;
                            }
                            // Nếu có sản phẩm, tiến hành gửi lên server
                            postOrderToServer(diaChiGiaoHang, gioHangList);
                        },
                        throwable -> {
                            Toast.makeText(getApplicationContext(), "Lỗi khi lấy sản phẩm từ giỏ hàng: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                            btnDatHang.setEnabled(true);
                        }
                ));
    }

    private void postOrderToServer(String diaChiGiaoHang, List<GioHang> gioHangList) {
        // Chuyển List<GioHang> thành chuỗi JSON. Gson sẽ tự động lấy đúng tên trường.
        String chitietJson = new Gson().toJson(gioHangList);

        // Tạo đối tượng RequestBody để gửi đi
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"),
                "{" +
                        "\"user_id\":" + Utils.user_current.getId() + "," +
                        "\"diachi\":\"" + diaChiGiaoHang + "\"," +
                        "\"tongtien\":" + tongtien + "," +
                        "\"chitiet\":" + chitietJson +
                        "}"
        );

        compositeDisposable.add(apiBanHang.datHang(requestBody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        userModel -> {
                            if (userModel.isSuccess()) {
                                // Nếu đặt hàng thành công trên server, xóa giỏ hàng trong RoomDB
                                clearCartInDatabase();
                            } else {
                                Toast.makeText(getApplicationContext(), userModel.getMessage(), Toast.LENGTH_LONG).show();
                                btnDatHang.setEnabled(true);
                            }
                        },
                        throwable -> {
                            Toast.makeText(getApplicationContext(), "Lỗi kết nối: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                            btnDatHang.setEnabled(true);
                        }
                ));
    }

    private void clearCartInDatabase() {
        // Chỉ cần xóa trong Room Database là đủ
        compositeDisposable.add(appDatabase.gioHangDAO().deleteAllItems()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> {
                            Toast.makeText(getApplicationContext(), "Đặt hàng thành công!", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        },
                        throwable -> {
                            Toast.makeText(getApplicationContext(), "Đặt hàng thành công! (Lỗi khi dọn dẹp giỏ hàng)", Toast.LENGTH_SHORT).show();
                            // Vẫn chuyển về MainActivity dù có lỗi dọn dẹp
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }
                ));
    }

    private void initView() {
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
