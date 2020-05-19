package com.example.arbitrapp.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.arbitrapp.competiciones.CompeticionesFragment;
import com.example.arbitrapp.LoginScreen;
import com.example.arbitrapp.R;
import com.example.arbitrapp.modelos.Partido;
import com.example.arbitrapp.modelos.Usuario;
import com.example.arbitrapp.perfil.PerfilFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import static com.example.arbitrapp.FirebaseData.COMPETICIONES;
import static com.example.arbitrapp.FirebaseData.PARTIDOS;
import static com.example.arbitrapp.FirebaseData.TEMPORADA_ACTUAL;
import static com.example.arbitrapp.FirebaseData.currentUser;

public class HomeScreen extends AppCompatActivity {

    private ArrayList<Partido> proximosPartidos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        //getSupportActionBar().setDisplayShowTitleEnabled(false);

        proximosPartidos = (ArrayList<Partido>) getIntent().getSerializableExtra("proximosPartidos");
        //Toast.makeText(HomeScreen.this, "HOME: " + currentUser.getNombreCompleto(), Toast.LENGTH_LONG).show();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        //Fragment inicial
        bottomNavigationView.setSelectedItemId(R.id.nav_home);

    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;

            switch (item.getItemId()){
                case R.id.nav_home:
                    selectedFragment = new HomeFragment(proximosPartidos);
                    break;
                case R.id.nav_competiciones:
                    selectedFragment = new CompeticionesFragment();
                    break;
                case R.id.nav_perfil:
                    selectedFragment = new PerfilFragment();
                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
            return true;
        }
    };

    @Override
    public void onBackPressed() {}

    //METODO PARA MOSTRAR OCULTAR MENU
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.actionbar_perfil, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int id = menuItem.getItemId();
        if (id == R.id.cerrar_sesion){
            Log.d("ARBITRAPP", "onOptionItemSelected: CERRAR SESION!");
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(HomeScreen.this, LoginScreen.class));
        }
        return super.onOptionsItemSelected(menuItem);
    }
}
