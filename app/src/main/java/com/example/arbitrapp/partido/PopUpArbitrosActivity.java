package com.example.arbitrapp.partido;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.arbitrapp.R;
import com.example.arbitrapp.modelos.Arbitro;
import com.example.arbitrapp.modelos.ComparadorNombres;
import com.example.arbitrapp.modelos.Jugador;
import com.example.arbitrapp.modelos.Tecnico;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.example.arbitrapp.FirebaseData.EQUIPO_CUERPO_TECNICO;
import static com.example.arbitrapp.FirebaseData.EQUIPO_SUPLENTES;
import static com.example.arbitrapp.FirebaseData.EQUIPO_TITULARES;
import static com.example.arbitrapp.FirebaseData.MAX_ARBITROS;
import static com.example.arbitrapp.FirebaseData.MAX_JUG_SUPLENTES;
import static com.example.arbitrapp.FirebaseData.MAX_JUG_TITULARES;

public class PopUpArbitrosActivity extends AppCompatActivity {

    private RelativeLayout btnFinalizar;
    private LinearLayout layoutArbitros;
    private List<CheckBox> checkBoxes;

    private ArrayList<Arbitro> arbitros;
    private ArrayList<Arbitro> arbitrosSeleccionados;

    private ComparadorNombres comparadorNombres;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_up_arbitros);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int)(width*.8),(int)(height*.7));

        checkBoxes = new ArrayList<>();
        comparadorNombres = new ComparadorNombres();

        layoutArbitros = findViewById(R.id.layoutArbitros);
        btnFinalizar = findViewById(R.id.confirmar_edicion_arbitros);

        arbitros = (ArrayList<Arbitro>) getIntent().getSerializableExtra("arbitros");
        arbitrosSeleccionados = (ArrayList<Arbitro>) getIntent().getSerializableExtra("seleccionados");
        rellenarArbitros();
        btnFinalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishPopUp();
            }
        });
    }

    private void rellenarArbitros() {
        int id = 0;
        //Ordenar tecnicos por nombre
        Collections.sort(arbitros,comparadorNombres);
        for (Arbitro arbitro : arbitros) {
            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(arbitro.getNombreCompleto());
            checkBox.setId(id);
            for (Arbitro seleccionado : arbitrosSeleccionados) {
                if (seleccionado.getUid().equals(arbitro.getUid())) {
                    checkBox.setChecked(true);
                }
            }
            checkBoxes.add(checkBox);
            layoutArbitros.addView(checkBox);
            id++;
        }
    }


    //FINALIZAR POPUP Y ENVIAR DATOS A PARTIDO EQUIPO
    private void finishPopUp(){
        ArrayList<Arbitro> resultado = new ArrayList<>();
        for (CheckBox checkBox : checkBoxes) {
            if (checkBox.isChecked()) {
                resultado.add(arbitros.get(checkBox.getId()));
            }
        }
        if (resultado.size() <= MAX_ARBITROS) {
            Intent intent = new Intent();
            intent.putExtra("resultado",resultado);
            setResult(0,intent);
            finish();
        } else {
            Toast.makeText(getApplicationContext(), "El máximo de árbitros es " + MAX_ARBITROS, Toast.LENGTH_LONG).show();
        }

    }


    @Override
    public void onBackPressed() {
    }
}
