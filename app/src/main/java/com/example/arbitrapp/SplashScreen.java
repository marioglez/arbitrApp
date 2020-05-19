package com.example.arbitrapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import static com.example.arbitrapp.FirebaseData.*;

import com.example.arbitrapp.home.HomeScreen;
import com.example.arbitrapp.modelos.Partido;
import com.example.arbitrapp.modelos.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class SplashScreen extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private ArrayList<Partido> proximosPartidos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        proximosPartidos = new ArrayList<>();
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() != null){
                    currentUser = new Usuario();
                    obtenerProximosPartidos();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(new Intent(SplashScreen.this, HomeScreen.class).putExtra("proximosPartidos",proximosPartidos));
                            mAuth.removeAuthStateListener(mAuthListener);
                        }
                    }, 3000);
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

    private void obtenerProximosPartidos() {
        Calendar cal = Calendar.getInstance();
        final int day = cal.get(Calendar.DAY_OF_YEAR);
        final int year = cal.get(Calendar.YEAR);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child(COMPETICIONES).child(TEMPORADA_ACTUAL).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    int partidosEncontrados = 0;
                    int diasBuscados = 0;
                    do {
                        final int dia = day + diasBuscados;
                        final String idPartidos = dayOfYear(year, dia);
                        for (DataSnapshot sede : dataSnapshot.getChildren()) {
                            for (DataSnapshot categoria : sede.getChildren()) {
                                for (DataSnapshot jornada : categoria.child(PARTIDOS).getChildren()) {
                                    for (DataSnapshot diaPartido : jornada.getChildren()) {
                                        if (diaPartido.getKey().equals(idPartidos)) {
                                            for (DataSnapshot partido : diaPartido.getChildren()) {
                                                if (!partido.child(PARTIDO_ESTADO).getValue().toString().equals(PARTIDO_FINALIZADO)) {
                                                    proximosPartidos.add(new Partido(TEMPORADA_ACTUAL, sede.getKey(), categoria.getKey(), idPartidos, partido.getKey()));
                                                    partidosEncontrados++;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        diasBuscados++;
                    } while (partidosEncontrados<3 && diasBuscados<7);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private String dayOfYear(int year, int dia){
        //Comprobar si pasa del dia maximo del año
        Calendar cal = new GregorianCalendar(year, 11, 31);
        int totalDias = cal.get(Calendar.DAY_OF_YEAR);

        if(dia>totalDias){
            year += + 1;
            dia -= totalDias;
            return (String.valueOf(year) + String.valueOf(dia));
        } else {
            return (String.valueOf(year) + String.valueOf(dia));
        }
    }

}
