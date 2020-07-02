package com.example.arbitrapp.home;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.example.arbitrapp.R;
import com.example.arbitrapp.modelos.Partido;
import com.example.arbitrapp.partido.PartidoActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.CountDownLatch;
import static com.example.arbitrapp.FirebaseData.*;

public class HomeFragment extends Fragment {

    private View view;
    private TableLayout tablaProximoPartido, tablaPartidoDirecto;
    private TextView tituloPartido, competicionPartido, lugarPartido, fechaPartido,
            nombreLocal, nombreVisitante, marcadorLocal, marcadorVisitante, ligaPartido;
    private TextView noProximosPartidos, noPartidosDirecto;
    private ImageView escudoLocal, escudoVisitante;
    private RelativeLayout comoLlegar, cajaDirecto, cajaProximos;
    private ImageButton chevronLeft, chevronRight, chevronLeftDirecto, chevronRightDirecto;
    private ProgressBar progressBarDirecto, progressBarProximos;

    private int posicionCarousel, posicionCarouselDirecto;

    private CountDownLatch countDownLatchDirecto;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);

        progressBarDirecto = view.findViewById(R.id.progressBarDirecto);
        cajaDirecto = view.findViewById(R.id.cajaDirecto);
        progressBarProximos = view.findViewById(R.id.progressBarProximos);
        cajaProximos = view.findViewById(R.id.caja_partido);
        noProximosPartidos = view.findViewById(R.id.no_proximos_partidos);
        noPartidosDirecto = view.findViewById(R.id.no_partidos_directo);

        tablaPartidoDirecto = view.findViewById(R.id.tabla_directo);
        chevronLeftDirecto = view.findViewById(R.id.chevron_izquierdaDirecto);
        chevronRightDirecto = view.findViewById(R.id.chevron_derechaDirecto);
        escudoLocal = view.findViewById(R.id.escudo_local);
        escudoVisitante = view.findViewById(R.id.escudo_visitante);
        nombreLocal = view.findViewById(R.id.nombre_local);
        nombreVisitante = view.findViewById(R.id.nombre_visitante);
        marcadorLocal = view.findViewById(R.id.marcador_local);
        marcadorVisitante = view.findViewById(R.id.marcador_visitante);
        ligaPartido = view.findViewById(R.id.ligaLocal);

        tablaProximoPartido = view.findViewById(R.id.tabla_proximo_partido);
        tituloPartido = view.findViewById(R.id.titulo_partido);
        competicionPartido = view.findViewById(R.id.competicion_partido);
        lugarPartido = view.findViewById(R.id.lugar_partido);
        fechaPartido = view.findViewById(R.id.fecha_partido);
        comoLlegar = view.findViewById(R.id.comoLlegar_partido);
        
        if (partidosDirecto.isEmpty() && proximosPartidos.isEmpty()) {
            obtenerEnDirecto();
            obtenerProximosPartidos();
        } else {
            pintarPartidosDirecto();
            pintarProximosPartidos();
        }

        return view;
    }

    private void obtenerEnDirecto() {
        Calendar cal = Calendar.getInstance();
        final int day = cal.get(Calendar.DAY_OF_YEAR);
        final int year = cal.get(Calendar.YEAR);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child(COMPETICIONES).child(TEMPORADA_ACTUAL).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    partidosDirecto.clear();
                    final String idPartidos = (String.valueOf(year) + String.valueOf(day));
                    for (DataSnapshot sede : dataSnapshot.getChildren()) {
                        for (DataSnapshot categoria : sede.getChildren()) {
                            for (DataSnapshot jornada : categoria.child(PARTIDOS).getChildren()) {
                                for (DataSnapshot diaPartido : jornada.getChildren()) {
                                    if (diaPartido.getKey().equals(idPartidos)) {
                                        for (DataSnapshot partido : diaPartido.getChildren()) {
                                            if (partido.child(PARTIDO_ESTADO).getValue().toString().equals(PARTIDO_EN_CURSO)) {
                                                partidosDirecto.add(new Partido(TEMPORADA_ACTUAL, sede.getKey(), categoria.getKey(), idPartidos, partido.getKey()));
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            pintarPartidosDirecto();
                        }
                    }, 4000);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void obtenerProximosPartidos() {
        Calendar cal = Calendar.getInstance();
        final int day = cal.get(Calendar.DAY_OF_YEAR);
        final int year = cal.get(Calendar.YEAR);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child(COMPETICIONES).child(TEMPORADA_ACTUAL).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    proximosPartidos.clear();
                    int partidosEncontrados = 0;
                    int diasBuscados = 0;
                    do {
                        final int dia = day + diasBuscados;
                        final String idPartidos = dayOfYear(year, dia);
                        for (DataSnapshot sede : dataSnapshot.getChildren()) {
                            for (DataSnapshot categoria : sede.getChildren()) {
                                for (DataSnapshot jornada : categoria.child(PARTIDOS).getChildren()) {
                                    for (DataSnapshot diaPartido : jornada.getChildren()) {
                                        if (diaPartido.getKey().equals(idPartidos)) {
                                            for (DataSnapshot partido : diaPartido.getChildren()) {
                                                if (!partido.child(PARTIDO_ESTADO).getValue().toString().equals(PARTIDO_FINALIZADO)
                                                        && !partido.child(PARTIDO_ESTADO).getValue().toString().equals(PARTIDO_EN_CURSO)) {
                                                    proximosPartidos.add(new Partido(TEMPORADA_ACTUAL, sede.getKey(), categoria.getKey(), idPartidos, partido.getKey()));
                                                    partidosEncontrados++;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        diasBuscados++;
                    } while (partidosEncontrados<3 && diasBuscados<7);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            pintarProximosPartidos();
                        }
                    }, 4000);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private String dayOfYear(int year, int dia){
        //Comprobar si pasa del dia maximo del año
        Calendar cal = new GregorianCalendar(year, 11, 31);
        int totalDias = cal.get(Calendar.DAY_OF_YEAR);

        if(dia>totalDias){
            year += + 1;
            dia -= totalDias;
            return (String.valueOf(year) + String.valueOf(dia));
        } else {
            return (String.valueOf(year) + String.valueOf(dia));
        }
    }

    private void pintarPartidosDirecto() {
        posicionCarouselDirecto = 0;
        if (!partidosDirecto.isEmpty()) {
            rellenarPartidosDirecto();
            tablaPartidoDirecto.setVisibility(View.VISIBLE);
            chevronLeftDirecto.setVisibility(View.VISIBLE);
            chevronRightDirecto.setVisibility(View.VISIBLE);
            progressBarDirecto.setVisibility(View.GONE);
            noPartidosDirecto.setVisibility(View.GONE);

            desactivarChevron(chevronLeftDirecto);
            chevronLeftDirecto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (posicionCarouselDirecto > 0) {
                        posicionCarouselDirecto--;
                        activarChevron(chevronRightDirecto);
                        if (posicionCarouselDirecto==0){
                            desactivarChevron(chevronLeftDirecto);
                        }
                        rellenarPartidosDirecto();
                    }
                }
            });

            if (partidosDirecto.size() < 2 ){
                desactivarChevron(chevronRightDirecto);
            } else {
                activarChevron(chevronRightDirecto);
                chevronRightDirecto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (posicionCarouselDirecto < partidosDirecto.size()-1) {
                            posicionCarouselDirecto++;
                            activarChevron(chevronLeftDirecto);
                            if (posicionCarouselDirecto==partidosDirecto.size()-1){
                                desactivarChevron(chevronRightDirecto);
                            }
                            rellenarPartidosDirecto();
                        }
                    }
                });
            }

        } else {
            progressBarDirecto.setVisibility(View.GONE);
            tablaPartidoDirecto.setVisibility(View.GONE);
            chevronLeftDirecto.setVisibility(View.GONE);
            chevronRightDirecto.setVisibility(View.GONE);
            noPartidosDirecto.setVisibility(View.VISIBLE);
        }
    }

    private void rellenarPartidosDirecto() {
        try {
            Glide
                    .with(view)
                    .load(Uri.parse(partidosDirecto.get(posicionCarouselDirecto).getEquipoLocal().getEscudo()))
                    .into(escudoLocal);
        }catch (Exception e){
            Log.d("HOME DIRECTO", "pintarDatos: ERROR AL PINTAR ESCUDO");
        }
        try {
            Glide
                    .with(view)
                    .load(Uri.parse(partidosDirecto.get(posicionCarouselDirecto).getEquipoVisitante().getEscudo()))
                    .into(escudoVisitante);
        }catch (Exception e){
            Log.d("HOME DIRECTO", "pintarDatos: ERROR AL PINTAR ESCUDO");
        }
        nombreLocal.setText(partidosDirecto.get(posicionCarouselDirecto).getEquipoLocal().getSiglas());
        nombreVisitante.setText(partidosDirecto.get(posicionCarouselDirecto).getEquipoVisitante().getSiglas());
        if (!partidosDirecto.get(posicionCarouselDirecto).getGolesLocal().isEmpty()) {
            marcadorLocal.setText(partidosDirecto.get(posicionCarouselDirecto).getGolesLocal());
        } else {
            marcadorLocal.setText("0");
        }
        if (!partidosDirecto.get(posicionCarouselDirecto).getGolesVisitante().isEmpty()) {
            marcadorVisitante.setText(partidosDirecto.get(posicionCarouselDirecto).getGolesVisitante());
        } else {
            marcadorVisitante.setText("0");
        }

        ligaPartido.setText(partidosDirecto.get(posicionCarouselDirecto).getLiga());

        tablaPartidoDirecto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), PartidoDirectoActivity.class).putExtra("partido",partidosDirecto.get(posicionCarouselDirecto)));
            }
        });
    }

    private void pintarProximosPartidos() {
        posicionCarousel = 0;
        if (!proximosPartidos.isEmpty()) {
            rellenarProximoPartido(posicionCarousel);
            cajaProximos.setVisibility(View.VISIBLE);
            progressBarProximos.setVisibility(View.GONE);
            noProximosPartidos.setVisibility(View.GONE);

            chevronLeft = view.findViewById(R.id.chevron_izquierda);
            desactivarChevron(chevronLeft);
            chevronLeft.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (posicionCarousel > 0) {
                        posicionCarousel--;
                        activarChevron(chevronRight);
                        if (posicionCarousel==0){
                            desactivarChevron(chevronLeft);
                        }
                        rellenarProximoPartido(posicionCarousel);
                    }
                }
            });

            chevronRight = view.findViewById(R.id.chevron_derecha);
            if (proximosPartidos.size() < 2 ){
                desactivarChevron(chevronRight);
            } else {
                activarChevron(chevronRight);
                chevronRight.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (posicionCarousel < proximosPartidos.size()-1) {
                            posicionCarousel++;
                            activarChevron(chevronLeft);
                            if (posicionCarousel==proximosPartidos.size()-1){
                                desactivarChevron(chevronRight);
                            }
                            rellenarProximoPartido(posicionCarousel);
                        }
                    }
                });
            }

        } else {
            progressBarProximos.setVisibility(View.GONE);
            cajaProximos.setVisibility(View.GONE);
            noProximosPartidos.setVisibility(View.VISIBLE);
        }
    }

    private void rellenarProximoPartido(final int posicion) {
        String tituloProximoPartido = proximosPartidos.get(posicion).getEquipoLocal().getSiglas() + " Vs "
                + proximosPartidos.get(posicion).getEquipoVisitante().getSiglas();
        tituloPartido.setText(tituloProximoPartido);
        competicionPartido.setText(proximosPartidos.get(posicion).getLiga());
        lugarPartido.setText(proximosPartidos.get(posicion).getLugar());
        fechaPartido.setText(proximosPartidos.get(posicion).getFecha());

        tablaProximoPartido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), PartidoActivity.class).putExtra("partido",proximosPartidos.get(posicion)));
            }
        });

        comoLlegar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + proximosPartidos.get(posicion).getEquipoLocal().getCampo().getUbicacion());
                final Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });
    }

    private void desactivarChevron(ImageButton chevron) {
        try {
            chevron.setColorFilter(getResources().getColor(R.color.secondary_text));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void activarChevron(ImageButton chevron) {
        try {
            chevron.setColorFilter(getResources().getColor(R.color.primary_text));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
