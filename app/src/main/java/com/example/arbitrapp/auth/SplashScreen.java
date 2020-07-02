package com.example.arbitrapp.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import static com.example.arbitrapp.FirebaseData.*;
import com.example.arbitrapp.R;
import com.example.arbitrapp.home.HomeScreen;
import com.example.arbitrapp.modelos.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import java.util.concurrent.CountDownLatch;

public class SplashScreen extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private CountDownLatch countDownLatchUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() != null){
                    currentUser = new Usuario(countDownLatchUsuario);
                    currentUser.start();
                    try{
                        countDownLatchUsuario.await();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    irAHome();
                } else {
                    startActivity(new Intent(SplashScreen.this, LoginScreen.class));
                    mAuth.removeAuthStateListener(mAuthListener);
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    private void irAHome() {
        startActivity(new Intent(SplashScreen.this, HomeScreen.class));
        mAuth.removeAuthStateListener(mAuthListener);
    }
}
