package com.example.fashionshopapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fashionshopapp.R;
import com.example.fashionshopapp.model.Review;
import com.example.fashionshopapp.util.Utils;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.MyViewHolder> {

    private final Context context;
    private final List<Review> reviewList;

    public ReviewAdapter(Context context, List<Review> reviewList) {
        this.context = context;
        this.reviewList = reviewList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Review review = reviewList.get(position);

        holder.txtUserName.setText(review.getHoten());
        holder.txtComment.setText(review.getBinhluan());
        holder.txtDate.setText(review.getNgaydanhgia());
        holder.ratingBar.setRating(review.getSao());

        if (review.getUser_avatar() != null && !review.getUser_avatar().isEmpty()) {
            String imageUrl;
            if (review.getUser_avatar().contains("http")) {
                imageUrl = review.getUser_avatar();
            } else {
                imageUrl = Utils.BASE_URL + "images/" + review.getUser_avatar();
            }
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.profile) // Sử dụng icon avatar mới
                    .error(R.drawable.profile)       // Sử dụng icon avatar mới
                    .into(holder.imgAvatar);
        } else {
            // Sửa tại đây: Dùng icon avatar mặc định đã tạo
            holder.imgAvatar.setImageResource(R.drawable.profile);
        }

        if (review.getBinhluan() == null || review.getBinhluan().trim().isEmpty()) {
            holder.txtComment.setVisibility(View.GONE);
        } else {
            holder.txtComment.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        CircleImageView imgAvatar;
        TextView txtUserName, txtComment, txtDate;
        RatingBar ratingBar;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.review_user_avatar);
            txtUserName = itemView.findViewById(R.id.review_user_name);
            txtComment = itemView.findViewById(R.id.review_comment);
            txtDate = itemView.findViewById(R.id.review_date);
            ratingBar = itemView.findViewById(R.id.review_rating_bar);
        }
    }
}
