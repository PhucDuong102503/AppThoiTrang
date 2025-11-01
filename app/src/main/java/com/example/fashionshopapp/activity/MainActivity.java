package com.example.fashionshopapp.activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fashionshopapp.Interface.ItemClickListener;
import com.example.fashionshopapp.R;
import com.example.fashionshopapp.adapter.LoaiSpAdapter;
import com.example.fashionshopapp.adapter.SanPhamMoiAdapter;
import com.example.fashionshopapp.model.Loaisp;
import com.example.fashionshopapp.model.SanPhamMoi;
import com.example.fashionshopapp.model.User;
import com.example.fashionshopapp.retrofit.ApiBanHang;
import com.example.fashionshopapp.retrofit.RetrofitClient;
import com.example.fashionshopapp.util.Utils;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

// ⭐ BƯỚC 1: Bỏ implements ItemClickListener không cần thiết nữa
public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;
    ViewFlipper viewFlipper;
    RecyclerView recyclerViewmanhinhchinh;
    NavigationView navigationView;
    ListView listViewManHinhChinh;
    DrawerLayout drawerLayout;

    // Các thành phần cho Loại Sản Phẩm
    LoaiSpAdapter loaiSpAdapter;
    List<Loaisp> mangloaisp;

    // Các thành phần cho Sản Phẩm Mới
    SanPhamMoiAdapter spMoiAdapter;
    List<SanPhamMoi> mangSpMoi;

    // Quản lý API calls
    ApiBanHang apiBanHang;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
        Paper.init(this);

        Anhxa();
        ActionBar();

        if (isConnected(this)) {
            ActionViewFlipper();
            getLoaiSanPham();
            getSpMoi();
            getEventClick();
            getToken();
        } else {
            Toast.makeText(getApplicationContext(), "Không có kết nối mạng", Toast.LENGTH_SHORT).show();
        }
    }

    private void Anhxa() {
        toolbar = findViewById(R.id.toobarmanhinhchinh);
        viewFlipper = findViewById(R.id.viewflipper);
        recyclerViewmanhinhchinh = findViewById(R.id.recycleview);
        navigationView = findViewById(R.id.navigationview);
        listViewManHinhChinh = findViewById(R.id.listviewmanhinhchinh);
        drawerLayout = findViewById(R.id.drawerlayout);

        // Khởi tạo list
        mangloaisp = new ArrayList<>();
        mangSpMoi = new ArrayList<>();

        // ⭐ BƯỚC 2: Khởi tạo Adapter ngay từ đầu với một danh sách rỗng
        // và định nghĩa ItemClickListener ngay tại đây.
        spMoiAdapter = new SanPhamMoiAdapter(this, mangSpMoi, (view, pos, isLongClick) -> {
            if (!isLongClick) {
                // Lấy sản phẩm được click từ danh sách
                SanPhamMoi sanPhamDaClick = mangSpMoi.get(pos);
                // Tạo Intent và truyền dữ liệu
                Intent intent = new Intent(MainActivity.this, ChiTietActivity.class);
                intent.putExtra("chitiet", sanPhamDaClick);
                startActivity(intent);
            }
        });

        // Cấu hình RecyclerView cho Sản Phẩm Mới
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerViewmanhinhchinh.setLayoutManager(layoutManager);
        recyclerViewmanhinhchinh.setHasFixedSize(true);
        recyclerViewmanhinhchinh.setAdapter(spMoiAdapter); // Gán adapter cho RecyclerView ngay lập tức
    }

    private void getSpMoi() {
        compositeDisposable.add(apiBanHang.getSpMoi()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        sanPhamMoiModel -> {
                            if (sanPhamMoiModel.isSuccess()) {
                                // ⭐ BƯỚC 3: Không tạo mới Adapter, chỉ cập nhật dữ liệu
                                mangSpMoi.clear(); // Xóa dữ liệu cũ
                                mangSpMoi.addAll(sanPhamMoiModel.getResult()); // Thêm dữ liệu mới
                                spMoiAdapter.notifyDataSetChanged(); // Thông báo cho Adapter biết dữ liệu đã thay đổi
                            }
                        },
                        throwable -> {
                            Toast.makeText(getApplicationContext(), "Không kết nối được server: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                ));
    }

    // ... Toàn bộ các hàm còn lại (getLoaiSanPham, getToken, onResume, v.v...) giữ nguyên như file của bạn ...
    // Không cần thay đổi gì ở các hàm này.

    private void getToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnSuccessListener(token -> {
                    if (!TextUtils.isEmpty(token)) {
                        Log.d("FCM_TOKEN", "Token của thiết bị: " + token);
                        if (Utils.user_current != null && Utils.user_current.getId() != 0) {
                            updateFcmTokenOnServer(token);
                        }
                    } else {
                        Log.w("FCM_TOKEN", "Không thể lấy được token.");
                    }
                });
    }

    private void updateFcmTokenOnServer(String token) {
        compositeDisposable.add(apiBanHang.updateFcmToken(Utils.user_current.getId(), token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        response -> {
                            if (response.isSuccess()) {
                                Log.d("FCM_TOKEN", "Cập nhật token trên server thành công.");
                            } else {
                                Log.d("FCM_TOKEN", "Cập nhật token thất bại: " + response.getMessage());
                            }
                        },
                        throwable -> {
                            Log.e("FCM_TOKEN", "Lỗi khi cập nhật token: ", throwable);
                        }
                ));
    }

    @Override
    protected void onResume() {
        super.onResume();
        User user = Paper.book().read("user");
        if (user != null) {
            Utils.user_current = user;
            Log.d("MainActivity", "onResume: User updated - " + Utils.user_current.getHoten());
        }
    }

    private void getEventClick() {
        listViewManHinhChinh.setOnItemClickListener((parent, view, i, l) -> {
            switch (i) {
                case 0:
                    Intent trangchu = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(trangchu);
                    break;
                case 1:
                    Intent ao = new Intent(getApplicationContext(), LoadMoreSpActivity.class);
                    ao.putExtra("idloaisanpham", 2);
                    startActivity(ao);
                    break;
                case 2:
                    Intent quan = new Intent(getApplicationContext(), LoadMoreSpActivity.class);
                    quan.putExtra("idloaisanpham", 3);
                    startActivity(quan);
                    break;
                case 3:
                    Intent giay = new Intent(getApplicationContext(), LoadMoreSpActivity.class);
                    giay.putExtra("idloaisanpham", 4);
                    startActivity(giay);
                    break;
                case 4:
                    Intent phukien = new Intent(getApplicationContext(), LoadMoreSpActivity.class);
                    phukien.putExtra("idloaisanpham", 5);
                    startActivity(phukien);
                    break;
            }
        });
    }

    private void getLoaiSanPham() {
        compositeDisposable.add(apiBanHang.getLoaiSp()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        loaiSpModel -> {
                            if (loaiSpModel.isSuccess()) {
                                // Sửa lại cách khởi tạo LoaiSpAdapter cho đúng
                                mangloaisp.clear();
                                mangloaisp.addAll(loaiSpModel.getResult());
                                // Khởi tạo nếu chưa có, hoặc cập nhật nếu đã có
                                if (loaiSpAdapter == null) {
                                    loaiSpAdapter = new LoaiSpAdapter(mangloaisp, getApplicationContext());
                                    listViewManHinhChinh.setAdapter(loaiSpAdapter);
                                } else {
                                    loaiSpAdapter.notifyDataSetChanged();
                                }
                            }
                        },
                        throwable -> {
                            Log.d("error", throwable.getMessage());
                        }
                ));
    }

    private void ActionViewFlipper() {
        List<String> mangquangcao = new ArrayList<>();
        mangquangcao.add("https://intphcm.com/data/upload/banner-thoi-trang.jpg");
        mangquangcao.add("https://zerdio.com.vn/wp-content/uploads/2023/03/trang-phuc-phong-cach-quy-ong.jpg");
        mangquangcao.add("https://simg.zalopay.com.vn/zlp-website/assets/dior_1_d854899ea3.jpg");
        mangquangcao.add("https://pos.nvncdn.com/650b61-144700/art/artCT/20240529_vDnzQ1np.jpg");
        for (String url : mangquangcao) {
            ImageView imageView = new ImageView(getApplicationContext());
            Picasso.get().load(url).into(imageView);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            viewFlipper.addView(imageView);
        }
        viewFlipper.setFlipInterval(5000);
        viewFlipper.setAutoStart(true);
        Animation slide_in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in_right);
        Animation slide_out = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_out_right);
        viewFlipper.setInAnimation(slide_in);
        viewFlipper.setOutAnimation(slide_out);
    }

    private boolean isConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            NetworkInfo mobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            return (wifi != null && wifi.isConnected()) || (mobile != null && mobile.isConnected());
        }
        return false;
    }

    private void ActionBar() {
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationIcon(android.R.drawable.ic_menu_sort_by_size);
            toolbar.setNavigationOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_giohang) {
            startActivity(new Intent(getApplicationContext(), GioHangActivity.class));
            return true;
        } else if (id == R.id.search) {
            startActivity(new Intent(this, SearchActivity.class));
            return true;
        } else if (id == R.id.menu_chat) {
            startActivity(new Intent(getApplicationContext(), AdminListActivity.class));
            return true;
        } else if (id == R.id.menu_donhang) {
            startActivity(new Intent(this, XemDonHangActivity.class));
            return true;
        } else if (id == R.id.menu_hoso) {
            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}
