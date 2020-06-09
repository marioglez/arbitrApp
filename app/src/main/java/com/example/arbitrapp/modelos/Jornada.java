package com.example.arbitrapp.modelos;

import java.io.Serializable;
import java.util.ArrayList;

public class Jornada implements Serializable {

    private String nombre;
    private ArrayList<String> fechas;
    private ArrayList<Partido> partidos;

    public Jornada(String nombre){
        this.nombre = nombre;
        this.fechas = new ArrayList<>();
        this.partidos = new ArrayList<>();
    }

    //GETTERS

    public String getNombre() {
        return nombre;
    }

    public ArrayList<String> getFechas() {
        return fechas;
    }

    public ArrayList<Partido> getPartidos() {
        return partidos;
    }

    public void setPartido(Partido partido){
        this.partidos.add(partido);
    }
}
