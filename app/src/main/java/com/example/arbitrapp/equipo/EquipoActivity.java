package com.example.arbitrapp.equipo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.arbitrapp.R;
import com.example.arbitrapp.modelos.Equipo;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class EquipoActivity extends AppCompatActivity {

    private Equipo equipo;

    private BottomNavigationView bottomNavigationView;
    private ImageView imagenEscudo, iconoCampo, iconoCamiseta, iconoPantalon, iconoMedias;
    private TextView categoria, ciudad, campo;
    private ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equipo);

        //ACTION BAR
        equipo = (Equipo) getIntent().getSerializableExtra("equipo");
        setTitle(equipo.getNombre());
        //boton flecha atras
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        scrollView = findViewById(R.id.scrollViewEquipo);
        //Barra de navegacion en partido
        bottomNavigationView = findViewById(R.id.equipo_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
        //Fragment inicial
        bottomNavigationView.setSelectedItemId(R.id.nav_plantilla_equipo);

        imagenEscudo = findViewById(R.id.imagen_escudo);
        iconoCampo = findViewById(R.id.imageView_campo);
        iconoCamiseta = findViewById(R.id.imageView_camiseta);
        iconoPantalon = findViewById(R.id.imageView_pantalon);
        iconoMedias = findViewById(R.id.imageView_medias);
        categoria = findViewById(R.id.textview_categoria_equipo);
        ciudad = findViewById(R.id.textView_ciudad_equipo);
        campo = findViewById(R.id.textView_campo_equipo);

        pintarDatos();
    }

    private void pintarDatos(){
        try{
            Glide
                    .with(EquipoActivity.this)
                    .load(Uri.parse(equipo.getEscudo()))
                    .into(imagenEscudo);
        }catch (Exception e){
            Log.d("EQUIPO ACTIVITY", "pintarDatos: ERROR AL PINTAR ESCUDO");
        }
        categoria.setText(equipo.getCategoria());
        ciudad.setText(equipo.getCiudad());
        iconoCampo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EquipoActivity.this, CampoActivity.class).putExtra("campo",equipo.getCampo()));
            }
        });
        campo.setText(equipo.getCampo().getNombre());
        campo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EquipoActivity.this, CampoActivity.class).putExtra("campo",equipo.getCampo()));
            }
        });

        //Rellenar equipacion
        if(equipo.getEquipacion().getCamiseta().equals("#ffffff")){
            iconoCamiseta.setImageResource(R.drawable.ic_camisetablanca);
        } else {
            iconoCamiseta.setColorFilter(Color.parseColor(equipo.getEquipacion().getCamiseta()));
        }
        if(equipo.getEquipacion().getPantalon().equals("#ffffff")){
            iconoPantalon.setImageResource(R.drawable.ic_shortsblancos);
        } else {
            iconoPantalon.setColorFilter(Color.parseColor(equipo.getEquipacion().getPantalon()));
        }
        if(equipo.getEquipacion().getMedias().equals("#ffffff")){
            iconoMedias.setImageResource(R.drawable.ic_calcetinesblancos);
        } else {
            iconoMedias.setColorFilter(Color.parseColor(equipo.getEquipacion().getMedias()));
        }
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
                case R.id.nav_plantilla_equipo:
                    scrollView.smoothScrollTo(0,0);
                    selectedFragment = new EquipoPlantillaFragment(equipo);
                    break;
                case R.id.nav_estadisticas_equipo:
                    scrollView.smoothScrollTo(0,0);
                    selectedFragment = new EquipoEstadisticasFragment(equipo);
                    break;
                case R.id.nav_calendario_equipo:
                    scrollView.smoothScrollTo(0,0);
                    selectedFragment = new EquipoCalendarioFragment(equipo);
                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_equipo_container, selectedFragment).commit();
            return true;
        }
    };
}
