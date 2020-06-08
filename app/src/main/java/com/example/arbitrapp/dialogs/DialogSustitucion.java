package com.example.arbitrapp.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.arbitrapp.R;
import com.example.arbitrapp.modelos.Jugador;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

public class DialogSustitucion extends AppCompatDialogFragment {

    private ArrayList<Jugador> jugadores;

    private Spinner spinnerJugadores;
    private ArrayList<String> spinnerJugadoresItems;
    private RelativeLayout btnConfirmar;
    private DialogSustitucionListener dialogSustitucionListener;

    public DialogSustitucion(ArrayList<Jugador> jugadores) {
        this.jugadores = jugadores;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog_sustitucion, null);
        builder.setView(view);

        spinnerJugadores = view.findViewById(R.id.spinner_sustitucion);
        btnConfirmar = view.findViewById(R.id.btn_confirmar);

        spinnerJugadoresItems = new ArrayList<>();
        if (jugadores.isEmpty()) {
            spinnerJugadoresItems.add("No hay suplentes");
        } else {
            spinnerJugadoresItems.add("Selecciona jugador");
            for (Jugador jugador : jugadores) {
                spinnerJugadoresItems.add(jugador.getNombreCompleto());
            }
        }
        ArrayAdapter<String> adapterJugadores = new ArrayAdapter<>(getContext(),R.layout.spinner_item,spinnerJugadoresItems);
        spinnerJugadores.setAdapter(adapterJugadores);

        spinnerJugadores.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    btnConfirmar.setBackground(getResources().getDrawable(R.drawable.boton_gris));
                    btnConfirmar.setClickable(false);
                } else {
                    btnConfirmar.setBackground(getResources().getDrawable(R.drawable.boton_verde));
                    btnConfirmar.setClickable(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (Jugador jugador : jugadores) {
                    if (jugador.getNombreCompleto().equals(spinnerJugadores.getSelectedItem().toString())) {
                        dialogSustitucionListener.applySustitucion(jugador.getUid());
                        dismiss();
                        return;
                    }
                }
            }
        });

        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            dialogSustitucionListener = (DialogSustitucionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement DialogSustitucionListener");
        }
    }

    public interface DialogSustitucionListener {
        void applySustitucion(String resultado);
    }
}
