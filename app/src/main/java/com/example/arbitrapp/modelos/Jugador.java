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
import java.util.concurrent.CountDownLatch;

public class Jugador extends Usuario implements Serializable {

    private Equipo equipoJugador;
    private boolean capitan;
    private String posicion;
    private String dorsal;

    private CountDownLatch countDownLatch;

    public Jugador(){
        super();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        obtenerJugador(uid);
    }

    public Jugador (String uid) {
        super(uid);
        obtenerJugador(uid);
    }

    public Jugador(String uid, CountDownLatch countDownLatch){
        super(uid);
        this.countDownLatch = countDownLatch;
        Log.d("JUGADOOR", "Jugador: " + uid);
        obtenerJugador(uid);
    }

    private void obtenerJugador(final String id){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child(USUARIOS).child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    try{
                        String c = dataSnapshot.child(JUGADOR_CAPITAN).getValue().toString();
                        capitan = true;
                    }catch (Exception e){
                        capitan=false;
                    }
                    posicion = dataSnapshot.child(JUGADOR_POSICION).getValue().toString();
                    dorsal = dataSnapshot.child(JUGADOR_DORSAL).getValue().toString();
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

    //GETTERS

    public String getPosicion() {
        return posicion;
    }

    public boolean isCapitan() {
        return capitan;
    }

    public String getDorsal() {
        return dorsal;
    }
}
