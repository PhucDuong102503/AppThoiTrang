package com.example.fashionshopapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.bumptech.glide.Glide;
import com.example.fashionshopapp.R;
import com.example.fashionshopapp.util.Utils;
import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class ProfileActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextView txtHoTen, txtEmail, txtSoDienThoai, txtDiaChi;
    Button btnDangXuat, btnEditProfile;
    CircleImageView imgProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initView();
        initToolbar();
        initControl();
    }

    // Cập nhật lại thông tin khi quay lại từ màn hình Edit
    @Override
    protected void onResume() {
        super.onResume();
        setUserInfo();
    }

    private void setUserInfo() {
        if (Utils.user_current != null) {
            txtHoTen.setText(Utils.user_current.getHoten());
            txtEmail.setText(Utils.user_current.getEmail());
            txtSoDienThoai.setText(Utils.user_current.getSodienthoai());
            txtDiaChi.setText(Utils.user_current.getDiachi());

            if (!TextUtils.isEmpty(Utils.user_current.getHinhanh())) {
                Glide.with(this).load(Utils.user_current.getHinhanh())
                        .placeholder(R.drawable.profile).error(R.drawable.profile)
                        .into(imgProfile);
            } else {
                imgProfile.setImageResource(R.drawable.profile);
            }
        }
    }

    private void initControl() {
        btnDangXuat.setOnClickListener(v -> {
            Paper.book().delete("user");
            Intent intent = new Intent(this, SplashActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        // Chuyển sang màn hình EditProfileActivity
        btnEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(this, EditProfileActivity.class);
            startActivity(intent);
        });
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        txtHoTen = findViewById(R.id.profile_hoten);
        txtEmail = findViewById(R.id.profile_email);
        txtSoDienThoai = findViewById(R.id.profile_sodienthoai);
        txtDiaChi = findViewById(R.id.profile_diachi);
        btnDangXuat = findViewById(R.id.btn_dangxuat);
        btnEditProfile = findViewById(R.id.btn_edit_profile); // Ánh xạ nút mới
        imgProfile = findViewById(R.id.profile_image);
    }
}
