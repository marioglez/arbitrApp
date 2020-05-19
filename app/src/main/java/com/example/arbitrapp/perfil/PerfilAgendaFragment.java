package com.example.arbitrapp.perfil;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.arbitrapp.MapsActivity;
import com.example.arbitrapp.modelos.Agenda;
import com.example.arbitrapp.modelos.Partido;
import com.example.arbitrapp.R;
import com.example.arbitrapp.modelos.Usuario;
import com.example.arbitrapp.partido.PartidoActivity;
import static com.example.arbitrapp.FirebaseData.*;

public class PerfilAgendaFragment extends Fragment {

    private TextView hoy, manana, semana, partidosHoy, partidosManana, partidosSemana;
    private TextView sinPartidos;

    private RelativeLayout relativeLayout;
    private TableLayout tablaPartidos;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_perfil_agenda, container, false);

        hoy = view.findViewById(R.id.textViewHoy);
        manana = view.findViewById(R.id.textViewManana);
        semana = view.findViewById(R.id.textViewSemana);

        partidosHoy = view.findViewById(R.id.textView_hoy);
        partidosManana = view.findViewById(R.id.textView_manana);
        partidosSemana = view.findViewById(R.id.textView_semana);

        sinPartidos = view.findViewById(R.id.textview_sin_partidos);

        relativeLayout = view.findViewById(R.id.layout_agenda);
        tablaPartidos = view.findViewById(R.id.tablaPartidos);

        //ONCLICKS AGENDA
        hoy.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                obtenerPartidosHoy();
            }
        });
        manana.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                obtenerPartidosManana();
            }
        });
        semana.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                obtenerPartidosSemana();
            }
        });
        partidosHoy.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                obtenerPartidosHoy();
            }
        });
        partidosManana.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                obtenerPartidosManana();
            }
        });
        partidosSemana.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                obtenerPartidosSemana();
            }
        });

        pintarAgenda();

        return view;
    }

    private void pintarAgenda(){
        partidosHoy.setText(String.valueOf(currentUser.getAgenda().getPartidosHoy().size()));
        partidosManana.setText(String.valueOf(currentUser.getAgenda().getPartidosManana().size()));
        partidosSemana.setText(String.valueOf(currentUser.getAgenda().getPartidosSemana().size()));
    }

    private void obtenerPartidosHoy(){
        hoy.setTextColor(ContextCompat.getColor(getContext(),R.color.colorAccent));
        partidosHoy.setTextColor(ContextCompat.getColor(getContext(),R.color.colorAccent));
        manana.setTextColor(ContextCompat.getColor(getContext(),R.color.icons));
        partidosManana.setTextColor(ContextCompat.getColor(getContext(),R.color.icons));
        semana.setTextColor(ContextCompat.getColor(getContext(),R.color.icons));
        partidosSemana.setTextColor(ContextCompat.getColor(getContext(),R.color.icons));
        tablaPartidos.removeAllViews();

        if(!currentUser.getAgenda().getPartidosHoy().isEmpty()){
            sinPartidos.setVisibility(View.GONE);
            for(final Partido p : currentUser.getAgenda().getPartidosHoy()) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                TableRow row = (TableRow) inflater.inflate(R.layout.tabla_partido, relativeLayout, false);

                TextView tituloCaja =  row.findViewById(R.id.textView_equipos);
                String nombrePartido = p.getEquipoLocal().getNombre() + " Vs " + p.getEquipoVisitante().getNombre();
                tituloCaja.setText(nombrePartido);

                TextView competicion = row.findViewById(R.id.textView_liga);
                competicion.setText(p.getLiga());

                TextView lugar = row.findViewById(R.id.textView_lugar);
                lugar.setText(p.getLugar());

                TextView fecha = row.findViewById(R.id.textView_fecha);
                fecha.setText(p.getFecha());

                if (p.getEstadoPartido().equals(PARTIDO_FINALIZADO)){
                    ImageView completado = row.findViewById(R.id.imageview_completado);
                    completado.setVisibility(View.VISIBLE);
                }

                TableLayout tableLayout = row.findViewById(R.id.tabla_partido);
                tableLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getContext(), PartidoActivity.class).putExtra("partido", p));
                    }
                });

                RelativeLayout comoLlegar = row.findViewById(R.id.comoLlegar);
                comoLlegar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + p.getEquipoLocal().getCampo().getUbicacion());
                        final Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        startActivity(mapIntent);
                    }
                });

                tablaPartidos.addView(row);
            }
        } else {
            String textoSinPartidos = "No hay partidos hoy";
            sinPartidos.setText(textoSinPartidos);
            sinPartidos.setVisibility(View.VISIBLE);
        }
    }

    private void obtenerPartidosManana(){
        hoy.setTextColor(ContextCompat.getColor(getContext(),R.color.icons));
        partidosHoy.setTextColor(ContextCompat.getColor(getContext(),R.color.icons));
        manana.setTextColor(ContextCompat.getColor(getContext(),R.color.colorAccent));
        partidosManana.setTextColor(ContextCompat.getColor(getContext(),R.color.colorAccent));
        semana.setTextColor(ContextCompat.getColor(getContext(),R.color.icons));
        partidosSemana.setTextColor(ContextCompat.getColor(getContext(),R.color.icons));
        tablaPartidos.removeAllViews();

        if(!currentUser.getAgenda().getPartidosManana().isEmpty()){
            sinPartidos.setVisibility(View.GONE);
            for(final Partido p : currentUser.getAgenda().getPartidosManana()) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                TableRow row = (TableRow) inflater.inflate(R.layout.tabla_partido, relativeLayout, false);

                TextView tituloCaja =  row.findViewById(R.id.textView_equipos);
                String nombrePartido = p.getEquipoLocal().getNombre() + " Vs " + p.getEquipoVisitante().getNombre();
                tituloCaja.setText(nombrePartido);

                TextView competicion = row.findViewById(R.id.textView_liga);
                competicion.setText(p.getLiga());

                TextView lugar = row.findViewById(R.id.textView_lugar);
                lugar.setText(p.getLugar());

                TextView fecha = row.findViewById(R.id.textView_fecha);
                fecha.setText(p.getFecha());

                if (p.getEstadoPartido().equals(PARTIDO_FINALIZADO)){
                    ImageView completado = row.findViewById(R.id.imageview_completado);
                    completado.setVisibility(View.VISIBLE);
                }

                TableLayout tableLayout = row.findViewById(R.id.tabla_partido);
                tableLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getContext(), PartidoActivity.class).putExtra("partido", p));
                    }
                });

                RelativeLayout comoLlegar = row.findViewById(R.id.comoLlegar);
                comoLlegar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + p.getEquipoLocal().getCampo().getUbicacion());
                        final Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        startActivity(mapIntent);
                    }
                });

                tablaPartidos.addView(row);
            }
        } else {
            String textoSinPartidos = "No hay partidos mañana";
            sinPartidos.setText(textoSinPartidos);
            sinPartidos.setVisibility(View.VISIBLE);
        }
    }

    private void obtenerPartidosSemana(){
        hoy.setTextColor(ContextCompat.getColor(getContext(),R.color.icons));
        partidosHoy.setTextColor(ContextCompat.getColor(getContext(),R.color.icons));
        manana.setTextColor(ContextCompat.getColor(getContext(),R.color.icons));
        partidosManana.setTextColor(ContextCompat.getColor(getContext(),R.color.icons));
        semana.setTextColor(ContextCompat.getColor(getContext(),R.color.colorAccent));
        partidosSemana.setTextColor(ContextCompat.getColor(getContext(),R.color.colorAccent));
        tablaPartidos.removeAllViews();

        if(!currentUser.getAgenda().getPartidosSemana().isEmpty()){
            sinPartidos.setVisibility(View.GONE);
            for(final Partido p : currentUser.getAgenda().getPartidosSemana()) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                TableRow row = (TableRow) inflater.inflate(R.layout.tabla_partido, relativeLayout, false);

                TextView tituloCaja =  row.findViewById(R.id.textView_equipos);
                String nombrePartido = p.getEquipoLocal().getNombre() + " Vs " + p.getEquipoVisitante().getNombre();
                tituloCaja.setText(nombrePartido);

                TextView competicion = row.findViewById(R.id.textView_liga);
                competicion.setText(p.getLiga());

                TextView lugar = row.findViewById(R.id.textView_lugar);
                lugar.setText(p.getLugar());

                TextView fecha = row.findViewById(R.id.textView_fecha);
                fecha.setText(p.getFecha());

                if (p.getEstadoPartido().equals(PARTIDO_FINALIZADO)){
                    ImageView completado = row.findViewById(R.id.imageview_completado);
                    completado.setVisibility(View.VISIBLE);
                }

                TableLayout tableLayout = row.findViewById(R.id.tabla_partido);
                tableLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getContext(), PartidoActivity.class).putExtra("partido", p));
                    }
                });

                RelativeLayout comoLlegar = row.findViewById(R.id.comoLlegar);
                comoLlegar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + p.getEquipoLocal().getCampo().getUbicacion());
                        final Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        startActivity(mapIntent);
                    }
                });

                tablaPartidos.addView(row);
            }
        } else {
            String textoSinPartidos = "No hay partidos esta semana";
            sinPartidos.setText(textoSinPartidos);
            sinPartidos.setVisibility(View.VISIBLE);
        }
    }
}
