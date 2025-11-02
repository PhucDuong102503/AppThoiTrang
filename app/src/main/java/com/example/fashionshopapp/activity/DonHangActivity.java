package com.example.fashionshopapp.activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import com.example.fashionshopapp.R;
import com.example.fashionshopapp.adapter.DonHangViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class DonHangActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private Toolbar toolbar;
    private DonHangViewPagerAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_don_hang);

        initView();
        initControl();
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar_donhang);
        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);
    }

    private void initControl() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        // Khởi tạo Adapter cho ViewPager
        viewPagerAdapter = new DonHangViewPagerAdapter(this);
        viewPager.setAdapter(viewPagerAdapter);

        // Kết nối TabLayout với ViewPager2 để hiển thị tên cho các tab
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Chờ giao hàng");
                    break;
                case 1:
                    tab.setText("Đã giao hàng");
                    break;
                case 2:
                    tab.setText("Đã hủy");
                    break;
            }
        }).attach();
    }
}
