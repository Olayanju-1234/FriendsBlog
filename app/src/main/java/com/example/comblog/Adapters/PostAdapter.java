package com.example.comblog.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.comblog.Activities.PostDetailsActivity;
import com.example.comblog.Models.Post;
import com.example.comblog.R;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder> {
    Context context;
    List<Post> mdata;

    public PostAdapter(Context context, List<Post> mdata) {
        this.context = context;
        this.mdata = mdata;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View row = LayoutInflater.from(context).inflate(R.layout.row_post_item, parent, false);


        return new MyViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.tvTitle.setText(mdata.get(position).getTitle());
        Glide.with(context).load(mdata.get(position).getPicture()).into(holder.imgPost);
        Glide.with(context).load(mdata.get(position).getUserPhoto()).into(holder.imgPostProfile);




    }

    @Override
    public int getItemCount() {
        return mdata.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle;
        ImageView imgPost, imgPostProfile;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.row_post_title);
            imgPost = itemView.findViewById(R.id.row_post_img);
            imgPostProfile = itemView.findViewById(R.id.row_post_profile_img);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent postDetailsActivity = new Intent(context, PostDetailsActivity.class);
                    int position = getAdapterPosition();

                    postDetailsActivity.putExtra("title", mdata.get(position).getTitle());
                    postDetailsActivity.putExtra("postImage", mdata.get(position).getPicture());
                    postDetailsActivity.putExtra("postDescription", mdata.get(position).getDescription());
                    postDetailsActivity.putExtra("postKey", mdata.get(position).getPostKey());
                    postDetailsActivity.putExtra("userPhoto", mdata.get(position).getUserPhoto());

                    long timeStamp = (long) mdata.get(position).getTimestamp();
                    postDetailsActivity.putExtra("postDate", timeStamp);
                    context.startActivity(postDetailsActivity);


                }
            });


        }
    }
}
