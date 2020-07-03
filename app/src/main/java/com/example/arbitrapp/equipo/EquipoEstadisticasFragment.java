package com.example.arbitrapp.equipo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.arbitrapp.R;
import com.example.arbitrapp.modelos.Equipo;
import com.example.arbitrapp.modelos.Evento;
import com.example.arbitrapp.modelos.Partido;
import static com.example.arbitrapp.FirebaseData.*;

public class EquipoEstadisticasFragment extends Fragment {

    private Equipo equipo;

    private TextView partidosTotal, partidosJugados, partidosPendientes, partidosGanados, partidosEmpatados, partidosPerdidos;
    private TextView golesFavor, golesContra;
    private TextView tarjetasTotal, tarjetasAmarillas, tarjetasRojas;

    private int contadorPartidosTotales, contadorPartidosJugados, contadorGolesFavor, contadorGolesContra,
    contadorVictorias, contadorDerrotas, contadorEmpates, contadorTarjetas, contadorAmarillas, contadorRojas;

    public EquipoEstadisticasFragment(Equipo equipo){
        this.equipo = equipo;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_equipo_estadisticas, container, false);

        partidosTotal = view.findViewById(R.id.textView_partidosTotal);
        partidosJugados = view.findViewById(R.id.textView_partidosJugados);
        partidosPendientes = view.findViewById(R.id.textView_partidosSinJugar);
        partidosGanados = view.findViewById(R.id.textView_partidosVictorias);
        partidosEmpatados = view.findViewById(R.id.textView_partidosEmpates);
        partidosPerdidos = view.findViewById(R.id.textView_partidosDerrotas);
        golesFavor = view.findViewById(R.id.textView_golesFavor);
        golesContra = view.findViewById(R.id.textView_golesContra);
        tarjetasTotal = view.findViewById(R.id.textView_tarjetasTotal);
        tarjetasAmarillas = view.findViewById(R.id.textView_tarjetasAmarillas);
        tarjetasRojas = view.findViewById(R.id.textView_tarjetasRojas);

        rellenarInfo();

        return view;
    }

    private void rellenarInfo(){

        for(Partido partido : equipo.getPartidos()){
            contadorPartidosTotales++;
        if(partido.getEstadoPartido().equals(PARTIDO_FINALIZADO)){
                contadorPartidosJugados++;
                //Comprobar si soy local o visitante en el partido
                if (partido.getEquipoLocal().getNombre().equals(equipo.getNombre())){
                    rellenarInfoLocal(partido);
                } else {
                    rellenarInfoVisitante(partido);
                }
            }
        }

        partidosTotal.setText(String.valueOf(contadorPartidosTotales));
        partidosJugados.setText(String.valueOf(contadorPartidosJugados));
        partidosPendientes.setText(String.valueOf(contadorPartidosTotales-contadorPartidosJugados));
        partidosGanados.setText(String.valueOf(contadorVictorias));
        partidosEmpatados.setText(String.valueOf(contadorEmpates));
        partidosPerdidos.setText(String.valueOf(contadorDerrotas));
        golesFavor.setText(String.valueOf(contadorGolesFavor));
        golesContra.setText(String.valueOf(contadorGolesContra));
        tarjetasTotal.setText(String.valueOf(contadorTarjetas));
        tarjetasAmarillas.setText(String.valueOf(contadorAmarillas));
        tarjetasRojas.setText(String.valueOf(contadorRojas));
    }

    private void rellenarInfoLocal(Partido partido){
        //Resultado
        int golesFavor = Integer.valueOf(partido.getGolesLocal());
        contadorGolesFavor += golesFavor;
        int golesContra = Integer.valueOf(partido.getGolesVisitante());
        contadorGolesContra += golesContra;
        int resultado = (golesFavor-golesContra);
        if(resultado > 0){
            contadorVictorias++;
        } else if (resultado<0){
            contadorDerrotas++;
        } else {
            contadorEmpates++;
        }

        //Eventos
        for(Evento evento : partido.getEventos()){
            if (evento.getEquipo().equals(EVENTO_LOCAL)){
                switch (evento.getAccion()){
                    case EVENTO_AMARILLA:
                    case EVENTO_SEGUNDA_AMARILLA:
                        contadorTarjetas++;
                        contadorAmarillas++;
                        break;
                    case EVENTO_ROJA:
                        contadorTarjetas++;
                        contadorRojas++;
                        break;
                }
            }
        }
    }

    private void rellenarInfoVisitante(Partido partido){
        //Resultado
        int golesContra = Integer.valueOf(partido.getGolesLocal());
        contadorGolesContra += golesContra;
        int golesFavor = Integer.valueOf(partido.getGolesVisitante());
        contadorGolesFavor += golesFavor;

        int resultado = (golesFavor-golesContra);
        if(resultado > 0){
            contadorVictorias++;
        } else if (resultado<0){
            contadorDerrotas++;
        } else {
            contadorEmpates++;
        }

        //Eventos
        for(Evento evento : partido.getEventos()){
            if (evento.getEquipo().equals(EVENTO_VISITANTE)){
                switch (evento.getAccion()){
                    case EVENTO_AMARILLA:
                    case EVENTO_SEGUNDA_AMARILLA:
                        contadorTarjetas++;
                        contadorAmarillas++;
                        break;
                    case EVENTO_ROJA:
                        contadorTarjetas++;
                        contadorRojas++;
                        break;
                }
            }
        }
    }
}
