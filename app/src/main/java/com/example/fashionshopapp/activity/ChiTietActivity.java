package com.example.fashionshopapp.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.fashionshopapp.R;
import com.example.fashionshopapp.model.AppDatabase;
import com.example.fashionshopapp.model.GioHang;
import com.example.fashionshopapp.model.SanPhamMoi;
import com.example.fashionshopapp.model.SanPhamSize;
import com.example.fashionshopapp.retrofit.ApiBanHang;
import com.example.fashionshopapp.retrofit.RetrofitClient;
import com.example.fashionshopapp.util.Utils;

import java.text.DecimalFormat;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ChiTietActivity extends AppCompatActivity {

    TextView txtTen, txtGia, txtMoTa;
    Button btnThemVaoGio;
    ImageView imgHinhAnh;
    Spinner spinnerSize, spinnerSoLuong;
    Toolbar toolbar;
    LinearLayout layoutSizeSelection;

    SanPhamMoi sanPhamMoi;
    AppDatabase appDatabase;
    ApiBanHang apiBanHang;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chi_tiet);

        initView();
        initData();
        ActionToolBar();

        if (sanPhamMoi != null) {
            getSanPhamSize();
            initSpinnerSoLuong();
        }

        btnThemVaoGio.setOnClickListener(v -> themVaoGio());
    }

    private void themVaoGio() {
        if (sanPhamMoi.getIdloaisanpham() == 5) {
            themPhuKienVaoGio();
        } else {
            themSanPhamCoSizeVaoGio();
        }
    }

    private void themPhuKienVaoGio() {
        final int soLuongMua = Integer.parseInt(spinnerSoLuong.getSelectedItem().toString());
        final int sanPhamIdThuc = sanPhamMoi.getId();
        final int sizeIdMacDinh = 0; // Phụ kiện luôn có sizeId = 0

        compositeDisposable.add(appDatabase.gioHangDAO().getProductByPrimaryKey(sanPhamIdThuc, sizeIdMacDinh)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        existingCartItem -> {
                            existingCartItem.setSoluong(existingCartItem.getSoluong() + soLuongMua);
                            insertOrUpdateDatabase(existingCartItem, "Đã cập nhật số lượng trong giỏ");
                        },
                        throwable -> {
                            GioHang newCartItem = new GioHang();
                            newCartItem.setIdsp(sanPhamIdThuc); // ID thật
                            newCartItem.setTensp(sanPhamMoi.getTensanpham());
                            newCartItem.setGiasp(Long.parseLong(sanPhamMoi.getGiasanpham()));
                            newCartItem.setHinhanh(sanPhamMoi.getHinhanhsanpham());
                            newCartItem.setSoluong(soLuongMua);
                            newCartItem.setSizeId(sizeIdMacDinh); // ID Size mặc định là 0
                            newCartItem.setSize("Phụ kiện");
                            insertOrUpdateDatabase(newCartItem, "Đã thêm sản phẩm vào giỏ hàng");
                        }
                ));
    }

    private void themSanPhamCoSizeVaoGio() {
        if (spinnerSize.getSelectedItem() == null) {
            Toast.makeText(this, "Vui lòng chọn size sản phẩm", Toast.LENGTH_SHORT).show();
            return;
        }

        SanPhamSize selectedSize = (SanPhamSize) spinnerSize.getSelectedItem();
        int soLuongMua = Integer.parseInt(spinnerSoLuong.getSelectedItem().toString());

        if (soLuongMua > selectedSize.getSoluong()) {
            Toast.makeText(this, "Số lượng trong kho không đủ!", Toast.LENGTH_SHORT).show();
            return;
        }

        final int sanPhamIdThuc = sanPhamMoi.getId(); // ID thật của sản phẩm
        final int sizeIdThuc = selectedSize.getSize_id(); // ID thật của size đã chọn

        // THAY ĐỔI: Gọi hàm DAO mới với 2 tham số
        compositeDisposable.add(appDatabase.gioHangDAO().getProductByPrimaryKey(sanPhamIdThuc, sizeIdThuc)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        existingCartItem -> {
                            existingCartItem.setSoluong(existingCartItem.getSoluong() + soLuongMua);
                            insertOrUpdateDatabase(existingCartItem, "Đã cập nhật số lượng trong giỏ");
                        },
                        throwable -> {
                            GioHang newCartItem = new GioHang();
                            // THAY ĐỔI: Gán ID sản phẩm thật, không nhân chia gì cả
                            newCartItem.setIdsp(sanPhamIdThuc);
                            newCartItem.setTensp(sanPhamMoi.getTensanpham());
                            newCartItem.setGiasp(Long.parseLong(sanPhamMoi.getGiasanpham()));
                            newCartItem.setHinhanh(sanPhamMoi.getHinhanhsanpham());
                            newCartItem.setSoluong(soLuongMua);
                            // THAY ĐỔI: Gán ID size thật
                            newCartItem.setSizeId(sizeIdThuc);
                            newCartItem.setSize(selectedSize.getTensize());
                            insertOrUpdateDatabase(newCartItem, "Đã thêm sản phẩm vào giỏ hàng");
                        }
                ));
    }

    private void insertOrUpdateDatabase(GioHang gioHang, String message) {
        compositeDisposable.add(appDatabase.gioHangDAO().insertOrReplace(gioHang)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> Toast.makeText(this, message, Toast.LENGTH_SHORT).show(),
                        throwable -> Toast.makeText(this, "Lỗi database: " + throwable.getMessage(), Toast.LENGTH_SHORT).show()
                ));
    }

    // --- Các hàm initView, initData, ActionToolBar, getSanPhamSize, initSpinnerSoLuong không thay đổi ---
    private void initView() {
        txtTen = findViewById(R.id.txttenchitietsp);
        txtGia = findViewById(R.id.txtgiachitietsp);
        txtMoTa = findViewById(R.id.txtmotachitiet);
        btnThemVaoGio = findViewById(R.id.btnthemvaogiohang);
        imgHinhAnh = findViewById(R.id.imgchitiet);
        spinnerSoLuong = findViewById(R.id.spinnersl);
        spinnerSize = findViewById(R.id.spinnerslsize);
        layoutSizeSelection = findViewById(R.id.layout_size_selection);
        toolbar = findViewById(R.id.toobar);

        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
        appDatabase = AppDatabase.getInstance(getApplicationContext());
    }

    private void initData() {
        sanPhamMoi = (SanPhamMoi) getIntent().getSerializableExtra("chitiet");
        if (sanPhamMoi == null) {
            Toast.makeText(this, "Không thể tải thông tin sản phẩm", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        txtTen.setText(sanPhamMoi.getTensanpham());
        txtMoTa.setText(sanPhamMoi.getMotasanpham());
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        txtGia.setText("Giá: " + decimalFormat.format(Double.parseDouble(sanPhamMoi.getGiasanpham())) + "đ");
        Glide.with(this).load(sanPhamMoi.getHinhanhsanpham()).into(imgHinhAnh);
    }

    private void ActionToolBar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Chi tiết sản phẩm");
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void getSanPhamSize() {
        if (sanPhamMoi.getIdloaisanpham() == 5) {
            layoutSizeSelection.setVisibility(View.GONE);
            return;
        }

        layoutSizeSelection.setVisibility(View.VISIBLE);

        compositeDisposable.add(apiBanHang.getSanPhamSize(sanPhamMoi.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        sanPhamSizeModel -> {
                            if (sanPhamSizeModel != null && sanPhamSizeModel.isSuccess()) {
                                List<SanPhamSize> listSize = sanPhamSizeModel.getResult();
                                if (listSize == null || listSize.isEmpty()) {
                                    layoutSizeSelection.setVisibility(View.GONE);
                                    Toast.makeText(getApplicationContext(), "Sản phẩm này tạm hết hàng hoặc chưa có size", Toast.LENGTH_LONG).show();
                                } else {
                                    ArrayAdapter<SanPhamSize> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listSize);
                                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    spinnerSize.setAdapter(adapter);
                                }
                            } else {
                                layoutSizeSelection.setVisibility(View.GONE);
                                Toast.makeText(getApplicationContext(), "Sản phẩm này chưa có size", Toast.LENGTH_SHORT).show();
                            }
                        },
                        throwable -> {
                            layoutSizeSelection.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), "Lỗi khi tải size: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                ));
    }


    private void initSpinnerSoLuong() {
        Integer[] so = new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, so);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSoLuong.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}
