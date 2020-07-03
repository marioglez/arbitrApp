package com.example.arbitrapp.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.arbitrapp.R;
import com.example.arbitrapp.modelos.ComparadorDorsales;
import com.example.arbitrapp.modelos.Equipo;
import com.example.arbitrapp.modelos.Evento;
import com.example.arbitrapp.modelos.Jugador;
import com.example.arbitrapp.modelos.Partido;
import com.example.arbitrapp.modelos.Tecnico;
import com.example.arbitrapp.modelos.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collections;
import static com.example.arbitrapp.FirebaseData.*;

public class PartidoDirectoActivity extends AppCompatActivity {

    private Partido partido;
    private RelativeLayout relativeLayout;
    private ImageView escudoLocal, escudoVisitante;
    private TextView nombreLocal, nombreVisitante;
    private TextView marcadorLocal, marcadorVisitante;
    private TableLayout tablaTitularesLocal, tablaSuplentesLocal, tablaTecnicosLocal;
    private TableLayout tablaTitularesVisitante, tablaSuplentesVisitante, tablaTecnicosVisitante;
    private ComparadorDorsales comparadorDorsales;
    private DatabaseReference databaseReference;
    private ValueEventListener valueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partido_directo);

        setTitle(getResources().getString(R.string.enDirecto));
        //boton flecha atras
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        partido = (Partido) getIntent().getSerializableExtra("partido");
        relativeLayout = findViewById(R.id.layout_partido_directo);
        escudoLocal = findViewById(R.id.arbitrar_escudo_local);
        escudoVisitante = findViewById(R.id.arbitrar_escudo_visitante);
        nombreLocal = findViewById(R.id.arbitrar_nombre_local);
        nombreVisitante = findViewById(R.id.arbitrar_nombre_visitante);
        marcadorLocal = findViewById(R.id.arbitrar_marcador_local);
        marcadorVisitante = findViewById(R.id.arbitrar_marcador_visitante);
        tablaTitularesLocal = findViewById(R.id.arbitrar_titulares_locales);
        tablaSuplentesLocal = findViewById(R.id.arbitrar_suplentes_locales);
        tablaTecnicosLocal = findViewById(R.id.arbitrar_tecnicos_locales);
        tablaTitularesVisitante = findViewById(R.id.arbitrar_titulares_visitantes);
        tablaSuplentesVisitante = findViewById(R.id.arbitrar_suplentes_visitantes);
        tablaTecnicosVisitante = findViewById(R.id.arbitrar_tecnicos_visitantes);
        comparadorDorsales = new ComparadorDorsales();

        rellenarMarcador();
        rellenarDatosLocal();
        rellenarDatosVisitante();

        try {
            obtenerInfoDirecto();
        } catch (Exception e) {
            Toast.makeText(PartidoDirectoActivity.this, getResources().getString(R.string.partidoError), Toast.LENGTH_LONG).show();
        }
    }

    private  void rellenarMarcador() {
        try {
            Glide
                    .with(PartidoDirectoActivity.this)
                    .load(Uri.parse(partido.getEquipoLocal().getEscudo()))
                    .into(escudoLocal);
            Glide
                    .with(PartidoDirectoActivity.this)
                    .load(Uri.parse(partido.getEquipoVisitante().getEscudo()))
                    .into(escudoVisitante);
        }catch (Exception e){
            Log.d("ARBITRAR", "pintarDatos: ERROR AL PINTAR ESCUDO");
        }
        nombreLocal.setText(partido.getEquipoLocal().getNombre());
        nombreVisitante.setText(partido.getEquipoVisitante().getNombre());
    }

    private void rellenarDatosLocal() {
        Equipo equipo = partido.getEquipoLocal();
        marcadorLocal.setText(partido.getGolesLocal());
        rellenarTecnicos(equipo.getTecnicosPartido(), tablaTecnicosLocal);
        rellenarJugadores(equipo.getTitulares(),tablaTitularesLocal);
        rellenarJugadores(equipo.getSuplentes(),tablaSuplentesLocal);
    }

    private void rellenarDatosVisitante() {
        Equipo equipo = partido.getEquipoVisitante();
        marcadorVisitante.setText(partido.getGolesVisitante());
        rellenarTecnicos(equipo.getTecnicosPartido(),tablaTecnicosVisitante);
        rellenarJugadores(equipo.getTitulares(),tablaTitularesVisitante);
        rellenarJugadores(equipo.getSuplentes(),tablaSuplentesVisitante);
    }

    private void rellenarTecnicos(ArrayList<Tecnico> tecnicos, TableLayout tableLayout) {
        tableLayout.removeAllViews();
        for(final Tecnico t : tecnicos){
            LayoutInflater inflater = LayoutInflater.from(PartidoDirectoActivity.this);
            TableRow row = (TableRow) inflater.inflate(R.layout.fila_tecnico_arbitrar, relativeLayout, false);
            TextView nombre = row.findViewById(R.id.nombrePersona);
            nombre.setText(t.getNombreCompleto());
            RelativeLayout relativeLayout = row.findViewById(R.id.caja_tecnico);

            for (Evento evento : partido.getEventos()) {
                for (Usuario u : evento.getAutores()){
                    if(u.getUid().equals(t.getUid())) {
                        switch (evento.getAccion()) {
                            case EVENTO_AMARILLA:
                                relativeLayout.setBackgroundColor(getResources().getColor(R.color.amarilloPastel));
                                break;
                            case EVENTO_ROJA:
                            case EVENTO_SEGUNDA_AMARILLA:
                                relativeLayout.setBackgroundColor(getResources().getColor(R.color.rojoPastel));
                                break;
                        }
                    }
                }
            }
            tableLayout.addView(row);
        }
    }

    private void rellenarJugadores(ArrayList<Jugador> jugadores, TableLayout tableLayout) {
        tableLayout.removeAllViews();
        Collections.sort(jugadores,comparadorDorsales);
        for(final Jugador j : jugadores){
            LayoutInflater inflater = LayoutInflater.from(PartidoDirectoActivity.this);
            TableRow row = (TableRow) inflater.inflate(R.layout.fila_jugador_arbitrar, relativeLayout, false);
            TextView dorsal = row.findViewById(R.id.dorsalPersona);
            dorsal.setText(j.getDorsal());

            //Capitan
            if(j.isCapitan()){
                ImageView iconoRol = row.findViewById(R.id.imageview_rol);
                iconoRol.setVisibility(View.VISIBLE);
            } else if(j.getPosicion().equals(JUGADOR_PORTERO)){
                ImageView iconoRol = row.findViewById(R.id.imageview_rol);
                iconoRol.setImageResource(R.drawable.ic_guantes);
                iconoRol.setColorFilter(getResources().getColor(R.color.primary_text));
                iconoRol.setVisibility(View.VISIBLE);
            }

            for (Evento evento : partido.getEventos()) {
                for (Usuario u : evento.getAutores()){
                    if(u.getUid().equals(j.getUid())) {
                        switch (evento.getAccion()) {
                            case EVENTO_GOL:
                                ImageView iconoGol = row.findViewById(R.id.imageview_golFavor);
                                iconoGol.setVisibility(View.VISIBLE);
                                break;
                            case EVENTO_GOL_PROPIA:
                                ImageView iconoGolPropia = row.findViewById(R.id.imageview_golPropia);
                                iconoGolPropia.setVisibility(View.VISIBLE);
                                break;
                            case EVENTO_AMARILLA:
                                ImageView iconoAmarilla = row.findViewById(R.id.imageview_amarilla);
                                iconoAmarilla.setVisibility(View.VISIBLE);
                                break;
                            case EVENTO_ROJA:
                                ImageView iconoRoja = row.findViewById(R.id.imageview_roja);
                                iconoRoja.setVisibility(View.VISIBLE);
                                break;
                            case EVENTO_SEGUNDA_AMARILLA:
                                ImageView iconoSegundaAmarilla = row.findViewById(R.id.imageview_segundaAmarilla);
                                iconoSegundaAmarilla.setVisibility(View.VISIBLE);
                                break;
                            case EVENTO_SUSTITUCION:
                                ImageView iconoSustitucion = row.findViewById(R.id.imageview_sustitucion);
                                iconoSustitucion.setVisibility(View.VISIBLE);
                                break;
                            case EVENTO_LESION:
                                ImageView iconoLesion = row.findViewById(R.id.imageview_lesion);
                                iconoLesion.setVisibility(View.VISIBLE);
                                break;
                        }
                    }
                }
            }

            tableLayout.addView(row);
        }
    }

    private void obtenerInfoDirecto() {
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    partido = new Partido(TEMPORADA_ACTUAL, partido.getSede(), partido.getCategoria(), partido.getDiaPartido(), partido.getIdPartido());
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            rellenarDatosLocal();
                            rellenarDatosVisitante();
                        }
                    }, 3000);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child(COMPETICIONES).child(TEMPORADA_ACTUAL).child(partido.getSede()).child(partido.getCategoria())
                .child(PARTIDOS).child(partido.getJornadaPartido()).child(partido.getDiaPartido()).child(partido.getIdPartido())
                .addValueEventListener(valueEventListener);
    }

    //Boton flecha atras
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        databaseReference.removeEventListener(valueEventListener);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        databaseReference.removeEventListener(valueEventListener);
    }
}
