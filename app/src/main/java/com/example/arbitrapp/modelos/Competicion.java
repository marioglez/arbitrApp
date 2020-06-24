package com.example.arbitrapp.modelos;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import static com.example.arbitrapp.FirebaseData.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Competicion implements Serializable {

    private String temporada;
    private String sede;
    private String categoria;
    private ArrayList<Equipo> equipos = new ArrayList<>();
    private ArrayList<Jornada> jornadas = new ArrayList<>();

    public Competicion(String temporada, String sede, String categoria){
        this.temporada = temporada;
        this.sede = sede;
        this.categoria = categoria;
    }

    public void obtenerEquipos(){
        equipos.clear();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child(EQUIPOS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for (DataSnapshot equipo : dataSnapshot.getChildren()){
                        if (equipo.child(EQUIPO_SEDE).getValue().toString().equals(sede) &&
                        equipo.child(EQUIPO_CATEGORIA).getValue().toString().equals(categoria)){
                            equipos.add(new Equipo(equipo.getKey()));
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void obtenerJornadas() {
        jornadas.clear();
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child(COMPETICIONES).child(temporada).child(sede).child(categoria).child(PARTIDOS)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            for (DataSnapshot jornada : dataSnapshot.getChildren()){
                                Jornada j = new Jornada(jornada.getKey());
                                jornadas.add(j);
                                for (DataSnapshot diaPartido : jornada.getChildren()){
                                    j.getFechas().add(obtenerFecha(diaPartido.getKey()));
                                    for (DataSnapshot idPartido : diaPartido.getChildren()){
                                        j.setPartido(new Partido(temporada, sede, categoria, diaPartido.getKey(), idPartido.getKey()));
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private String obtenerFecha(String diaPartido) {
        String year = diaPartido.substring(0,4);
        String dayOfYear = diaPartido.substring(4);
        int ano = Integer.valueOf(year);
        int diaAno = Integer.valueOf(dayOfYear);
        Year y = Year.of(ano);
        LocalDate localDate = y.atDay(diaAno);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return localDate.format(formatter);
    }

    //GETTERS

    public String getTemporada() {
        return temporada;
    }

    public String getSede() {
        return sede;
    }

    public String getCategoria() {
        return categoria;
    }

    public ArrayList<Equipo> getEquipos() {
        return equipos;
    }

    public ArrayList<Jornada> getJornadas() {
        return jornadas;
    }
}
