package com.example.arbitrapp.partido;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.arbitrapp.R;
import com.example.arbitrapp.modelos.ComparadorEventos;
import com.example.arbitrapp.modelos.Evento;
import com.example.arbitrapp.modelos.Partido;
import static com.example.arbitrapp.FirebaseData.*;
import java.util.Collections;

public class ActaPartidoFragment extends Fragment {

    Partido partido;
    RelativeLayout relativeLayout;
    TableLayout tablaEventos;

    public ActaPartidoFragment(Partido p){
        this.partido = p;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_acta_partido, container, false);

        relativeLayout = view.findViewById(R.id.layout_acta_partido);
        tablaEventos = view.findViewById(R.id.tabla_acta_partido);

        obtenerEventos();

        return view;
    }

    private void obtenerEventos(){

        ComparadorEventos comparadorEventos = new ComparadorEventos();
        Collections.sort(partido.getEventos(),comparadorEventos);

        for(Evento e : partido.getEventos()){
            switch (e.getEquipo()){
                case EVENTO_LOCAL:
                    switch (e.getAccion()){
                        case EVENTO_GOL:
                            addEvent(e,R.layout.fila_evento_local_acta, R.drawable.ic_balon);
                            break;
                        case EVENTO_GOL_PROPIA:
                            addEvent(e,R.layout.fila_evento_local_acta, R.drawable.ic_balon_rojo);
                            break;
                        case EVENTO_AMARILLA:
                            addEvent(e,R.layout.fila_evento_local_acta, R.drawable.ic_yellowcard);
                            break;
                        case EVENTO_ROJA:
                            addEvent(e,R.layout.fila_evento_local_acta, R.drawable.ic_redcard);
                            break;
                        case EVENTO_SEGUNDA_AMARILLA:
                            addEvent(e,R.layout.fila_evento_local_acta, R.drawable.ic_tarjetas);
                            break;
                        case EVENTO_SUSTITUCION:
                            addSustitutionEvent(e,R.layout.fila_evento_sustitucion_local_acta);
                            break;
                        case EVENTO_LESION:
                            addEvent(e,R.layout.fila_evento_local_acta, R.drawable.ic_lesion);
                            break;
                    }
                    break;
                case EVENTO_VISITANTE:
                    switch (e.getAccion()){
                        case EVENTO_GOL:
                            addEvent(e,R.layout.fila_evento_visitante_acta, R.drawable.ic_balon);
                            break;
                        case EVENTO_GOL_PROPIA:
                            addEvent(e,R.layout.fila_evento_visitante_acta, R.drawable.ic_balon_rojo);
                            break;
                        case EVENTO_AMARILLA:
                            addEvent(e,R.layout.fila_evento_visitante_acta, R.drawable.ic_yellowcard);
                            break;
                        case EVENTO_ROJA:
                            addEvent(e,R.layout.fila_evento_visitante_acta, R.drawable.ic_redcard);
                            break;
                        case EVENTO_SEGUNDA_AMARILLA:
                            addEvent(e,R.layout.fila_evento_visitante_acta, R.drawable.ic_tarjetas);
                            break;
                        case EVENTO_SUSTITUCION:
                            addSustitutionEvent(e,R.layout.fila_evento_sustitucion_visitante_acta);
                            break;
                        case EVENTO_LESION:
                            addEvent(e,R.layout.fila_evento_visitante_acta, R.drawable.ic_lesion);
                            break;
                    }
                    break;
            }
        }
    }

    private void addEvent(Evento e, int layout, int icon){
        LayoutInflater inflater = LayoutInflater.from(getContext());
        TableRow row = (TableRow) inflater.inflate(layout, relativeLayout, false);
        TextView autor = row.findViewById(R.id.texto_evento);
        autor.setText(e.getAutores().get(0).getNombreCompleto());
        TextView minuto = row.findViewById(R.id.minuto_evento);
        String min = e.getMinuto() + "'";
        minuto.setText(min);
        ImageView icono = row.findViewById(R.id.icono_evento);
        icono.setImageResource(icon);
        tablaEventos.addView(row);
    }

    private void addSustitutionEvent(Evento e, int layout){
        LayoutInflater inflater = LayoutInflater.from(getContext());
        TableRow row = (TableRow) inflater.inflate(layout, relativeLayout, false);
        TextView sustituido = row.findViewById(R.id.textoSustituido);
        String nombreSustituido = e.getAutores().get(0).getNombreCompleto();
        sustituido.setText(nombreSustituido);
        TextView sustituto = row.findViewById(R.id.textoSustituto);
        String nombreSustituto = e.getAutores().get(1).getNombreCompleto();
        sustituto.setText(nombreSustituto);
        TextView minuto = row.findViewById(R.id.minuto_evento);
        String min = e.getMinuto() + "'";
        minuto.setText(min);
        ImageView icono = row.findViewById(R.id.icono_evento);
        icono.setImageResource(R.drawable.ic_sustitucion);
        tablaEventos.addView(row);
    }
}
