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
import static com.example.arbitrapp.FirebaseData.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.arbitrapp.R;

public class DialogFinalizarPartido extends AppCompatDialogFragment {

    private RelativeLayout aceptar, cancelar;
    private Spinner spinner_finalizar;
    private DialogFinalizarPartidoListener dialogFinalizarPartidoListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog_finalizar_partido, null);
        builder.setView(view);

        spinner_finalizar = view.findViewById(R.id.spinner_finalizar);
        String[] opcionesFinalizar = {"Estado del partido", PARTIDO_FINALIZADO,PARTIDO_SIN_JUGAR,PARTIDO_APLAZADO,PARTIDO_SUSPENDIDO};
        ArrayAdapter<String> adapterFinalizar = new ArrayAdapter<String>(getContext(),R.layout.spinner_item,opcionesFinalizar);
        spinner_finalizar.setAdapter(adapterFinalizar);

        aceptar = view.findViewById(R.id.finalizar_aceptar);
        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogFinalizarPartidoListener.applyFinalizar(spinner_finalizar.getSelectedItem().toString());
                dismiss();
            }
        });

        cancelar = view.findViewById(R.id.finalizar_cancelar);
        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        spinner_finalizar.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    aceptar.setBackground(getResources().getDrawable(R.drawable.boton_gris));
                    aceptar.setClickable(false);
                } else {
                    aceptar.setBackground(getResources().getDrawable(R.drawable.boton_verde));
                    aceptar.setClickable(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            dialogFinalizarPartidoListener = (DialogFinalizarPartidoListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement DialogFinalizarPartidoListener");
        }
    }

    public interface DialogFinalizarPartidoListener {
        void applyFinalizar(String finalizar);
    }
}
