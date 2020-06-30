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
import com.example.arbitrapp.arbitro.ArbitroActivity;
import com.example.arbitrapp.modelos.Arbitro;
import com.example.arbitrapp.modelos.Partido;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import static com.example.arbitrapp.FirebaseData.*;

public class PartidoInformacionFragment extends Fragment {

    private Partido partido;
    private ArrayList<Arbitro> arbitros;
    private TextView estado, local, visitante, lugar, fecha, hora, resultado, sinArbitros;
    private RelativeLayout relativeLayout;
    private TableLayout tablaArbitros;
    private ImageButton btnEditarArbitros;

    public PartidoInformacionFragment(Partido p){
        this.partido = p;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_partido_informacion, container, false);

        estado = view.findViewById(R.id.textView_estado_partido);
        local = view.findViewById(R.id.textView_local_partido);
        visitante = view.findViewById(R.id.textView_visitante_partido);
        lugar = view.findViewById(R.id.textView_lugar_partido);
        fecha = view.findViewById(R.id.textView_fecha_partido);
        hora = view.findViewById(R.id.textView_hora_partido);
        resultado = view.findViewById(R.id.textView_resultado_partido);
        btnEditarArbitros = view.findViewById(R.id.imageButton_arbitros);
        relativeLayout = view.findViewById(R.id.layout_arbitros);
        tablaArbitros = view.findViewById(R.id.tablaArbitros);

        if (currentUser.getTipoUsuario().equals(USUARIO_ADMIN)) {
            obtenerArbitros();
            btnEditarArbitros.setVisibility(View.VISIBLE);
            btnEditarArbitros.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mostrarArbitros();
                }
            });
        }

        rellenarInfoPartido();

        return view;
    }

    private void rellenarInfoPartido(){
        estado.setText(partido.getEstadoPartido());
        local.setText(partido.getEquipoLocal().getNombre());
        visitante.setText(partido.getEquipoVisitante().getNombre());
        lugar.setText(partido.getLugar());
        fecha.setText(partido.getFecha());
        hora.setText(partido.getHora());
        String resultadoPartido = partido.getGolesLocal() + " - " +partido.getGolesVisitante();
        resultado.setText(resultadoPartido);

        rellenarArbitros();

    }

    private void rellenarArbitros(){
        tablaArbitros.removeAllViews();
        for(final Arbitro arbitro : partido.getArbitros()){
            LayoutInflater inflater = LayoutInflater.from(getContext());
            final TableRow row = (TableRow) inflater.inflate(R.layout.tabla_arbitro, relativeLayout, false);

            ImageView imagenArbitro = row.findViewById(R.id.imagen_arbitro);
            try {
                Glide
                        .with(row)
                        .load(Uri.parse(arbitro.getImagen()))
                        .into(imagenArbitro);
            }catch (Exception e){
                Log.d("PARTIDO INFO", "pintarDatos: ERROR AL PINTAR IMAGEN");
            }

            TextView tipoArbitro = row.findViewById(R.id.tipo_arbitro);
            tipoArbitro.setText(arbitro.getCategoria());

            TextView nombreArbitro = row.findViewById(R.id.nombre_arbitro);
            String nombreArbitroCompleto = arbitro.getNombre() + " " + arbitro.getApellidos();
            nombreArbitro.setText(nombreArbitroCompleto);

            //Ir al Arbitro concreto
            row.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    irAArbitro(arbitro);
                }
            });

            tablaArbitros.addView(row);
        }
        //Añadir Arbitros
        rellenarArbitrosRestantes();
    }

    private void obtenerArbitros() {
        arbitros = new ArrayList<>();
        Query query = FirebaseDatabase.getInstance().getReference(USUARIOS)
                .orderByChild(USUARIO_TIPO).equalTo(ARBITRO);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for (DataSnapshot arbitro : dataSnapshot.getChildren() ){
                        arbitros.add(new Arbitro(arbitro.getKey()));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void rellenarArbitrosRestantes() {
        for (int i = partido.getArbitros().size();i<MAX_ARBITROS;i++) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            final TableRow row = (TableRow) inflater.inflate(R.layout.tabla_arbitro, relativeLayout, false);

            TextView tipoArbitro = row.findViewById(R.id.tipo_arbitro);
            tipoArbitro.setVisibility(View.GONE);

            TextView nombreArbitro = row.findViewById(R.id.nombre_arbitro);
            nombreArbitro.setText(R.string.anadirArbitro);
            nombreArbitro.setTextColor(getResources().getColor(R.color.colorPrimaryDark));

            //Lista de arbitros
            row.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    mostrarArbitros();
                }
            });

            tablaArbitros.addView(row);
        }
    }

    private void mostrarArbitros() {
        startActivityForResult(new Intent(getContext(), PopUpArbitrosActivity.class)
                .putExtra("arbitros",arbitros)
                .putExtra("seleccionados",partido.getArbitros()),0);
    }

    private void irAArbitro(final Arbitro arbitro) {
        if (arbitro.getPartidos().isEmpty()){
            //ESPERAR POR LOS DATOS
            final ProgressDialog tempDialog;
            int i = 0;

            //Diseñar CUADRO DE DIALOGO MIENTRAS CARGA
            tempDialog = new ProgressDialog(getContext());
            tempDialog.setMessage("Recuperando Arbitro...");
            tempDialog.setCancelable(false);
            tempDialog.setProgress(i);
            tempDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

            tempDialog.show();
            arbitro.obtenerPartidos();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    tempDialog.dismiss();
                    startActivity(new Intent(getContext(), ArbitroActivity.class).putExtra("arbitro", arbitro));
                }
            }, 3000);
        } else {
            startActivity(new Intent(getContext(), ArbitroActivity.class).putExtra("arbitro", arbitro));
        }
    }

    private void guardarArbitros() {
        boolean resultado;
        resultado = partido.guardarArbitros();

        if (resultado) {
            Toast.makeText(getContext(),"Árbitros actualizados con éxito",Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getContext(),"Error al actualizar árbitros",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            partido.setArbitros((ArrayList<Arbitro>) data.getSerializableExtra("resultado"));
            rellenarArbitros();
            guardarArbitros();
        } catch (Exception e) {
            Log.w("PARTIDO ARBITROS", "onActivityResult: No se han modificado los árbitros");
        }
    }
}
