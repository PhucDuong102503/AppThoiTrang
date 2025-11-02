package com.example.fashionshopapp.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.fashionshopapp.fragment.DonHangFragment; // Đảm bảo bạn đã tạo DonHangFragment

public class DonHangViewPagerAdapter extends FragmentStateAdapter {

    public DonHangViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Dựa vào vị trí của tab, chúng ta tạo ra một DonHangFragment tương ứng
        // và truyền vào status_id (0, 1, hoặc 2)
        switch (position) {
            case 0:
                // Tab đầu tiên: "Chờ giao hàng"
                return DonHangFragment.newInstance(0);
            case 1:
                // Tab thứ hai: "Đã giao hàng"
                return DonHangFragment.newInstance(1);
            case 2:
                // Tab thứ ba: "Đã hủy"
                return DonHangFragment.newInstance(2);
            default:
                // Mặc định trả về tab đầu tiên
                return DonHangFragment.newInstance(0);
        }
    }

    @Override
    public int getItemCount() {
        // Chúng ta có tổng cộng 3 tab
        return 3;
    }
}
