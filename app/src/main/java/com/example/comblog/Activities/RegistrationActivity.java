package com.example.comblog.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.comblog.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.badge.BadgeUtils;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.net.URI;

public class RegistrationActivity extends AppCompatActivity {

    ImageView userPhoto;
    static int PReqcode = 1;
    static int REQUESTCODE = 1;
    Uri pickedImageUri;
    private EditText userName, userMail, userPassword, userConfirmPassword;
    private ProgressBar loadingBar;
    private Button regButon;
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        userName = findViewById(R.id.regName);
        userMail = findViewById(R.id.regMail);
        userPassword = findViewById(R.id.regPassword);
        userConfirmPassword = findViewById(R.id.regPassword2);
        loadingBar = findViewById(R.id.progressBar);
        regButon = findViewById(R.id.regButton);

        loadingBar.setVisibility(View.INVISIBLE);

        firebaseAuth = FirebaseAuth.getInstance();

        regButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                regButon.setVisibility(View.INVISIBLE);
                loadingBar.setVisibility(View.VISIBLE);
                final String email = userMail.getText().toString();
                final String name = userName.getText().toString();
                final String password = userPassword.getText().toString();
                final String confirmPassword = userConfirmPassword.getText().toString();

                if (email.isEmpty() || name.isEmpty() || password.isEmpty() || !confirmPassword.equals(password)) {
                    showMessage("Please Verify all fields!");
                    regButon.setVisibility(View.VISIBLE);
                    loadingBar.setVisibility(View.INVISIBLE);
                }else {
                    createUserAccount(email,name,password);
                }
            }
        });




        userPhoto = findViewById(R.id.regUserImage);
        userPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= 23) {
                    checkAndRequestForPermission();
                }else{
                    openGallery();
                }
            }
        });
    }

    private void createUserAccount(String email, String name, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            showMessage("Account Created");
                            updateUserInfo(name, pickedImageUri, firebaseAuth.getCurrentUser());
                        }
                        else {
                            showMessage("Account creation failed" + task.getException().getMessage());
                            regButon.setVisibility(View.VISIBLE);
                            loadingBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });
    }

    //update user info
    private void updateUserInfo(String name, Uri pickedImageUri, FirebaseUser currentUser) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("users_photo");
        StorageReference imageFilePath = storageReference.child(pickedImageUri.getLastPathSegment());
        imageFilePath.putFile(pickedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                                .setDisplayName(name)
                                .setPhotoUri(uri)
                                .build();

                        currentUser.updateProfile(profileUpdate).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    showMessage("Registration Complete");
                                    updateUI();
                                }

                            }
                        });
                    }
                });
            }
        });
    }

    private void updateUI() {
        Intent homeActivity = new Intent(RegistrationActivity.this, Home.class);
        startActivity(homeActivity);
        finish();
    }

    private void showMessage(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }

    private void openGallery() {
        //Open Gallery to pick UserPhoto
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, REQUESTCODE);
    }

    private void checkAndRequestForPermission() {
        if (ContextCompat.checkSelfPermission(RegistrationActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(RegistrationActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(this, "Please accept for required permissions", Toast.LENGTH_SHORT).show();
            }else {
                ActivityCompat.requestPermissions(RegistrationActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},PReqcode);
            }
                
        }else {
            openGallery();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUESTCODE && data != null) {
            //successfully chosen a pic from gallery, we save URI variable (the ref)
        pickedImageUri = data.getData();
        userPhoto.setImageURI(pickedImageUri);
        }
    }
}