package com.example.fashionshopapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.fashionshopapp.R;
import com.example.fashionshopapp.retrofit.ApiBanHang;
import com.example.fashionshopapp.retrofit.RetrofitClient;
import com.example.fashionshopapp.util.Utils;

import io.paperdb.Paper;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class DangNhapActivity extends AppCompatActivity {
    TextView txtdangki, txtresetpass;
    EditText username; // Giữ nguyên tên biến 'username' để khớp với layout
    EditText pass;
    AppCompatButton btndangnhap;
    ApiBanHang apiBanHang;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dang_nhap);
        initview();
        initControll();
    }

    private void initControll() {
        txtdangki.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), DangKiActivity.class);
            startActivity(intent);
        });

        txtresetpass.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), ResetPassActivity.class);
            startActivity(intent);
        });

        btndangnhap.setOnClickListener(view -> dangNhap());
    }

    private void dangNhap() {
        // Lấy dữ liệu người dùng nhập, có thể là username hoặc email
        String str_username_or_email = username.getText().toString().trim();
        String str_pass = pass.getText().toString().trim();

        if (TextUtils.isEmpty(str_username_or_email)) {
            Toast.makeText(getApplicationContext(), "Bạn chưa nhập Tên đăng nhập hoặc Email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(str_pass)) {
            Toast.makeText(getApplicationContext(), "Bạn chưa nhập mật khẩu", Toast.LENGTH_SHORT).show();
            return;
        }

        // Lưu thông tin đăng nhập vào PaperDB để tự động điền lần sau
        Paper.book().write("username", str_username_or_email);
        Paper.book().write("pass", str_pass);

        // Gọi API đăng nhập, gửi lên chuỗi người dùng đã nhập
        // File PHP của bạn đang mong đợi tham số 'tendangnhap'
        compositeDisposable.add(apiBanHang.dangNhap(str_username_or_email, str_pass)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        userModel -> {
                            if (userModel.isSuccess()) {
                                // Đăng nhập thành công, lưu thông tin user
                                Utils.user_current = userModel.getResult().get(0);
                                // Chuyển sang màn hình chính
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                // Đăng nhập thất bại, hiển thị thông báo từ server
                                Toast.makeText(getApplicationContext(), userModel.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        },
                        throwable -> {
                            // Lỗi kết nối hoặc lỗi server
                            Toast.makeText(getApplicationContext(), "Lỗi kết nối: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                ));
    }

    private void initview() {
        Paper.init(this);
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
        txtdangki = findViewById(R.id.txtdangki);
        txtresetpass = findViewById(R.id.txtresetpass);
        username = findViewById(R.id.username);
        pass = findViewById(R.id.pass);
        btndangnhap = findViewById(R.id.btndangnhap);

        // Đọc dữ liệu từ PaperDB để điền vào các ô EditText
        // Logic này đã xử lý việc điền lại thông tin, không cần onResume()
        if (Paper.book().read("username") != null && Paper.book().read("pass") != null) {
            username.setText(Paper.book().read("username"));
            pass.setText(Paper.book().read("pass"));
        }
    }

    // << XÓA BỎ HÀM onResume() >>
    // Hàm này không cần thiết và gây xung đột với logic của PaperDB.
    // Việc tự động điền thông tin đã được xử lý trong initView().
    /*
    @Override
    protected void onResume() {
        super.onResume();
        if(Utils.user_current.getTendangnhap() != null && Utils.user_current.getMatkhau() != null){
            username.setText(Utils.user_current.getTendangnhap());
            pass.setText(Utils.user_current.getMatkhau());
        }
    }
    */

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}
