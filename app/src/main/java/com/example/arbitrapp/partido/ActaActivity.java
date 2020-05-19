package com.example.arbitrapp.partido;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.arbitrapp.R;
import com.example.arbitrapp.arbitro.ArbitroActivity;
import com.example.arbitrapp.modelos.Partido;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ActaActivity extends AppCompatActivity {

    private Partido partido;
    private ImageView escudoLocal, escudoVisitante;
    private TextView nombreLocal, nombreVisitante, golesLocal, golesVisitante;
    private ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acta);

        partido = (Partido) getIntent().getSerializableExtra("partido");

        //ACTION BAR
        setTitle("ACTA");
        //boton flecha atras
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        scrollView = findViewById(R.id.scrollViewActa);
        //Barra de navegacion en partido
        BottomNavigationView bottomNavigationView = findViewById(R.id.acta_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        //Fragment inicial
        bottomNavigationView.setSelectedItemId(R.id.nav_alineaciones);

        escudoLocal = findViewById(R.id.escudo_local);
        escudoVisitante = findViewById(R.id.escudo_visitante);
        nombreLocal = findViewById(R.id.nombre_local);
        nombreVisitante = findViewById(R.id.nombre_visitante);
        golesLocal = findViewById(R.id.marcador_local);
        golesVisitante = findViewById(R.id.marcador_visitante);

        //RELLENAR INFO EN MARCADOR
        try {
            Glide
                    .with(ActaActivity.this)
                    .load(Uri.parse(partido.getEquipoLocal().getEscudo()))
                    .into(escudoLocal);
        }catch (Exception e){
            Log.d("ARBITRAR LOCAL", "pintarDatos: ERROR AL PINTAR ESCUDO");
        }

        try {
            Glide
                    .with(ActaActivity.this)
                    .load(Uri.parse(partido.getEquipoVisitante().getEscudo()))
                    .into(escudoVisitante);
        }catch (Exception e){
            Log.d("ARBITRAR VISITANTE", "pintarDatos: ERROR AL PINTAR ESCUDO");
        }

        nombreLocal.setText(partido.getEquipoLocal().getNombre());
        nombreVisitante.setText(partido.getEquipoVisitante().getNombre());
        golesLocal.setText(partido.getGolesLocal());
        golesVisitante.setText(partido.getGolesVisitante());


    }

    //Boton flecha atras
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;

            switch (item.getItemId()){
                case R.id.nav_alineaciones:
                    scrollView.smoothScrollTo(0,0);
                    selectedFragment = new ActaAlineacionesFragment(partido);
                    break;
                case R.id.nav_partido:
                    scrollView.smoothScrollTo(0,0);
                    selectedFragment = new ActaPartidoFragment(partido);
                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_acta_container, selectedFragment).commit();
            return true;
        }
    };
}
