package com.example.arbitrapp.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import static com.example.arbitrapp.FirebaseData.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.arbitrapp.R;
import com.google.android.material.textfield.TextInputLayout;

public class DialogEvento extends AppCompatDialogFragment {

    private String evento;

    private RelativeLayout relativeLayout;
    private TextView tipoEvento;
    private TextInputLayout comentario;
    private RelativeLayout btnConfirmar;
    private DialogEventoListener dialogEventoListener;

    public DialogEvento(String evento) {
        this.evento = evento;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog_evento, null);
        builder.setView(view);

        relativeLayout = view.findViewById(R.id.layout_evento);
        tipoEvento = view.findViewById(R.id.textview_evento);
        switch (evento){
            case EVENTO_AMARILLA:
                relativeLayout.setBackgroundColor(getResources().getColor(R.color.amarilloPastel));
                tipoEvento.setText(evento);
                break;
            case EVENTO_ROJA:
            case EVENTO_SEGUNDA_AMARILLA:
                relativeLayout.setBackgroundColor(getResources().getColor(R.color.rojoPastel));
                tipoEvento.setText(evento);
                break;
            case EVENTO_LESION:
                relativeLayout.setBackgroundColor(getResources().getColor(R.color.azulPastel));
                tipoEvento.setText(evento);
                break;
        }

        comentario = view.findViewById(R.id.editText_motivo);

        btnConfirmar = view.findViewById(R.id.btn_confirmar);
        btnConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dialogEventoListener.applyEvento(comentario.getEditText().getText().toString());
                dialogEventoListener.applyEvento(evento);
                dismiss();
            }
        });

        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            dialogEventoListener = (DialogEventoListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement DialogEventoListener");
        }
    }

    public interface DialogEventoListener {
        void applyEvento(String resultado);
    }
}
