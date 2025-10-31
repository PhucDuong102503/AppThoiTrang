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
import com.example.fashionshopapp.model.LoaiSpModel;
import com.example.fashionshopapp.model.Loaisp;
import com.example.fashionshopapp.model.MessageModel;
import com.example.fashionshopapp.model.SanPhamMoi;
import com.example.fashionshopapp.model.SanPhamMoiModel;
// ⭐ SỬA LỖI: Trỏ đến đúng lớp User trong model của bạn ⭐
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

public class MainActivity extends AppCompatActivity implements ItemClickListener {

    // ... (toàn bộ code còn lại của bạn giữ nguyên, không cần thay đổi gì thêm)
    // Tôi sẽ chỉ viết lại những phần quan trọng

    Toolbar toolbar;
    ViewFlipper viewFlipper;
    RecyclerView recyclerViewmanhinhchinh;
    NavigationView navigationView;
    ListView listViewManHinhChinh;
    DrawerLayout drawerLayout;
    LoaiSpAdapter loaiSpAdapter;
    List<Loaisp> mangloaisp;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ApiBanHang apiBanHang;
    List<SanPhamMoi> mangSpMoi;
    SanPhamMoiAdapter spMoiAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Paper.init(this); // Đảm bảo Paper được khởi tạo
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);

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


    private void getToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnSuccessListener(token -> {
                    if (!TextUtils.isEmpty(token)) {
                        Log.d("FCM_TOKEN", "Token của thiết bị: " + token);
                        // Chỉ cập nhật token nếu người dùng đã đăng nhập
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

        // Bây giờ dòng code này sẽ không còn báo lỗi
        User user = Paper.book().read("user");
        if (user != null) {
            // Cập nhật lại biến toàn cục
            Utils.user_current = user;
            Log.d("MainActivity", "onResume: User updated - " + Utils.user_current.getHoten());
        }
    }


    @Override
    public void onClick(View view, int pos, boolean isLongClick) {
        if (!isLongClick) {
            SanPhamMoi sanPhamDaClick = mangSpMoi.get(pos);
            Intent intent = new Intent(MainActivity.this, ChiTietActivity.class);
            intent.putExtra("chitiet", sanPhamDaClick);
            startActivity(intent);
        }
    }

    private void getEventClick() {
        listViewManHinhChinh.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long l) {
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
            }
        });
    }

    private void getSpMoi() {
        compositeDisposable.add(apiBanHang.getSpMoi()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        sanPhamMoiModel -> {
                            if (sanPhamMoiModel.isSuccess()) {
                                mangSpMoi = sanPhamMoiModel.getResult();
                                spMoiAdapter = new SanPhamMoiAdapter(getApplicationContext(), mangSpMoi, this);
                                recyclerViewmanhinhchinh.setAdapter(spMoiAdapter);
                            }
                        },
                        throwable -> {
                            Toast.makeText(getApplicationContext(), "Không kết nối được server" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                ));
    }


    private void getLoaiSanPham() {
        compositeDisposable.add(apiBanHang.getLoaiSp()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        loaiSpModel -> {
                            if (loaiSpModel.isSuccess()) {
                                mangloaisp = loaiSpModel.getResult();
                                loaiSpAdapter = new LoaiSpAdapter(mangloaisp, getApplicationContext());
                                listViewManHinhChinh.setAdapter(loaiSpAdapter);
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
        for (int i = 0; i < mangquangcao.size(); i++) {
            ImageView imageView = new ImageView(getApplicationContext());
            Picasso.get().load(mangquangcao.get(i)).into(imageView);
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

    private void Anhxa() {
        toolbar = findViewById(R.id.toobarmanhinhchinh);
        viewFlipper = findViewById(R.id.viewflipper);
        recyclerViewmanhinhchinh = findViewById(R.id.recycleview);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerViewmanhinhchinh.setLayoutManager(layoutManager);
        recyclerViewmanhinhchinh.setHasFixedSize(true);
        navigationView = findViewById(R.id.navigationview);
        listViewManHinhChinh = findViewById(R.id.listviewmanhinhchinh);
        drawerLayout = findViewById(R.id.drawerlayout);
        mangloaisp = new ArrayList<>();
        mangSpMoi = new ArrayList<>();
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
            Intent cartIntent = new Intent(getApplicationContext(), GioHangActivity.class);
            startActivity(cartIntent);
            return true;
        } else if (id == R.id.search) {
            Intent searchIntent = new Intent(this, SearchActivity.class);
            startActivity(searchIntent);
            return true;
        } else if (id == R.id.menu_chat) {
            Intent adminListIntent = new Intent(getApplicationContext(), AdminListActivity.class);
            startActivity(adminListIntent);
            return true;
        } else if (id == R.id.menu_donhang) {
            Intent orderIntent = new Intent(this, XemDonHangActivity.class);
            startActivity(orderIntent);
            return true;
        } else if (id == R.id.menu_hoso) {
            Intent profileIntent = new Intent(getApplicationContext(), ProfileActivity.class);
            startActivity(profileIntent);
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
