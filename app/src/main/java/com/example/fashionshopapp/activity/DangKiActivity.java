package com.example.fashionshopapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.fashionshopapp.R;
import com.example.fashionshopapp.retrofit.ApiBanHang;
import com.example.fashionshopapp.retrofit.RetrofitClient;
import com.example.fashionshopapp.util.Utils;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class DangKiActivity extends AppCompatActivity {

    TextView txtdangnhap;
    EditText hoten, email, sodienthoai, diachi, pass, repass, username;
    AppCompatButton button;
    ApiBanHang apiBanHang;
    CompositeDisposable compositeDisposable = new CompositeDisposable();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Tạm thời vô hiệu hóa EdgeToEdge để tránh xung đột layout
        // EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dang_ki);
        initview();
        initControll();
    }

    private void initControll() {
        txtdangnhap.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), DangNhapActivity.class);
                startActivity(intent);
                finish(); // << SỬA 1: Thêm finish()
            }
        });

        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dangKi();
            }
        });
    }

    private void dangKi() {
        String str_username = username.getText().toString().trim();
        String str_hoten = hoten.getText().toString().trim();
        String str_email = email.getText().toString().trim();
        String str_sodienthoai = sodienthoai.getText().toString().trim();
        String str_diachi = diachi.getText().toString().trim();
        String str_pass = pass.getText().toString().trim();
        String str_repass = repass.getText().toString().trim();

        if(TextUtils.isEmpty(str_hoten)){
            Toast.makeText(getApplicationContext(), "Bạn chưa nhập họ tên", Toast.LENGTH_SHORT).show();
        } else if(TextUtils.isEmpty(str_email)){
            Toast.makeText(getApplicationContext(), "Bạn chưa nhập email", Toast.LENGTH_SHORT).show();
        } else if(TextUtils.isEmpty(str_sodienthoai)){
            Toast.makeText(getApplicationContext(), "Bạn chưa nhập số điện thoại", Toast.LENGTH_SHORT).show();
        } else if(TextUtils.isEmpty(str_diachi)){
            Toast.makeText(getApplicationContext(), "Bạn chưa nhập địa chỉ", Toast.LENGTH_SHORT).show();
        } else if(TextUtils.isEmpty(str_pass)){
            Toast.makeText(getApplicationContext(), "Bạn chưa nhập mật khẩu", Toast.LENGTH_SHORT).show();
        } else if(TextUtils.isEmpty(str_repass)){
            Toast.makeText(getApplicationContext(), "Bạn chưa nhập lại mật khẩu", Toast.LENGTH_SHORT).show();
        } else if(TextUtils.isEmpty(str_username)){
            Toast.makeText(getApplicationContext(), "Bạn chưa nhập user name", Toast.LENGTH_SHORT).show();
        } else {
            if(str_pass.equals(str_repass)){
                //post data
                compositeDisposable.add(apiBanHang.dangki(str_username, str_hoten, str_pass, str_sodienthoai, str_email, str_diachi)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                userModel -> {
                                    if(userModel.isSuccess()){
                                        // << SỬA 2: Đảm bảo lưu đúng username và pass >>
                                        Utils.user_current.setTendangnhap(str_username);
                                        Utils.user_current.setMatkhau(str_pass);

                                        Toast.makeText(getApplicationContext(), "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getApplicationContext(), DangNhapActivity.class);
                                        startActivity(intent);
                                        finish(); // << SỬA 3: Thêm finish()
                                    } else{
                                        Toast.makeText(getApplicationContext(), userModel.getMessage(), Toast.LENGTH_SHORT).show();
                                    }

                                },
                                throwable -> {
                                    Toast.makeText(getApplicationContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                        ));
            } else{
                Toast.makeText(getApplicationContext(), "Mật khẩu nhập lại không khớp", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void initview() {
        txtdangnhap = findViewById(R.id.txtdangnhap);
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);

        hoten = findViewById(R.id.hoten);
        email = findViewById(R.id.email);
        sodienthoai = findViewById(R.id.sodienthoai);
        diachi = findViewById(R.id.diachi);
        pass = findViewById(R.id.passdk);
        repass = findViewById(R.id.repass);
        username = findViewById(R.id.usernamedk);
        button = findViewById(R.id.btndangki);
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}
