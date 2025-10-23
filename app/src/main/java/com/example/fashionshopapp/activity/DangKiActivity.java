package com.example.fashionshopapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
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
    EditText hoten, usernamedk, email, sodienthoai, diachi, passdk, repass;
    AppCompatButton btndangki;
    ApiBanHang apiBanHang;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dang_ki);
        initView();
        initControl();
    }

    private void initControl() {
        btndangki.setOnClickListener(view -> handleRegisterClick());
        txtdangnhap.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), DangNhapActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void handleRegisterClick() {
        String str_hoten = hoten.getText().toString().trim();
        String str_user = usernamedk.getText().toString().trim();
        String str_email = email.getText().toString().trim();
        String str_sdt = sodienthoai.getText().toString().trim();
        String str_diachi = diachi.getText().toString().trim();
        String str_pass = passdk.getText().toString().trim();
        String str_repass = repass.getText().toString().trim();

        if (TextUtils.isEmpty(str_hoten) || TextUtils.isEmpty(str_user) || TextUtils.isEmpty(str_email) || TextUtils.isEmpty(str_pass)) {
            Toast.makeText(getApplicationContext(), "Vui lòng điền đủ thông tin bắt buộc", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!str_pass.equals(str_repass)) {
            Toast.makeText(getApplicationContext(), "Mật khẩu nhập lại không khớp", Toast.LENGTH_SHORT).show();
            return;
        }

        btndangki.setEnabled(false);
        compositeDisposable.add(apiBanHang.sendRegisterOtp(str_email, str_user)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        userModel -> {
                            btndangki.setEnabled(true);
                            if (userModel.isSuccess()) {
                                Toast.makeText(getApplicationContext(), userModel.getMessage(), Toast.LENGTH_LONG).show();

                                Intent intent = new Intent(getApplicationContext(), OtpVerifyActivity.class);
                                intent.putExtra("flow_type", "register");
                                intent.putExtra("hoten", str_hoten);
                                intent.putExtra("username", str_user);
                                intent.putExtra("email", str_email);
                                intent.putExtra("sdt", str_sdt);
                                intent.putExtra("diachi", str_diachi);
                                intent.putExtra("password", str_pass);
                                startActivity(intent);
                            } else {
                                Toast.makeText(getApplicationContext(), userModel.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        },
                        throwable -> {
                            btndangki.setEnabled(true);
                            Toast.makeText(getApplicationContext(), "Lỗi kết nối: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                ));
    }

    private void initView() {
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
        txtdangnhap = findViewById(R.id.txtdangnhap);
        hoten = findViewById(R.id.hoten);
        email = findViewById(R.id.email);
        sodienthoai = findViewById(R.id.sodienthoai);
        diachi = findViewById(R.id.diachi);
        passdk = findViewById(R.id.passdk);
        repass = findViewById(R.id.repass);
        usernamedk = findViewById(R.id.usernamedk);
        btndangki = findViewById(R.id.btndangki);
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}
