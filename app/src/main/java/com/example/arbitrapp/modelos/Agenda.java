package com.example.arbitrapp.modelos;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import static com.example.arbitrapp.FirebaseData.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class Agenda implements Serializable {

    private ArrayList<Partido> partidosHoy = new ArrayList<>();
    private ArrayList<Partido> partidosManana = new ArrayList<>();
    private ArrayList<Partido> partidosSemana = new ArrayList<>();

    public Agenda(){
        switch (currentUser.getTipoUsuario()){
            case TECNICO:
            case JUGADOR:
                obtenerPartidosSemana();
                break;
            case ARBITRO:
                obtenerPartidosSemanaArbitro();
                break;
        }
    }

    private void obtenerPartidosSemana(){
        Calendar cal = Calendar.getInstance();
        final int day = cal.get(Calendar.DAY_OF_YEAR);
        //Log.w("DIA DEL AÑO", "obtenerPartidosSemana: " + day );
        final int year = cal.get(Calendar.YEAR);
        final String sede = currentUser.getEquipo().getSede();
        final String categoria = currentUser.getEquipo().getCategoria();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child(COMPETICIONES).child(TEMPORADA_ACTUAL).child(sede).child(categoria)
                .child(PARTIDOS).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    partidosHoy.clear();
                    partidosManana.clear();
                    partidosSemana.clear();
                    for (int diaSemana = 0; diaSemana<=6; diaSemana++) {
                        final int dia = day + diaSemana;
                        final String idPartidos = dayOfYear(year, dia);
                        for (DataSnapshot jornada : dataSnapshot.getChildren()) {
                            for (DataSnapshot diaPartido : jornada.getChildren()) {
                                if (diaPartido.getKey().equals(idPartidos)) {
                                    for (DataSnapshot partido : diaPartido.getChildren()) {
                                        if (currentUser.getEquipo().getNombre().equals(partido.child(EQUIPO_LOCAL).child(EQUIPO_NOMBRE).getValue().toString()) ||
                                                currentUser.getEquipo().getNombre().equals(partido.child(EQUIPO_VISITANTE).child(EQUIPO_NOMBRE).getValue().toString())) {
                                            pertenezcoPartido(new Partido(TEMPORADA_ACTUAL, sede, categoria, idPartidos, partido.getKey()), diaSemana);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void obtenerPartidosSemanaArbitro(){
        Calendar cal = Calendar.getInstance();
        final int day = cal.get(Calendar.DAY_OF_YEAR);
        final int year = cal.get(Calendar.YEAR);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child(COMPETICIONES).child(TEMPORADA_ACTUAL).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    partidosHoy.clear();
                    partidosManana.clear();
                    partidosSemana.clear();
                    for (int diaSemana = 0; diaSemana<=6; diaSemana++) {
                        final int dia = day + diaSemana;
                        final String idPartidos = dayOfYear(year, dia);
                        for (DataSnapshot sede : dataSnapshot.getChildren()) {
                            for (DataSnapshot categoria : sede.getChildren()) {
                                for (DataSnapshot jornada : categoria.child(PARTIDOS).getChildren()) {
                                    for (DataSnapshot diaPartido : jornada.getChildren()) {
                                        if (diaPartido.getKey().equals(idPartidos)) {
                                            for (DataSnapshot partido : diaPartido.getChildren()) {
                                                for (DataSnapshot arbitro : partido.child(PARTIDO_ARBITRAJE).getChildren()) {
                                                    if (currentUser.getUid().equals(arbitro.child(ID).getValue().toString())) {
                                                        pertenezcoPartido(new Partido(TEMPORADA_ACTUAL, sede.getKey(), categoria.getKey(), idPartidos, partido.getKey()), diaSemana);
                                                        Log.d("PERTENEZCO", "onDataChange: pertenezco al partido como arbitro" + idPartidos);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void pertenezcoPartido(Partido partido, int diaSemana){
        switch (diaSemana){
            case 0:
                partidosHoy.add(partido);
                partidosSemana.add(partido);
                break;
            case 1:
                partidosManana.add(partido);
                partidosSemana.add(partido);
                break;
            default:
                partidosSemana.add(partido);
                break;
        }
    }

    private String dayOfYear(int year, int dia){
        //Comprobar si pasa del dia maximo del año
        Calendar cal = new GregorianCalendar(year, 11, 31);
        int totalDias = cal.get(Calendar.DAY_OF_YEAR);

        if(dia>totalDias){
            year += + 1;
            dia -= totalDias;
            return (String.valueOf(year) + String.valueOf(dia));
        } else {
            return (String.valueOf(year) + String.valueOf(dia));
        }
    }

    //GETTTERS

    public ArrayList<Partido> getPartidosHoy() {
        return partidosHoy;
    }

    public ArrayList<Partido> getPartidosManana() {
        return partidosManana;
    }

    public ArrayList<Partido> getPartidosSemana() {
        return partidosSemana;
    }
}
