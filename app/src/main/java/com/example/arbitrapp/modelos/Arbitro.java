package com.example.arbitrapp.modelos;

import android.util.Log;

import androidx.annotation.NonNull;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import static com.example.arbitrapp.FirebaseData.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

public class Arbitro extends Usuario implements Serializable {

    private String categoria;
    private ArrayList<Partido> partidos = new ArrayList<>();
    private ArrayList<Integer> valoraciones = new ArrayList<>();

    public Arbitro(){
        super();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        obtenerArbitro(uid);
        obtenerPartidos();
    }

    public Arbitro(String uid) {
        super(uid);
        obtenerArbitro(uid);
    }

    private void obtenerArbitro(String uid){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child(USUARIOS).child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    try{
                        categoria = dataSnapshot.child(ARBITRO_CATEGORIA).getValue().toString();
                    }catch (Exception e){
                        Log.d("ARBITRO", "onDataChange: Error al obtener ARBITRO");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void obtenerPartidos(){
        partidos.clear();
        valoraciones.clear();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child(COMPETICIONES).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot temporada : dataSnapshot.getChildren()){
                        for (DataSnapshot sede : temporada.getChildren()){
                            for (DataSnapshot categoria : sede.getChildren()){
                                for (DataSnapshot jornada : categoria.child(PARTIDOS).getChildren()){
                                    for (DataSnapshot diaPartido : jornada.getChildren()){
                                        for (DataSnapshot idPartido : diaPartido.getChildren()){
                                            for (DataSnapshot arbitro : idPartido.child(PARTIDO_ARBITRAJE).getChildren()){
                                                if (arbitro.getKey().equals(Arbitro.super.getUid())){
                                                    partidos.add(new Partido(
                                                            temporada.getKey(),
                                                            sede.getKey(),
                                                            categoria.getKey(),
                                                            diaPartido.getKey(),
                                                            idPartido.getKey()));
                                                    try {
                                                        valoraciones.add(Integer.valueOf(arbitro.child(ARBITRO_VALORACION).getValue().toString()));
                                                    } catch (Exception e) {
                                                        Log.d("ARBITRO", "onDataChange: NO HAY VALORACION EN ESTE PARTIDO");
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
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //GETTERS


    public ArrayList<Integer> getValoraciones() {
        return valoraciones;
    }

    public String getCategoria() {
        return categoria;
    }

    public ArrayList<Partido> getPartidos() {
        return partidos;
    }
}
