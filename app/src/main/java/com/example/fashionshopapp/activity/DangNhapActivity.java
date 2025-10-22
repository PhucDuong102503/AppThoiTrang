package com.example.fashionshopapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.fashionshopapp.R;
import com.example.fashionshopapp.retrofit.ApiBanHang;
import com.example.fashionshopapp.retrofit.RetrofitClient;
import com.example.fashionshopapp.util.Utils;

import io.paperdb.Paper;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class DangNhapActivity extends AppCompatActivity {
    TextView txtdangki;
    EditText username, pass;
    AppCompatButton btndangnhap;
    ApiBanHang apiBanHang;
    CompositeDisposable compositeDisposable = new CompositeDisposable();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Tạm thời vô hiệu hóa EdgeToEdge để tránh xung đột layout nếu cần
        // EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dang_nhap);
        initview();
        initControll();
    }

    private void initControll() {
        txtdangki.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), DangKiActivity.class);
                startActivity(intent);
            }
        });

        btndangnhap.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                dangNhap(); // Gọi hàm đăng nhập
            }
        });
    }

    private void dangNhap() {
        String str_username = username.getText().toString().trim();
        String str_pass = pass.getText().toString().trim();

        if(TextUtils.isEmpty(str_username)){
            Toast.makeText(getApplicationContext(), "Bạn chưa nhập user name", Toast.LENGTH_SHORT).show();
            return; // Dừng lại nếu rỗng
        }
        if(TextUtils.isEmpty(str_pass)){
            Toast.makeText(getApplicationContext(), "Bạn chưa nhập mật khẩu", Toast.LENGTH_SHORT).show();
            return; // Dừng lại nếu rỗng
        }
        //luu san mk
        Paper.book().write("username", str_username);
        Paper.book().write("pass", str_pass);

        // Gọi API đăng nhập
        compositeDisposable.add(apiBanHang.dangNhap(str_username, str_pass)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        userModel -> {
                            if(userModel.isSuccess()){
                                // Đăng nhập thành công
                                Utils.user_current = userModel.getResult().get(0);
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
                            Toast.makeText(getApplicationContext(), "Lỗi: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                ));
    }

    private void initview() {
        Paper.init(this);
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
        txtdangki = findViewById(R.id.txtdangki);
        username = findViewById(R.id.username);
        pass = findViewById(R.id.pass);
        btndangnhap = findViewById(R.id.btndangnhap);

        //read data from thu vien paper
        if(Paper.book().read("username") != null && Paper.book().read("pass") != null){
            username.setText(Paper.book().read("username"));
            pass.setText(Paper.book().read("pass"));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // << SỬA LẠI HOÀN CHỈNH >>
        // Điền TÊN ĐĂNG NHẬP vào ô username
        // Điền MẬT KHẨU vào ô pass
        if(Utils.user_current.getTendangnhap() != null && Utils.user_current.getMatkhau() != null){
            username.setText(Utils.user_current.getTendangnhap());
            pass.setText(Utils.user_current.getMatkhau());
        }
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}
