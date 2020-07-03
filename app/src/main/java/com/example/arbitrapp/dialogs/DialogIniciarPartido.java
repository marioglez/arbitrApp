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

public class DialogIniciarPartido extends AppCompatDialogFragment {

    private DialogIniciarPartidoListener dialogIniciarPartidoListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog_arbitrar_partido, null);
        builder.setView(view);

        RelativeLayout aceptar = view.findViewById(R.id.iniciar_aceptar);
        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogIniciarPartidoListener.applyIniciar(true);
                dismiss();
            }
        });

        RelativeLayout cancelar = view.findViewById(R.id.iniciar_cancelar);
        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogIniciarPartidoListener.applyIniciar(false);
                dismiss();
            }
        });

        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            dialogIniciarPartidoListener = (DialogIniciarPartidoListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement DialogIniciarPartidoListener");
        }
    }

    public interface DialogIniciarPartidoListener {
        void applyIniciar(boolean iniciar);
    }
}
