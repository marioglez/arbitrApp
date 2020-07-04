package com.example.arbitrapp.arbitro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.arbitrapp.R;
import com.example.arbitrapp.modelos.Arbitro;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ArbitroActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private Arbitro arbitro;
    private ImageView imagenArbitro;
    private TextView nombre, categoria, nacionalidad, edad;
    private ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arbitro);

        //ACTION BAR
        setTitle(getResources().getString(R.string.ARBITRO));
        //boton flecha atras
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        arbitro = (Arbitro) getIntent().getSerializableExtra("arbitro");

        scrollView = findViewById(R.id.scrollViewArbitro);
        bottomNavigationView = findViewById(R.id.arbitro_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
        bottomNavigationView.setSelectedItemId(R.id.nav_informacion);

        imagenArbitro = findViewById(R.id.imagen_arbitro);
        nombre = findViewById(R.id.textview_nombre_arbitro);
        categoria = findViewById(R.id.textview_categoria_arbitro);
        nacionalidad = findViewById(R.id.textview_nacionalidad_arbitro);
        edad = findViewById(R.id.textview_edad_arbitro);

        rellenarInformacion();
    }

    public void rellenarInformacion(){
        try {
            Glide
                    .with(ArbitroActivity.this)
                    .load(Uri.parse(arbitro.getImagen()))
                    .into(imagenArbitro);
        }catch (Exception e){
            Log.d("ARBITRO ACTIVITY", "pintarDatos: ERROR AL PINTAR IMAGEN");
        }

        nombre.setText(arbitro.getNombreCompleto());
        categoria.setText(arbitro.getCategoria());
        nacionalidad.setText(arbitro.getNacionalidad());
        String edadCompleta = arbitro.getEdad() + " " + getResources().getString(R.string.anos);
        edad.setText(edadCompleta);
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
                case R.id.nav_informacion:
                    scrollView.smoothScrollTo(0,0);
                    selectedFragment = new ArbitroInformacionFragment(arbitro);
                    break;
                case R.id.nav_agenda:
                    scrollView.smoothScrollTo(0,0);
                    selectedFragment = new ArbitroAgendaFragment(arbitro);
                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_arbitro_container, selectedFragment).commit();
            return true;
        }
    };
}
