package com.example.fashionshopapp.Interface;

import android.view.View;

/**
 * Interface này chỉ dành riêng cho việc xử lý các sự kiện trong giỏ hàng.
 */
public interface GioHangItemClickListener {
    /**
     * @param view      View được click (nút xóa, nút tăng, nút giảm...).
     * @param pos       Vị trí của item.
     * @param typeClick Loại sự kiện (ví dụ: 1-tăng, 2-giảm, 3-xóa).
     */
    void onItemClick(View view, int pos, int typeClick);
}

