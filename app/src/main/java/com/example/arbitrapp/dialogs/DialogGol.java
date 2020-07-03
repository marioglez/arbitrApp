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

public class DialogGol extends AppCompatDialogFragment {

    private DialogGolListener dialogGolListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog_gol, null);
        builder.setView(view);

        RelativeLayout golAFavor = view.findViewById(R.id.gol_favor);
        golAFavor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogGolListener.applyGol(1);
                dismiss();
            }
        });

        RelativeLayout golEnContra = view.findViewById(R.id.gol_propia);
        golEnContra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogGolListener.applyGol(-1);
                dismiss();
            }
        });

        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            dialogGolListener = (DialogGolListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement DialogGolListener");
        }
    }

    public interface DialogGolListener {
        void applyGol(int resultado);
    }
}
