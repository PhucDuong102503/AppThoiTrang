package com.example.fashionshopapp.activity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fashionshopapp.R;
import com.example.fashionshopapp.adapter.AoAdapter;
import com.example.fashionshopapp.model.SanPhamMoi;
import com.example.fashionshopapp.retrofit.ApiBanHang;
import com.example.fashionshopapp.retrofit.RetrofitClient;
import com.example.fashionshopapp.util.Utils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class LoadMoreSpActivity extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView recyclerView;
    ApiBanHang apiBanHang;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    int page = 1;
    int idloaisanpham;
    AoAdapter adapterAo;
    List<SanPhamMoi> sanPhamMoiList;
    LinearLayoutManager linearLayoutManager;
    Handler handler = new Handler();
    boolean isLoading = false;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ao);

        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
        idloaisanpham = getIntent().getIntExtra("idloaisanpham", 1);
        AnhXa();
        ActionToolBar();
        getData(page);
        addEventLoad();
    }

    private void addEventLoad() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(isLoading == false){
                    if(linearLayoutManager.findLastCompletelyVisibleItemPosition() == sanPhamMoiList.size() - 1){
                        isLoading = true;
                        loadMore();
                    }
                }
            }
        });
    }

    private void loadMore() {
        // isLoading đã được đặt thành true trong hàm onScrolled, không cần đặt lại ở đây.

        handler.post(new Runnable() {
            @Override
            public void run() {
                // Bước 1: Thêm item null vào danh sách để hiển thị ProgressBar
                sanPhamMoiList.add(null);
                // Bước 2: Thông báo cho Adapter rằng một item MỚI đã được chèn vào vị trí cuối
                adapterAo.notifyItemInserted(sanPhamMoiList.size() - 1);
            }
        });

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Bước 3: Gỡ bỏ ProgressBar
                // Lấy vị trí của item null (chính là vị trí cuối cùng)
                int position = sanPhamMoiList.size() - 1;
                // Xóa item null khỏi danh sách
                sanPhamMoiList.remove(position);
                // Thông báo cho Adapter rằng item tại ĐÚNG VỊ TRÍ ĐÓ đã bị xóa
                adapterAo.notifyItemRemoved(position);

                // Bước 4: Tăng trang và gọi getData để tải dữ liệu thật
                page = page + 1;
                getData(page);
                // isLoading sẽ được đặt lại thành false bên trong hàm getData()
            }
        }, 2000); // Giả lập thời gian chờ 2 giây
    }


    private void getData(int page) {
        compositeDisposable.add(apiBanHang.getSanPham(page, idloaisanpham)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        sanPhamMoiModel -> {
                            if (sanPhamMoiModel.isSuccess()) {
                                List<SanPhamMoi> result = sanPhamMoiModel.getResult();

                                // KIỂM TRA NẾU CÓ DỮ LIỆU TRẢ VỀ
                                if (result != null && result.size() > 0) {
                                    if (adapterAo == null) {
                                        sanPhamMoiList = result;
                                        adapterAo = new AoAdapter(getApplicationContext(), sanPhamMoiList);
                                        recyclerView.setAdapter(adapterAo);
                                    } else {
                                        // Vị trí bắt đầu thêm dữ liệu mới
                                        int vitriBatDau = sanPhamMoiList.size();
                                        // Dùng addAll để thêm cả danh sách mới vào
                                        sanPhamMoiList.addAll(result);
                                        // Thông báo cho Adapter về một khoảng dữ liệu mới được chèn vào
                                        adapterAo.notifyItemRangeInserted(vitriBatDau, result.size());
                                    }
                                    // Đặt lại cờ để cho phép lần tải tiếp theo
                                    isLoading = false;
                                } else {
                                    // HẾT SẢN PHẨM ĐỂ TẢI
                                    // Không đặt lại isLoading = false nữa, để ngăn việc tải vô tận
                                    Toast.makeText(getApplicationContext(), "Đã tải hết sản phẩm", Toast.LENGTH_SHORT).show();
                                }
                            }
                        },
                        throwable -> {
                            // KHI GẶP LỖI, cũng phải đặt lại cờ để người dùng có thể thử lại
                            isLoading = false;
                            Toast.makeText(getApplicationContext(), "Không kết nối được server", Toast.LENGTH_SHORT).show();
                        }
                ));
    }


    private void ActionToolBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void AnhXa() {
        toolbar = findViewById(R.id.toobar);
        recyclerView = findViewById(R.id.recycleview_ao);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        sanPhamMoiList = new ArrayList<>();

    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}