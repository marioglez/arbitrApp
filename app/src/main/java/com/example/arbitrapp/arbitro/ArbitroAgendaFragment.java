package com.example.arbitrapp.arbitro;

import android.content.Intent;
import android.net.Uri;
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
import com.example.arbitrapp.modelos.Arbitro;
import com.example.arbitrapp.modelos.Partido;
import com.example.arbitrapp.partido.PartidoActivity;
import static com.example.arbitrapp.FirebaseData.*;

public class ArbitroAgendaFragment extends Fragment {

    private Arbitro arbitro;
    private RelativeLayout relativeLayout;
    private TextView sinPartidos;
    private TableLayout tablaPartidos;

    public ArbitroAgendaFragment(Arbitro arbitro){
        this.arbitro = arbitro;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_arbitro_agenda, container, false);

        relativeLayout = view.findViewById(R.id.arbitroAgendaLayout);
        sinPartidos = view.findViewById(R.id.textview_sin_partidos);
        tablaPartidos = view.findViewById(R.id.tabla_arbitro_agenda);

        rellenarAgenda();

        return view;
    }

    private void rellenarAgenda(){
        for(final Partido p : arbitro.getPartidos()){
            if(!p.getEstadoPartido().equals(PARTIDO_FINALIZADO)){
                sinPartidos.setVisibility(View.GONE);
                LayoutInflater inflater = LayoutInflater.from(getContext());
                TableRow row = (TableRow) inflater.inflate(R.layout.tabla_partido, relativeLayout, false);

                TextView tituloCaja =  row.findViewById(R.id.textView_equipos);
                String nombrePartido = p.getEquipoLocal().getNombre() + " Vs " + p.getEquipoVisitante().getNombre();
                tituloCaja.setText(nombrePartido);

                TextView competicion = row.findViewById(R.id.textView_liga);
                competicion.setText(p.getLiga());

                TextView lugar = row.findViewById(R.id.textView_lugar);
                lugar.setText(p.getLugar());

                TextView fecha = row.findViewById(R.id.textView_fecha);
                fecha.setText(p.getFecha());

                TableLayout tableLayout = row.findViewById(R.id.tabla_partido);
                tableLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getContext(), PartidoActivity.class).putExtra("partido", p));
                    }
                });

                RelativeLayout comoLlegar = row.findViewById(R.id.comoLlegar);
                comoLlegar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + p.getEquipoLocal().getCampo().getUbicacion());
                        final Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        startActivity(mapIntent);
                    }
                });

                tablaPartidos.addView(row);
            }
        }
    }
}
