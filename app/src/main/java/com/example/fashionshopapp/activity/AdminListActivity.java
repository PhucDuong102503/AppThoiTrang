package com.example.fashionshopapp.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fashionshopapp.R;
import com.example.fashionshopapp.adapter.AdminAdapter;
import com.example.fashionshopapp.model.User;
import com.example.fashionshopapp.model.UserApiResponse;
import com.example.fashionshopapp.retrofit.ApiBanHang;
import com.example.fashionshopapp.retrofit.RetrofitClient;
import com.example.fashionshopapp.util.Utils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class AdminListActivity extends AppCompatActivity {

    private static final String TAG = "AdminListActivity";

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView txtEmptyMessage;

    private ApiBanHang apiBanHang;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private AdminAdapter adminAdapter;
    private List<User> adminList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_list);

        initView();
        setupToolbar();
        fetchAdminListFromApi();
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar_admin_list);
        recyclerView = findViewById(R.id.recycleview_admin_list);
        progressBar = findViewById(R.id.progressbar_loading);
        txtEmptyMessage = findViewById(R.id.text_empty_message);

        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);

        adminList = new ArrayList<>();
        adminAdapter = new AdminAdapter(this, adminList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adminAdapter);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            toolbar.setNavigationOnClickListener(v -> finish());
        }
    }

    private void fetchAdminListFromApi() {
        showLoadingState(true);

        compositeDisposable.add(apiBanHang.getAdmins()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        userApiResponse -> {
                            showLoadingState(false);
                            if (userApiResponse.isSuccess() && userApiResponse.getUserList() != null && !userApiResponse.getUserList().isEmpty()) {
                                showDataState(userApiResponse.getUserList());
                            } else {
                                String message = (userApiResponse.getMessage() != null) ? userApiResponse.getMessage() : "Không có dữ liệu.";
                                showEmptyOrErrorState(message);
                            }
                        },
                        throwable -> {
                            showLoadingState(false);
                            Log.e(TAG, "Lỗi khi gọi API getAdmins: ", throwable);
                            showEmptyOrErrorState("Lỗi kết nối máy chủ. Vui lòng thử lại.");
                        }
                ));
    }

    private void showLoadingState(boolean isLoading) {
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            txtEmptyMessage.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    private void showDataState(List<User> data) {
        recyclerView.setVisibility(View.VISIBLE);
        txtEmptyMessage.setVisibility(View.GONE);
        adminList.clear();
        adminList.addAll(data);
        adminAdapter.notifyDataSetChanged();
    }

    private void showEmptyOrErrorState(String message) {
        recyclerView.setVisibility(View.GONE);
        txtEmptyMessage.setVisibility(View.VISIBLE);
        txtEmptyMessage.setText(message);
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}
