package com.example.arbitrapp.modelos;

import java.util.Comparator;

public class ComparadorPuntos implements Comparator<Equipo> {

    public int compare(Equipo equipo1, Equipo equipo2){
        return Integer.compare(equipo2.getPuntos(),equipo1.getPuntos());
    }
}
