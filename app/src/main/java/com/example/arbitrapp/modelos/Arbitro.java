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
    private Partido partido;

    private CountDownLatch countDownLatch;

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

    public Arbitro(String uid, CountDownLatch countDownLatch){
        super(uid);
        this.countDownLatch = countDownLatch;
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
                try{
                    countDownLatch.countDown();
                } catch (Exception e) {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void obtenerPartidos(){
        partidos.clear();
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
                                                if (arbitro.child(ID).getValue().toString().equals(Arbitro.super.getUid())){
                                                    partidos.add(new Partido(
                                                            temporada.getKey(),
                                                            sede.getKey(),
                                                            categoria.getKey(),
                                                            diaPartido.getKey(),
                                                            idPartido.getKey()));
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

    public String getCategoria() {
        return categoria;
    }

    public ArrayList<Partido> getPartidos() {
        return partidos;
    }
}
