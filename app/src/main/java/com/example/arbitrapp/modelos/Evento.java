package com.example.arbitrapp.modelos;

import java.io.Serializable;
import java.util.ArrayList;

public class Evento implements Serializable {

    private String minuto;
    private String accion;
    private ArrayList<Usuario> autores = new ArrayList<>();
    private String equipo;

    public Evento(String minuto, String accion, String[] idAutores, String equipo){
        this.minuto = minuto;
        this.accion = accion;
        for(String id : idAutores){
            this.autores.add(new Usuario(id));
        }
        this.equipo = equipo;
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
}
