// Đường dẫn: C:/Users/PC/Documents/GitHub/AppThoiTrang/app/src/main/java/com/example/fashionshopapp/activity/MainActivity.java
package com.example.fashionshopapp.activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fashionshopapp.R;
import com.example.fashionshopapp.adapter.LoaiSpAdapter;
import com.example.fashionshopapp.adapter.SanPhamMoiAdapter;
import com.example.fashionshopapp.model.Loaisp;
import com.example.fashionshopapp.model.SanPhamMoi;
import com.example.fashionshopapp.model.User;
import com.example.fashionshopapp.retrofit.ApiBanHang;
import com.example.fashionshopapp.retrofit.RetrofitClient;
import com.example.fashionshopapp.util.Utils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.messaging.FirebaseMessaging;
import com.nex3z.notificationbadge.NotificationBadge;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;
    ViewFlipper viewFlipper;
    RecyclerView recyclerViewmanhinhchinh;
    NavigationView navigationView;
    ListView listViewManHinhChinh;
    DrawerLayout drawerLayout;

    FloatingActionButton fabAiChat;

    LoaiSpAdapter loaiSpAdapter;
    List<Loaisp> mangloaisp;

    SanPhamMoiAdapter spMoiAdapter;
    List<SanPhamMoi> mangSpMoi;

    ApiBanHang apiBanHang;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    NotificationBadge badge;
    FirebaseFirestore db;
    ListenerRegistration unreadListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();
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
            listenForUnreadMessages();
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
        fabAiChat = findViewById(R.id.fab_ai_chat);

        mangloaisp = new ArrayList<>();
        mangSpMoi = new ArrayList<>();

        spMoiAdapter = new SanPhamMoiAdapter(this, mangSpMoi);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerViewmanhinhchinh.setLayoutManager(layoutManager);
        recyclerViewmanhinhchinh.setHasFixedSize(true);
        recyclerViewmanhinhchinh.setAdapter(spMoiAdapter);
    }


    private void listenForUnreadMessages() {
        if (Utils.user_current == null || Utils.user_current.getId() == 0) {
            updateBadge(0);
            return;
        }
        String currentUserId = String.valueOf(Utils.user_current.getId());
        if (unreadListener != null) {
            unreadListener.remove();
        }
        unreadListener = db.collection("messages")
                .whereEqualTo("receiver_id", currentUserId)
                .whereEqualTo("read", false)
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) {
                        Log.w("FirestoreListener", "Listen failed.", error);
                        return;
                    }
                    if (snapshots != null) {
                        updateBadge(snapshots.size());
                    } else {
                        updateBadge(0);
                    }
                });
    }

    private void updateBadge(int count) {
        if (badge != null) {
            if (count > 0) {
                badge.setVisibility(View.VISIBLE);
                badge.setNumber(count);
            } else {
                badge.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        User user = Paper.book().read("user");
        if (user != null) {
            Utils.user_current = user;
        }
        listenForUnreadMessages();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
        if (unreadListener != null) {
            unreadListener.remove();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        final MenuItem menuItem = menu.findItem(R.id.menu_chat);
        View actionView = menuItem.getActionView();
        actionView.setOnClickListener(v -> onOptionsItemSelected(menuItem));
        badge = actionView.findViewById(R.id.badge);
        listenForUnreadMessages();
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
            startActivity(new Intent(this, DonHangActivity.class));
            return true;
        } else if (id == R.id.menu_hoso) {
            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getSpMoi() {
        compositeDisposable.add(apiBanHang.getSpMoi()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        sanPhamMoiModel -> {
                            if (sanPhamMoiModel.isSuccess()) {
                                mangSpMoi.clear();
                                mangSpMoi.addAll(sanPhamMoiModel.getResult());
                                spMoiAdapter.notifyDataSetChanged();
                            }
                        },
                        throwable -> Toast.makeText(getApplicationContext(), "Không kết nối được server: " + throwable.getMessage(), Toast.LENGTH_SHORT).show()
                ));
    }

    private void getToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnSuccessListener(token -> {
                    if (!TextUtils.isEmpty(token)) {
                        if (Utils.user_current != null && Utils.user_current.getId() != 0) {
                            updateFcmTokenOnServer(token);
                        }
                    }
                });
    }

    private void updateFcmTokenOnServer(String token) {
        compositeDisposable.add(apiBanHang.updateFcmToken(Utils.user_current.getId(), token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        response -> Log.d("FCM_TOKEN", "Cập nhật token: " + (response.isSuccess() ? "Thành công" : "Thất bại")),
                        throwable -> Log.e("FCM_TOKEN", "Lỗi khi cập nhật token: ", throwable)
                ));
    }

    private void getEventClick() {
        // Sự kiện click cho ListView trong NavigationDrawer
        listViewManHinhChinh.setOnItemClickListener((parent, view, i, l) -> {
            switch (i) {
                case 0:
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    break;
                case 1:
                    startActivity(new Intent(getApplicationContext(), LoadMoreSpActivity.class).putExtra("idloaisanpham", 2));
                    break;
                case 2:
                    startActivity(new Intent(getApplicationContext(), LoadMoreSpActivity.class).putExtra("idloaisanpham", 3));
                    break;
                case 3:
                    startActivity(new Intent(getApplicationContext(), LoadMoreSpActivity.class).putExtra("idloaisanpham", 4));
                    break;
                case 4:
                    startActivity(new Intent(getApplicationContext(), LoadMoreSpActivity.class).putExtra("idloaisanpham", 5));
                    break;
            }
        });

        fabAiChat.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), GeminiChatActivity.class);
            startActivity(intent);
        });
    }

    private void getLoaiSanPham() {
        compositeDisposable.add(apiBanHang.getLoaiSp()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        loaiSpModel -> {
                            if (loaiSpModel.isSuccess()) {
                                mangloaisp.clear();
                                mangloaisp.addAll(loaiSpModel.getResult());
                                if (loaiSpAdapter == null) {
                                    loaiSpAdapter = new LoaiSpAdapter(mangloaisp, getApplicationContext());
                                    listViewManHinhChinh.setAdapter(loaiSpAdapter);
                                } else {
                                    loaiSpAdapter.notifyDataSetChanged();
                                }
                            }
                        },
                        throwable -> Log.d("error", throwable.getMessage())
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
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationIcon(android.R.drawable.ic_menu_sort_by_size);
            toolbar.setNavigationOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
        }
    }
}
