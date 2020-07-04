package com.example.arbitrapp.modelos;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.arbitrapp.R;
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

public class Campo implements Serializable {

    private String nombre;
    private String ciudad;
    private String direccion;
    private String capacidad;
    private String ubicacion;
    private String imagen;

    public Campo(final String nombreCampo) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child(CAMPOS).child(nombreCampo).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    try {
                        nombre = dataSnapshot.getKey();
                        ciudad = dataSnapshot.child(EQUIPO_CIUDAD).getValue().toString();
                        direccion = dataSnapshot.child(CAMPO_DIRECCION).getValue().toString();
                        capacidad = dataSnapshot.child(CAMPO_CAPACIDAD).getValue().toString();
                        ubicacion = dataSnapshot.child(CAMPO_UBICACION).getValue().toString();
                        obtenerImagen(nombreCampo);
                    } catch (Exception e) {
                        Log.d(CAMPOS, "onDataChange: Error al obtener el campo: " + nombre);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void obtenerImagen(String nombreCampo){
        try {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(IMAGENES_CAMPO + nombreCampo + IMAGENES_JPG);
            //Cagar foto Firebase
            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    // Got the download URL
                    imagen = uri.toString();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                    Log.d("CAMPO", "onDataChange: download image FAIL");
                }
            });
        }catch (Exception e){
            Log.d("CAMPO", "onDataChange: download image FAIL");
        }
    }

    public String getNombre() {
        return nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public String getCiudad() {
        return ciudad;
    }

    public String getCapacidad() {
        return capacidad + " " + R.string.espectadores;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public String getImagen() {
        return imagen;
    }
}
