package com.example.arbitrapp.equipo;

import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.example.arbitrapp.R;
import com.example.arbitrapp.modelos.Evento;
import com.example.arbitrapp.modelos.Jugador;
import com.example.arbitrapp.modelos.Partido;
import com.example.arbitrapp.modelos.Usuario;
import static com.example.arbitrapp.FirebaseData.*;
import java.util.ArrayList;

public class JugadorActivity extends AppCompatActivity {

    private Jugador jugador;
    private ArrayList<Partido> partidos;

    private ImageView fotoPersona, iconoPersona;
    private TextView dorsalPersona, nombrePersona, rolPersona, nacionalidadPersona, edadPersona;
    private TextView partidosTotal, partidosJugados, partidosTitular, partidosSuplente, partidosSinJugar;
    private TextView golesMarcados, golesPropia;
    private TextView tarjetasTotal, tarjetasAmarillas, tarjetasRojas;

    private int contadorPartidosTotal, contadorJugados, contadorTitular, contadorSuplente, contadorMarcados,
            contadorPropia, contadorAmarillas, contadorRojas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jugador);

        jugador = (Jugador) getIntent().getSerializableExtra("jugador");
        partidos = (ArrayList<Partido>) getIntent().getSerializableExtra("partidos");
        setTitle(getResources().getString(R.string.JUGADOR));
        //boton flecha atras
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fotoPersona = findViewById(R.id.imagen_persona);
        iconoPersona = findViewById(R.id.icono_persona);
        dorsalPersona = findViewById(R.id.dorsal_persona);
        nombrePersona = findViewById(R.id.textview_nombre_persona);
        rolPersona = findViewById(R.id.textview_rol_persona);
        nacionalidadPersona = findViewById(R.id.textview_nacionalidad_persona);
        edadPersona = findViewById(R.id.textview_edad_persona);
        partidosTotal = findViewById(R.id.textView_partidosTotal);
        partidosJugados = findViewById(R.id.textView_partidosJugados);
        partidosTitular = findViewById(R.id.textView_partidosTitular);
        partidosSuplente = findViewById(R.id.textView_partidosSuplente);
        partidosSinJugar = findViewById(R.id.textView_partidosSinJugar);
        golesMarcados = findViewById(R.id.textView_golesMarcados);
        golesPropia = findViewById(R.id.textView_golesPropia);
        tarjetasTotal = findViewById(R.id.textView_tarjetasTotal);
        tarjetasAmarillas = findViewById(R.id.textView_tarjetasAmarillas);
        tarjetasRojas = findViewById(R.id.textView_tarjetasRojas);

        try {
            rellenarInfo();
        } catch (Exception e) {
            finalizarActividad();
            //recargarDatos();
        }
    }

    private void rellenarInfo(){
        try {
            Glide
                    .with(JugadorActivity.this)
                    .load(Uri.parse(jugador.getImagen()))
                    .into(fotoPersona);
        }catch (Exception e){
            Log.d("JUGADOR ACTIVITY", "pintarDatos: ERROR AL PINTAR IMAGEN");
        }

        nombrePersona.setText(jugador.getNombreCompleto());
        rolPersona.setText(jugador.getPosicion());
        nacionalidadPersona.setText(jugador.getNacionalidad());
        String edadCompleta = jugador.getEdad() + " " + getResources().getString(R.string.anos);
        edadPersona.setText(edadCompleta);
        dorsalPersona.setText(jugador.getDorsal());
        //Capitan
        if(jugador.isCapitan()){
            iconoPersona.setVisibility(View.VISIBLE);
        } else if(jugador.getPosicion().equals(JUGADOR_PORTERO)){
            iconoPersona.setImageResource(R.drawable.ic_guantes);
            iconoPersona.setColorFilter(getResources().getColor(R.color.icons));
            iconoPersona.setVisibility(View.VISIBLE);
        }

        rellenarEstadisticas();
    }

    private void rellenarEstadisticas(){
        for(Partido partido : partidos){
            if(partido.getEstadoPartido().equals(PARTIDO_FINALIZADO)){
                //Partidos
                contadorPartidosTotal++;
                if(jugador.getEquipo().getNombre().equals(partido.getEquipoLocal().getNombre())){
                    for(Jugador j : partido.getEquipoLocal().getTitulares()){
                        if(j.getUid().equals(jugador.getUid())){
                            contadorJugados++;
                            contadorTitular++;
                            break;
                        }
                    }
                    for(Jugador j : partido.getEquipoLocal().getSuplentes()){
                        if(j.getUid().equals(jugador.getUid())){
                            contadorJugados++;
                            contadorSuplente++;
                            break;
                        }
                    }
                } else if (jugador.getEquipo().getNombre().equals(partido.getEquipoVisitante().getNombre())){
                    for(Jugador j : partido.getEquipoVisitante().getTitulares()){
                        if(j.getUid().equals(jugador.getUid())){
                            contadorJugados++;
                            contadorTitular++;
                            break;
                        }
                    }
                    for(Jugador j : partido.getEquipoVisitante().getSuplentes()){
                        if(j.getUid().equals(jugador.getUid())){
                            contadorJugados++;
                            contadorSuplente++;
                            break;
                        }
                    }
                }

                //Eventos
                for(Evento evento : partido.getEventos()){
                    for(Usuario u : evento.getAutores()){
                        if(u.getUid().equals(jugador.getUid())){
                            switch (evento.getAccion()){
                                case EVENTO_GOL:
                                    contadorMarcados++;
                                    break;
                                case EVENTO_GOL_PROPIA:
                                    contadorPropia++;
                                    break;
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
        partidosTitular.setText(String.valueOf(contadorTitular));
        partidosSuplente.setText(String.valueOf(contadorSuplente));
        partidosSinJugar.setText(String.valueOf(contadorPartidosTotal-contadorJugados));

        golesMarcados.setText(String.valueOf(contadorMarcados));
        golesPropia.setText(String.valueOf(contadorPropia));

        tarjetasTotal.setText(String.valueOf(contadorAmarillas+contadorRojas));
        tarjetasAmarillas.setText(String.valueOf(contadorAmarillas));
        tarjetasRojas.setText(String.valueOf(contadorRojas));
    }

    private void finalizarActividad() {
        Intent returnIntent = new Intent();
        setResult(1, returnIntent);
        finish();
    }

    private void recargarDatos() {
        final Jugador j = new Jugador(jugador.getUid());
        jugador.getEquipo().obtenerPartidos(TEMPORADA_ACTUAL);
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.dialogDatos));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                partidos = jugador.getEquipo().getPartidos();
                jugador = j;
                rellenarInfo();
                progressDialog.dismiss();
            }
        }, 5000);

    }

    //Boton flecha atras
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
