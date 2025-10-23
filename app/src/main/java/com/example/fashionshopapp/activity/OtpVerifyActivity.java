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
import com.google.gson.JsonObject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
// KHÔNG import okhttp3.MediaType ở đây để tránh nhầm lẫn

public class OtpVerifyActivity extends AppCompatActivity {

    EditText edtOtp, edtNewPassword;
    AppCompatButton btnVerify;
    TextView txtBack;
    ApiBanHang apiBanHang;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    String flow_type, email, hoten, username, password, sdt, diachi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verify);
        initView();
        getIntentDataAndSetupUI();
        initControl();
    }

    private void getIntentDataAndSetupUI() {
        flow_type = getIntent().getStringExtra("flow_type");
        email = getIntent().getStringExtra("email");

        if ("register".equals(flow_type)) {
            hoten = getIntent().getStringExtra("hoten");
            username = getIntent().getStringExtra("username");
            password = getIntent().getStringExtra("password");
            sdt = getIntent().getStringExtra("sdt");
            diachi = getIntent().getStringExtra("diachi");

            findViewById(R.id.layoutNewPassword).setVisibility(View.GONE);
            btnVerify.setText("Xác nhận và Hoàn tất Đăng ký");
        } else {
            flow_type = "reset";
            findViewById(R.id.layoutNewPassword).setVisibility(View.VISIBLE);
            btnVerify.setText("Xác nhận và Đổi mật khẩu");
        }
    }

    private void initControl() {
        btnVerify.setOnClickListener(v -> {
            String otp = edtOtp.getText().toString().trim();
            if (TextUtils.isEmpty(otp) || otp.length() != 6) {
                Toast.makeText(this, "Mã OTP phải có 6 chữ số", Toast.LENGTH_SHORT).show();
                return;
            }

            if ("register".equals(flow_type)) {
                handleRegisterVerification(otp);
            } else {
                String newPassword = edtNewPassword.getText().toString().trim();
                if (TextUtils.isEmpty(newPassword)) {
                    Toast.makeText(this, "Vui lòng nhập mật khẩu mới", Toast.LENGTH_SHORT).show();
                    return;
                }
                handleResetPasswordVerification(otp, newPassword);
            }
        });
        txtBack.setOnClickListener(v -> finish());
    }

    private void handleRegisterVerification(String otp) {
        btnVerify.setEnabled(false);
        compositeDisposable.add(apiBanHang.dangKiFinal(hoten, username, email, sdt, diachi, password, otp)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        userModel -> {
                            btnVerify.setEnabled(true);
                            if (userModel.isSuccess()) {
                                Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(this, DangNhapActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(this, userModel.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        },
                        throwable -> {
                            btnVerify.setEnabled(true);
                            Toast.makeText(this, "Lỗi kết nối: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                ));
    }

    private void handleResetPasswordVerification(String otp, String newPassword) {
        btnVerify.setEnabled(false);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("email", email);
        jsonObject.addProperty("otp", otp);
        jsonObject.addProperty("new_password", newPassword);

        // <<< SỬA LỖI TẠI ĐÂY: DÙNG ĐƯỜNG DẪN ĐẦY ĐỦ ĐỂ TRÁNH NHẦM LẪN >>>
        okhttp3.RequestBody requestBody = okhttp3.RequestBody.create(
                okhttp3.MediaType.parse("application/json"),
                jsonObject.toString()
        );

        compositeDisposable.add(apiBanHang.verifyAndResetPassword(requestBody) // <<< SỬA LỖI TẠI ĐÂY: GỌI ĐÚNG TÊN HÀM
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        userModel -> {
                            btnVerify.setEnabled(true);
                            if (userModel.isSuccess()) {
                                Toast.makeText(getApplicationContext(), "Đổi mật khẩu thành công!", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(this, DangNhapActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), userModel.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        },
                        throwable -> {
                            btnVerify.setEnabled(true);
                            Toast.makeText(getApplicationContext(), "Lỗi kết nối: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                ));
    }

    private void initView() {
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
        edtOtp = findViewById(R.id.edtOtp);
        edtNewPassword = findViewById(R.id.edtNewPassword);
        btnVerify = findViewById(R.id.btnVerify);
        txtBack = findViewById(R.id.txtBack);
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}
