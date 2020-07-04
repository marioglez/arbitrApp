package com.example.arbitrapp.modelos;

import java.io.Serializable;
import java.util.ArrayList;

public class Evento implements Serializable {

    private String minuto;
    private String accion;
    private ArrayList<Usuario> autores = new ArrayList<>();
    private String equipo;
    private String comentario;

    public Evento(String minuto, String accion, String[] idAutores, String equipo, String comentario){
        this.minuto = minuto;
        this.accion = accion;
        for(String id : idAutores){
            this.autores.add(new Usuario(id));
        }
        this.equipo = equipo;
        this.comentario = comentario;
    }

    //GETTERS

    public String getMinuto() {
        return minuto;
    }

    public String getAccion() {
        return accion;
    }

    public ArrayList<Usuario> getAutores() {
        return autores;
    }

    public String getEquipo() {
        return equipo;
    }

    public String getComentario() {
        return comentario;
    }
}
