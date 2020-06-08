package com.example.arbitrapp.modelos;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import static com.example.arbitrapp.FirebaseData.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class Usuario extends Thread implements Serializable {

    private String uid;
    private String nombre;
    private String apellidos;
    private String edad;
    private String nacionalidad;
    private String tipoUsuario;
    private String email;
    private String movil;
    private String imagen;
    //Esto deberia ir en cada tipo de usuario
    private Equipo equipo;
    private Agenda agenda;
    private ArrayList<Competicion> competicionesfavoritas;

    private CountDownLatch countDownLatch;

    public Usuario(){
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        obtenerUsuario(uid);
        obtenerCompeticionesFavoritas(uid);
    }

    public Usuario(CountDownLatch countDownLatch){
        this.countDownLatch = countDownLatch;
    }

    public Usuario(String uid){
        obtenerUsuario(uid);
    }

    @Override
    public void run() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        obtenerUsuario(uid);
        obtenerCompeticionesFavoritas(uid);
    }

    private void obtenerUsuario(final String id){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child(USUARIOS).child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    try{
                        uid = dataSnapshot.getKey();
                        obtenerImagen(uid);
                        nombre = dataSnapshot.child(USUARIO_NOMBRE).getValue().toString();
                        apellidos = dataSnapshot.child(USUARIO_APELLIDOS).getValue().toString();
                        Log.d("USUARIO", "Usuario: " + id);
                        edad = calcularEdad(dataSnapshot.child(USUARIO_NACIMIENTO).getValue().toString());
                        nacionalidad = dataSnapshot.child(USUARIO_NACIONALIDAD).getValue().toString();
                        tipoUsuario = dataSnapshot.child(USUARIO_TIPO).getValue().toString();
                        movil = dataSnapshot.child(USUARIO_MOVIL).getValue().toString();
                        email = dataSnapshot.child(USUARIO_EMAIL).getValue().toString();
                        //Esto deberia ir en cada tipo de usuario
                        try {
                            equipo = new Equipo(dataSnapshot.child(USUARIO_EQUIPO).getValue().toString());
                        } catch (Exception e) {
                            equipo = null;
                        }
                        agenda = null;
                        competicionesfavoritas = new ArrayList<>();
                    }catch (Exception e){
                        e.printStackTrace();
                        Log.w("USUARIO", "onDataChange: Error al cargar usuario");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private String calcularEdad(String fecha){
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate fechaNac = LocalDate.parse(fecha, fmt);
        LocalDate ahora = LocalDate.now();

        return (String.valueOf(Period.between(fechaNac, ahora).getYears()));
    }

    private void obtenerImagen(String uid){
        try {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(IMAGENES_PERFIL + uid + IMAGENES_JPG);
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
                    Log.d("USUARIO", "onDataChange: download image FAIL");
                }
            });
        }catch (Exception e){
            Log.d("USUARIO", "onDataChange: download image FAIL");
        }
    }

    public void obtenerCompeticionesFavoritas(String uid){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child(USUARIOS).child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    competicionesfavoritas.clear();
                    for(DataSnapshot competicion : dataSnapshot.child(USUARIO_COMPETICIONES_FAVORITAS).getChildren()){
                        competicionesfavoritas.add(new Competicion(
                                competicion.child(COMPETICION_TEMPORADA).getValue().toString(),
                                competicion.child(COMPETICION_SEDE).getValue().toString(),
                                competicion.child(COMPETICION_CATEGORIA).getValue().toString()
                        ));
                    }
                    try{
                        countDownLatch.countDown();
                    } catch (Exception e) {
                        Log.d("USUARIO", "onDataChange: NO HAY COUNTDOWN");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void addCompeticionFavorita(final Competicion competicion){
        Map<String, String> favorita = new HashMap<>();
        favorita.put(COMPETICION_TEMPORADA,competicion.getTemporada());
        favorita.put(COMPETICION_SEDE, competicion.getSede());
        favorita.put(COMPETICION_CATEGORIA, competicion.getCategoria());
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child(USUARIOS).child(uid).child(USUARIO_COMPETICIONES_FAVORITAS)
                .push()
                .setValue(favorita);
    }

    public void eliminarCompeticionFavorita(final Competicion competicion) {
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child(USUARIOS).child(this.uid).child(USUARIO_COMPETICIONES_FAVORITAS);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for (DataSnapshot competicionFavorita : dataSnapshot.getChildren()){
                        if(competicionFavorita.child(COMPETICION_TEMPORADA).getValue().toString().equals(competicion.getTemporada())){
                            if (competicionFavorita.child(COMPETICION_SEDE).getValue().toString().equals(competicion.getSede())){
                                if (competicionFavorita.child(COMPETICION_CATEGORIA).getValue().toString().equals(competicion.getCategoria())){
                                    competicionFavorita.getRef().removeValue();
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


    public String getUid() {
        return uid;
    }

    public String getTipoUsuario() {
        return tipoUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public String getNombreCompleto(){
        return nombre + " " + apellidos;
    }

    public String getEdad() {
        return edad;
    }

    public String getNacionalidad() {
        return nacionalidad;
    }

    public String getMovil() {
        return movil;
    }

    public String getEmail() {
        return email;
    }

    public String getImagen() {
        return imagen;
    }

    public Equipo getEquipo() {
        return equipo;
    }

    public void setAgenda(Agenda agenda) {
        this.agenda = agenda;
    }

    public Agenda getAgenda() {
        return agenda;
    }

    public ArrayList<Competicion> getCompeticionesfavoritas() {
        return competicionesfavoritas;
    }
}
