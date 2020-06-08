package com.example.arbitrapp.modelos;

import java.util.Comparator;

public class ComparadorNombres implements Comparator<Usuario> {

    public int compare(Usuario usuario1, Usuario usuario2){
        return usuario1.getNombre().compareToIgnoreCase(usuario2.getNombre());
    }
}
