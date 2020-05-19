package com.example.arbitrapp.modelos;

import java.io.Serializable;

public class Equipacion implements Serializable {

    private String camiseta;
    private String pantalon;
    private String medias;

    public Equipacion(String camiseta, String pantalon, String medias){
        this.camiseta = camiseta;
        this.pantalon = pantalon;
        this.medias = medias;
    }

    public String getCamiseta() {
        return camiseta;
    }

    public String getPantalon() {
        return pantalon;
    }

    public String getMedias() {
        return medias;
    }
}
