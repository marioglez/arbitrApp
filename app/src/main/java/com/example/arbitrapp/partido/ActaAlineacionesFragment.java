package com.example.arbitrapp.partido;

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
import com.example.arbitrapp.modelos.ComparadorDorsales;
import com.example.arbitrapp.modelos.Jugador;
import com.example.arbitrapp.modelos.Partido;
import com.example.arbitrapp.modelos.Tecnico;
import java.util.Collections;

public class ActaAlineacionesFragment extends Fragment {

    private Partido partido;
    private RelativeLayout relativeLayout;
    private TableLayout tablaTitularesLocal, tablaTitularesVisitante, tablaSuplentesLocal, tablaSuplentesVisitante, tablaTecnicosLocal, tablaTecnicosVisitante;

    public ActaAlineacionesFragment(Partido p){
        this.partido = p;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_acta_alineaciones, container, false);

        relativeLayout = view.findViewById(R.id.layout_acta_alineaciones);
        tablaTitularesLocal = view.findViewById(R.id.titulares_locales);
        tablaTitularesVisitante = view.findViewById(R.id.titulares_visitantes);
        tablaSuplentesLocal = view.findViewById(R.id.suplentes_locales);
        tablaSuplentesVisitante = view.findViewById(R.id.suplentes_visitantes);
        tablaTecnicosLocal = view.findViewById(R.id.tecnicos_locales);
        tablaTecnicosVisitante = view.findViewById(R.id.tecnicos_visitantes);

        rellenarInfoEquipoLocal();
        rellenarInfoEquipoVisitante();

        return view;
    }

    private void rellenarInfoEquipoLocal(){
        ComparadorDorsales comparadorDorsales = new ComparadorDorsales();

        for(Tecnico t : partido.getEquipoLocal().getTecnicosPartido()){
            LayoutInflater inflater = LayoutInflater.from(getContext());
            TableRow row = (TableRow) inflater.inflate(R.layout.fila_persona_acta, relativeLayout, false);
            TextView nombre = row.findViewById(R.id.nombrePersona);
            String nombrePersona = t.getNombre() + " " + t.getApellidos();
            nombre.setText(nombrePersona);
            TextView dorsal = row.findViewById(R.id.dorsalPersona);
            dorsal.setText(t.getCargo().substring(0,3));
            tablaTecnicosLocal.addView(row);
        }
        Collections.sort(partido.getEquipoLocal().getTitulares(), comparadorDorsales);
        for(Jugador j : partido.getEquipoLocal().getTitulares()){
            LayoutInflater inflater = LayoutInflater.from(getContext());
            TableRow row = (TableRow) inflater.inflate(R.layout.fila_persona_acta, relativeLayout, false);
            TextView nombre = row.findViewById(R.id.nombrePersona);
            String nombrePersona = j.getNombre() + " " + j.getApellidos();
            nombre.setText(nombrePersona);
            TextView dorsal = row.findViewById(R.id.dorsalPersona);
            dorsal.setText(j.getDorsal());
            tablaTitularesLocal.addView(row);
        }
        Collections.sort(partido.getEquipoLocal().getSuplentes(), comparadorDorsales);
        for(Jugador j : partido.getEquipoLocal().getSuplentes()){
            LayoutInflater inflater = LayoutInflater.from(getContext());
            TableRow row = (TableRow) inflater.inflate(R.layout.fila_persona_acta, relativeLayout, false);
            TextView nombre = row.findViewById(R.id.nombrePersona);
            String nombrePersona = j.getNombre() + " " + j.getApellidos();
            nombre.setText(nombrePersona);
            TextView dorsal = row.findViewById(R.id.dorsalPersona);
            dorsal.setText(j.getDorsal());
            tablaSuplentesLocal.addView(row);
        }
    }

    private void rellenarInfoEquipoVisitante(){
        for(Tecnico t : partido.getEquipoVisitante().getTecnicosPartido()){
            LayoutInflater inflater = LayoutInflater.from(getContext());
            TableRow row = (TableRow) inflater.inflate(R.layout.fila_persona_acta, relativeLayout, false);
            TextView nombre = row.findViewById(R.id.nombrePersona);
            String nombrePersona = t.getNombre() + " " + t.getApellidos();
            nombre.setText(nombrePersona);
            TextView dorsal = row.findViewById(R.id.dorsalPersona);
            dorsal.setText(t.getCargo().substring(0,3));
            tablaTecnicosVisitante.addView(row);
        }
        for(Jugador j : partido.getEquipoVisitante().getTitulares()){
            LayoutInflater inflater = LayoutInflater.from(getContext());
            TableRow row = (TableRow) inflater.inflate(R.layout.fila_persona_acta, relativeLayout, false);
            TextView nombre = row.findViewById(R.id.nombrePersona);
            String nombrePersona = j.getNombre() + " " + j.getApellidos();
            nombre.setText(nombrePersona);
            TextView dorsal = row.findViewById(R.id.dorsalPersona);
            dorsal.setText(j.getDorsal());
            tablaTitularesVisitante.addView(row);
        }
        for(Jugador j : partido.getEquipoVisitante().getSuplentes()){
            LayoutInflater inflater = LayoutInflater.from(getContext());
            TableRow row = (TableRow) inflater.inflate(R.layout.fila_persona_acta, relativeLayout, false);
            TextView nombre = row.findViewById(R.id.nombrePersona);
            String nombrePersona = j.getNombre() + " " + j.getApellidos();
            nombre.setText(nombrePersona);
            TextView dorsal = row.findViewById(R.id.dorsalPersona);
            dorsal.setText(j.getDorsal());
            tablaSuplentesVisitante.addView(row);
        }
    }
}
