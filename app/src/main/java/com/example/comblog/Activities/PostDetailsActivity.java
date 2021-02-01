package com.example.comblog.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.comblog.Adapters.CommentAdapter;
import com.example.comblog.Models.Comment;
import com.example.comblog.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class PostDetailsActivity extends AppCompatActivity {

    ImageView imgPost, imgUserPost, imgCurrentUser;
    TextView textPostDescription, textPostDateName, textPostTitle;
    EditText editTextCommentBox;
    Button addCommentButton;

    String PostKey;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;

    RecyclerView commentRecyclerView;
    CommentAdapter commentAdapter;
    List<Comment> listComment;

    static String COMMENT_KEY = "Comment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);

        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        Objects.requireNonNull(getSupportActionBar()).hide();


        commentRecyclerView = findViewById(R.id.recyclerview_comment);
        imgCurrentUser = findViewById(R.id.post_details_currentuserimage);
        imgPost = findViewById(R.id.post_details_postUserImage);
        imgUserPost = findViewById(R.id.post_details_image);
        textPostDescription = findViewById(R.id.post_details_description);
        textPostDateName = findViewById(R.id.post_details_date_name);
        textPostTitle = findViewById(R.id.post_detail_title);
        editTextCommentBox = findViewById(R.id.post_details_comment_box);
        addCommentButton = findViewById(R.id.post_details_add_comment_button);

        firebaseAuth =  FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();

        addCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference commentReference = firebaseDatabase.getReference(COMMENT_KEY).child(PostKey).push();
                String comment_content = editTextCommentBox.getText().toString();
                String uid = firebaseUser.getUid();
                String uName = firebaseUser.getDisplayName();
                String uImg = Objects.requireNonNull(firebaseUser.getPhotoUrl()).toString();

                Comment comment = new Comment(comment_content, uid, uImg, uName);

                commentReference.setValue(comment).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        showMessage("Comment Added");
                        editTextCommentBox.setText("");
                        addCommentButton.setVisibility(View.VISIBLE);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showMessage("Failed to add comment" + e.getMessage());
                    }
                });
            }
        });

        String postImage = getIntent().getExtras().getString("postImage");
        Glide.with(this).load(postImage).into(imgUserPost);

        String postTitle = getIntent().getExtras().getString("title");
        textPostTitle.setText(postTitle);

        String userPostImage =  getIntent().getExtras().getString("userPhoto");
        Glide.with(this).load(userPostImage).into(imgPost);

        String postDescription = getIntent().getExtras().getString("postDescription");
        textPostDescription.setText(postDescription);

        Glide.with(this).load(firebaseUser.getPhotoUrl()).into(imgCurrentUser);

        PostKey = getIntent().getExtras().getString("postKey");

        String date = timeStampToString(getIntent().getExtras().getLong("postDate"));
        textPostDateName.setText(date);

        initializeRecyclerComment();


    }

    private void initializeRecyclerComment() {

        commentRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        DatabaseReference commentReference = firebaseDatabase.getReference(COMMENT_KEY).child(PostKey);
        commentReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listComment = new ArrayList<>();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    Comment comment = snapshot1.getValue(Comment.class);
                    listComment.add(comment);
                }

                commentAdapter = new CommentAdapter(getApplicationContext(), listComment);
                commentRecyclerView.setAdapter(commentAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void showMessage(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    private String timeStampToString(long time) {
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        String date = DateFormat.format("dd-MM-yyyy",calendar).toString();
        return date;
    }
}