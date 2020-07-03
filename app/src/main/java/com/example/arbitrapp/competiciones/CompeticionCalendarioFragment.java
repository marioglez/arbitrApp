package com.example.arbitrapp.competiciones;

import android.content.Intent;
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
import com.example.arbitrapp.modelos.Jornada;
import com.example.arbitrapp.modelos.Partido;
import static com.example.arbitrapp.FirebaseData.*;
import java.util.ArrayList;

public class CompeticionCalendarioFragment extends Fragment {

    private TableLayout tablaJornadas;
    private RelativeLayout relativeLayout;

    private ArrayList<Jornada> jornadas;

    public CompeticionCalendarioFragment(ArrayList<Jornada> jornadas){
        this.jornadas = jornadas;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_competicion_calendario, container, false);

        tablaJornadas = view.findViewById(R.id.tabla_competicion_calendario);
        relativeLayout = view.findViewById(R.id.competicionCalendarioLayout);

        obtenerInfoJornada();

        return view;
    }

    private void obtenerInfoJornada(){
        int partidosJugados,partidosSinJugar,partidosSuspendidos,partidosAplazados;
        for(Jornada jornada : jornadas){
            partidosJugados = partidosSinJugar = partidosSuspendidos = partidosAplazados = 0;
            for (Partido partido : jornada.getPartidos()){
                switch (partido.getEstadoPartido()){
                    case PARTIDO_FINALIZADO:
                        partidosJugados++;
                        break;
                    case PARTIDO_SIN_JUGAR:
                        partidosSinJugar++;
                        break;
                    case PARTIDO_SUSPENDIDO:
                        partidosSuspendidos++;
                        break;
                    case PARTIDO_APLAZADO:
                        partidosAplazados++;
                        break;
                }
            }
            pintarJornada(jornada, partidosJugados,partidosSinJugar,partidosSuspendidos,partidosAplazados);
        }
    }

    private void pintarJornada(final Jornada jornada, int partidosJugados, int partidosSinJugar, int partidosSuspendidos, int partidosAplazados) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            final TableRow row = (TableRow) inflater.inflate(R.layout.tabla_competicion_jornada, relativeLayout, false);

            TextView nombreJornada = row.findViewById(R.id.textview_competicion_jornada);
            nombreJornada.setText(jornada.getNombre());

            TextView fechas = row.findViewById(R.id.textview_competicion_fecha);
            String fechasCompleto = jornada.getFechas().get(0) + " - " + jornada.getFechas().get(jornada.getFechas().size()-1);
            fechas.setText(fechasCompleto);

            TextView partidos = row.findViewById(R.id.textview_competicion_partidos);
            partidos.setText(String.valueOf(jornada.getPartidos().size()));

            TextView pj = row.findViewById(R.id.textview_competicion_partidos_jugados);
            pj.setText(String.valueOf(partidosJugados));

            TextView pnj = row.findViewById(R.id.textview_competicion_partidos_no_jugados);
            pnj.setText(String.valueOf(partidosSinJugar));

            TextView ps = row.findViewById(R.id.textview_competicion_partidos_suspendidos);
            ps.setText(String.valueOf(partidosSuspendidos));

            if (partidosJugados == jornada.getPartidos().size()) {
                ImageView completada = row.findViewById(R.id.imageview_completada);
                completada.setVisibility(View.VISIBLE);
            }

            //Ir a la jornada concreta
            row.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    startActivity(new Intent(getContext(), JornadaActivity.class).putExtra("jornada", jornada));
                }
            });

            tablaJornadas.addView(row);
    }
}
