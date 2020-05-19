package com.example.arbitrapp.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.arbitrapp.R;

public class DialogSegundaPartePartido extends AppCompatDialogFragment {

    private RelativeLayout aceptar;
    private DialogSegundaPartePartidoListener dialogSegundaPartePartidoListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog_segunda_parte_partido, null);
        builder.setView(view);

        aceptar = view.findViewById(R.id.iniciar_segundaParte);
        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSegundaPartePartidoListener.applySegundaParte(true);
                dismiss();
            }
        });

        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            dialogSegundaPartePartidoListener = (DialogSegundaPartePartidoListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement DialogSegundaPartePartidoListener");
        }
    }

    public interface DialogSegundaPartePartidoListener {
        void applySegundaParte(boolean iniciar);
    }
}
