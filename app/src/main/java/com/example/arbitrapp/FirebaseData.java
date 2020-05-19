package com.example.arbitrapp;

import android.app.Application;

import com.example.arbitrapp.modelos.Usuario;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public final class FirebaseData extends Application {

    public static Usuario currentUser = null;

    static Calendar cal = Calendar.getInstance();
    public static final String TEMPORADA_ACTUAL = String.valueOf(cal.get(Calendar.YEAR)-1)+"-"+String.valueOf(cal.get(Calendar.YEAR));

    //Variables constantes globales utilizadas para buscar en la base de datos
    public static final String USUARIOS = "usuarios";
    public static final String COMPETICIONES = "competiciones";
    public static final String PARTIDOS = "partidos";
    public static final String CAMPOS = "campos";
    public static final String ID = "id";

    //IMAGENES
    public static final String IMAGENES_PERFIL = "imagenes/perfil/";
    public static final String IMAGENES_EQUIPO = "imagenes/equipos/";
    public static final String IMAGENES_CAMPO = "imagenes/campos/";
    public static final String IMAGENES_PNG = ".png";
    public static final String IMAGENES_JPG = ".jpg";

    //CAMPOS
    public static final String CAMPO_CIUDAD = "ciudad";
    public static final String CAMPO_CAPACIDAD = "capacidad";
    public static final String CAMPO_DIRECCION = "direccion";
    public static final String CAMPO_UBICACION = "ubicacion";

    //EQUIPOS
    public static final String EQUIPOS = "equipos";
    public static final String EQUIPO_ANO = "año";
    public static final String EQUIPO_CIUDAD = "ciudad";
    public static final String EQUIPO_CAMPO = "campo";
    public static final String EQUIPO_CATEGORIA = "categoria";
    public static final String EQUIPO_SEDE = "sede";
    public static final String EQUIPO_PLANTILLA = "plantilla";
    public static final String EQUIPO_PLANTILLA_TIPO = "tipo";
    public static final String EQUIPO_PLANTILLA_TIPO_TECNICO = "tecnico";
    public static final String EQUIPO_PLANTILLA_TIPO_JUGADOR = "jugador";
    public static final String EQUIPO_PLANTILLA_TIPO_JUGADOR_TITULAR = "titular";
    public static final String EQUIPO_PLANTILLA_TIPO_JUGADOR_SUPLENTE = "suplente";
    public static final String EQUIPO_EQUIPACION = "equipacion";
    public static final String EQUIPO_EQUIPACION_CAMISETA = "camiseta";
    public static final String EQUIPO_EQUIPACION_PANTALON = "pantalon";
    public static final String EQUIPO_EQUIPACION_MEDIAS = "medias";

    //COMPETICIONES
    public static final String COMPETICION_TEMPORADA = "temporada";
    public static final String COMPETICION_SEDE = "sede";
    public static final String COMPETICION_CATEGORIA = "categoria";

    //USUARIOS
    public static final String USUARIO_NOMBRE = "nombre";
    public static final String USUARIO_APELLIDOS = "apellidos";
    public static final String USUARIO_NACIMIENTO = "nacimiento";
    public static final String USUARIO_NACIONALIDAD = "nacionalidad";
    public static final String USUARIO_TIPO = "tipo";
    public static final String USUARIO_MOVIL = "movil";
    public static final String USUARIO_EMAIL = "email";
    public static final String USUARIO_EQUIPO = "equipo";
    public static final String USUARIO_COMPETICIONES_FAVORITAS = "competicionesFavoritas";


    //ARBITROS
    public static final String ARBITRO = "Árbitro";
    public static final String ARBITRO_CATEGORIA = "categoria";

    //JUGADORES
    public static final String JUGADOR = "Jugador";
    public static final String JUGADOR_CAPITAN = "capitan";
    public static final String JUGADOR_POSICION = "posicion";
    public static final String JUGADOR_DORSAL = "dorsal";

    //TECNICOS
    public static final String TECNICO = "Técnico";
    public static final String TECNICO_CARGO = "cargo";

    //PARTIDO
    public static final String PARTIDO_ARBITRAJE = "arbitraje";
    public static final String PARTIDO_ESTADO = "estado";
    public static final String PARTIDO_FECHA = "fecha";
    public static final String PARTIDO_HORA = "hora";
    public static final String PARTIDO_LUGAR = "lugar";
    public static final String PARTIDO_LIGA = "liga";
    public static final String PARTIDO_EVENTOS = "eventos";

    public static final String EQUIPO_LOCAL = "equipoLocal";
    public static final String EQUIPO_VISITANTE = "equipoVisitante";
    public static final String EQUIPO_NOMBRE = "nombre";
    public static final String EQUIPO_MIEMBROS = "miembros";
    public static final String EQUIPO_GOLES = "goles";

    //ESTADOS DE PARTIDO
    public static final String PARTIDO_FINALIZADO = "Finalizado";
    public static final String PARTIDO_SIN_JUGAR = "Sin jugar";
    public static final String PARTIDO_SUSPENDIDO = "Suspendido";
    public static final String PARTIDO_APLAZADO = "Aplazado";

    //EVENTOS DE PARTIDO
    public static final String EVENTO_AUTOR = "autor";
    public static final String EVENTO_MINUTO = "minuto";
    public static final String EVENTO_TIPO = "tipo";
    public static final String EVENTO_EQUIPO = "equipo";
    public static final String EVENTO_LOCAL = "local";
    public static final String EVENTO_VISITANTE = "visitante";
    public static final String EVENTO_GOL = "Gol";
    public static final String EVENTO_GOL_PROPIA = "Gol propia";
    public static final String EVENTO_AMARILLA = "Amarilla";
    public static final String EVENTO_ROJA = "Roja";
    public static final String EVENTO_SEGUNDA_AMARILLA = "Doble amarilla";
    public static final String EVENTO_SUSTITUCION = "Sustitución";
    public static final String EVENTO_LESION = "Lesión";

    @Override
    public void onCreate() {

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        super.onCreate();
    }
}
