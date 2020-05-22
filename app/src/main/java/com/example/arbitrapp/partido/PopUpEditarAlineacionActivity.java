package com.example.arbitrapp.partido;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import static com.example.arbitrapp.FirebaseData.*;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.arbitrapp.R;
import static com.example.arbitrapp.FirebaseData.*;

import com.example.arbitrapp.modelos.Jugador;
import com.example.arbitrapp.modelos.Tecnico;
import com.example.arbitrapp.modelos.Usuario;

import java.util.ArrayList;
import java.util.List;

public class PopUpEditarAlineacionActivity extends AppCompatActivity {

    private TextView tituloPopUp;
    private RelativeLayout btnFinalizar;
    private LinearLayout layoutJugadores;
    private List<CheckBox> checkBoxes;

    private String titulo;
    private ArrayList<Tecnico> tecnicos;
    private ArrayList<Tecnico> tecnicosSeleccionados;
    private ArrayList<Jugador> jugadores;
    private ArrayList<Jugador> jugadoresSeleccionados;
    private ArrayList<Jugador> jugadoresUsados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_up_editar);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int)(width*.8),(int)(height*.7));

        titulo = getIntent().getStringExtra("titulo");
        checkBoxes = new ArrayList<>();

        tituloPopUp = findViewById(R.id.titulo_editar);
        layoutJugadores = findViewById(R.id.layoutJugadores);
        btnFinalizar = findViewById(R.id.confirmar_edicion);

        tituloPopUp.setText(titulo);


        switch (titulo) {
            case EQUIPO_CUERPO_TECNICO:
                tecnicos = (ArrayList<Tecnico>) getIntent().getSerializableExtra("usuarios");
                tecnicosSeleccionados = (ArrayList<Tecnico>) getIntent().getSerializableExtra("seleccionados");
                rellenarTecnicos();
                btnFinalizar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finishPopUpTecnicos();
                    }
                });
                break;
            case EQUIPO_TITULARES:
            case EQUIPO_SUPLENTES:
                jugadores = (ArrayList<Jugador>) getIntent().getSerializableExtra("usuarios");
                jugadoresSeleccionados = (ArrayList<Jugador>) getIntent().getSerializableExtra("seleccionados");
                jugadoresUsados = (ArrayList<Jugador>) getIntent().getSerializableExtra("usados");
                rellenarJugadores();
                btnFinalizar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (titulo.equals(EQUIPO_TITULARES)) {
                            finishPopUpJugadores(1);
                        } else {
                            finishPopUpJugadores(2);
                        }
                    }
                });
                break;
        }
    }

    private void rellenarTecnicos() {
        int id = 0;
        for (Tecnico tecnico : tecnicos) {
            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(tecnico.getNombreCompleto());
            checkBox.setId(id);
            for (Tecnico seleccionado : tecnicosSeleccionados) {
                if (seleccionado.getId().equals(tecnico.getId())) {
                    checkBox.setChecked(true);
                }
            }
            checkBoxes.add(checkBox);
            layoutJugadores.addView(checkBox);
            id++;
        }
    }

    private void rellenarJugadores() {
        int id = 0;
        for (Jugador jugador : jugadores) {
            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(jugador.getNombreCompleto());
            checkBox.setId(id);
            for (Jugador usado: jugadoresUsados) {
                if (usado.getId().equals(jugador.getId())) {
                    checkBox.setEnabled(false);
                }
            }
            for (Jugador seleccionado : jugadoresSeleccionados) {
                if (seleccionado.getId().equals(jugador.getId())) {
                    checkBox.setChecked(true);
                }
            }
            checkBoxes.add(checkBox);
            layoutJugadores.addView(checkBox);
            id++;
        }
    }

    //FINALIZAR POPUP Y ENVIAR DATOS A PARTIDO EQUIPO
    private void finishPopUpTecnicos(){
        ArrayList<Tecnico> resultado = new ArrayList<>();
        for (CheckBox checkBox : checkBoxes) {
            if (checkBox.isChecked()) {
                resultado.add(tecnicos.get(checkBox.getId()));
            }
        }
        Intent intent = new Intent();
        intent.putExtra("resultado",resultado);
        setResult(0,intent);
        finish();
    }

    private void finishPopUpJugadores(int resultCode){
        ArrayList<Jugador> resultado = new ArrayList<>();
        for (CheckBox checkBox : checkBoxes) {
            if (checkBox.isChecked()) {
                resultado.add(jugadores.get(checkBox.getId()));
            }
        }
        if (resultCode==1 && resultado.size() < MAX_JUG_TITULARES) {
            Intent intent = new Intent();
            intent.putExtra("resultado",resultado);
            setResult(resultCode,intent);
            finish();
        } else if (resultCode==2 && resultado.size() < MAX_JUG_SUPLENTES ) {
            Intent intent = new Intent();
            intent.putExtra("resultado",resultado);
            setResult(resultCode,intent);
            finish();
        } else {
            if (resultCode==1){
                Toast.makeText(getApplicationContext(), "El máximo de jugadores titulares es " + MAX_JUG_TITULARES, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "El máximo de jugadores suplentes es " + MAX_JUG_SUPLENTES, Toast.LENGTH_LONG).show();
            }
        }

    }

    @Override
    public void onBackPressed() {
    }
}
