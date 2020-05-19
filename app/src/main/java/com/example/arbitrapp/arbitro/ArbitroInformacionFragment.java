package com.example.arbitrapp.arbitro;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.arbitrapp.R;
import com.example.arbitrapp.modelos.Arbitro;
import com.example.arbitrapp.modelos.Partido;
import static com.example.arbitrapp.FirebaseData.*;

public class ArbitroInformacionFragment extends Fragment {

    private Arbitro arbitro;
    private TextView movil, email, partidosArbitrados, valoracion;

    public ArbitroInformacionFragment(Arbitro arbitro){
        this.arbitro = arbitro;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_arbitro_informacion, container, false);

        movil = view.findViewById(R.id.textView_arbitro_movil);
        email = view.findViewById(R.id.textView_arbitro_email);
        partidosArbitrados = view.findViewById(R.id.textView_arbitro_partidosArbitrados);
        valoracion = view.findViewById(R.id.textView_arbitro_valoracion);

        rellenarInformacion();

        return view;
    }

    private void rellenarInformacion(){
        int contadorPartidosArbitrados = 0;
        movil.setText(arbitro.getMovil());
        email.setText(arbitro.getEmail());
        for(Partido partido : arbitro.getPartidos()){
            if(partido.getEstadoPartido().equals(PARTIDO_FINALIZADO)){
                contadorPartidosArbitrados++;
            }
        }
        partidosArbitrados.setText(String.valueOf(contadorPartidosArbitrados));
    }
}
