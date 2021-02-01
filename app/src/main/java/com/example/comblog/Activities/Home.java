package com.example.comblog.Activities;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.comblog.Fragments.HomeFragment;
import com.example.comblog.Fragments.ProfileFragment;
import com.example.comblog.Fragments.SettinsFragment;
import com.example.comblog.Models.Post;
import com.example.comblog.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Objects;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private static final int REQUESTCODE = 2;
    FirebaseAuth firebaseAuth;
    FirebaseUser currentUser;
    Dialog popAddPost;
    ImageView popUpUserImage, popUpPostImage, popUpAddButton;
    TextView popUpTitle, popUpDesc;
    ProgressBar popUpProgress;
    private static final int PReqcode = 2;
    private Uri pickedImageUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home2);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        popUpPostImage = findViewById(R.id.popup_image);
        
        initializePopUp();
        setUpPopUpImageClick();


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popAddPost.show();
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        updateNavHeader();

        getSupportFragmentManager().beginTransaction().replace(R.id.contentcontainer, new HomeFragment()).commit();

          }

    private void setUpPopUpImageClick() {

        popUpPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAndRequestForPermission();
            }
        });
    }

    private void checkAndRequestForPermission() {
        if (ContextCompat.checkSelfPermission(Home.this, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(Home.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(this, "Please accept for required permissions", Toast.LENGTH_SHORT).show();
            }else {
                ActivityCompat.requestPermissions(Home.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},PReqcode);
            }

        }else {
            openGallery();
        }
    }

    private void openGallery() {
        //Open Gallery to pick UserPhoto
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, REQUESTCODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUESTCODE && data != null) {
            //successfully chosen a pic from gallery, we save URI variable (the ref)
            pickedImageUri = data.getData();
            popUpPostImage.setImageURI(pickedImageUri);
        }
    }

    private void initializePopUp() {

        popAddPost = new Dialog(this);
        popAddPost.setContentView(R.layout.popup_add_post);
        popAddPost.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popAddPost.getWindow().setLayout(Toolbar.LayoutParams.MATCH_PARENT, Toolbar.LayoutParams.WRAP_CONTENT);
        popAddPost.getWindow().getAttributes().gravity = Gravity.TOP;

        popUpUserImage = popAddPost.findViewById(R.id.popup_user_photo);
        popUpTitle = popAddPost.findViewById(R.id.popup_title);
        popUpDesc = popAddPost.findViewById(R.id.popup_description);
        popUpAddButton = popAddPost.findViewById(R.id.popup_create);
        popUpProgress = popAddPost.findViewById(R.id.popup_progressBar);
        popUpPostImage = popAddPost.findViewById(R.id.popup_image);

        Glide.with(Home.this).load(currentUser.getPhotoUrl()).into(popUpUserImage);

        popUpAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                popUpAddButton.setVisibility(View.INVISIBLE);
                popUpProgress.setVisibility(View.VISIBLE);

                if (!popUpTitle.getText().toString().isEmpty() &&
                !popUpDesc.getText().toString().isEmpty() && pickedImageUri != null) {

                    StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("blog_images");
                    final StorageReference imageFilePath = storageReference.child(pickedImageUri.getLastPathSegment());
                    imageFilePath.putFile(pickedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            imageFilePath.getDownloadUrl().addOnSuccessListener(uri -> {
                                String imageDownloadLink = uri.toString();
                                Post post = new Post(popUpTitle.getText().toString(),
                                        popUpDesc.getText().toString(),
                                        imageDownloadLink,
                                        currentUser.getUid(),
                                        Objects.requireNonNull(currentUser.getPhotoUrl()).toString());

                                addPost(post);



                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    showMessage(e.getMessage());
                                    popUpProgress.setVisibility(View.INVISIBLE);
                                    popUpAddButton.setVisibility(View.VISIBLE);
                                }
                            });
                        }
                    });

                }
                else {
                    showMessage("Please verify all fields and add an image");
                    popUpAddButton.setVisibility(View.VISIBLE);
                    popUpProgress.setVisibility(View.INVISIBLE);
                }
            }
        });

    }

    private void addPost(Post post) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("Posts").push();

        String key = databaseReference.getKey();
        post.setPostKey(key);

        databaseReference.setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                showMessage("Post added successfully");
                popUpProgress.setVisibility(View.INVISIBLE);
                popUpAddButton.setVisibility(View.VISIBLE);
                popAddPost.dismiss();

            }
        });
    }

    private void showMessage(String messages) {
        Toast.makeText(this, messages, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {

            getSupportActionBar().setTitle("Home");
            getSupportFragmentManager().beginTransaction().replace(R.id.contentcontainer, new HomeFragment()).commit();

        }else if (id == R.id.nav_profile) {

            getSupportActionBar().setTitle("Profile");
            getSupportFragmentManager().beginTransaction().replace(R.id.contentcontainer, new ProfileFragment()).commit();


        } else if (id == R.id.nav_settings) {

            getSupportActionBar().setTitle("Settings");
            getSupportFragmentManager().beginTransaction().replace(R.id.contentcontainer, new SettinsFragment()).commit();


        } else if (id == R.id.nav_log_out) {

            FirebaseAuth.getInstance().signOut();
            Intent logout = new Intent(this, LoginActivity.class);
            startActivity(logout);
            finish();



        }

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    public void updateNavHeader() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView navUserName = headerView.findViewById(R.id.nav_user_name);
        TextView navEmail = headerView.findViewById(R.id.nav_user_mail);
        ImageView navUserPhoto = headerView.findViewById(R.id.nav_user_photo);

        navEmail.setText(currentUser.getEmail());
        navUserName.setText(currentUser.getDisplayName());

        Glide.with(this).load(currentUser.getPhotoUrl()).into(navUserPhoto);

    }
}