package com.example.comblog.Adapters;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.comblog.Models.Comment;
import com.example.comblog.R;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.MyViewHolder> {

    Context context;
    List<Comment> mData;

    public CommentAdapter(Context context, List<Comment> mData) {
        this.context = context;
        this.mData = mData;
    }

    @NonNull
    @Override
    public CommentAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(context).inflate(R.layout.row_comment, parent, false);
        return new MyViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

    Glide.with(context).load(mData.get(position).getuImg()).into(holder.img_user);
    holder.tv_name.setText(mData.get(position).getuName());
    holder.tv_content.setText(mData.get(position).getContent());
    holder.tv_date.setText(timeStampToString((Long) mData.get(position).getTimeStamp()));

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView img_user;
        TextView tv_name, tv_content, tv_date;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            img_user = itemView.findViewById(R.id.comment_user_image);
            tv_name = itemView.findViewById(R.id.comment_username);
            tv_content = itemView.findViewById(R.id.comment_content);
            tv_date = itemView.findViewById(R.id.comment_date);


        }
    }

    private String timeStampToString(long time) {
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        String date = DateFormat.format("dd-MM-yyyy",calendar).toString();
        return date;
    }
}
