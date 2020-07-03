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
import android.widget.TableRow;
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
    private TableRow tableRowMovil, tableRowEmail;
    private TextView movil, email, partidosArbitrados, valoracion;
    private ImageView iconoMovil, iconoEmail;

    public ArbitroInformacionFragment(Arbitro arbitro){
        this.arbitro = arbitro;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_arbitro_informacion, container, false);

        tableRowMovil = view.findViewById(R.id.tablerow_movil);
        movil = view.findViewById(R.id.textView_arbitro_movil);
        iconoMovil = view.findViewById(R.id.icono_movil);
        tableRowEmail = view.findViewById(R.id.tablerow_email);
        email = view.findViewById(R.id.textView_arbitro_email);
        iconoEmail = view.findViewById(R.id.icono_email);
        partidosArbitrados = view.findViewById(R.id.textView_arbitro_partidosArbitrados);
        valoracion = view.findViewById(R.id.textView_arbitro_valoracion);

        try {
            rellenarInformacion();
        } catch (Exception e) {
            Toast.makeText(getContext(),getResources().getString(R.string.arbitroError),Toast.LENGTH_LONG).show();
        }

        return view;
    }

    private void rellenarInformacion(){
        int contadorPartidosArbitrados = 0, contadorValoracionesRecibidas=0;
        if (!currentUser.getTipoUsuario().equals(JUGADOR)) {
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
        } else {
            tableRowMovil.setVisibility(View.GONE);
            tableRowEmail.setVisibility(View.GONE
            );
        }

        for(Partido partido : arbitro.getPartidos()){
            if(partido.getEstadoPartido().equals(PARTIDO_FINALIZADO)){
                contadorPartidosArbitrados++;
            }
        }
        partidosArbitrados.setText(String.valueOf(contadorPartidosArbitrados));
        for (Integer i : arbitro.getValoraciones()) {
            contadorValoracionesRecibidas += i;
        }
        int media = contadorValoracionesRecibidas/arbitro.getValoraciones().size();
        String valoracionText = media + "/5";
        valoracion.setText(valoracionText);
    }

    private void enviarMail() {
        String[] destinatario = {arbitro.getEmail()};
        Intent mailIntent = new Intent(Intent.ACTION_SEND);
        mailIntent.putExtra(Intent.EXTRA_EMAIL, destinatario);
        mailIntent.putExtra(Intent.EXTRA_SUBJECT,getResources().getString(R.string.emailSubject));
        mailIntent.setType("message/rfc822");

        try{
            startActivity(Intent.createChooser(mailIntent,getResources().getString(R.string.emailChoose)));
        } catch (Exception e) {
            Toast.makeText(getContext(), getResources().getString(R.string.emailChooseError), Toast.LENGTH_LONG).show();
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
                Toast.makeText(getContext(), getResources().getString(R.string.callError), Toast.LENGTH_LONG).show();
            }
        }
    }
}
