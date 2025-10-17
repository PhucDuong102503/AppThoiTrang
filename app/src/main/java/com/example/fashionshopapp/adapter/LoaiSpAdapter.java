package com.example.fashionshopapp.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.fashionshopapp.R;
import com.example.fashionshopapp.model.Loaisp;
import com.squareup.picasso.Picasso;

import java.util.List;

public class LoaiSpAdapter extends BaseAdapter {
    List<Loaisp> array;
    Context context;

    public LoaiSpAdapter(List<Loaisp> array, Context context) {
        this.array = array;
        this.context = context;
    }

    @Override
    public int getCount() {
        return array.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public class ViewHolder{
        TextView txtTenloaisp;
        ImageView imgHinhanhloaisp;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if (view == null){
            viewHolder = new ViewHolder();
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.item_sanpham,null);
            viewHolder.txtTenloaisp = view.findViewById(R.id.item_tenloaisp);
            viewHolder.imgHinhanhloaisp = view.findViewById(R.id.item_image);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();

        }
        viewHolder.txtTenloaisp.setText(array.get(i).getTenloaisanpham());
        Picasso.get().load(array.get(i).getHinhloaisanpham()).into(viewHolder.imgHinhanhloaisp);
//
//        Glide.with(context).load("https://cdn-icons-png.flaticon.com/128/1170/1170576.png")
//                .placeholder(R.drawable.ic_media_24)
//                .skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE)
//                .into(viewHolder.imgHinhanhloaisp);
//        Log.d("loggg", array.get(i).getHinhloaisanpham());
        return view;
    }
}
