package com.example.arbitrapp.modelos;

import java.util.Comparator;

public class ComparadorEventos implements Comparator<Evento> {

    public int compare(Evento evento1, Evento evento2){
        return Integer.valueOf(evento1.getMinuto()).compareTo(Integer.valueOf(evento2.getMinuto()));
    }
}
