package com.example.arbitrapp.competiciones;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ScrollView;
import android.widget.Toast;
import com.example.arbitrapp.R;
import com.example.arbitrapp.modelos.Competicion;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import static com.example.arbitrapp.FirebaseData.*;

public class CompeticionActivity extends AppCompatActivity {

    private Competicion competicion;
    private boolean favorito;
    private ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_competicion);

        competicion = (Competicion) getIntent().getSerializableExtra("competicion");
        if (!currentUser.getTipoUsuario().equals(USUARIO_INVITADO)  && !currentUser.getTipoUsuario().equals(USUARIO_ADMIN)) {
            favorito = esFavorito();
        }

        setTitle(competicion.getCategoria() + " - " + competicion.getSede());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        scrollView = findViewById(R.id.scrollViewCompeticion);
        BottomNavigationView bottomNavigationView = findViewById(R.id.competicion_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
        bottomNavigationView.setSelectedItemId(R.id.nav_competicion_equipos);
    }

    //Boton flecha atras
    @Override
    public boolean onSupportNavigateUp(){
        Intent returnIntent = new Intent();
        setResult(RESULT_CANCELED, returnIntent);
        finish();
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        setResult(RESULT_CANCELED, returnIntent);
        finish();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;

            switch (item.getItemId()){
                case R.id.nav_competicion_equipos:
                    scrollView.smoothScrollTo(0,0);
                    selectedFragment = new CompeticionEquiposFragment(competicion.getEquipos(),competicion.getTemporada());
                    break;
                case R.id.nav_competicion_clasificacion:
                    scrollView.smoothScrollTo(0,0);
                    selectedFragment = new CompeticionClasificacionFragment(competicion);
                    break;
                case R.id.nav_competicion_calendario:
                    scrollView.smoothScrollTo(0,0);
                    selectedFragment = new CompeticionCalendarioFragment(competicion.getJornadas());
                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_competicion_container, selectedFragment).commit();
            return true;
        }
    };

    public boolean onCreateOptionsMenu(Menu menu){
        if (!currentUser.getTipoUsuario().equals(USUARIO_INVITADO) && !currentUser.getTipoUsuario().equals(USUARIO_ADMIN)) {
            getMenuInflater().inflate(R.menu.actionbar_favoritos, menu);
            if (favorito){
                menu.findItem(R.id.favoritos).setIcon(getResources().getDrawable(R.drawable.ic_star_accent));
            }
            return true;
        } else {
            return false;
        }
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int id = menuItem.getItemId();
        if (id == R.id.favoritos){

            Drawable.ConstantState constantStateDrawableA = getResources().getDrawable(R.drawable.ic_star_border_black_24dp).getConstantState();

            if(constantStateDrawableA.equals(menuItem.getIcon().getConstantState())) {
                menuItem.setIcon(R.drawable.ic_star_accent);
                Toast.makeText(CompeticionActivity.this, getResources().getString(R.string.competicionFavorita),Toast.LENGTH_LONG).show();
                currentUser.addCompeticionFavorita(competicion);
            } else {
                menuItem.setIcon(R.drawable.ic_star_border_black_24dp);
                currentUser.eliminarCompeticionFavorita(competicion);
            }

        }
        return super.onOptionsItemSelected(menuItem);
    }

    private boolean esFavorito(){
        for (Competicion c : currentUser.getCompeticionesfavoritas()){
            if (c.getTemporada().equals(competicion.getTemporada())){
                if (c.getSede().equals(competicion.getSede())){
                    if (c.getCategoria().equals(competicion.getCategoria())){
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
