package com.example.app_food_basic.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.app_food_basic.Activity.KhachHang.HomeKH;
import com.example.app_food_basic.Activity.QuanAn.Home;
import com.example.app_food_basic.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class Splash extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        firebaseAuth = FirebaseAuth.getInstance();
        new Handler().postDelayed(() -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user==null){
                startActivity(new Intent(getApplicationContext(), DangNhap.class));
                finish();
            }
            else {
                kiemTra();
            }
        }, 1000);
    }
    private void kiemTra() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("TaiKhoan");
        ref.child(Objects.requireNonNull(firebaseAuth.getUid()))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String taiKhoan = ""+dataSnapshot.child("taiKhoan").getValue();
                        if (Objects.requireNonNull(firebaseAuth.getCurrentUser()).isEmailVerified()) {
                            if (taiKhoan.equals("QuanAn")){
                                startActivity(new Intent(getApplicationContext(), Home.class));
                            }
                            else {
                                startActivity(new Intent(getApplicationContext(), HomeKH.class));
                            }
                            finish();
                        } else startActivity(new Intent(getApplicationContext(), DangNhap.class));

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}