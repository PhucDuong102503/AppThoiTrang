package com.example.fashionshopapp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fashionshopapp.R;
import com.example.fashionshopapp.adapter.DonHangAdapter;
import com.example.fashionshopapp.model.DonHang;
import com.example.fashionshopapp.retrofit.ApiBanHang;
import com.example.fashionshopapp.retrofit.RetrofitClient;
import com.example.fashionshopapp.util.Utils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class DonHangFragment extends Fragment {

    private static final String ARG_STATUS_ID = "status_id";
    private int statusId;

    private RecyclerView recyclerView;
    private TextView txtNoOrders;
    private DonHangAdapter donHangAdapter;
    private List<DonHang> listDonHang;
    private ApiBanHang apiBanHang;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public static DonHangFragment newInstance(int statusId) {
        DonHangFragment fragment = new DonHangFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_STATUS_ID, statusId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            statusId = getArguments().getInt(ARG_STATUS_ID);
        }
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_don_hang, container, false);
        recyclerView = view.findViewById(R.id.recycler_view_don_hang_fragment);
        txtNoOrders = view.findViewById(R.id.txt_no_orders);
        initRecyclerView();
        getDonHang();
        return view;
    }

    private void initRecyclerView() {
        listDonHang = new ArrayList<>();
        donHangAdapter = new DonHangAdapter(getContext(), listDonHang, donHang -> {
            // Xử lý logic hủy đơn ở đây
            Toast.makeText(getContext(), "Bạn đã nhấn hủy đơn hàng #" + donHang.getId(), Toast.LENGTH_SHORT).show();
            // Ví dụ gọi API hủy đơn
            // huyDonApiCall(donHang.getId());
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(donHangAdapter);
    }

    private void getDonHang() {
        if (Utils.user_current == null) return;

        compositeDisposable.add(apiBanHang.xemDonHang(Utils.user_current.getId(), statusId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        donHangModel -> {
                            if (donHangModel.isSuccess() && donHangModel.getResult() != null && !donHangModel.getResult().isEmpty()) {
                                listDonHang.clear();
                                listDonHang.addAll(donHangModel.getResult());
                                donHangAdapter.notifyDataSetChanged();
                                txtNoOrders.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
                            } else {
                                // API trả về success = false hoặc result rỗng
                                showEmptyView();
                            }
                        },
                        throwable -> {
                            // Lỗi kết nối hoặc lỗi phân tích JSON
                            showEmptyView();
                        }
                ));
    }

    private void showEmptyView() {
        listDonHang.clear();
        donHangAdapter.notifyDataSetChanged();
        txtNoOrders.setText("Chưa có đơn hàng nào.");
        txtNoOrders.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }

    @Override
    public void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}
