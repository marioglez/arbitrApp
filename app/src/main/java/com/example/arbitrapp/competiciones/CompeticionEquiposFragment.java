package com.example.arbitrapp.competiciones;

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
import com.example.arbitrapp.modelos.Equipo;

import java.util.ArrayList;

public class CompeticionEquiposFragment extends Fragment {

    private ArrayList<Equipo> equipos;
    private String temporada;
    private RelativeLayout relativeLayout;
    private TableLayout tablaEquipos;

    public CompeticionEquiposFragment(ArrayList<Equipo> equipos, String temporada){
        this.equipos = equipos;
        this.temporada = temporada;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_competicion_equipos, container, false);

        relativeLayout = view.findViewById(R.id.layout_equipos);
        tablaEquipos = view.findViewById(R.id.tablaEquipos);

        obtenerEquipos();

        return view;
    }

    private void obtenerEquipos(){
        for(final Equipo equipo: equipos){
            LayoutInflater inflater = LayoutInflater.from(getContext());
            final TableRow row = (TableRow) inflater.inflate(R.layout.fila_competicion_equipo, relativeLayout, false);

            final ImageView imagen = row.findViewById(R.id.imagen_escudo_equipo);
            try{
                Glide
                        .with(row)
                        .load(Uri.parse(equipo.getEscudo()))
                        .into(imagen);
            }catch (Exception e){
                Log.d("COMPETICION EQUIPOS", "obtenerEquipos: Error al cargar foto equipo");
            }

            TextView nombre = row.findViewById(R.id.nombre_equipo);
            nombre.setText(equipo.getNombre());

            TextView ciudad = row.findViewById(R.id.equipo_ciudad);
            ciudad.setText(equipo.getCiudad());
            TextView fundacion = row.findViewById(R.id.equipo_fundacion);
            fundacion.setText(equipo.getAno());

            //Ir al Equipo concreto
            row.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    equipo.obtenerPlantilla();
                    equipo.obtenerPartidos(temporada);
                    //Diseñar CUADRO DE DIALOGO MIENTRAS CARGA
                    final ProgressDialog tempDialog = new ProgressDialog(getContext());
                    tempDialog.setMessage(getResources().getString(R.string.dialogEquipo));
                    tempDialog.setCancelable(false);
                    tempDialog.show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            tempDialog.dismiss();
                            startActivity(new Intent(getContext(), EquipoActivity.class).putExtra("equipo", equipo));
                        }
                    }, 5000);
                }
            });

            tablaEquipos.addView(row);
        }
    }
}
