package com.example.arbitrapp.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.arbitrapp.R;

public class DialogEditarAlineacion extends AppCompatDialogFragment {

    private String grupo;
    private TextView titulo;
    private RelativeLayout aceptar;
    private DialogEditarAlineacionListener dialogEditarAlineacionListener;

    public DialogEditarAlineacion(String grupo) {
        this.grupo = grupo;
    }

   /* @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_dialog_editar_alineacion,container,false);

        titulo = view.findViewById(R.id.alineacion);
        titulo.setText(grupo);

        return view;
    }*/

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog_editar_alineacion, null);
        builder.setView(view);

        titulo = view.findViewById(R.id.alineacion);
        titulo.setText(grupo);

        aceptar = view.findViewById(R.id.iniciar_segundaParte);
        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogEditarAlineacionListener.applyEditarAlineacion(true);
                dismiss();
            }
        });

        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            dialogEditarAlineacionListener = (DialogEditarAlineacionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement DialogEditarAlineacionListener");
        }
    }

    public interface DialogEditarAlineacionListener {
        void applyEditarAlineacion(boolean iniciar);
    }
}
