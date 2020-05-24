package com.example.arbitrapp.competiciones;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.arbitrapp.R;
import com.example.arbitrapp.modelos.Competicion;
import com.example.arbitrapp.modelos.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import static com.example.arbitrapp.FirebaseData.*;
import java.util.ArrayList;

public class CompeticionesFragment extends Fragment {

    private ArrayList<String> spinnerTemporadaItems = new ArrayList<>();
    private ArrayList<String> spinnerSedeItems = new ArrayList<>();
    private ArrayList<String> spinnerCategoriaItems = new ArrayList<>();

    private RelativeLayout relativeLayout;
    private Spinner spinnerTemporada, spinnerSede, spinnerCategoria;
    private RelativeLayout botonBuscar;
    private TableLayout tablaFavoritos;

    private ProgressDialog tempDialog;
    private int i = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_competiciones, container, false);

        //Diseñar CUADRO DE DIALOGO MIENTRAS CARGA
        tempDialog = new ProgressDialog(getContext());
        tempDialog.setMessage("Recuperando Datos...");
        tempDialog.setCancelable(false);
        tempDialog.setProgress(i);
        tempDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        relativeLayout = view.findViewById(R.id.competicionesLayout);
        spinnerTemporada = view.findViewById(R.id.spinner_temporada);
        spinnerSede = view.findViewById(R.id.spinner_sede);
        spinnerCategoria = view.findViewById(R.id.spinner_categoria);
        botonBuscar = view.findViewById(R.id.boton_competiciones_buscar);
        botonBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                obtenerCompeticion();
            }
        });
        tablaFavoritos = view.findViewById(R.id.tablaFavoritos);

        obtenerTemporadas();
        pintarFavoritos();

        spinnerTemporada.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position>0){
                    obtenerSedes(spinnerTemporada.getSelectedItem().toString());
                } else {
                    botonBuscar.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerSede.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position>0){
                    obtenerCategorias(spinnerTemporada.getSelectedItem().toString(), spinnerSede.getSelectedItem().toString());
                } else {
                    botonBuscar.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinnerCategoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {
                if (position>0) {
                    //Mostrar boton Buscar
                    botonBuscar.setVisibility(View.VISIBLE);
                } else {
                    botonBuscar.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return view;
    }

    private void pintarFavoritos() {
        tablaFavoritos.removeAllViews();
        for (final Competicion competicion : currentUser.getCompeticionesfavoritas()){
            LayoutInflater inflater = LayoutInflater.from(getContext());
            TableRow row = (TableRow) inflater.inflate(R.layout.fila_competicion_favoritos, relativeLayout, false);

            TextView sede = row.findViewById(R.id.favoritos_sede);
            sede.setText(String.valueOf(competicion.getSede()));

            TextView temporada = row.findViewById(R.id.favoritos_temporada);
            temporada.setText(competicion.getTemporada());

            TextView categoria = row.findViewById(R.id.favoritos_categoria);
            categoria.setText(competicion.getCategoria());

            //Ir a la competicion concreta
            row.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    irACompeticion(competicion);
                }
            });

            tablaFavoritos.addView(row);
        }
    }

    private void obtenerTemporadas(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child(COMPETICIONES).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    spinnerTemporadaItems.add("Selecciona temporada");
                    for(DataSnapshot temporada: dataSnapshot.getChildren()){
                        spinnerTemporadaItems.add(temporada.getKey());
                    }
                    ArrayAdapter<String> adapterTemporada = new ArrayAdapter<>(getContext(), R.layout.spinner_item, spinnerTemporadaItems);
                    spinnerTemporada.setAdapter(adapterTemporada);
                } else {
                    Log.d("COMPETICIONES", "onDataChange: NO EXISTEN TEMPORADAS ");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void obtenerSedes(String temporada){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child(COMPETICIONES).child(temporada).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    spinnerSedeItems.clear();
                    spinnerSedeItems.add("selecciona sede");
                    for(DataSnapshot sede: dataSnapshot.getChildren()){
                        spinnerSedeItems.add(sede.getKey());
                    }
                    ArrayAdapter<String> adapterSede = new ArrayAdapter<>(getContext(), R.layout.spinner_item, spinnerSedeItems);
                    spinnerSede.setAdapter(adapterSede);
                } else {
                    Log.d("COMPETICIONES", "onDataChange: NO EXISTEN SEDES ");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void obtenerCategorias(String temporada, String sede){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child(COMPETICIONES).child(temporada).child(sede).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    spinnerCategoriaItems.clear();
                    spinnerCategoriaItems.add("Selecciona categoría");
                    for(DataSnapshot categoria: dataSnapshot.getChildren()){
                        spinnerCategoriaItems.add(categoria.getKey());
                    }
                    ArrayAdapter<String> adapterCategoria = new ArrayAdapter<>(getContext(), R.layout.spinner_item, spinnerCategoriaItems);
                    spinnerCategoria.setAdapter(adapterCategoria);
                } else {
                    Log.d("COMPETICIONES", "onDataChange: NO EXISTEN CATEGORIAS ");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void obtenerCompeticion(){
        String temporada = spinnerTemporada.getSelectedItem().toString();
        String sede = spinnerSede.getSelectedItem().toString();
        String categoria = spinnerCategoria.getSelectedItem().toString();

        //Obtener Competicion
        Competicion competicion = new Competicion(temporada,sede,categoria);
        irACompeticion(competicion);
    }

    public void irACompeticion(final Competicion competicion){
        if (competicion.getJornadas().isEmpty() || competicion.getEquipos().isEmpty()) {
            competicion.obtenerJornadas();
            competicion.obtenerEquipos();
            tempDialog.show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    tempDialog.dismiss();

                    startActivityForResult(new Intent(getContext(), CompeticionActivity.class)
                            .putExtra("competicion", competicion),0);
                }
            }, 4000);
        } else {
            startActivityForResult(new Intent(getContext(), CompeticionActivity.class)
                    .putExtra("competicion", competicion),0);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //obtenerFavoritos();
        pintarFavoritos();
    }
}
