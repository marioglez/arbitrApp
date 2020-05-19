package com.example.arbitrapp.equipo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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

import com.bumptech.glide.Glide;
import com.example.arbitrapp.R;
import com.example.arbitrapp.modelos.ComparadorDorsales;
import com.example.arbitrapp.modelos.ComparadorPuntos;
import com.example.arbitrapp.modelos.Equipo;
import com.example.arbitrapp.modelos.Jugador;
import com.example.arbitrapp.modelos.Tecnico;

import java.util.Collections;

public class EquipoPlantillaFragment extends Fragment {

    private Equipo equipo;
    private RelativeLayout relativeLayout;
    private TableLayout tablaTecnicos, tablaJugadores;

    public EquipoPlantillaFragment(Equipo equipo){
        this.equipo = equipo;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_equipo_plantilla, container, false);

        relativeLayout = view.findViewById(R.id.equipoPlantillaLayout);
        tablaTecnicos = view.findViewById(R.id.tablaCuerpoTecnico);
        tablaJugadores = view.findViewById(R.id.tablaJugadores);

        rellenarTecnicos();

        //Ordenar jugadores por dorsal
        ComparadorDorsales comparadorDorsales = new ComparadorDorsales();
        Collections.sort(equipo.getJugadores(),comparadorDorsales);
        rellenarJugadores();

        return view;
    }

    private void rellenarTecnicos(){
        for(final Tecnico tecnico : equipo.getTecnicos()){
            LayoutInflater inflater = LayoutInflater.from(getContext());
            final TableRow row = (TableRow) inflater.inflate(R.layout.tabla_miembro_equipo, relativeLayout, false);

            final ImageView imagen = row.findViewById(R.id.imagen_miembro_equipo);
            try {
                Glide
                        .with(row)
                        .load(Uri.parse(tecnico.getImagen()))
                        .into(imagen);
            }catch (Exception e){
                Log.d("EQUIPO PLANTILLA", "pintarDatos: ERROR AL PINTAR IMAGEN");
            }

            TextView nombre = row.findViewById(R.id.nombre_miembro_equipo);
            nombre.setText(tecnico.getNombreCompleto());

            TextView tipo = row.findViewById(R.id.tipo_miembro_equipo);
            tipo.setText(tecnico.getCargo());

            TextView edad = row.findViewById(R.id.edad_miembro_equipo);
            String edadCompleta = tecnico.getEdad() + " años";
            edad.setText(edadCompleta);

            //Ir al Tecnico concreto
            row.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    startActivity(new Intent(getContext(), TecnicoActivity.class)
                            .putExtra("tecnico", tecnico).putExtra("partidos", equipo.getPartidos()));
                }
            });

            tablaTecnicos.addView(row);
        }
    }

    private void rellenarJugadores(){
        for(final Jugador jugador : equipo.getJugadores()){
            LayoutInflater inflater = LayoutInflater.from(getContext());
            final TableRow row = (TableRow) inflater.inflate(R.layout.tabla_jugador_equipo, relativeLayout, false);

            final ImageView imagen = row.findViewById(R.id.imagen_miembro_equipo);
            try {
                Glide
                        .with(row)
                        .load(Uri.parse(jugador.getImagen()))
                        .into(imagen);
            }catch (Exception e){
                Log.d("EQUIPO PLANTILLA", "pintarDatos: ERROR AL PINTAR IMAGEN");
            }

            TextView nombre = row.findViewById(R.id.nombre_miembro_equipo);
            nombre.setText(jugador.getNombreCompleto());

            TextView tipo = row.findViewById(R.id.tipo_miembro_equipo);
            tipo.setText(jugador.getPosicion());

            TextView edad = row.findViewById(R.id.edad_miembro_equipo);
            String edadCompleta = jugador.getEdad() + " años";
            edad.setText(edadCompleta);

            TextView dorsal = row.findViewById(R.id.textView_dorsal);
            dorsal.setText(jugador.getDorsal());

            //Ir al Jugador concreto
            row.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    startActivity(new Intent(getContext(), JugadorActivity.class)
                            .putExtra("jugador", jugador).putExtra("partidos", equipo.getPartidos()));
                }
            });

            tablaJugadores.addView(row);
        }
    }
}
