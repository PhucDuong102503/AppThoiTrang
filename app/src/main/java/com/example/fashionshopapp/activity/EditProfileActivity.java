package com.example.fashionshopapp.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.fashionshopapp.R;
import com.example.fashionshopapp.model.UserModel;
import com.example.fashionshopapp.retrofit.ApiBanHang;
import com.example.fashionshopapp.retrofit.RetrofitClient;
import com.example.fashionshopapp.util.Utils;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

// KHÔNG CẦN IMPORT IMAGEPICKER NỮA
// import com.github.dhaval2404.imagepicker.ImagePicker;

public class EditProfileActivity extends AppCompatActivity {

    private static final String TAG = "EditProfileActivity";

    // Views
    private Toolbar toolbar;
    private EditText edtHoTen, edtSoDienThoai, edtDiaChi;
    private Button btnLuuThayDoi;
    private CircleImageView imgProfile;
    private ImageView btnChonAnh;

    // RxJava & Retrofit
    private ApiBanHang apiBanHang;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    // Data
    private Uri selectedImageUri;

    // === PHẦN THAY THẾ IMAGEPICKER ===
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private ActivityResultLauncher<String> requestPermissionLauncher;
    // =================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        initActivityLaunchers();
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
        initView();
        initToolbar();
        initControl();
        setUserInfo();
    }

    private void initActivityLaunchers() {
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        openGallery();
                    } else {
                        Toast.makeText(this, "Bạn cần cấp quyền để chọn ảnh", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        if (selectedImageUri != null) {
                            Glide.with(this).load(selectedImageUri).into(imgProfile);
                        }
                    }
                }
        );
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        edtHoTen = findViewById(R.id.profile_hoten_edit);
        edtSoDienThoai = findViewById(R.id.profile_sodienthoai_edit);
        edtDiaChi = findViewById(R.id.profile_diachi_edit);
        btnLuuThayDoi = findViewById(R.id.btn_luu_thay_doi);
        imgProfile = findViewById(R.id.profile_image_edit);
        btnChonAnh = findViewById(R.id.btn_chon_anh);
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Chỉnh Sửa Hồ Sơ");
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void initControl() {
        // Thay đổi sự kiện click để gọi hàm mới
        btnChonAnh.setOnClickListener(v -> checkPermissionAndOpenGallery());

        btnLuuThayDoi.setOnClickListener(v -> {
            if (Utils.user_current != null) {
                updateProfile();
            } else {
                Toast.makeText(this, "Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Hàm mới để kiểm tra quyền và mở thư viện
    private void checkPermissionAndOpenGallery() {
        String permission;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permission = Manifest.permission.READ_MEDIA_IMAGES;
        } else {
            permission = Manifest.permission.READ_EXTERNAL_STORAGE;
        }

        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            openGallery();
        } else {
            requestPermissionLauncher.launch(permission);
        }
    }

    // Hàm mới để mở thư viện
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    // HÀM onActivityResult KHÔNG CÒN CẦN THIẾT NỮA

    private void setUserInfo() {
        if (Utils.user_current != null) {
            edtHoTen.setText(Utils.user_current.getHoten());
            edtSoDienThoai.setText(Utils.user_current.getSodienthoai());
            edtDiaChi.setText(Utils.user_current.getDiachi());

            if (!TextUtils.isEmpty(Utils.user_current.getHinhanh())) {
                Glide.with(this)
                        .load(Utils.user_current.getHinhanh())
                        .placeholder(R.drawable.profile)
                        .error(R.drawable.profile)
                        .into(imgProfile);
            } else {
                imgProfile.setImageResource(R.drawable.profile);
            }
        }
    }

    private void updateProfile() {
        // Phần này giữ nguyên, không thay đổi
        Toast.makeText(this, "Đang cập nhật...", Toast.LENGTH_SHORT).show();

        String str_id = String.valueOf(Utils.user_current.getId());
        String str_hoten = edtHoTen.getText().toString().trim();
        String str_sdt = edtSoDienThoai.getText().toString().trim();
        String str_diachi = edtDiaChi.getText().toString().trim();

        RequestBody idBody = RequestBody.create(MediaType.parse("text/plain"), str_id);
        RequestBody hotenBody = RequestBody.create(MediaType.parse("text/plain"), str_hoten);
        RequestBody sdtBody = RequestBody.create(MediaType.parse("text/plain"), str_sdt);
        RequestBody diachiBody = RequestBody.create(MediaType.parse("text/plain"), str_diachi);

        MultipartBody.Part imagePart = null;
        if (selectedImageUri != null) {
            String realPath = getRealPathFromURI(selectedImageUri);
            if (realPath != null) {
                File file = new File(realPath);
                RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
                imagePart = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
            }
        }

        compositeDisposable.add(apiBanHang.updateProfile(idBody, hotenBody, sdtBody, diachiBody, imagePart)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        userModel -> {
                            if (userModel != null && userModel.isSuccess()) {
                                Toast.makeText(getApplicationContext(), "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                                Utils.user_current = userModel.getResult().get(0);
                                Paper.book().write("user", Utils.user_current);
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), userModel != null ? userModel.getMessage() : "Có lỗi xảy ra", Toast.LENGTH_SHORT).show();
                            }
                        },
                        throwable -> {
                            Toast.makeText(getApplicationContext(), "Lỗi: " + throwable.getMessage(), Toast.LENGTH_LONG).show();
                            Log.e(TAG, "updateProfile Error: " + throwable.getMessage());
                        }
                ));
    }

    private String getRealPathFromURI(Uri contentUri) {
        // Phần này giữ nguyên, không thay đổi
        String path = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                path = cursor.getString(column_index);
            }
            cursor.close();
        }
        return path;
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}
