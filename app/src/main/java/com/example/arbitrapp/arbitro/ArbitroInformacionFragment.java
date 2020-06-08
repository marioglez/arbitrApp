package com.example.arbitrapp.arbitro;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.arbitrapp.R;
import com.example.arbitrapp.modelos.Arbitro;
import com.example.arbitrapp.modelos.Partido;
import static com.example.arbitrapp.FirebaseData.*;

public class ArbitroInformacionFragment extends Fragment {

    private Arbitro arbitro;
    private TextView movil, email, partidosArbitrados, valoracion;
    private ImageView iconoMovil, iconoEmail;

    public ArbitroInformacionFragment(Arbitro arbitro){
        this.arbitro = arbitro;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_arbitro_informacion, container, false);

        movil = view.findViewById(R.id.textView_arbitro_movil);
        iconoMovil = view.findViewById(R.id.icono_movil);
        email = view.findViewById(R.id.textView_arbitro_email);
        iconoEmail = view.findViewById(R.id.icono_email);
        partidosArbitrados = view.findViewById(R.id.textView_arbitro_partidosArbitrados);
        valoracion = view.findViewById(R.id.textView_arbitro_valoracion);

        rellenarInformacion();

        return view;
    }

    private void rellenarInformacion(){
        int contadorPartidosArbitrados = 0;
        movil.setText(arbitro.getMovil());
        movil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llamarTelefono();
            }
        });
        iconoMovil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llamarTelefono();
            }
        });
        email.setText(arbitro.getEmail());
        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviarMail();
            }
        });
        iconoEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviarMail();
            }
        });
        for(Partido partido : arbitro.getPartidos()){
            if(partido.getEstadoPartido().equals(PARTIDO_FINALIZADO)){
                contadorPartidosArbitrados++;
            }
        }
        partidosArbitrados.setText(String.valueOf(contadorPartidosArbitrados));
    }

    private void enviarMail() {
        String[] destinatario = {arbitro.getEmail()};
        Intent mailIntent = new Intent(Intent.ACTION_SEND);
        mailIntent.putExtra(Intent.EXTRA_EMAIL, destinatario);
        mailIntent.putExtra(Intent.EXTRA_SUBJECT,"Mensaje de arbitrApp");
        mailIntent.setType("message/rfc822");

        try{
            startActivity(Intent.createChooser(mailIntent,"Elige la aplicación de correo"));
        } catch (Exception e) {
            Toast.makeText(getContext(), "No hay aplicaciones de correo instaladas", Toast.LENGTH_LONG).show();
        }
    }

    private void llamarTelefono() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.CALL_PHONE},1);
        } else {
            String numero = "tel:" + arbitro.getMovil();
            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(numero)));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                llamarTelefono();
            } else {
                Toast.makeText(getContext(), "Permiso de llamadas denegado", Toast.LENGTH_LONG).show();
            }
        }
    }
}
