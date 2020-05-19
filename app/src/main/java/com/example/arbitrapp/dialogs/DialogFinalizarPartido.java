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

public class DialogFinalizarPartido extends AppCompatDialogFragment {

    private RelativeLayout aceptar, cancelar;
    private DialogFinalizarPartidoListener dialogFinalizarPartidoListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog_finalizar_partido, null);
        builder.setView(view);

        aceptar = view.findViewById(R.id.finalizar_aceptar);
        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogFinalizarPartidoListener.applyFinalizar(true);
                dismiss();
            }
        });

        cancelar = view.findViewById(R.id.finalizar_cancelar);
        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dialogFinalizarPartidoListener.applyFinalizar(-1);
                dismiss();
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
        void applyFinalizar(boolean finalizar);
    }
}
