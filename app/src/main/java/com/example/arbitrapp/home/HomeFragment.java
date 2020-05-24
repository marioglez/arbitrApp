package com.example.arbitrapp.home;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.arbitrapp.R;
import com.example.arbitrapp.modelos.Partido;
import com.example.arbitrapp.partido.PartidoActivity;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private View view;
    private ArrayList<Partido> proximosPartidos;
    private ArrayList<Partido> partidosDirecto;
    private TableLayout tablaProximoPartido, tablaPartidoDirecto;
    private TextView tituloPartido, competicionPartido, lugarPartido, fechaPartido,
            nombreLocal, nombreVisitante, marcadorLocal, marcadorVisitante, ligaPartido;
    private ImageView escudoLocal, escudoVisitante;
    private RelativeLayout comoLlegar;
    private ImageButton chevronLeft, chevronRight, chevronLeftDirecto, chevronRightDirecto;

    private int posicionCarousel, posicionCarouselDirecto;

    public HomeFragment(ArrayList<Partido> proximosPartidos, ArrayList<Partido> partidosDirecto) {
        this.proximosPartidos = proximosPartidos;
        this.partidosDirecto = partidosDirecto;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);

        tablaPartidoDirecto = view.findViewById(R.id.tabla_directo);
        chevronLeftDirecto = view.findViewById(R.id.chevron_izquierdaDirecto);
        chevronRightDirecto = view.findViewById(R.id.chevron_derechaDirecto);
        escudoLocal = view.findViewById(R.id.escudo_local);
        escudoVisitante = view.findViewById(R.id.escudo_visitante);
        nombreLocal = view.findViewById(R.id.nombre_local);
        nombreVisitante = view.findViewById(R.id.nombre_visitante);
        marcadorLocal = view.findViewById(R.id.marcador_local);
        marcadorVisitante = view.findViewById(R.id.marcador_visitante);
        ligaPartido = view.findViewById(R.id.ligaLocal);

        tablaProximoPartido = view.findViewById(R.id.tabla_proximo_partido);
        tituloPartido = view.findViewById(R.id.titulo_partido);
        competicionPartido = view.findViewById(R.id.competicion_partido);
        lugarPartido = view.findViewById(R.id.lugar_partido);
        fechaPartido = view.findViewById(R.id.fecha_partido);
        comoLlegar = view.findViewById(R.id.comoLlegar_partido);

        pintarPartidosDirecto();
        pintarProximosPartidos();

        return view;
    }

    private void pintarPartidosDirecto() {
        posicionCarouselDirecto = 0;
        if (!partidosDirecto.isEmpty()) {
            rellenarPartidosDirecto();

            chevronLeftDirecto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (posicionCarouselDirecto > 0) {
                        posicionCarouselDirecto--;
                        activarChevron(chevronRightDirecto);
                        if (posicionCarouselDirecto==0){
                            desactivarChevron(chevronLeftDirecto);
                        }
                        rellenarPartidosDirecto();
                    }
                }
            });

            if (partidosDirecto.size() < 2 ){
                desactivarChevron(chevronRightDirecto);
            } else {
                chevronRightDirecto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (posicionCarouselDirecto < partidosDirecto.size()-1) {
                            posicionCarouselDirecto++;
                            activarChevron(chevronLeftDirecto);
                            if (posicionCarouselDirecto==partidosDirecto.size()-1){
                                desactivarChevron(chevronRightDirecto);
                            }
                            rellenarPartidosDirecto();
                        }
                    }
                });
            }

        } else {
            tablaPartidoDirecto.setVisibility(View.GONE);
            chevronLeftDirecto.setVisibility(View.GONE);
            chevronRightDirecto.setVisibility(View.GONE);
            TextView noPartidosDirecto = view.findViewById(R.id.no_partidos_directo);
            noPartidosDirecto.setVisibility(View.VISIBLE);
        }
    }

    private void rellenarPartidosDirecto() {
        try {
            Glide
                    .with(view)
                    .load(Uri.parse(partidosDirecto.get(posicionCarouselDirecto).getEquipoLocal().getEscudo()))
                    .into(escudoLocal);
        }catch (Exception e){
            Log.d("HOME DIRECTO", "pintarDatos: ERROR AL PINTAR ESCUDO");
        }
        try {
            Glide
                    .with(view)
                    .load(Uri.parse(partidosDirecto.get(posicionCarouselDirecto).getEquipoVisitante().getEscudo()))
                    .into(escudoVisitante);
        }catch (Exception e){
            Log.d("HOME DIRECTO", "pintarDatos: ERROR AL PINTAR ESCUDO");
        }
        nombreLocal.setText(partidosDirecto.get(posicionCarouselDirecto).getEquipoLocal().getNombre());
        nombreVisitante.setText(partidosDirecto.get(posicionCarouselDirecto).getEquipoVisitante().getNombre());
        marcadorLocal.setText(partidosDirecto.get(posicionCarouselDirecto).getGolesLocal());
        marcadorVisitante.setText(partidosDirecto.get(posicionCarouselDirecto).getGolesVisitante());
        ligaPartido.setText(partidosDirecto.get(posicionCarouselDirecto).getLiga());

        tablaPartidoDirecto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), PartidoActivity.class).putExtra("partido",partidosDirecto.get(posicionCarouselDirecto)));
            }
        });
    }

    private void pintarProximosPartidos() {
        posicionCarousel = 0;
        if (!proximosPartidos.isEmpty()) {
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
            if (proximosPartidos.size() < 2 ){
                desactivarChevron(chevronRight);
            } else {
                chevronRight.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (posicionCarousel < proximosPartidos.size()-1) {
                            posicionCarousel++;
                            activarChevron(chevronLeft);
                            if (posicionCarousel==proximosPartidos.size()-1){
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
    }

    private void rellenarProximoPartido(final int posicion) {
        String tituloProximoPartido = proximosPartidos.get(posicion).getEquipoLocal().getNombre() + " Vs "
                + proximosPartidos.get(posicion).getEquipoVisitante().getNombre();
        tituloPartido.setText(tituloProximoPartido);
        competicionPartido.setText(proximosPartidos.get(posicion).getLiga());
        lugarPartido.setText(proximosPartidos.get(posicion).getLugar());
        fechaPartido.setText(proximosPartidos.get(posicion).getFecha());

        tablaProximoPartido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), PartidoActivity.class).putExtra("partido",proximosPartidos.get(posicion)));
            }
        });

        comoLlegar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + proximosPartidos.get(posicion).getEquipoLocal().getCampo().getUbicacion());
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
