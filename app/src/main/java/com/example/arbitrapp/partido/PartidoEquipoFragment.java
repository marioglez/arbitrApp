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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.arbitrapp.R;
import com.example.arbitrapp.equipo.EquipoActivity;
import com.example.arbitrapp.modelos.ComparadorDorsales;
import com.example.arbitrapp.modelos.Equipo;
import com.example.arbitrapp.modelos.Jugador;
import com.example.arbitrapp.modelos.Partido;
import com.example.arbitrapp.modelos.Tecnico;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;

import static com.example.arbitrapp.FirebaseData.EQUIPO_CUERPO_TECNICO;
import static com.example.arbitrapp.FirebaseData.EQUIPO_LOCAL;
import static com.example.arbitrapp.FirebaseData.EQUIPO_SUPLENTES;
import static com.example.arbitrapp.FirebaseData.EQUIPO_TITULARES;
import static com.example.arbitrapp.FirebaseData.EQUIPO_VISITANTE;
import static com.example.arbitrapp.FirebaseData.PARTIDO_FINALIZADO;
import static com.example.arbitrapp.FirebaseData.TECNICO;
import static com.example.arbitrapp.FirebaseData.TEMPORADA_ACTUAL;
import static com.example.arbitrapp.FirebaseData.currentUser;

public class PartidoEquipoFragment extends Fragment {

    private View view;
    private Equipo equipo;
    private Partido partido;
    private RelativeLayout relativeLayout;
    private ImageView imageView;
    private TextView nombreEquipo;
    private TextView numeroTecnicos, numeroJugadores;
    private ImageButton editarTecnicos, editarTitulares, editarSuplentes;
    private TableLayout tablaTecnicos, tablaTitulares, tablaSuplentes;
    private FloatingActionButton botonGuardar;

    public PartidoEquipoFragment(Equipo equipo, Partido partido){
        this.equipo = equipo;
        this.partido = partido;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_partido_equipo, container, false);

        relativeLayout = view.findViewById(R.id.layout_partido_equipo);
        imageView = view.findViewById(R.id.imagenEscudo);
        nombreEquipo = view.findViewById(R.id.nombreEquipo);
        numeroTecnicos = view.findViewById(R.id.textview_numero_tecnicos);
        numeroJugadores = view.findViewById(R.id.textview_numero_jugadores);
        tablaTecnicos = view.findViewById(R.id.tablaTecnicos);
        tablaTitulares = view.findViewById(R.id.tablaTitulares);
        tablaSuplentes = view.findViewById(R.id.tablaSuplentes);
        botonGuardar = view.findViewById(R.id.floating_action_button_guardar_alineacion);

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

        rellenarInfoEquipo(view);

        if (!partido.getEstadoPartido().equals(PARTIDO_FINALIZADO) && currentUser.getTipoUsuario().equals(TECNICO)
                && currentUser.getEquipo().getNombre().equals(equipo.getNombre())) {

            obtenerPlantillaEquipo();

            editarTecnicos = view.findViewById(R.id.imageButton_tecnicos);
            editarTecnicos.setVisibility(View.VISIBLE);
            editarTecnicos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivityForResult(new Intent(getContext(), PopUpEditarAlineacionActivity.class)
                            .putExtra("titulo",EQUIPO_CUERPO_TECNICO)
                            .putExtra("usuarios",equipo.getTecnicos())
                            .putExtra("seleccionados",equipo.getTecnicosPartido()),0);
                }
            });

            editarTitulares = view.findViewById(R.id.imageButton_titulares);
            editarTitulares.setVisibility(View.VISIBLE);
            editarTitulares.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivityForResult(new Intent(getContext(), PopUpEditarAlineacionActivity.class)
                            .putExtra("titulo",EQUIPO_TITULARES)
                            .putExtra("usuarios",equipo.getJugadores())
                            .putExtra("usados",equipo.getSuplentes())
                            .putExtra("seleccionados",equipo.getTitulares()),1);
                }
            });

            editarSuplentes = view.findViewById(R.id.imageButton_suplentes);
            editarSuplentes.setVisibility(View.VISIBLE);
            editarSuplentes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivityForResult(new Intent(getContext(), PopUpEditarAlineacionActivity.class)
                            .putExtra("titulo",EQUIPO_SUPLENTES)
                            .putExtra("usuarios",equipo.getJugadores())
                            .putExtra("usados", equipo.getTitulares())
                            .putExtra("seleccionados",equipo.getSuplentes()),2);
                }
            });
        }


        return view;
    }

    private void rellenarInfoEquipo(final View view){
        limpiarTabla(tablaTecnicos);
        limpiarTabla(tablaTitulares);
        limpiarTabla(tablaSuplentes);

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
            obtenerPlantillaEquipo();

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
            }, 4000);
        } else {
            startActivity(new Intent(getContext(), EquipoActivity.class).putExtra("equipo", equipo));
        }

    }

    private void obtenerPlantillaEquipo() {
        if (equipo.getTecnicos().isEmpty() || equipo.getJugadores().isEmpty()) {
            equipo.obtenerPlantilla();
        }
    }

    private void limpiarTabla(TableLayout tableLayout) {
        while (tableLayout.getChildCount() > 1) {
            tableLayout.removeView(tableLayout.getChildAt(tableLayout.getChildCount() - 1));
        }
    }

    private void guardarAlineacion() {
        boolean resultado;
        if (partido.getEquipoLocal().getNombre().equals(equipo.getNombre())) {
            resultado = partido.guardarAlineacion(equipo, EQUIPO_LOCAL);
        } else {
            resultado = partido.guardarAlineacion(equipo, EQUIPO_VISITANTE);
        }

        if (resultado) {
            Toast.makeText(getContext(),"Alineación actualizada con éxito",Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getContext(),"Error al actualizar alineación",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try{
            switch (resultCode) {
                case 0:
                    equipo.setTecnicosPartido((ArrayList<Tecnico>) data.getSerializableExtra("resultado"));
                    rellenarInfoEquipo(view);
                    break;
                case 1:
                    equipo.setTitulares((ArrayList<Jugador>) data.getSerializableExtra("resultado"));
                    rellenarInfoEquipo(view);
                    break;
                case 2:
                    equipo.setSuplentes((ArrayList<Jugador>) data.getSerializableExtra("resultado"));
                    rellenarInfoEquipo(view);
                    break;
            }
            if (botonGuardar.getVisibility() != View.VISIBLE) {
                botonGuardar.setVisibility(View.VISIBLE);
                botonGuardar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        guardarAlineacion();
                    }
                });
            }
        } catch (Exception e) {
            Log.w("PARTIDO EQUIPO", "onActivityResult: No se ha modificado alineacion");
        }
    }
}
