package com.example.fashionshopapp.activity;

import android.os.Bundle;
import android.os.Handler;import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fashionshopapp.R;
import com.example.fashionshopapp.adapter.SanPhamMoiAdapter;
import com.example.fashionshopapp.model.SanPhamMoi;
import com.example.fashionshopapp.retrofit.ApiBanHang;
import com.example.fashionshopapp.retrofit.RetrofitClient;
import com.example.fashionshopapp.util.Utils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SearchActivity extends AppCompatActivity {

    Toolbar toolbar;
    EditText edtSearch;
    RecyclerView recyclerView;
    SanPhamMoiAdapter sanPhamAdapter;
    List<SanPhamMoi> sanPhamMoiList;
    ApiBanHang apiBanHang;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    private Handler handler = new Handler();
    private Runnable searchRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initView();
        ActionToolBar();
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar_search);
        edtSearch = findViewById(R.id.edt_search);
        recyclerView = findViewById(R.id.recycleview_search);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        sanPhamMoiList = new ArrayList<>();
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);

        // ⭐ ĐÂY LÀ DÒNG ĐÃ ĐƯỢC SỬA LẠI
        sanPhamAdapter = new SanPhamMoiAdapter(this, sanPhamMoiList, null);

        recyclerView.setAdapter(sanPhamAdapter);

        // Bắt sự kiện người dùng nhập văn bản với độ trễ (debounce)
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Hủy bỏ lệnh tìm kiếm cũ nếu có
                if (searchRunnable != null) {
                    handler.removeCallbacks(searchRunnable);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Tạo một lệnh tìm kiếm mới sẽ chạy sau 500ms
                searchRunnable = () -> {
                    String keyword = s.toString().trim();
                    if (keyword.isEmpty()) {
                        sanPhamMoiList.clear();
                        sanPhamAdapter.notifyDataSetChanged();
                    } else {
                        searchProduct(keyword);
                    }
                };
                handler.postDelayed(searchRunnable, 500); // Độ trễ 0.5 giây
            }
        });
    }

    private void searchProduct(String keyword) {
        sanPhamMoiList.clear(); // Xóa kết quả cũ trước khi hiển thị kết quả mới

        compositeDisposable.add(apiBanHang.search(keyword)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        sanPhamMoiModel -> {
                            if (sanPhamMoiModel.isSuccess()) {
                                sanPhamMoiList.addAll(sanPhamMoiModel.getResult());
                            } else {
                                // Nếu server trả về success=false, hiển thị thông báo
                                // Toast.makeText(this, sanPhamMoiModel.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                            sanPhamAdapter.notifyDataSetChanged(); // Cập nhật lại giao diện
                        },
                        throwable -> {
                            Toast.makeText(this, "Lỗi kết nối: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                ));
    }

    private void ActionToolBar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}
