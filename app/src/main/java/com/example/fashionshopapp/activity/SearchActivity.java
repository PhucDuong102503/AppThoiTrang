package com.example.fashionshopapp.activity;

import android.content.Intent; // ⭐ THÊM IMPORT NÀY
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fashionshopapp.Interface.ItemClickListener; // ⭐ THÊM IMPORT NÀY
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

        // ⭐⭐ SỬA LỖI Ở ĐÂY: Thay thế 'null' bằng một ItemClickListener hợp lệ ⭐⭐
        sanPhamAdapter = new SanPhamMoiAdapter(this, sanPhamMoiList, (view, pos, isLongClick) -> {
            // Khi người dùng click vào một item, thực hiện hành động này:
            if (!isLongClick) {
                // 1. Tạo một Intent để mở ChiTietActivity
                Intent intent = new Intent(SearchActivity.this, ChiTietActivity.class);
                // 2. Đính kèm dữ liệu của sản phẩm được click vào Intent
                intent.putExtra("chitiet", sanPhamMoiList.get(pos));
                // 3. Khởi động Activity mới
                startActivity(intent);
            }
        });

        recyclerView.setAdapter(sanPhamAdapter);

        // Bắt sự kiện người dùng nhập văn bản với độ trễ (debounce)
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (searchRunnable != null) {
                    handler.removeCallbacks(searchRunnable);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                searchRunnable = () -> {
                    String keyword = s.toString().trim();
                    if (keyword.isEmpty()) {
                        sanPhamMoiList.clear();
                        sanPhamAdapter.notifyDataSetChanged();
                    } else {
                        searchProduct(keyword);
                    }
                };
                handler.postDelayed(searchRunnable, 500);
            }
        });
    }

    private void searchProduct(String keyword) {
        sanPhamMoiList.clear();

        compositeDisposable.add(apiBanHang.search(keyword)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        sanPhamMoiModel -> {
                            if (sanPhamMoiModel.isSuccess()) {
                                sanPhamMoiList.addAll(sanPhamMoiModel.getResult());
                            }
                            sanPhamAdapter.notifyDataSetChanged();
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
