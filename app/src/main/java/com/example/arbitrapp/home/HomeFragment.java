package com.example.arbitrapp.home;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.arbitrapp.R;
import com.example.arbitrapp.modelos.Partido;
import com.example.arbitrapp.partido.PartidoActivity;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private ArrayList<Partido> partidos;
    private TableLayout tablaProximoPartido;
    private TextView tituloPartido, competicionPartido, lugarPartido, fechaPartido;
    private RelativeLayout comoLlegar;
    private ImageButton chevronLeft, chevronRight;

    private int posicionCarousel;

    public HomeFragment(ArrayList<Partido> partidos) {
        this.partidos = partidos;
        Log.d("HOME FRAGMENT", "HomeFragment: " + partidos + partidos.isEmpty());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_home, container, false);

        posicionCarousel = 0;

        tablaProximoPartido = view.findViewById(R.id.tabla_proximo_partido);
        tituloPartido = view.findViewById(R.id.titulo_partido);
        competicionPartido = view.findViewById(R.id.competicion_partido);
        lugarPartido = view.findViewById(R.id.lugar_partido);
        fechaPartido = view.findViewById(R.id.fecha_partido);
        comoLlegar = view.findViewById(R.id.comoLlegar_partido);

        if (!partidos.isEmpty()) {
            rellenarProximoPartido(posicionCarousel);

            chevronLeft = view.findViewById(R.id.chevron_izquierda);
            chevronLeft.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (posicionCarousel > 0) {
                        posicionCarousel--;
                        activarChevron(chevronRight);
                        if (posicionCarousel==0){
                            desactivarChevron(chevronLeft);
                        }
                        rellenarProximoPartido(posicionCarousel);
                    }
                }
            });

            chevronRight = view.findViewById(R.id.chevron_derecha);
            if (partidos.size() < 2 ){
                desactivarChevron(chevronRight);
            } else {
                chevronRight.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (posicionCarousel < partidos.size()-1) {
                            posicionCarousel++;
                            activarChevron(chevronLeft);
                            if (posicionCarousel==partidos.size()-1){
                                desactivarChevron(chevronRight);
                            }
                            rellenarProximoPartido(posicionCarousel);
                        }
                    }
                });
            }

        } else {
            RelativeLayout cajaPartido = view.findViewById(R.id.caja_partido);
            cajaPartido.setVisibility(View.GONE);
            TextView noProximosPartidos = view.findViewById(R.id.no_proximos_partidos);
            noProximosPartidos.setVisibility(View.VISIBLE);
        }

        return view;
    }

    private void rellenarProximoPartido(final int posicion) {
        String tituloProximoPartido = partidos.get(posicion).getEquipoLocal().getNombre() + " Vs "
                + partidos.get(posicion).getEquipoVisitante().getNombre();
        tituloPartido.setText(tituloProximoPartido);
        competicionPartido.setText(partidos.get(posicion).getLiga());
        lugarPartido.setText(partidos.get(posicion).getLugar());
        fechaPartido.setText(partidos.get(posicion).getFecha());

        tablaProximoPartido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), PartidoActivity.class).putExtra("partido",partidos.get(posicion)));
            }
        });

        comoLlegar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + partidos.get(posicion).getEquipoLocal().getCampo().getUbicacion());
                final Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });
    }

    private void desactivarChevron(ImageButton chevron) {
        chevron.setColorFilter(getResources().getColor(R.color.secondary_text));
    }

    private void activarChevron(ImageButton chevron) {
        chevron.setColorFilter(getResources().getColor(R.color.primary_text));
    }
}
