package com.example.arbitrapp.modelos;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import static com.example.arbitrapp.FirebaseData.*;
import java.io.Serializable;
import java.util.ArrayList;

public class Equipo implements Serializable {

    private String nombre;
    private String siglas;
    private String ano;
    private String ciudad;
    private Campo campo;
    private String sede;
    private String categoria;
    private String escudo;
    private Equipacion equipacion;
    private ArrayList<Tecnico> tecnicos = new ArrayList<>();
    private ArrayList<Jugador> jugadores = new ArrayList<>();
    private ArrayList<Tecnico> tecnicosPartido = new ArrayList<>();
    private ArrayList<Jugador> titulares = new ArrayList<>();
    private ArrayList<Jugador> suplentes = new ArrayList<>();
    private ArrayList<Partido> partidos = new ArrayList<>();
    private int puntos;

    public Equipo(String nombreEquipo){
        obtenerInfoEquipo(nombreEquipo);
    }

    public Equipo(final String nombreEquipo, String temporada, String sede, String categoria, final String diaPartido, final String idPartido) {
        obtenerInfoEquipo(nombreEquipo);
        obtenerInfoEquipoPartido(nombreEquipo,temporada,sede,categoria,diaPartido,idPartido);
    }

    private void obtenerInfoEquipo(final String nombreEquipo){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child(EQUIPOS).child(nombreEquipo).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    nombre = dataSnapshot.getKey();
                    siglas = dataSnapshot.child(EQUIPO_SIGLAS).getValue().toString();
                    ano = dataSnapshot.child(EQUIPO_ANO).getValue().toString();
                    obtenerEscudo(nombreEquipo);
                    ciudad = dataSnapshot.child(EQUIPO_CIUDAD).getValue().toString();
                    campo = new Campo(dataSnapshot.child(EQUIPO_CAMPO).getValue().toString());
                    categoria = dataSnapshot.child(EQUIPO_CATEGORIA).getValue().toString();
                    sede = dataSnapshot.child(EQUIPO_SEDE).getValue().toString();
                    equipacion = obtenerEquipacion(dataSnapshot);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void obtenerInfoEquipoPartido(final String nombreEquipo, String temporada, String sede, String categoria, final String diaPartido, final String idPartido) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child(COMPETICIONES).child(temporada).child(sede).child(categoria).child(PARTIDOS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot jornada : dataSnapshot.getChildren()){
                    for (DataSnapshot dia : jornada.getChildren()){
                        if (dia.getKey().equals(diaPartido)){
                            for (DataSnapshot partido : dia.getChildren()){
                                if (partido.getKey().equals(idPartido)){
                                    if(partido.child(EQUIPO_LOCAL).child(EQUIPO_NOMBRE).getValue().toString().equals(nombreEquipo)){
                                        obtenerPlantillaPartido(partido.child(EQUIPO_LOCAL).child(EQUIPO_MIEMBROS));
                                    } else {
                                        obtenerPlantillaPartido(partido.child(EQUIPO_VISITANTE).child(EQUIPO_MIEMBROS));
                                    }
                                }
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

    public void obtenerPlantilla(){
        if (this.tecnicos.isEmpty() || this.jugadores.isEmpty()){
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            databaseReference.child(EQUIPOS).child(nombre).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        for(DataSnapshot miembro : dataSnapshot.child(EQUIPO_PLANTILLA).getChildren()){
                            switch (miembro.child(EQUIPO_PLANTILLA_TIPO).getValue().toString()){
                                case EQUIPO_PLANTILLA_TIPO_TECNICO:
                                    tecnicos.add(new Tecnico(miembro.getKey()));
                                    break;
                                case EQUIPO_PLANTILLA_TIPO_JUGADOR:
                                    jugadores.add(new Jugador(miembro.getKey()));
                                    break;
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void obtenerPlantillaPartido(DataSnapshot miembros){
        for(DataSnapshot miembro : miembros.getChildren()){
            switch (miembro.child(EQUIPO_PLANTILLA_TIPO).getValue().toString()){
                case EQUIPO_PLANTILLA_TIPO_TECNICO:
                    tecnicosPartido.add(new Tecnico(miembro.getKey()));
                    break;
                case EQUIPO_PLANTILLA_TIPO_JUGADOR_TITULAR:
                    titulares.add(new Jugador(miembro.getKey()));
                    break;
                case EQUIPO_PLANTILLA_TIPO_JUGADOR_SUPLENTE:
                    suplentes.add(new Jugador(miembro.getKey()));
                    break;
            }
        }
    }

    private Equipacion obtenerEquipacion(DataSnapshot equipo){
        String camiseta = equipo.child(EQUIPO_EQUIPACION).child(EQUIPO_EQUIPACION_CAMISETA).getValue().toString();
        String pantalon = equipo.child(EQUIPO_EQUIPACION).child(EQUIPO_EQUIPACION_PANTALON).getValue().toString();
        String medias = equipo.child(EQUIPO_EQUIPACION).child(EQUIPO_EQUIPACION_MEDIAS).getValue().toString();

        return new Equipacion(camiseta,pantalon,medias);
    }

    private void obtenerEscudo(String nombreEquipo){
        try {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(IMAGENES_EQUIPO + nombreEquipo + IMAGENES_PNG);
            //Cagar foto Firebase
            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    escudo = uri.toString();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.d("EQUIPO", "onDataChange: download image FAIL");
                }
            });
        }catch (Exception e){
            Log.d("EQUIPO", "onDataChange: download image FAIL");
        }
    }

    public void obtenerPartidos(final String temporada){
        partidos.clear();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child(COMPETICIONES).child(temporada).child(sede).child(categoria).child(PARTIDOS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for (DataSnapshot jornada : dataSnapshot.getChildren()){
                        for(DataSnapshot diaPartido : jornada.getChildren()){
                            for (DataSnapshot partido : diaPartido.getChildren()){
                                if(partido.child(EQUIPO_LOCAL).child(EQUIPO_NOMBRE).getValue().toString().equals(nombre) ||
                                        partido.child(EQUIPO_VISITANTE).child(EQUIPO_NOMBRE).getValue().toString().equals(nombre)){
                                    partidos.add(new Partido(temporada,sede,categoria,diaPartido.getKey(), partido.getKey()));
                                }
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

    //GETTERS

    public String getNombre() {
        return nombre;
    }

    public String getSiglas() {
        return siglas;
    }

    public String getAno() {
        return ano;
    }

    public String getCiudad() {
        return ciudad;
    }

    public Campo getCampo() {
        return campo;
    }

    public String getSede() {
        return sede;
    }

    public String getCategoria() {
        return categoria;
    }

    public String getEscudo() {
        return escudo;
    }

    public Equipacion getEquipacion() {
        return equipacion;
    }

    public ArrayList<Tecnico> getTecnicos() {
        return tecnicos;
    }

    public ArrayList<Jugador> getJugadores() {
        return jugadores;
    }

    public ArrayList<Tecnico> getTecnicosPartido() {
        return tecnicosPartido;
    }

    public void setTecnicosPartido(ArrayList<Tecnico> tecnicosPartido) {
        this.tecnicosPartido = tecnicosPartido;
    }

    public ArrayList<Jugador> getTitulares() {
        return titulares;
    }

    public void setTitulares(ArrayList<Jugador> titulares) {
        this.titulares = titulares;
    }

    public ArrayList<Jugador> getSuplentes() {
        return suplentes;
    }

    public void setSuplentes(ArrayList<Jugador> suplentes) {
        this.suplentes = suplentes;
    }

    public ArrayList<Partido> getPartidos() {
        return partidos;
    }

    public void setPuntos(int puntos) {
        this.puntos = puntos;
    }

    public int getPuntos() {
        return puntos;
    }
}
