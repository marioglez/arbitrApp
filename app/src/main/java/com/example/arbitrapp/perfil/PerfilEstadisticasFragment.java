package com.example.arbitrapp.perfil;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import static com.example.arbitrapp.FirebaseData.*;

import com.example.arbitrapp.R;
import com.example.arbitrapp.modelos.Arbitro;
import com.example.arbitrapp.modelos.Evento;
import com.example.arbitrapp.modelos.Jugador;
import com.example.arbitrapp.modelos.Partido;
import com.example.arbitrapp.modelos.Tecnico;
import com.example.arbitrapp.modelos.Usuario;

public class PerfilEstadisticasFragment extends Fragment {

    private Arbitro arbitro;
    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (currentUser.getTipoUsuario().equals(ARBITRO)) {
            arbitro = new Arbitro();
        }

        final ProgressDialog tempDialog;
        int i = 0;

        //Diseñar CUADRO DE DIALOGO MIENTRAS CARGA
        tempDialog = new ProgressDialog(getContext());
        tempDialog.setMessage("Recuperando Estadísticas...");
        tempDialog.setCancelable(false);
        tempDialog.setProgress(i);
        tempDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        switch (currentUser.getTipoUsuario()){
            case ARBITRO:
                view =  inflater.inflate(R.layout.fragment_perfil_estadisticas_arbitro, container, false);
                if (arbitro.getPartidos().isEmpty()) {
                    tempDialog.show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            rellenarEstadisticasArbitro(view);
                            tempDialog.dismiss();
                        }
                    }, 3000);
                } else {
                    rellenarEstadisticasArbitro(view);
                }
                break;
            case JUGADOR:
                view =  inflater.inflate(R.layout.fragment_perfil_estadisticas_jugador, container, false);
                if (currentUser.getEquipo().getPartidos().isEmpty()) {
                    tempDialog.show();
                    currentUser.getEquipo().obtenerPartidos(TEMPORADA_ACTUAL);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            rellenarEstadisticasJugador(view);
                            tempDialog.dismiss();
                        }
                    }, 3000);
                } else {
                    rellenarEstadisticasJugador(view);
                }
                break;
            case TECNICO:
                view =  inflater.inflate(R.layout.fragment_perfil_estadisticas_tecnico, container, false);
                if (currentUser.getEquipo().getPartidos().isEmpty()) {
                    tempDialog.show();
                    currentUser.getEquipo().obtenerPartidos(TEMPORADA_ACTUAL);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            rellenarEstadisticasTecnico(view);
                            tempDialog.dismiss();
                        }
                    }, 3000);
                } else {
                    rellenarEstadisticasTecnico(view);
                }
                break;
        }

        return view;
    }

    private void rellenarEstadisticasArbitro(View view) {
        int contadorPartidosArbitrados = 0, contadorValoracionesRecibidas = 0;
        RatingBar ratingBar = view.findViewById(R.id.ratingBar);
        TextView partidosArbitrados = view.findViewById(R.id.textView_arbitro_partidos_arbitrados);
        TextView valoracionesRecibidas = view.findViewById(R.id.textView_arbitro_valoraciones_recibidas);

        for ( Partido partido : arbitro.getPartidos()){
            if(partido.getEstadoPartido().equals(PARTIDO_FINALIZADO)){
                contadorPartidosArbitrados++;
            }
        }

        ratingBar.setRating(3);
        partidosArbitrados.setText(String.valueOf(contadorPartidosArbitrados));
        valoracionesRecibidas.setText(String.valueOf(0));
    }

    private void rellenarEstadisticasJugador(View view) {
        int contadorPartidosTotal = 0, contadorJugados = 0, contadorTitular = 0, contadorSuplente = 0,
                contadorMarcados = 0, contadorPropia = 0, contadorAmarillas = 0, contadorRojas = 0;

        TextView partidosTotal = view.findViewById(R.id.textView_partidosTotal);
        TextView partidosJugados = view.findViewById(R.id.textView_partidosJugados);
        TextView partidosTitular = view.findViewById(R.id.textView_partidosTitular);
        TextView  partidosSuplente = view.findViewById(R.id.textView_partidosSuplente);
        TextView partidosSinJugar = view.findViewById(R.id.textView_partidosSinJugar);
        TextView golesMarcados = view.findViewById(R.id.textView_golesMarcados);
        TextView golesPropia = view.findViewById(R.id.textView_golesPropia);
        TextView tarjetasTotal = view.findViewById(R.id.textView_tarjetasTotal);
        TextView tarjetasAmarillas = view.findViewById(R.id.textView_tarjetasAmarillas);
        TextView tarjetasRojas = view.findViewById(R.id.textView_tarjetasRojas);

        try {
            for(Partido partido : currentUser.getEquipo().getPartidos()){
                if(partido.getEstadoPartido().equals(PARTIDO_FINALIZADO)){
                    //Partidos
                    contadorPartidosTotal++;
                    if(currentUser.getEquipo().getNombre().equals(partido.getEquipoLocal().getNombre())){
                        for(Jugador j : partido.getEquipoLocal().getTitulares()){
                            if(j.getUid().equals(currentUser.getUid())){
                                contadorJugados++;
                                contadorTitular++;
                                break;
                            }
                        }
                        for(Jugador j : partido.getEquipoLocal().getSuplentes()){
                            if(j.getUid().equals(currentUser.getUid())){
                                contadorJugados++;
                                contadorSuplente++;
                                break;
                            }
                        }
                    } else if (currentUser.getEquipo().getNombre().equals(partido.getEquipoVisitante().getNombre())){
                        for(Jugador j : partido.getEquipoVisitante().getTitulares()){
                            if(j.getUid().equals(currentUser.getUid())){
                                contadorJugados++;
                                contadorTitular++;
                                break;
                            }
                        }
                        for(Jugador j : partido.getEquipoVisitante().getSuplentes()){
                            if(j.getUid().equals(currentUser.getUid())){
                                contadorJugados++;
                                contadorSuplente++;
                                break;
                            }
                        }
                    }

                    //Eventos
                    for(Evento evento : partido.getEventos()){
                        for(Usuario u : evento.getAutores()){
                            if(u.getUid().equals(currentUser.getUid())){
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
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error al obtener las estadisticas",Toast.LENGTH_SHORT).show();
        }
    }

    private void rellenarEstadisticasTecnico(View view) {
        int contadorPartidosTotal = 0, contadorJugados = 0, contadorGanados = 0, contadorPerdidos = 0,
                contadorEmpatados = 0, contadorAmarillas = 0, contadorRojas = 0;

        TextView partidosTotal = view.findViewById(R.id.textView_partidosTotal);
        TextView partidosJugados = view.findViewById(R.id.textView_partidosJugados);
        TextView partidosGanados = view.findViewById(R.id.textView_partidosGanados);
        TextView partidosPerdidos = view.findViewById(R.id.textView_partidosPerdidos);
        TextView partidosEmpatados = view.findViewById(R.id.textView_partidosEmpatados);
        TextView tarjetasTotal = view.findViewById(R.id.textView_tarjetasTotal);
        TextView tarjetasAmarillas = view.findViewById(R.id.textView_tarjetasAmarillas);
        TextView tarjetasRojas = view.findViewById(R.id.textView_tarjetasRojas);

        for(Partido partido : currentUser.getEquipo().getPartidos()){
            if(partido.getEstadoPartido().equals(PARTIDO_FINALIZADO)){
                //Partidos
                contadorPartidosTotal++;
                if(currentUser.getEquipo().getNombre().equals(partido.getEquipoLocal().getNombre())){
                    for(Tecnico t : partido.getEquipoLocal().getTecnicosPartido()){
                        if(t.getUid().equals(currentUser.getUid())){
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
                } else if (currentUser.getEquipo().getNombre().equals(partido.getEquipoVisitante().getNombre())){
                    for(Tecnico t : partido.getEquipoVisitante().getTecnicosPartido()){
                        if(t.getUid().equals(currentUser.getUid())){
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
                        if(u.getUid().equals(currentUser.getUid())){
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
}
