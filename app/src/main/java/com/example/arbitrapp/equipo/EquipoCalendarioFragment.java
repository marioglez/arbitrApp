package com.example.arbitrapp.equipo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.arbitrapp.R;
import com.example.arbitrapp.modelos.Equipo;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class EquipoCalendarioFragment extends Fragment {

    private Equipo equipo;

    public EquipoCalendarioFragment(Equipo equipo){
        this.equipo = equipo;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_equipo_calendario, container, false);

        BottomNavigationView bottomNavigationView = view.findViewById(R.id.equipo_calendario_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
        bottomNavigationView.setSelectedItemId(R.id.nav_jugados);

        return view;
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;

            switch (item.getItemId()){
                case R.id.nav_jugados:
                    selectedFragment = new EquipoCalendarioJugadosFragment(equipo.getPartidos());
                    break;
                case R.id.nav_pendientes:
                    selectedFragment = new EquipoCalendarioPendientesFragment(equipo.getPartidos());
                    break;
            }
            getFragmentManager().beginTransaction().replace(R.id.fragment_equipo_calendario_container, selectedFragment).commit();
            return true;
        }
    };
}
