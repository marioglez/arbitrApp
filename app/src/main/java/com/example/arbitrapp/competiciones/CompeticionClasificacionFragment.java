package com.example.arbitrapp.competiciones;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.arbitrapp.R;
import com.example.arbitrapp.modelos.ComparadorPuntos;
import com.example.arbitrapp.modelos.Competicion;
import com.example.arbitrapp.modelos.Equipo;
import com.example.arbitrapp.modelos.Jornada;
import com.example.arbitrapp.modelos.Partido;
import static com.example.arbitrapp.FirebaseData.*;
import java.util.Collections;

public class CompeticionClasificacionFragment extends Fragment {

    private Competicion competicion;

    private RelativeLayout relativeLayout;
    private TableLayout tablaClasificacion;

    public CompeticionClasificacionFragment(Competicion competicion){
        this.competicion = competicion;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_competicion_clasificacion, container, false);

        relativeLayout = view.findViewById(R.id.layout_competicion_clasificacion);
        tablaClasificacion = view.findViewById(R.id.tablaClasificacion);

        //Obtener los puntos de cada equipo
        for(Equipo equipo : competicion.getEquipos()){
            equipo.setPuntos(obtenerPuntos(equipo));
        }
        //Ordenador equipos por puntos
        ComparadorPuntos comparadorPuntos = new ComparadorPuntos();
        Collections.sort(competicion.getEquipos(),comparadorPuntos);
        //pintar equipos
        rellenarClasificacion();

        return view;
    }

    private int obtenerPuntos(Equipo equipo){
        int puntos = 0;

        for (Jornada jornada : competicion.getJornadas()) {
            for (Partido partido : jornada.getPartidos()) {
                if (partido.getEstadoPartido().equals(PARTIDO_FINALIZADO)){
                    int resultado = Integer.valueOf(partido.getGolesLocal()) - Integer.valueOf(partido.getGolesVisitante());
                    if(partido.getEquipoLocal().getNombre().equals(equipo.getNombre())){
                        if (resultado>0) {
                            puntos+=3;
                        } else if(resultado==0){
                            puntos+=1;
                        }
                    } else if (partido.getEquipoVisitante().getNombre().equals(equipo.getNombre())){
                        if (resultado<0) {
                            puntos+=3;
                        } else if(resultado==0){
                            puntos+=1;
                        }
                    }
                }
            }
        }
        return puntos;
    }

    private void rellenarClasificacion(){
        int contadorEquipos = 0;
        for (Equipo equipo : competicion.getEquipos()){
            contadorEquipos++;
            LayoutInflater inflater = LayoutInflater.from(getContext());
            TableRow row = (TableRow) inflater.inflate(R.layout.fila_equipo_clasificacion, relativeLayout, false);
            //Color de la fila
            if(contadorEquipos%2!=0){
                row.setBackgroundColor(getResources().getColor(R.color.primary_light));
            }
            TextView casilla = row.findViewById(R.id.clasificacion_casilla);
            casilla.setText(String.valueOf(contadorEquipos));
            if (contadorEquipos==1){
                casilla.setBackgroundColor(getResources().getColor(R.color.oro));
            }else if (contadorEquipos==2) {
                casilla.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            } else if (contadorEquipos == competicion.getEquipos().size()) {
                casilla.setBackgroundColor(getResources().getColor(R.color.error));
            }
            TextView nombreEquipo = row.findViewById(R.id.clasificacion_nombreEquipo);
            nombreEquipo.setText(equipo.getNombre());
            TextView puntos = row.findViewById(R.id.clasificacion_puntos);
            puntos.setText(String.valueOf(equipo.getPuntos()));
            tablaClasificacion.addView(row);
        }
    }
}
