package com.example.arbitrapp.partido;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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
import com.example.arbitrapp.equipo.EquipoActivity;
import com.example.arbitrapp.modelos.ComparadorDorsales;
import com.example.arbitrapp.modelos.Equipo;
import com.example.arbitrapp.modelos.Jugador;
import com.example.arbitrapp.modelos.Tecnico;

import java.util.Collections;

import static com.example.arbitrapp.FirebaseData.TEMPORADA_ACTUAL;

public class PartidoEquipoFragment extends Fragment {

    private Equipo equipo;
    private RelativeLayout relativeLayout;
    private ImageView imageView;
    private TextView nombreEquipo;
    private TextView numeroTecnicos, numeroJugadores;
    private TableLayout tablaTecnicos, tablaTitulares, tablaSuplentes;

    public PartidoEquipoFragment(Equipo equipo){
        this.equipo = equipo;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_partido_equipo, container, false);

        relativeLayout = view.findViewById(R.id.layout_partido_equipo);
        imageView = view.findViewById(R.id.imagenEscudo);
        nombreEquipo = view.findViewById(R.id.nombreEquipo);
        numeroTecnicos = view.findViewById(R.id.textview_numero_tecnicos);
        numeroJugadores = view.findViewById(R.id.textview_numero_jugadores);
        tablaTecnicos = view.findViewById(R.id.tablaTecnicos);
        tablaTitulares = view.findViewById(R.id.tablaTitulares);
        tablaSuplentes = view.findViewById(R.id.tablaSuplentes);

        rellenarInfoEquipo(view);

        return view;
    }

    private void rellenarInfoEquipo(final View view){

        nombreEquipo.setText(equipo.getNombre());
        try {
            Glide
                    .with(view)
                    .load(Uri.parse(equipo.getEscudo()))
                    .into(imageView);
        }catch (Exception e){
            Log.d("PARTIDO LOCAL", "pintarDatos: ERROR AL PINTAR IMAGEN");
        }

        //Ir al equipo concreto
        imageView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                irAEquipo();
            }
        });

        ComparadorDorsales comparadorDorsales = new ComparadorDorsales();
        int contadorTecnicos = 0, contadorTitulares = 0, contadorSuplentes = 0;
        for(Tecnico t : equipo.getTecnicosPartido()){
            contadorTecnicos++;
            LayoutInflater inflater = LayoutInflater.from(getContext());
            TableRow row = (TableRow) inflater.inflate(R.layout.fila_persona_partido, relativeLayout, false);
            TextView casilla = row.findViewById(R.id.casilla);
            casilla.setText(String.valueOf(contadorTecnicos));
            TextView nombre = row.findViewById(R.id.nombrePersona);
            nombre.setText(t.getNombreCompleto());
            TextView dorsal = row.findViewById(R.id.dorsalPersona);
            dorsal.setText(t.getCargo().substring(0,3));
            tablaTecnicos.addView(row);
        }
        //Ordenar jugadores titulares por dorsal
        Collections.sort(equipo.getTitulares(),comparadorDorsales);
        for(Jugador j : equipo.getTitulares()){
            contadorTitulares++;
            LayoutInflater inflater = LayoutInflater.from(getContext());
            TableRow row = (TableRow) inflater.inflate(R.layout.fila_persona_partido, relativeLayout, false);
            TextView casilla = row.findViewById(R.id.casilla);
            casilla.setText(String.valueOf(contadorTitulares));
            TextView nombre = row.findViewById(R.id.nombrePersona);
            nombre.setText(j.getNombreCompleto());
            TextView dorsal = row.findViewById(R.id.dorsalPersona);
            dorsal.setText(j.getDorsal());
            tablaTitulares.addView(row);
        }
        //Ordenar jugadores suplentes por dorsal
        Collections.sort(equipo.getSuplentes(),comparadorDorsales);
        for(Jugador j : equipo.getSuplentes()){
            contadorSuplentes++;
            LayoutInflater inflater = LayoutInflater.from(getContext());
            TableRow row = (TableRow) inflater.inflate(R.layout.fila_persona_partido, relativeLayout, false);
            TextView casilla = row.findViewById(R.id.casilla);
            casilla.setText(String.valueOf(contadorSuplentes));
            TextView nombre = row.findViewById(R.id.nombrePersona);
            nombre.setText(j.getNombreCompleto());
            TextView dorsal = row.findViewById(R.id.dorsalPersona);
            dorsal.setText(j.getDorsal());
            tablaSuplentes.addView(row);
        }
        String textTecnicos = "Técnicos: " + String.valueOf(contadorTecnicos);
        numeroTecnicos.setText(textTecnicos);
        String textJugadores = "Jugadores: " + String.valueOf(contadorTitulares+contadorSuplentes);
        numeroJugadores.setText(textJugadores);
    }

    private void irAEquipo() {
        if (equipo.getPartidos().isEmpty() || equipo.getJugadores().isEmpty()) {
            equipo.obtenerPartidos(TEMPORADA_ACTUAL);
            equipo.obtenerPlantilla();

            //ESPERAR POR LOS DATOS
            final ProgressDialog tempDialog;
            int i = 0;

            //Diseñar CUADRO DE DIALOGO MIENTRAS CARGA
            tempDialog = new ProgressDialog(getContext());
            tempDialog.setMessage("Recuperando Equipo...");
            tempDialog.setCancelable(false);
            tempDialog.setProgress(i);
            tempDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

            tempDialog.show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    tempDialog.dismiss();
                    startActivity(new Intent(getContext(), EquipoActivity.class).putExtra("equipo", equipo));
                }
            }, 3000);
        } else {
            startActivity(new Intent(getContext(), EquipoActivity.class).putExtra("equipo", equipo));
        }

    }
}
