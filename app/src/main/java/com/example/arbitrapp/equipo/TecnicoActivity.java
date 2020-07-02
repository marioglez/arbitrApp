package com.example.arbitrapp.equipo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.arbitrapp.R;
import com.example.arbitrapp.modelos.Evento;
import com.example.arbitrapp.modelos.Partido;
import com.example.arbitrapp.modelos.Tecnico;
import com.example.arbitrapp.modelos.Usuario;
import static com.example.arbitrapp.FirebaseData.*;
import java.util.ArrayList;

public class TecnicoActivity extends AppCompatActivity {

    private Tecnico tecnico;
    private ArrayList<Partido> partidos;

    private ImageView fotoPersona;
    private TextView nombrePersona, rolPersona, nacionalidadPersona, edadPersona;
    private TextView partidosTotal, partidosJugados, partidosGanados, partidosPerdidos, partidosEmpatados;
    private TextView tarjetasTotal, tarjetasAmarillas, tarjetasRojas;

    private int contadorPartidosTotal, contadorJugados, contadorGanados, contadorPerdidos, contadorEmpatados, contadorAmarillas, contadorRojas;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tecnico);

        tecnico = (Tecnico) getIntent().getSerializableExtra("tecnico");
        partidos = (ArrayList<Partido>) getIntent().getSerializableExtra("partidos");
        setTitle("TÉCNICO");
        //boton flecha atras
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fotoPersona = findViewById(R.id.imagen_persona);
        nombrePersona = findViewById(R.id.textview_nombre_persona);
        rolPersona = findViewById(R.id.textview_rol_persona);
        nacionalidadPersona = findViewById(R.id.textview_nacionalidad_persona);
        edadPersona = findViewById(R.id.textview_edad_persona);
        partidosTotal = findViewById(R.id.textView_partidosTotal);
        partidosJugados = findViewById(R.id.textView_partidosJugados);
        partidosGanados = findViewById(R.id.textView_partidosGanados);
        partidosPerdidos = findViewById(R.id.textView_partidosPerdidos);
        partidosEmpatados = findViewById(R.id.textView_partidosEmpatados);
        tarjetasTotal = findViewById(R.id.textView_tarjetasTotal);
        tarjetasAmarillas = findViewById(R.id.textView_tarjetasAmarillas);
        tarjetasRojas = findViewById(R.id.textView_tarjetasRojas);

        try {
            rellenarInfo();
        } catch (Exception e) {
            finalizarActividad();
        }
    }

    //Boton flecha atras
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    private void rellenarInfo(){
        try{
            Glide
                    .with(TecnicoActivity.this)
                    .load(Uri.parse(tecnico.getImagen()))
                    .into(fotoPersona);
        }catch (Exception e){
            Log.d("TECNICO ACTIVITY", "pintarDatos: ERROR AL PINTAR IMAGEN");
        }

        nombrePersona.setText(tecnico.getNombreCompleto());
        rolPersona.setText(tecnico.getCargo());
        nacionalidadPersona.setText(tecnico.getNacionalidad());
        String edadCompleta = tecnico.getEdad() + " años";
        edadPersona.setText(edadCompleta);

        rellenarEstadisticas();
    }

    private void rellenarEstadisticas(){
        for(Partido partido : partidos){
            if(partido.getEstadoPartido().equals(PARTIDO_FINALIZADO)){
                //Partidos
                contadorPartidosTotal++;
                if(tecnico.getEquipo().getNombre().equals(partido.getEquipoLocal().getNombre())){
                    for(Tecnico t : partido.getEquipoLocal().getTecnicosPartido()){
                        if(t.getUid().equals(tecnico.getUid())){
                            contadorJugados++;
                            if(Integer.valueOf(partido.getGolesLocal()) > Integer.valueOf(partido.getGolesVisitante())){
                                contadorGanados++;
                            } else if(Integer.valueOf(partido.getGolesLocal()) < Integer.valueOf(partido.getGolesVisitante())){
                                contadorPerdidos++;
                            } else {
                                contadorEmpatados++;
                            }
                        }
                    }
                } else if (tecnico.getEquipo().getNombre().equals(partido.getEquipoVisitante().getNombre())){
                    for(Tecnico t : partido.getEquipoVisitante().getTecnicosPartido()){
                        if(t.getUid().equals(tecnico.getUid())){
                            contadorJugados++;
                            if(Integer.valueOf(partido.getGolesLocal()) < Integer.valueOf(partido.getGolesVisitante())){
                                contadorGanados++;
                            } else if(Integer.valueOf(partido.getGolesLocal()) > Integer.valueOf(partido.getGolesVisitante())){
                                contadorPerdidos++;
                            } else {
                                contadorEmpatados++;
                            }
                        }
                    }
                }

                //Eventos
                for(Evento evento : partido.getEventos()){
                    for(Usuario u : evento.getAutores()){
                        if(u.getUid().equals(tecnico.getUid())){
                            switch (evento.getAccion()){
                                case EVENTO_AMARILLA:
                                case EVENTO_SEGUNDA_AMARILLA:
                                    contadorAmarillas++;
                                    break;
                                case EVENTO_ROJA:
                                    contadorRojas++;
                                    break;
                            }
                        }
                    }
                }
            }
        }
        //Pintar estadisticas
        partidosTotal.setText(String.valueOf(contadorPartidosTotal));
        partidosJugados.setText(String.valueOf(contadorJugados));
        partidosGanados.setText(String.valueOf(contadorGanados));
        partidosPerdidos.setText(String.valueOf(contadorPerdidos));
        partidosEmpatados.setText(String.valueOf(contadorEmpatados));

        tarjetasTotal.setText(String.valueOf(contadorAmarillas+contadorRojas));
        tarjetasAmarillas.setText(String.valueOf(contadorAmarillas));
        tarjetasRojas.setText(String.valueOf(contadorRojas));
    }

    private void finalizarActividad() {
        Intent returnIntent = new Intent();
        setResult(1, returnIntent);
        finish();
    }
}
