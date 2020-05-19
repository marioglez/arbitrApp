package com.example.arbitrapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.arbitrapp.modelos.Campo;

public class CampoActivity extends AppCompatActivity {

    private Campo campo;
    private ImageView imagenCampo;
    private TextView nombreCampo, ciudad, direccion, capacidad;
    private RelativeLayout botonComoLlegar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campo);

        //ACTION BAR
        campo = (Campo) getIntent().getSerializableExtra("campo");
        setTitle(campo.getNombre());
        //boton flecha atras
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imagenCampo = findViewById(R.id.imageView_campo);
        nombreCampo = findViewById(R.id.nombre_campo);
        ciudad = findViewById(R.id.ciudad_campo);
        direccion = findViewById(R.id.direccion_campo);
        capacidad = findViewById(R.id.capacidad_campo);
        botonComoLlegar = findViewById(R.id.campo_comoLlegar);

        pintarDatos();
    }

    private void pintarDatos() {
        try{
            Glide
                    .with(CampoActivity.this)
                    .load(Uri.parse(campo.getImagen()))
                    .into(imagenCampo);
        }catch (Exception e){
            Log.d("EQUIPO ACTIVITY", "pintarDatos: ERROR AL PINTAR ESCUDO");
        }
        nombreCampo.setText(campo.getNombre());
        ciudad.setText(campo.getCiudad());
        direccion.setText(campo.getDireccion());
        capacidad.setText(campo.getCapacidad());
        botonComoLlegar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + campo.getUbicacion());
                final Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });
    }

    //Boton flecha atras
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
