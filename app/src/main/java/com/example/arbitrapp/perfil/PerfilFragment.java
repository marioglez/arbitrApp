package com.example.arbitrapp.perfil;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import static com.example.arbitrapp.FirebaseData.*;

import com.bumptech.glide.Glide;
import com.example.arbitrapp.R;
import com.example.arbitrapp.modelos.Agenda;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class PerfilFragment extends Fragment {

    private TextView nombrePerfil, tipoPerfil;
    private ImageView imagenPerfil;

    private BottomNavigationView bottomNavigationView;
    private ScrollView scrollView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Log.d("PERFIL", "onCreateView: ");
        if(currentUser.getAgenda() == null){
            currentUser.setAgenda(new Agenda());
        }

        //Cargar Fragment
        final View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        imagenPerfil = view.findViewById((R.id.imagenPerfil));
        nombrePerfil = view.findViewById(R.id.txtNombre);
        tipoPerfil = view.findViewById(R.id.txtCategoria);
        scrollView = view.findViewById(R.id.scrollViewPerfil);
        //Barra de navegacion Perfil
        bottomNavigationView = view.findViewById(R.id.perfil_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
        //Fragment inicial
        bottomNavigationView.setSelectedItemId(R.id.nav_datos);

        pintarDatos(view);

        return view;
    }

    private void pintarDatos(View view){
        try{
            Glide
                .with(view)
                .load(Uri.parse(currentUser.getImagen()))
                .into(imagenPerfil);
        }catch (Exception e){
            Log.d("PERFIL AGENDA", "pintarDatos: ERROR AL PINTAR IMAGEN");
        }
        nombrePerfil.setText(currentUser.getNombreCompleto());
        tipoPerfil.setText(currentUser.getTipoUsuario());
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;

            switch (item.getItemId()){
                case R.id.nav_datos:
                    scrollView.smoothScrollTo(0,0);
                    selectedFragment = new PerfilDatosFragment();
                    break;
                case R.id.nav_agenda:
                    scrollView.smoothScrollTo(0,0);
                    selectedFragment = new PerfilAgendaFragment();
                    break;
                case R.id.nav_estadisticas:
                    scrollView.smoothScrollTo(0,0);
                    selectedFragment = new PerfilEstadisticasFragment();
                    break;
            }
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_perfil_container, selectedFragment).commit();
            return true;
        }
    };
}
