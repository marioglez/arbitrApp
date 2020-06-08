package com.example.arbitrapp.modelos;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import static com.example.arbitrapp.FirebaseData.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class Partido extends Thread implements Serializable {

    private String uid;
    private Equipo equipoLocal, equipoVisitante;
    private String estadoPartido;
    private String temporada,sede,categoria,diaPartido,idPartido,jornadaPartido;
    private String fecha, hora, lugar;
    private String golesLocal, golesVisitante;
    private String liga;
    private ArrayList<Arbitro> arbitros = new ArrayList<>();
    private ArrayList<Evento> eventos = new ArrayList<>();
    private int equiposObtenidos = 0;
    private int arbitrosObtenidos = 0;

    private CountDownLatch countDownLatch,countDownLatchEquipos, countDownLatchArbitros;

    public Partido (CountDownLatch countDownLatch, String temporada, String sede, String categoria, String diaPartido, String idPartido) {
        this.countDownLatch = countDownLatch;
        this.temporada = temporada;
        this.sede = sede;
        this.categoria = categoria;
        this.diaPartido = diaPartido;
        this.idPartido = idPartido;
    }

    @Override
    public void run() {
        countDownLatchEquipos = new CountDownLatch(2);
        countDownLatchArbitros = new CountDownLatch(0);
        obtenerPartido();
        try {
            //ESPERAR A TENER LOS EQUIPOS
            countDownLatchEquipos.await();
            countDownLatchArbitros.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
        countDownLatch.countDown();
    }

    private void obtenerPartido() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child(COMPETICIONES).child(temporada).child(sede).child(categoria).child(PARTIDOS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for (DataSnapshot jornada : dataSnapshot.getChildren()){
                        for (DataSnapshot dia : jornada.getChildren()){
                            if (dia.getKey().equals(diaPartido)){
                                for (DataSnapshot partido : dia.getChildren()){
                                    if (partido.getKey().equals(idPartido)){
                                        jornadaPartido = jornada.getKey();
                                        uid = partido.getKey();
                                        estadoPartido = partido.child(PARTIDO_ESTADO).getValue().toString();
                                        fecha = partido.child(PARTIDO_FECHA).getValue().toString();
                                        hora = partido.child(PARTIDO_HORA).getValue().toString();
                                        lugar = partido.child(PARTIDO_LUGAR).getValue().toString();
                                        golesLocal = partido.child(EQUIPO_LOCAL).child(EQUIPO_GOLES).getValue().toString();
                                        golesVisitante = partido.child(EQUIPO_VISITANTE).child(EQUIPO_GOLES).getValue().toString();
                                        liga = partido.child(PARTIDO_LIGA).getValue().toString();
                                        //Equipos
                                        String nombreLocal = partido.child(EQUIPO_LOCAL).child(EQUIPO_NOMBRE).getValue().toString();
                                        equipoLocal = new Equipo(countDownLatchEquipos,nombreLocal, temporada, sede, categoria, diaPartido, idPartido);
                                        String nombreVisitante = partido.child(EQUIPO_VISITANTE).child(EQUIPO_NOMBRE).getValue().toString();
                                        equipoVisitante = new Equipo(countDownLatchEquipos,nombreVisitante, temporada, sede, categoria, diaPartido, idPartido);
                                        //Arbitros
                                        try {
                                            for (DataSnapshot arbitro : partido.child(PARTIDO_ARBITRAJE).getChildren()){
                                                countDownLatchArbitros = new CountDownLatch((int)countDownLatchArbitros.getCount()+1);
                                                Arbitro a = new Arbitro(arbitro.child(ID).getValue().toString(),countDownLatchArbitros);
                                                arbitros.add(a);
                                            }
                                        } catch (Exception e) {
                                            Log.w("PARTIDO", "onDataChange: NO HAY ARBITROS ASIGNADOS");
                                        }
                                        //Eventos
                                        try {
                                            for(DataSnapshot evento : partido.child(PARTIDO_EVENTOS).getChildren()){
                                                String[] autores = evento.child(EVENTO_AUTOR).getValue().toString().split("-");
                                                eventos.add(
                                                        new Evento(
                                                                evento.child(EVENTO_MINUTO).getValue().toString(),
                                                                evento.child(EVENTO_TIPO).getValue().toString(),
                                                                autores,
                                                                evento.child(EVENTO_EQUIPO).getValue().toString()));
                                            }
                                        }catch (Exception e){
                                            Log.w("PARTIDO", "onDataChange: NO HAY EVENTOS ASIGNADOS");
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

    public Partido(final String temporada, final String sede, final String categoria, final String diaPartido, final String idPartido){
        final Partido p = this;
        this.temporada = temporada;
        this.sede = sede;
        this.categoria = categoria;
        this.diaPartido = diaPartido;
        this.idPartido = idPartido;
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child(COMPETICIONES).child(temporada).child(sede).child(categoria).child(PARTIDOS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for (DataSnapshot jornada : dataSnapshot.getChildren()){
                        for (DataSnapshot dia : jornada.getChildren()){
                            if (dia.getKey().equals(diaPartido)){
                                for (DataSnapshot partido : dia.getChildren()){
                                    if (partido.getKey().equals(idPartido)){
                                        jornadaPartido = jornada.getKey();
                                        uid = partido.getKey();
                                        String nombreLocal = partido.child(EQUIPO_LOCAL).child(EQUIPO_NOMBRE).getValue().toString();
                                        equipoLocal = new Equipo(nombreLocal, temporada, sede, categoria, diaPartido, idPartido);
                                        String nombreVisitante = partido.child(EQUIPO_VISITANTE).child(EQUIPO_NOMBRE).getValue().toString();
                                        equipoVisitante = new Equipo(nombreVisitante, temporada, sede, categoria, diaPartido, idPartido);
                                        estadoPartido = partido.child(PARTIDO_ESTADO).getValue().toString();
                                        fecha = partido.child(PARTIDO_FECHA).getValue().toString();
                                        hora = partido.child(PARTIDO_HORA).getValue().toString();
                                        lugar = partido.child(PARTIDO_LUGAR).getValue().toString();
                                        golesLocal = partido.child(EQUIPO_LOCAL).child(EQUIPO_GOLES).getValue().toString();
                                        golesVisitante = partido.child(EQUIPO_VISITANTE).child(EQUIPO_GOLES).getValue().toString();
                                        liga = partido.child(PARTIDO_LIGA).getValue().toString();
                                        //Arbitros
                                        try {
                                            for (DataSnapshot arbitro : partido.child(PARTIDO_ARBITRAJE).getChildren()){
                                                Arbitro a = new Arbitro(arbitro.child(ID).getValue().toString());
                                                arbitros.add(a);
                                            }
                                        } catch (Exception e) {
                                            Log.w("PARTIDO", "onDataChange: NO HAY ARBITROS ASIGNADOS");
                                        }
                                        //Eventos
                                        try {
                                            for(DataSnapshot evento : partido.child(PARTIDO_EVENTOS).getChildren()){
                                                String[] autores = evento.child(EVENTO_AUTOR).getValue().toString().split("-");
                                                eventos.add(
                                                        new Evento(
                                                                evento.child(EVENTO_MINUTO).getValue().toString(),
                                                                evento.child(EVENTO_TIPO).getValue().toString(),
                                                                autores,
                                                                evento.child(EVENTO_EQUIPO).getValue().toString()));
                                            }
                                        }catch (Exception e){
                                            Log.w("PARTIDO", "onDataChange: NO HAY EVENTOS ASIGNADOS");
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

    public void equipoObtenido() {
        equiposObtenidos++;
        if (equiposObtenidos == 2 && arbitrosObtenidos == arbitros.size()) {
            //PARTIDO HA TERMINADO DE OBTENERSE
            try{
                countDownLatch.countDown();
            } catch (Exception e) {
                Log.w("PARTIDO", "arbitroObtenido: No hay CountDownLatch");
                e.printStackTrace();
            }
        }
    }

    public void arbitroObtenido() {
        arbitrosObtenidos++;
        if (arbitrosObtenidos == arbitros.size() && equiposObtenidos == 2) {
            //PARTIDO HA TERMINADO DE OBTENERSE
            try{
                countDownLatch.countDown();
            } catch (Exception e) {
                Log.w("PARTIDO", "arbitroObtenido: No hay CountDownLatch");
                e.printStackTrace();
            }
        }
    }

    public void iniciarPartido() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child(COMPETICIONES).child(this.temporada).child(this.sede).child(this.categoria)
                .child(PARTIDOS).child(this.jornadaPartido).child(this.diaPartido).child(this.idPartido);

        databaseReference.child(PARTIDO_ESTADO).setValue(PARTIDO_EN_CURSO);
    }

    public void actualizarMarcador(String equipo, int goles) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child(COMPETICIONES).child(this.temporada).child(this.sede).child(this.categoria)
                .child(PARTIDOS).child(this.jornadaPartido).child(this.diaPartido).child(this.idPartido);

        databaseReference.child(equipo).child(EQUIPO_GOLES).setValue(goles);
    }

    public void guardarPartido() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child(COMPETICIONES).child(this.temporada).child(this.sede).child(this.categoria)
                .child(PARTIDOS).child(this.jornadaPartido).child(this.diaPartido).child(this.idPartido);

        databaseReference.child(PARTIDO_ESTADO).setValue(this.estadoPartido);
        databaseReference.child(EQUIPO_LOCAL).child(EQUIPO_GOLES).setValue(this.golesLocal);
        databaseReference.child(EQUIPO_VISITANTE).child(EQUIPO_GOLES).setValue(this.golesVisitante);
        //eventos
        if (!eventos.isEmpty()) {
            for (Evento e : eventos) {
                Map<String, String> evento = new HashMap<>();
                String autores = "";
                for (Usuario usuario : e.getAutores()) {
                    autores += usuario.getUid() + "-";
                }
                evento.put(EVENTO_AUTOR,autores);
                evento.put(EVENTO_EQUIPO, e.getEquipo());
                evento.put(EVENTO_MINUTO, e.getMinuto());
                evento.put(EVENTO_TIPO,e.getAccion());
                databaseReference.child(PARTIDO_EVENTOS).push().setValue(evento);
            }
        }
    }

    public boolean guardarAlineacion(Equipo equipo, String condicionEquipo) {
        try {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                    .child(COMPETICIONES).child(this.temporada).child(this.sede).child(this.categoria)
                    .child(PARTIDOS).child(this.jornadaPartido).child(this.diaPartido).child(this.idPartido)
                    .child(condicionEquipo).child(EQUIPO_MIEMBROS);
            databaseReference.removeValue();

            for (Tecnico tecnico : equipo.getTecnicosPartido()) {
                Map<String, String> miembro = new HashMap<>();
                miembro.put(EQUIPO_PLANTILLA_TIPO, EQUIPO_PLANTILLA_TIPO_TECNICO);
                databaseReference.child(tecnico.getUid()).setValue(miembro);
            }
            for (Jugador jugador : equipo.getTitulares()) {
                Map<String, String> miembro = new HashMap<>();
                miembro.put(EQUIPO_PLANTILLA_TIPO, EQUIPO_PLANTILLA_TIPO_JUGADOR_TITULAR);
                databaseReference.child(jugador.getUid()).setValue(miembro);
            }
            for (Jugador jugador : equipo.getSuplentes()) {
                Map<String, String> miembro = new HashMap<>();
                miembro.put(EQUIPO_PLANTILLA_TIPO, EQUIPO_PLANTILLA_TIPO_JUGADOR_SUPLENTE);
                databaseReference.child(jugador.getUid()).setValue(miembro);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    //GETTERS

    public String getUid() {
        return uid;
    }

    public Equipo getEquipoLocal(){
        return this.equipoLocal;
    }

    public Equipo getEquipoVisitante(){
        return this.equipoVisitante;
    }

    public String getEstadoPartido(){
        return this.estadoPartido;
    }

    public void setEstadoPartido(String estadoPartido) {
        this.estadoPartido = estadoPartido;
    }

    public String getTemporada() {
        return temporada;
    }

    public String getFecha(){
        return this.fecha;
    }

    public String getLugar(){
        return this.lugar;
    }

    public String getHora(){
        return this.hora;
    }

    public String getGolesLocal() {
        return golesLocal;
    }

    public void setGolesLocal(String golesLocal) {
        this.golesLocal = golesLocal;
    }

    public String getGolesVisitante() {
        return golesVisitante;
    }

    public void setGolesVisitante(String golesVisitante) {
        this.golesVisitante = golesVisitante;
    }

    public String getLiga() { return this.liga;}

    public ArrayList<Arbitro> getArbitros() {
        return arbitros;
    }

    public ArrayList<Evento> getEventos() {
        return eventos;
    }
}
