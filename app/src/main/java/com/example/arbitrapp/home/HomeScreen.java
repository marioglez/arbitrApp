package com.example.arbitrapp.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import com.example.arbitrapp.competiciones.CompeticionesFragment;
import com.example.arbitrapp.auth.LoginScreen;
import com.example.arbitrapp.R;
import com.example.arbitrapp.perfil.PerfilFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import static com.example.arbitrapp.FirebaseData.USUARIO_ADMIN;
import static com.example.arbitrapp.FirebaseData.USUARIO_INVITADO;
import static com.example.arbitrapp.FirebaseData.currentArbitro;
import static com.example.arbitrapp.FirebaseData.currentUser;

public class HomeScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        //getSupportActionBar().setDisplayShowTitleEnabled(false);

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
                    selectedFragment = new HomeFragment();
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
        if (currentUser.getTipoUsuario().equals(USUARIO_INVITADO) || currentUser.getTipoUsuario().equals(USUARIO_ADMIN)) {
            getMenuInflater().inflate(R.menu.actionbar_perfil_invitado, menu);
        } else {
            getMenuInflater().inflate(R.menu.actionbar_perfil, menu);
        }
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        currentUser = null;
        currentArbitro = null;
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(HomeScreen.this, LoginScreen.class));
        return super.onOptionsItemSelected(menuItem);
    }
}
