package com.example.arbitrapp.arbitrar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import static com.example.arbitrapp.FirebaseData.*;
import com.bumptech.glide.Glide;
import com.example.arbitrapp.R;
import com.example.arbitrapp.dialogs.DialogFinalizarPartido;
import com.example.arbitrapp.dialogs.DialogIniciarPartido;
import com.example.arbitrapp.dialogs.DialogSegundaPartePartido;
import com.example.arbitrapp.modelos.ComparadorDorsales;
import com.example.arbitrapp.modelos.Equipo;
import com.example.arbitrapp.modelos.Evento;
import com.example.arbitrapp.modelos.Jugador;
import com.example.arbitrapp.modelos.Partido;
import com.example.arbitrapp.modelos.Tecnico;
import com.example.arbitrapp.modelos.Usuario;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;

public class ArbitrarActivity extends AppCompatActivity implements DialogFinalizarPartido.DialogFinalizarPartidoListener,
        DialogIniciarPartido.DialogIniciarPartidoListener, DialogSegundaPartePartido.DialogSegundaPartePartidoListener {

    private Partido partido;
    private ImageView escudoLocal, escudoVisitante;
    private TextView nombreLocal, nombreVisitante;
    private TextView marcadorLocal, marcadorVisitante;
    private Chronometer cronoCorrido, cronoParado;
    private long lastPauseParado, tiempoDescuento;
    private ImageView btnCrono;
    private TableLayout tablaTitularesLocal, tablaSuplentesLocal, tablaTecnicosLocal;
    private TableLayout tablaTitularesVisitante, tablaSuplentesVisitante, tablaTecnicosVisitante;
    private RelativeLayout relativeLayout;
    private FloatingActionButton floatingActionButton;
    private ComparadorDorsales comparadorDorsales;
    private int contadorLocal, contadorVisitante;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arbitrar);

        partido = (Partido) getIntent().getSerializableExtra("partido");

        relativeLayout = findViewById(R.id.layout_arbitrar);
        floatingActionButton = findViewById(R.id.floating_action_button_arbitrar);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalizarPartido();
            }
        });

        escudoLocal = findViewById(R.id.arbitrar_escudo_local);
        escudoVisitante = findViewById(R.id.arbitrar_escudo_visitante);
        nombreLocal = findViewById(R.id.arbitrar_nombre_local);
        nombreVisitante = findViewById(R.id.arbitrar_nombre_visitante);
        marcadorLocal = findViewById(R.id.arbitrar_marcador_local);
        marcadorVisitante = findViewById(R.id.arbitrar_marcador_visitante);
        btnCrono = findViewById(R.id.imageView_crono);
        btnCrono.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cronometro();
            }
        });
        cronoCorrido = findViewById(R.id.crono_corrido);
        cronoParado = findViewById(R.id.crono_parado);
        tablaTitularesLocal = findViewById(R.id.arbitrar_titulares_locales);
        tablaSuplentesLocal = findViewById(R.id.arbitrar_suplentes_locales);
        tablaTecnicosLocal = findViewById(R.id.arbitrar_tecnicos_locales);
        tablaTitularesVisitante = findViewById(R.id.arbitrar_titulares_visitantes);
        tablaSuplentesVisitante = findViewById(R.id.arbitrar_suplentes_visitantes);
        tablaTecnicosVisitante = findViewById(R.id.arbitrar_tecnicos_visitantes);
        comparadorDorsales = new ComparadorDorsales();
        contadorLocal = contadorVisitante = 0;

        cronoCorrido.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                if (cronoCorrido.getText().toString().equals("00:09")) {

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            cronoCorrido.stop();
                            cronoParado.stop();
                            btnCrono.setImageResource(R.drawable.ic_play_circle);
                            lastPauseParado = 0;
                            empezarDescuento(false);
                        }
                    }, 1500);
                } else if (cronoCorrido.getText().toString().equals("00:20")) {
                    cronoCorrido.stop();
                    cronoParado.stop();
                    btnCrono.setImageResource(R.drawable.ic_play_circle);
                    empezarDescuento(true);
                }
            }
        });

        rellenarMarcador();

        rellenarDatosLocal();
        rellenarDatosVisitante();

        DialogIniciarPartido dialogIniciarPartido = new DialogIniciarPartido();
        dialogIniciarPartido.show(getSupportFragmentManager(),"INICIAR DIALOG");
    }

    private  void rellenarMarcador() {
        try {
            Glide
                    .with(ArbitrarActivity.this)
                    .load(Uri.parse(partido.getEquipoLocal().getEscudo()))
                    .into(escudoLocal);
            Glide
                    .with(ArbitrarActivity.this)
                    .load(Uri.parse(partido.getEquipoVisitante().getEscudo()))
                    .into(escudoVisitante);
        }catch (Exception e){
            Log.d("ARBITRAR", "pintarDatos: ERROR AL PINTAR ESCUDO");
        }
        nombreLocal.setText(partido.getEquipoLocal().getNombre());
        nombreVisitante.setText(partido.getEquipoVisitante().getNombre());
    }

    private void rellenarDatosLocal() {
        Equipo equipo = partido.getEquipoLocal();
        marcadorLocal.setText(String.valueOf(contadorLocal));
        rellenarTecnicos(equipo.getTecnicosPartido(), EVENTO_LOCAL, tablaTecnicosLocal);
        rellenarJugadores(equipo.getTitulares(),equipo.getSuplentes(),EVENTO_LOCAL,tablaTitularesLocal);
        rellenarJugadores(equipo.getSuplentes(),equipo.getTitulares(),EVENTO_LOCAL,tablaSuplentesLocal);
    }

    private void rellenarDatosVisitante() {
        Equipo equipo = partido.getEquipoVisitante();
        marcadorVisitante.setText(String.valueOf(contadorVisitante));
        rellenarTecnicos(equipo.getTecnicosPartido(),EVENTO_VISITANTE,tablaTecnicosVisitante);
        rellenarJugadores(equipo.getTitulares(),equipo.getSuplentes(),EVENTO_VISITANTE,tablaTitularesVisitante);
        rellenarJugadores(equipo.getSuplentes(),equipo.getTitulares(),EVENTO_VISITANTE,tablaSuplentesVisitante);
    }

    private void rellenarTecnicos(ArrayList<Tecnico> tecnicos, final String condicionEquipo, TableLayout tableLayout) {
        tableLayout.removeAllViews();
        for(final Tecnico t : tecnicos){
            LayoutInflater inflater = LayoutInflater.from(ArbitrarActivity.this);
            TableRow row = (TableRow) inflater.inflate(R.layout.fila_tecnico_arbitrar, relativeLayout, false);
            TextView nombre = row.findViewById(R.id.nombrePersona);
            nombre.setText(t.getNombreCompleto());
            RelativeLayout relativeLayout = row.findViewById(R.id.caja_tecnico);

            for (Evento evento : partido.getEventos()) {
                for (Usuario u : evento.getAutores()){
                    if(u.getId().equals(t.getId())) {
                        switch (evento.getAccion()) {
                            case EVENTO_AMARILLA:
                                relativeLayout.setBackgroundColor(getResources().getColor(R.color.amarilloPastel));
                                Log.d("BUSCANDO EVENTO", "rellenarTecnicos: Amarilla EVENTO");
                                break;
                            case EVENTO_ROJA:
                            case EVENTO_SEGUNDA_AMARILLA:
                                relativeLayout.setBackgroundColor(getResources().getColor(R.color.rojoPastel));
                                Log.d("BUSCANDO EVENTO", "rellenarTecnicos: roja EVENTO");
                                break;
                            default:
                                Log.d("BUSCANDO EVENTO", "rellenarTecnicos: NO HAY EVENTO");
                                break;
                        }
                    }
                }
            }

            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivityForResult(new Intent(ArbitrarActivity.this, PopUpArbitrarActivity.class)
                            .putExtra("tecnico",t).putExtra("equipo",condicionEquipo)
                            .putExtra("minuto",cronoCorrido.getText().toString()),0);
                }
            });
            tableLayout.addView(row);
        }
    }

    private void rellenarJugadores(ArrayList<Jugador> jugadores, final ArrayList<Jugador> sustitutos,
                                   final String condicionEquipo, TableLayout tableLayout) {
        tableLayout.removeAllViews();
        Collections.sort(jugadores,comparadorDorsales);
        for(final Jugador j : jugadores){
            LayoutInflater inflater = LayoutInflater.from(ArbitrarActivity.this);
            TableRow row = (TableRow) inflater.inflate(R.layout.fila_jugador_arbitrar, relativeLayout, false);
            TextView dorsal = row.findViewById(R.id.dorsalPersona);
            dorsal.setText(j.getDorsal());

            //Capitan
            if(j.isCapitan()){
                ImageView iconoRol = row.findViewById(R.id.imageview_rol);
                iconoRol.setVisibility(View.VISIBLE);
            } else if(j.getPosicion().equals("Portero")){
                ImageView iconoRol = row.findViewById(R.id.imageview_rol);
                iconoRol.setImageResource(R.drawable.ic_guantes);
                iconoRol.setColorFilter(getResources().getColor(R.color.primary_text));
                iconoRol.setVisibility(View.VISIBLE);
            }

            for (Evento evento : partido.getEventos()) {
                for (Usuario u : evento.getAutores()){
                    if(u.getId().equals(j.getId())) {
                        switch (evento.getAccion()) {
                            case EVENTO_GOL:
                                ImageView iconoGol = row.findViewById(R.id.imageview_golFavor);
                                iconoGol.setVisibility(View.VISIBLE);
                                Log.d("BUSCANDO EVENTO", "rellenarJugadores: GOL EVENTO");
                                break;
                            case EVENTO_GOL_PROPIA:
                                ImageView iconoGolPropia = row.findViewById(R.id.imageview_golPropia);
                                iconoGolPropia.setVisibility(View.VISIBLE);
                                Log.d("BUSCANDO EVENTO", "rellenarJugadores: GOL Propia EVENTO");
                                break;
                            case EVENTO_AMARILLA:
                                ImageView iconoAmarilla = row.findViewById(R.id.imageview_amarilla);
                                iconoAmarilla.setVisibility(View.VISIBLE);
                                Log.d("BUSCANDO EVENTO", "rellenarJugadores: Amarilla EVENTO");
                                break;
                            case EVENTO_ROJA:
                                ImageView iconoRoja = row.findViewById(R.id.imageview_roja);
                                iconoRoja.setVisibility(View.VISIBLE);
                                Log.d("BUSCANDO EVENTO", "rellenarJugadores: Roja EVENTO");
                                break;
                            case EVENTO_SEGUNDA_AMARILLA:
                                ImageView iconoSegundaAmarilla = row.findViewById(R.id.imageview_segundaAmarilla);
                                iconoSegundaAmarilla.setVisibility(View.VISIBLE);
                                Log.d("BUSCANDO EVENTO", "rellenarJugadores: Segunda Amarilla EVENTO");
                                break;
                            case EVENTO_SUSTITUCION:
                                ImageView iconoSustitucion = row.findViewById(R.id.imageview_sustitucion);
                                iconoSustitucion.setVisibility(View.VISIBLE);
                                Log.d("BUSCANDO EVENTO", "rellenarJugadores: Sustitución EVENTO");
                                break;
                            case EVENTO_LESION:
                                ImageView iconoLesion = row.findViewById(R.id.imageview_lesion);
                                iconoLesion.setVisibility(View.VISIBLE);
                                Log.d("BUSCANDO EVENTO", "rellenarTecnicos: Lesión EVENTO");
                                break;
                        }
                    }
                }
            }

            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivityForResult(new Intent(ArbitrarActivity.this, PopUpArbitrarActivity.class)
                                    .putExtra("jugador",j).putExtra("jugadores",sustitutos)
                                    .putExtra("equipo",condicionEquipo).putExtra("minuto", cronoCorrido.getText().toString())
                            ,0);
                }
            });
            tableLayout.addView(row);
        }
    }

    private void actualizarMarcador(Evento evento) {
        if (evento.getAccion().equals(EVENTO_GOL)) {
            if (evento.getEquipo().equals(EVENTO_LOCAL)) {
                contadorLocal++;
            } else {
                contadorVisitante++;
            }
        } else if (evento.getAccion().equals(EVENTO_GOL_PROPIA)) {
            if (evento.getEquipo().equals(EVENTO_LOCAL)) {
                contadorVisitante++;
            } else {
                contadorLocal++;
            }
        }
    }

    private void cronometro () {
        Drawable.ConstantState constantStateDrawableA = getResources().getDrawable(R.drawable.ic_play_circle).getConstantState();

        if(constantStateDrawableA.equals(btnCrono.getDrawable().getConstantState())) {
            btnCrono.setImageResource(R.drawable.ic_pause_circle);
            cronoParado.setBase(SystemClock.elapsedRealtime() - lastPauseParado);
            cronoParado.start();
        } else {
            btnCrono.setImageResource(R.drawable.ic_play_circle);
            cronoParado.stop();
            lastPauseParado = SystemClock.elapsedRealtime() - cronoParado.getBase();
        }
    }

    private void empezarDescuento (final boolean segundaParte) {
        String[] tiempo = cronoParado.getText().toString().split(":");
        int min = Integer.parseInt(tiempo[0])*60*1000;
        int seg = Integer.parseInt(tiempo[1])*1000;
        tiempoDescuento = min + seg;
        new CountDownTimer(tiempoDescuento, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tiempoDescuento = millisUntilFinished;
                actualizarDescuento();
            }

            @Override
            public void onFinish() {
                if (!segundaParte) {
                    DialogSegundaPartePartido dialogSegundaPartePartido = new DialogSegundaPartePartido();
                    dialogSegundaPartePartido.show(getSupportFragmentManager(),"SEGUNDA PARTE DIALOG");
                } else {
                    finalizarPartido();
                }
            }
        }.start();
    }

    private void actualizarDescuento() {
        int minutos = (int) tiempoDescuento / 60000;
        int segundos = (int) tiempoDescuento % 60000 / 1000;

        String tiempoDescuento = "";
        if (minutos < 10 ) {
            tiempoDescuento += "0";
        }
        tiempoDescuento += minutos;
        tiempoDescuento += ":";
        if (segundos < 10 ) {
            tiempoDescuento += "0";
        }
        tiempoDescuento += segundos;

        cronoParado.setText(tiempoDescuento);
    }

    private void finalizarPartido () {
        DialogFinalizarPartido dialogFinalizarPartido = new DialogFinalizarPartido();
        dialogFinalizarPartido.show(getSupportFragmentManager(),"FINALIZAR DIALOG");
    }

    @Override
    public void applyIniciar(boolean iniciar) {
        if (iniciar) {
            cronoCorrido.setBase(SystemClock.elapsedRealtime());
            cronoCorrido.start();
        } else {
            Intent intent = new Intent();
            setResult(RESULT_CANCELED, intent);
            finish();
        }
    }

    @Override
    public void applySegundaParte(boolean iniciar) {
        if (iniciar) {
            cronoCorrido.setBase(SystemClock.elapsedRealtime() - 10*1000);
            cronoCorrido.start();
        }
    }

    @Override
    public void applyFinalizar(boolean finalizar) {
        if (finalizar) {
            //Actualizar datos finales del partido
            partido.setEstadoPartido("Finalizado");
            partido.setGolesLocal(String.valueOf(contadorLocal));
            partido.setGolesVisitante(String.valueOf(contadorVisitante));
            //Eventos ya se han ido añadiendo

            //Subir partido a la BD
            partido.guardarPartido();

            Intent intent = new Intent();
            intent.putExtra("partido",partido);
            setResult(1,intent);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        finalizarPartido();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            ArrayList<Evento> eventos = (ArrayList<Evento>) data.getSerializableExtra("eventos");
            Log.d("DEVUELVE", "onActivityResult: " + eventos);
            for (Evento evento : eventos) {
                partido.getEventos().add(evento);
                actualizarMarcador(evento);
            }
            rellenarDatosLocal();
            rellenarDatosVisitante();
        } catch (Exception e) {
            Log.d("DEVUELVE", "onActivityResult: NO hay nuevos eventos");
        }
    }
}
