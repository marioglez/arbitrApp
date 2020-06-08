package com.example.arbitrapp.arbitrar;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import static com.example.arbitrapp.FirebaseData.*;

import com.bumptech.glide.Glide;
import com.example.arbitrapp.dialogs.DialogGol;
import com.example.arbitrapp.R;
import com.example.arbitrapp.dialogs.DialogEvento;
import com.example.arbitrapp.dialogs.DialogSustitucion;
import com.example.arbitrapp.modelos.Evento;
import com.example.arbitrapp.modelos.Jugador;
import com.example.arbitrapp.modelos.Tecnico;
import java.util.ArrayList;

public class PopUpArbitrarActivity extends AppCompatActivity implements DialogGol.DialogGolListener, DialogEvento.DialogEventoListener, DialogSustitucion.DialogSustitucionListener {

    private Tecnico tecnico;
    private Jugador jugador;
    private ArrayList<Jugador> jugadores;
    private String equipo;
    private int minuto;
    private ArrayList<Evento> eventos;

    private ImageView imagenPersona;
    private TextView nombrePersona, equipoPersona;
    private ImageView tarjetaAmarilla, tarjetaRoja, dobleAmarilla;
    private RelativeLayout btnFinalizar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int)(width*.8),(int)(height*.7));

        jugadores =new ArrayList<>();
        eventos = new ArrayList<>();

        tecnico = (Tecnico) getIntent().getSerializableExtra("tecnico");
        jugador = (Jugador) getIntent().getSerializableExtra("jugador");
        equipo = getIntent().getStringExtra("equipo");
        String[] tiempo = getIntent().getStringExtra("minuto").split(":");
        minuto = Integer.parseInt(tiempo[0]) + 1;
        jugadores = (ArrayList<Jugador>) getIntent().getSerializableExtra("jugadores");
        if (tecnico!=null){
            setContentView(R.layout.activity_pop_up_arbitrar_tecnico);
            rellenarDatosTecnico();
        } else if (jugador!=null){
            setContentView(R.layout.activity_pop_up_arbitrar_jugador);
            rellenarDatosJugador();
        }

        btnFinalizar = findViewById(R.id.arbitrar_salir);
        btnFinalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishPopUp();
            }
        });
    }

    private void rellenarDatosTecnico() {
        imagenPersona = findViewById(R.id.imagen_persona);
        nombrePersona = findViewById(R.id.textview_arbitrar_nombre);
        equipoPersona = findViewById(R.id.textview_arbitrar_equipo);

        try{
            Glide
                    .with(PopUpArbitrarActivity.this)
                    .load(Uri.parse(tecnico.getImagen()))
                    .into(imagenPersona);
        }catch (Exception e){
            Log.d("TECNICO ARBITRAR", "pintarDatos: ERROR AL PINTAR IMAGEN");
        }
        nombrePersona.setText(tecnico.getNombreCompleto());
        equipoPersona.setText(tecnico.getEquipo().getNombre());

        tarjetaAmarilla = findViewById(R.id.evento_amarilla);
        tarjetaAmarilla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogEvento dialogEvento = new DialogEvento(EVENTO_AMARILLA);
                dialogEvento.show(getSupportFragmentManager(),"EVENTO DIALOG");
            }
        });

        tarjetaRoja = findViewById(R.id.evento_roja);
        tarjetaRoja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogEvento dialogEvento = new DialogEvento(EVENTO_ROJA);
                dialogEvento.show(getSupportFragmentManager(),"EVENTO DIALOG");
            }
        });

        dobleAmarilla = findViewById(R.id.evento_segundaAmarilla);
        dobleAmarilla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogEvento dialogEvento = new DialogEvento(EVENTO_SEGUNDA_AMARILLA);
                dialogEvento.show(getSupportFragmentManager(),"EVENTO DIALOG");
            }
        });
    }

    private void rellenarDatosJugador() {
        imagenPersona = findViewById(R.id.imagen_persona);
        nombrePersona = findViewById(R.id.textview_arbitrar_nombre);
        equipoPersona = findViewById(R.id.textview_arbitrar_equipo);
        TextView dorsal = findViewById(R.id.dorsal_persona);
        ImageView iconoPersona = findViewById(R.id.icono_persona);

        try{
            Glide
                    .with(PopUpArbitrarActivity.this)
                    .load(Uri.parse(jugador.getImagen()))
                    .into(imagenPersona);
        }catch (Exception e){
            Log.d("JUGADOR ARBITRAR", "pintarDatos: ERROR AL PINTAR IMAGEN");
        }
        nombrePersona.setText(jugador.getNombreCompleto());
        equipoPersona.setText(jugador.getEquipo().getNombre());
        dorsal.setText(jugador.getDorsal());
        if(jugador.isCapitan()){
            iconoPersona.setVisibility(View.VISIBLE);
        } else if(jugador.getPosicion().equals("Portero")){
            iconoPersona.setImageResource(R.drawable.ic_guantes);
            iconoPersona.setColorFilter(getResources().getColor(R.color.icons));
            iconoPersona.setVisibility(View.VISIBLE);
        }

        ImageView gol = findViewById(R.id.evento_gol);
        gol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogGol dialogGol = new DialogGol();
                dialogGol.show(getSupportFragmentManager(),"GOL DIALOG");
            }
        });

        ImageView sustitucion = findViewById(R.id.evento_sustitucion);
        sustitucion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogSustitucion dialogSustitucion = new DialogSustitucion(jugadores);
                dialogSustitucion.show(getSupportFragmentManager(),"SUSTITUCION DIALOG");
            }
        });

        ImageView lesion = findViewById(R.id.evento_lesion);
        lesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogEvento dialogEvento = new DialogEvento(EVENTO_LESION);
                dialogEvento.show(getSupportFragmentManager(),"EVENTO DIALOG");
            }
        });

        tarjetaAmarilla = findViewById(R.id.evento_amarilla);
        tarjetaAmarilla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogEvento dialogEvento = new DialogEvento(EVENTO_AMARILLA);
                dialogEvento.show(getSupportFragmentManager(),"EVENTO DIALOG");
            }
        });

        tarjetaRoja = findViewById(R.id.evento_roja);
        tarjetaRoja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogEvento dialogEvento = new DialogEvento(EVENTO_ROJA);
                dialogEvento.show(getSupportFragmentManager(),"EVENTO DIALOG");
            }
        });

        dobleAmarilla = findViewById(R.id.evento_segundaAmarilla);
        dobleAmarilla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogEvento dialogEvento = new DialogEvento(EVENTO_SEGUNDA_AMARILLA);
                dialogEvento.show(getSupportFragmentManager(),"EVENTO DIALOG");
            }
        });
    }

    //RESPUESTAS DE DIALOGS
    @Override
    public void applyGol(int resultado) {
        Log.d("RESULTADO GOL", "applyGol: " + resultado);
        String[] autores = {jugador.getUid()};
        if (resultado == 1) {
            eventos.add(new Evento(String.valueOf(minuto),EVENTO_GOL,autores,equipo));
        } else {
            eventos.add(new Evento(String.valueOf(minuto),EVENTO_GOL_PROPIA,autores,equipo));
        }
    }

    @Override
    public void applyEvento(String resultado) {
        Log.d("RESULTADO TARJETA", "applyTarjeta: " + resultado);
        String[] autores = new String[1];
        if (tecnico!=null){
            autores[0] = tecnico.getUid();
        } else {
            autores[0] = jugador.getUid();
        }
        Log.d("AUTORES EVENTO", "applySustitucion: " + autores[0]);
        switch (resultado) {
            case EVENTO_AMARILLA:
                eventos.add(new Evento(String.valueOf(minuto),EVENTO_AMARILLA,autores,equipo));
                break;
            case EVENTO_ROJA:
                eventos.add(new Evento(String.valueOf(minuto),EVENTO_ROJA,autores,equipo));
                break;
            case EVENTO_SEGUNDA_AMARILLA:
                eventos.add(new Evento(String.valueOf(minuto),EVENTO_SEGUNDA_AMARILLA,autores,equipo));
                break;
            case EVENTO_LESION:
                eventos.add(new Evento(String.valueOf(minuto),EVENTO_LESION,autores,equipo));
                break;
        }
    }

    @Override
    public void applySustitucion(String resultado) {
        Log.d("RESULTADO SUSTITUCION", "applySustitucion: " + resultado);
        String[] autores = {jugador.getUid(), resultado};
        eventos.add(new Evento(String.valueOf(minuto),EVENTO_SUSTITUCION,autores,equipo));
    }

    //FINALIZAR POPUP Y ENVIAR DATOS A ARBITRAR ACTIVITY
    private void finishPopUp(){
        if (!eventos.isEmpty()) {
            //ESPERAR POR LOS EVENTOS
            final ProgressDialog tempDialog;
            int i = 0;
            //Diseñar CUADRO DE DIALOGO MIENTRAS CARGA
            tempDialog = new ProgressDialog(PopUpArbitrarActivity.this);
            tempDialog.setMessage("Aplicando eventos...");
            tempDialog.setCancelable(false);
            tempDialog.setProgress(i);
            tempDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

            tempDialog.show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    tempDialog.dismiss();
                    Intent intent = new Intent();
                    intent.putExtra("eventos",eventos);
                    setResult(0,intent);
                    finish();
                }
            }, 1000);
        } else {
            finish();
        }

    }

    @Override
    public void onBackPressed() {
    }
}
