package com.example.arbitrapp.perfil;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.arbitrapp.R;
import static com.example.arbitrapp.FirebaseData.*;

public class PerfilDatosFragment extends Fragment {

    private TextView txtNombre, txtApellidos, txtEdad, txtNacionalidad, txtMovil, txtEmail;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_perfil_datos, container, false);

        txtNombre = view.findViewById(R.id.textView_nombre);
        txtApellidos = view.findViewById(R.id.textView_apellidos);
        txtEdad = view.findViewById(R.id.textView_edad);
        txtNacionalidad = view.findViewById(R.id.textView_nacionalidad);
        txtMovil = view.findViewById(R.id.textView_movil);
        txtEmail = view.findViewById(R.id.textView_email);

        rellenarDatos();

        return view;
    }

    private void rellenarDatos(){
        txtNombre.setText(currentUser.getNombre());
        txtApellidos.setText(currentUser.getApellidos());
        txtEdad.setText(currentUser.getEdad());
        txtNacionalidad.setText(currentUser.getNacionalidad());
        txtMovil.setText(currentUser.getMovil());
        txtEmail.setText(currentUser.getEmail());
    }
}
