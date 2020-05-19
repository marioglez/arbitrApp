package com.example.arbitrapp.modelos;

import java.util.Comparator;

public class ComparadorDorsales implements Comparator<Jugador> {

    public int compare(Jugador jugador1, Jugador jugador2){
        return Integer.valueOf(jugador1.getDorsal()).compareTo(Integer.valueOf(jugador2.getDorsal()));
    }
}
