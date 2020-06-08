package com.example.arbitrapp.modelos;

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

public class Tecnico extends Usuario implements Serializable {

    private Equipo equipoTecnico;
    private String cargo;
    private CountDownLatch countDownLatch;

    public Tecnico(){
        super();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        obtenerTecnico(uid);
    }

    public Tecnico (String uid) {
        super(uid);
        obtenerTecnico(uid);
    }

    public Tecnico(String uid, CountDownLatch countDownLatch){
        super(uid);
        this.countDownLatch = countDownLatch;
        obtenerTecnico(uid);
    }

    private void obtenerTecnico(final String id){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child(USUARIOS).child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    //equipo = dataSnapshot.child("equipo").getValue().toString();
                    cargo = dataSnapshot.child(TECNICO_CARGO).getValue().toString();
                }
                try {
                    countDownLatch.countDown();
                } catch (Exception e){

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //GETTERS

    public String getCargo() {
        return cargo;
    }
}
